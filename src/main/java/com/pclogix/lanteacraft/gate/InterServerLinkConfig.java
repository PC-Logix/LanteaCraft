package com.pclogix.lanteacraft.gate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pclogix.lanteacraft.LanteaCraft;
import com.pclogix.lanteacraft.network.InterServerTransferPayload;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLPaths;

/**
 * Server-administered routing for nine-chevron Stargates.
 *
 * <p>This is deliberately separate from the normal NeoForge config. The link
 * table is dynamic, server-only data that is reread when a transfer is
 * attempted, so admins can edit it without changing the mod's fixed config
 * schema.</p>
 */
public final class InterServerLinkConfig {
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get()
            .resolve("lanteacraft")
            .resolve("interserver-links.json");
    private static final long TOKEN_LIFETIME_MILLIS = 120_000L;
    private static final Set<String> USED_NONCES = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> TRANSFER_ATTEMPTS = ConcurrentHashMap.newKeySet();

    private InterServerLinkConfig() {
    }

    public static Optional<TransferRequest> createTransfer(ServerPlayer player, String address) {
        LoadedConfig config = load();
        Link link = config.links().get(address);
        if (link == null) {
            return Optional.empty();
        }

        long expiresAt = System.currentTimeMillis() + TOKEN_LIFETIME_MILLIS;
        String nonce = UUID.randomUUID().toString();
        String body = String.join("|",
                config.serverId(),
                link.remoteServerId(),
                address,
                player.getUUID().toString(),
                Long.toString(expiresAt),
                nonce);
        String token = encodeToken(body, sign(link.linkId(), body));
        return Optional.of(new TransferRequest(link, token));
    }

    /**
     * Sends exactly one transfer request. There is intentionally no retry
     * loop: once the vanilla transfer packet is sent, the source server stops
     * trying to move this player.
     */
    public static boolean sendTransfer(ServerPlayer player, String address) {
        Optional<TransferRequest> request = createTransfer(player, address);
        if (request.isEmpty()) {
            return false;
        }

        if (!TRANSFER_ATTEMPTS.add(player.getUUID())) {
            return false;
        }

        TransferRequest transfer = request.get();
        player.connection.send(new InterServerTransferPayload(
                transfer.link().host(),
                transfer.link().port(),
                transfer.token()));
        player.connection.send(new ClientboundTransferPacket(transfer.link().host(), transfer.link().port()));
        LanteaCraft.LOGGER.info("Sent one-shot inter-server Stargate transfer for {} to {}:{}", address, transfer.link().host(), transfer.link().port());
        return true;
    }

