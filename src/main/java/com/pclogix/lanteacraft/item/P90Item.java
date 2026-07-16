package com.pclogix.lanteacraft.item;

import com.pclogix.lanteacraft.entity.P90BulletEntity;
import com.pclogix.lanteacraft.enchantment.ExplodingEnchantmentHandler;
import com.pclogix.lanteacraft.registry.ModDataComponents;
import com.pclogix.lanteacraft.registry.ModItems;
import com.pclogix.lanteacraft.registry.ModSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class P90Item extends Item {
    public static final int MAGAZINE_CAPACITY = 50;
    private static final int FIRE_INTERVAL = 2;
    private static final float BULLET_SPEED = 5.0F;
    private static final float INACCURACY = 1.25F;

    public P90Item(Properties properties) {
        super(properties);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        ResourceLocation id = enchantment.unwrapKey().map(ResourceKey::location).orElse(null);
        return id != null && (id.equals(ResourceLocation.withDefaultNamespace("power"))
                || id.equals(ResourceLocation.withDefaultNamespace("punch"))
                || id.equals(ResourceLocation.withDefaultNamespace("flame"))
                || id.equals(ResourceLocation.withDefaultNamespace("infinity"))
                || id.equals(ExplodingEnchantmentHandler.EXPLODING.location()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return reload(level, player, hand, stack);
        }

        if (!player.getAbilities().instabuild && rounds(stack) <= 0) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.P90_EMPTY.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
            player.getCooldowns().addCooldown(this, 8);
            return InteractionResultHolder.fail(stack);
        }

        if (!isAutomatic(stack)) {
            player.startUsingItem(hand);
            fire(level, player, stack);
            return InteractionResultHolder.consume(stack);
        }

        player.startUsingItem(hand);
        fire(level, player, stack);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!(living instanceof Player player)) {
            return;
        }
        if (!isAutomatic(stack)) {
            return;
        }
        int usedTicks = getUseDuration(stack, living) - remainingUseDuration;
        if (usedTicks > 0 && usedTicks % FIRE_INTERVAL == 0) {
            if (!player.getAbilities().instabuild && rounds(stack) <= 0) {
                player.stopUsingItem();
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.P90_EMPTY.get(), SoundSource.PLAYERS, 0.7F, 1.0F);
                return;
            }
            fire(level, player, stack);
        }
    }

    private void fire(Level level, Player player, ItemStack stack) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                isAutomatic(stack) ? ModSounds.P90_FIRE_AUTO.get() : ModSounds.P90_FIRE_SINGLE.get(),
                SoundSource.PLAYERS, 0.65F, 0.94F + level.random.nextFloat() * 0.12F);
        if (!level.isClientSide) {
            P90BulletEntity bullet = new P90BulletEntity(level, player);
            bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, BULLET_SPEED, INACCURACY);
            level.addFreshEntity(bullet);
            if (!player.getAbilities().instabuild && !hasEnchantment(level, stack, "infinity")) {
                stack.set(ModDataComponents.P90_ROUNDS, rounds(stack) - 1);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    private InteractionResultHolder<ItemStack> reload(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (rounds(stack) >= MAGAZINE_CAPACITY) {
            return InteractionResultHolder.pass(stack);
        }
        if (!player.getAbilities().instabuild && !consumeMagazine(player)) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.P90_EMPTY.get(), SoundSource.PLAYERS, 0.7F, 0.8F);
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            if (stack.has(ModDataComponents.P90_ROUNDS)) {
                player.getInventory().placeItemBackInInventory(new ItemStack(ModItems.P90_EMPTY_MAGAZINE.get()));
            }
            stack.set(ModDataComponents.P90_ROUNDS, MAGAZINE_CAPACITY);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.P90_RELOAD.get(), SoundSource.PLAYERS, 0.85F, 1.0F);
        player.getCooldowns().addCooldown(this, 30);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static boolean consumeMagazine(Player player) {
        for (ItemStack inventoryStack : player.getInventory().items) {
            if (inventoryStack.is(ModItems.P90_MAGAZINE.get())) {
                inventoryStack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static int rounds(ItemStack stack) {
        return Mth.clamp(stack.getOrDefault(ModDataComponents.P90_ROUNDS, 0), 0, MAGAZINE_CAPACITY);
    }

    public static boolean isAutomatic(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.P90_AUTOMATIC, true);
    }

    private static boolean hasEnchantment(Level level, ItemStack stack, String name) {
        ResourceKey<Enchantment> key = ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.withDefaultNamespace(name));
        Holder<Enchantment> enchantment = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(key)
                .orElse(null);
        return enchantment != null && stack.getEnchantmentLevel(enchantment) > 0;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * rounds(stack) / MAGAZINE_CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.hsvToRgb((float)rounds(stack) / MAGAZINE_CAPACITY / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.lanteacraft.p90.ammo", rounds(stack), MAGAZINE_CAPACITY).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(isAutomatic(stack)
                ? "item.lanteacraft.p90.mode.auto"
                : "item.lanteacraft.p90.mode.single").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.lanteacraft.p90.mode.toggle").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.lanteacraft.p90.reload").withStyle(ChatFormatting.DARK_GRAY));
    }
}