    public static void onPlayerLoggedOut(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TRANSFER_ATTEMPTS.remove(player.getUUID());
        }
    }

    public static void acceptTransfer(ServerPlayer player, String token) {
        ParsedToken parsed = parseToken(token);
        if (parsed == null) {
            rejectTransfer(player, "Invalid inter-server Stargate transfer token.");
            return;
        }

        LoadedConfig config = load();
        Link link = config.links().get(parsed.address());
        if (link == null
                || !config.serverId().equals(parsed.targetServerId())
                || !link.remoteServerId().equals(parsed.sourceServerId())
                || !player.getUUID().equals(parsed.playerId())
                || parsed.expiresAt() < System.currentTimeMillis()
                || !MessageDigest.isEqual(sign(link.linkId(), parsed.body()).getBytes(StandardCharsets.UTF_8), parsed.signature().getBytes(StandardCharsets.UTF_8))
                || !USED_NONCES.add(parsed.nonce())) {
            rejectTransfer(player, "Inter-server Stargate transfer was not accepted by this server.");
            return;
        }

        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, link.localDimension());
        ServerLevel destination = player.server.getLevel(dimensionKey);
        if (destination == null) {
            rejectTransfer(player, "The configured Stargate dimension is unavailable.");
            return;
        }

        BlockPos gate = link.localGate();
        player.teleportTo(destination, gate.getX() + 0.5D, gate.getY(), gate.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.sendSystemMessage(Component.literal("Arrived through the inter-server Stargate."));
        LanteaCraft.LOGGER.info("Accepted inter-server Stargate transfer {} for player {} at {} {}", parsed.address(), player.getGameProfile().getName(), link.localDimension(), gate);
    }

    private static void rejectTransfer(ServerPlayer player, String message) {
        player.connection.disconnect(Component.literal(message));
    }

    private static LoadedConfig load() {
        if (!Files.isRegularFile(CONFIG_PATH)) {
            return new LoadedConfig("", Map.of());
        }

        try {
            JsonObject root = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            String serverId = string(root, "serverId");
            Map<String, Link> links = new HashMap<>();
            JsonObject linkObject = root.has("links") && root.get("links").isJsonObject()
                    ? root.getAsJsonObject("links")
                    : new JsonObject();
            for (Map.Entry<String, JsonElement> entry : linkObject.entrySet()) {
                if (!entry.getValue().isJsonObject()) {
                    continue;
                }

                JsonObject json = entry.getValue().getAsJsonObject();
                BlockPos localGate = coordinates(json, "localGateCoords");
                BlockPos remoteGate = coordinates(json, "remoteGateCoords");
                if (localGate == null || remoteGate == null) {
                    continue;
                }

                links.put(entry.getKey().trim().toUpperCase(), new Link(
                        string(json, "remoteServerId"),
                        string(json, "host"),
                        integer(json, "port", 25565),
                        dimension(json, "localDimension"),
                        localGate,
                        dimension(json, "remoteDimension"),
                        remoteGate,
                        string(json, "linkId")));
            }
            return new LoadedConfig(serverId, links);
        } catch (Exception exception) {
            LanteaCraft.LOGGER.error("Could not read {}", CONFIG_PATH, exception);
            return new LoadedConfig("", Map.of());
        }
    }

    private static BlockPos coordinates(JsonObject object, String name) {
        if (!object.has(name)) {
            return null;
        }

        JsonElement value = object.get(name);
        if (value.isJsonPrimitive()) {
            String[] parts = value.getAsString().trim().split("\\s+");
            if (parts.length != 3) {
                return null;
            }
            try {
                return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        if (!value.isJsonObject()) {
            return null;
        }

        JsonObject coordinates = value.getAsJsonObject();
        try {
            return new BlockPos(coordinates.get("x").getAsInt(), coordinates.get("y").getAsInt(), coordinates.get("z").getAsInt());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String string(JsonObject object, String name) {
        return object.has(name) ? object.get(name).getAsString().trim() : "";
    }

    private static int integer(JsonObject object, String name, int fallback) {
        try {
            return object.has(name) ? object.get(name).getAsInt() : fallback;
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static ResourceLocation dimension(JsonObject object, String name) {
        String value = string(object, name);
        return value.isBlank() ? Level.OVERWORLD.location() : ResourceLocation.parse(value);
    }

    private static String encodeToken(String body, String signature) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString((body + "|" + signature).getBytes(StandardCharsets.UTF_8));
    }

    private static ParsedToken parseToken(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", -1);
            if (parts.length != 7) {
                return null;
            }

            long expiresAt = Long.parseLong(parts[4]);
            String body = String.join("|", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
            return new ParsedToken(body, parts[0], parts[1], parts[2], UUID.fromString(parts[3]), expiresAt, parts[5], parts[6]);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String sign(String key, String body) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not sign inter-server Stargate transfer", exception);
        }
    }

    public record Link(
            String remoteServerId,
            String host,
            int port,
            ResourceLocation localDimension,
            BlockPos localGate,
            ResourceLocation remoteDimension,
            BlockPos remoteGate,
            String linkId) {
    }

    public record TransferRequest(Link link, String token) {
    }

    private record LoadedConfig(String serverId, Map<String, Link> links) {
    }

    private record ParsedToken(
            String body,
            String sourceServerId,
            String targetServerId,
            String address,
            UUID playerId,
            long expiresAt,
            String nonce,
            String signature) {
    }
}
