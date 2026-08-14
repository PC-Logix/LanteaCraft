# LanteaCraft

LanteaCraft is a modern NeoForge rewrite of the classic Stargate-themed Minecraft mod. The current 0.1.x build targets Minecraft 1.21.1 and focuses on getting the core Stargate loop working again: build an assembled gate, discover its address, dial another gate, and travel through the event horizon.

This is still an early rewrite. The mod is playable enough to test the main systems, but several features are prototype-quality and the final balance, progression, visuals, and compatibility surface are still in progress.

## Current Status

Implemented in the current source tree:

- NeoForge Minecraft 1.21.1 project targeting Java 21.
- Buildable 7x7 Stargate multiblocks.
- Four Stargate/DHD visual variants:
    - Milky Way
    - Nox
    - Wraith
    - Pegasus
- Seven-symbol Stargate addresses using six location glyphs plus one dimension glyph.
- Persistent Stargate network saved data.
- Dial Home Device block and custom dialing screen.
- Cross-dimensional Stargate travel between active gates.
- One-way wormhole connections with timeout cleanup.
- Optional incoming-side travel through config.
- Opening kawoosh damage/death through config.
- Event horizon rendering, glyph ring rendering, chevron lighting, and dialing animation.
- Bottom-row camouflage for assembled Stargates.
- Transport Ring blocks for vertical transport.
- Naquadah and trinium ores, raw materials, ingots, and storage blocks.
- Worldgen JSON for naquadah and trinium ores.
- Crafting recipes for Stargate parts, DHDs, crystals, materials, variants, and transport rings.
- Optional CC:Tweaked peripheral support for assembled Stargates.

Not currently implemented or not considered complete:

- Power requirements.
- Iris/shield mechanics.
- Address books.
- Gate ownership or permissions.
- Destination busy/lockout rules beyond the current prototype behavior.
- Dedicated progression/balance pass.
- REI/JEI custom integration.

## Requirements

- Minecraft: `1.21.1`
- Mod loader: NeoForge `21.1.234`
- Java: `21`
- Optional: CC:Tweaked `1.120.0+`

## Building From Source

From the project root:

```bash
./gradlew build
```

On Windows:

```bat
gradlew.bat build
```

The built mod jar is written to:

```text
build/libs/
```

Common development runs:

```bash
./gradlew runClient
./gradlew runServer
./gradlew runData
```

## Stargates

A Stargate is a vertical 7x7 frame centered on a Stargate Base block at the bottom middle. All frame blocks must be from the same variant set. Mixed Milky Way/Nox/Wraith/Pegasus frames do not assemble.

Required blocks:

- 1 Stargate Base
- 14 Stargate Ring Segments
- 9 Stargate Chevrons

Frame layout:

```text
Y=6   R C R C R C R
Y=5   R           R
Y=4   C           C
Y=3   R           R
Y=2   R           R
Y=1   C           C
Y=0   R C R B R C R
```

Legend:

- `B` = Stargate Base
- `R` = Stargate Ring Segment
- `C` = Stargate Chevron
- blank spaces = air

When `autoAssembleStargates` is enabled, a complete frame assembles automatically after the final valid block is placed.

### Stargate Interaction

With an empty hand:

- Right-click an assembled Stargate to display its address in chat.
- The address message is clickable/copyable.
- Sneak + right-click an assembled Stargate to disassemble it.

With a block item:

- Right-click the assembled bottom row with a valid solid block to apply bottom-row camouflage.

Breaking an assembled Stargate block attempts to disassemble and unregister that gate from the saved network.

## Addresses

Gate addresses are seven glyphs long.

The current address format is:

```text
6 location glyphs + 1 dimension glyph
```

The glyph alphabet is:

```text
ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-+
```

Reserved dimension glyphs:

- `A` = Overworld
- `N` = Nether
- `E` = End

Other dimensions are assigned available glyphs and persisted in the Stargate network saved data.

## Dial Home Devices

Place a DHD near an assembled Stargate and right-click it to open the dialing screen.

Current DHD behavior:

- The DHD searches for the nearest assembled Stargate within `dhdSearchRadius`.
- The default search radius is 8 blocks.
- Addresses can be typed, pasted, or entered with the on-screen glyph buttons.
- Press Enter or the center button to dial.
- Submitting a blank address disconnects the local outgoing connection.

Dialing rules in the current implementation:

- The destination address must exist in saved Stargate network data.
- Dialing the same local gate is rejected.
- The connection is stored as one-way: source address -> destination address.
- The outgoing gate controls disconnect.
- Incoming-side travel is rejected unless enabled in config.

### Inter-server Stargates

Nine-chevron addresses are reserved for server-to-server Stargate transfers. They are configured by server administrators in the server-only file `config/lanteacraft/interserver-links.json`. The source server sends one client transfer request and does not retry if the destination connection fails.

Both servers must configure the same link. `localGateCoords` identifies the gate used on that server, `remoteGateCoords` documents the matching gate on the other server, and `linkId` must be identical on both sides. Treat `linkId` as a shared secret.

Server A:

```json
{
  "serverId": "server-a",
  "links": {
    "ADDRESS1-": {
      "localDimension": "minecraft:overworld",
      "localGateCoords": "10 64 10",
      "remoteServerId": "server-b",
      "host": "serverb.com",
      "port": 25536,
      "remoteDimension": "minecraft:overworld",
      "remoteGateCoords": "50 64 -40",
      "linkId": "replace-with-a-long-random-shared-value"
    }
  }
}
```

Server B uses the same address and `linkId`, reverses the server IDs and endpoints, and sets its own gate as `localGateCoords`:

```json
{
  "serverId": "server-b",
  "links": {
    "ADDRESS1-": {
      "localDimension": "minecraft:overworld",
      "localGateCoords": "50 64 -40",
      "remoteServerId": "server-a",
      "host": "servera.com",
      "port": 25565,
      "remoteDimension": "minecraft:overworld",
      "remoteGateCoords": "10 64 10",
      "linkId": "replace-with-a-long-random-shared-value"
    }
  }
}
```

The destination receives a short-lived, player-bound handoff token and accepts it only when its matching link configuration authorizes the source server. No Minecraft-version, mod-list, or other compatibility probing is performed; those are administrator responsibilities.

## Stargate Travel

When a wormhole is open, entities entering the outgoing event horizon are teleported to the destination gate. Travel preserves relative position and attempts to transform facing/velocity to match the destination gate orientation.

The current handler affects non-passenger entities. Players, mobs, and other entities may be moved if they can be teleported by Minecraft's normal entity/dimension rules.

By default:

- Wormholes time out after 60 seconds.
- Incoming-side travel is disabled.
- A teleport cooldown prevents immediate re-trigger loops.
- The kawoosh can kill entities caught in front of an opening gate.

## Transport Rings

Transport Rings are a separate prototype transport system.

Current behavior:

- Place at least two Transport Ring blocks at different positions in the same chunk.
- Right-click one ring to activate it.
- The source searches the same chunk column for the nearest non-busy Transport Ring.
- After the animation delay, entities in the beam area are moved to the destination ring.
- The source and destination enter a short cooldown.

Current limitations:

- Transport Rings do not search across chunks.
- There is no address, power, ownership, or controller system for rings yet.

## Materials And Worldgen

LanteaCraft currently adds:

- Naquadah Ore
- Trinium Ore
- Naquadah
- Trinium
- Naquadah Ingot
- Trinium Ingot
- Block of Naquadah
- Block of Trinium
- Blank Crystal
- Control Crystal
- Core Crystal

Naquadah and trinium ore generation is provided through worldgen JSON and a NeoForge biome modifier.

## Recipes

The source includes vanilla crafting recipes for most current craftable items and blocks. Zero Point Modules intentionally do not have a default recipe; pack makers can add one with a datapack if they want craftable ZPMs.

### Materials

Naquadah Ingot, shapeless:

```text
Iron Ingot + Naquadah -> Naquadah Ingot
```

Trinium Ingot, shapeless:

```text
Iron Ingot + Trinium -> Trinium Ingot
```

Naquadah Block:

```text
NNN
NNN
NNN
```

`N` = Naquadah Ingot

The block can be crafted back into 9 Naquadah Ingots.

Trinium Block:

```text
TTT
TTT
TTT
```

`T` = Trinium Ingot

The block can be crafted back into 9 Trinium Ingots.

### Crystals

Blank Crystal:

```text
GGG
GDG
GGG
```

- `G` = Glass Pane
- `D` = Diamond

Control Crystal:

```text
RRR
RBR
RRR
```

- `R` = Redstone
- `B` = Blank Crystal

Core Crystal:

```text
LLL
LBL
LLL
```

- `L` = Lapis Lazuli
- `B` = Blank Crystal

### Stargate Parts

Stargate Ring Segment:

```text
ISI
NNN
III
```

- `I` = Iron Ingot
- `S` = Chiseled Sandstone
- `N` = Naquadah Ingot

Stargate Chevron:

```text
SGS
NPN
IRI
```

- `S` = Chiseled Sandstone
- `G` = Glowstone Dust
- `N` = Naquadah Ingot
- `P` = Ender Pearl
- `I` = Iron Ingot
- `R` = Redstone

Stargate Base:

```text
SRS
NEN
ICI
```

- `S` = Chiseled Sandstone
- `R` = Redstone
- `N` = Naquadah Ingot
- `E` = Eye of Ender
- `I` = Iron Ingot
- `C` = Core Crystal

### Dial Home Device

DHD:

```text
SRS
NCN
III
```

- `S` = Chiseled Sandstone
- `R` = Redstone
- `N` = Naquadah Ingot
- `C` = Control Crystal
- `I` = Iron Ingot

### Variant Conversions

Variant Stargate parts and DHDs are shapeless dye conversions from the Milky Way versions:

```text
Milky Way part + Green Dye  -> Nox part
Milky Way part + Blue Dye   -> Pegasus part
Milky Way part + Purple Dye -> Wraith part
```

This applies to:

- Stargate Ring Segment
- Stargate Chevron
- Stargate Base
- DHD

### Transport Ring

Transport Ring:

```text
TGT
NCN
TET
```

- `T` = Trinium Ingot
- `G` = Gold Ingot
- `N` = Naquadah Ingot
- `C` = Control Crystal
- `E` = Ender Pearl

## Configuration

The common config currently exposes:

| Option | Default | Description |
| --- | ---: | --- |
| `debugLogging` | `false` | Enables extra startup and system logging. |
| `allowIncomingWormholeTravel` | `false` | Allows entities to travel back through the incoming side of a wormhole. |
| `kawooshDeath` | `true` | Makes the opening kawoosh kill entities caught in front of an active gate. |
| `autoAssembleStargates` | `true` | Automatically assembles complete Stargate multiblocks. |
| `gateTimeoutSeconds` | `60` | How long an open wormhole remains active. Set to `0` to disable automatic timeout. |
| `dhdSearchRadius` | `8` | Maximum block radius a DHD searches for a nearby assembled Stargate. |
| `gateTeleportCooldownTicks` | `40` | Cooldown before the same entity can trigger Stargate travel again. |
| `enableOreRetrogen` | `false` | Generates LanteaCraft ores in already-existing chunks as they load. |
| `oreRetrogenChunksPerTick` | `1` | Maximum old chunks processed for ore retrogen per dimension tick. |

### Optional FE Power

Stargate bases can accept FE into an internal buffer when `power.enableFePower=true`. Classic gate behavior remains available by default: `power.requirePowerToDial=false` and `power.requirePowerToMaintainWormhole=false` mean dialing and active wormholes do not require FE unless a pack enables those rules.

Powered dialing charges the origin gate by default. Active wormholes can draw sustain power from `ORIGIN_ONLY`, `DESTINATION_ONLY`, `BOTH_SIDES`, `PREFER_ORIGIN`, or `PREFER_DESTINATION`, and can use configurable same-dimension distance scaling and cross-dimension multipliers. Ordinary cross-dimension dials such as Nether, End, and Abydos are intentionally modest by default. Atlantis has a separate long-range dial cost so a single Energy Crystal is not enough by default. Generated ancient gates provide a configurable per-operation ancient power allowance instead of infinite FE; ordinary cross-dimension dials fit inside that allowance, but very expensive dials need supplemental player-provided power. Energy irises consume FE while closed when enabled and can fail open if unpowered.

The Naquadah Generator is LanteaCraft's native FE source. It uses the legacy 1.7.10 generator model, burns Naquadah fuel, stores FE internally, and outputs to adjacent FE handlers.

Energy Crystals hold 1,000,000 FE by default. Zero Point Modules hold 1,000,000,000 FE and can be installed in a ZPM Hub, which exposes that stored power to nearby linked Stargates and adjacent FE consumers. Zero Point Modules intentionally have no default crafting recipe. A default Atlantis dial requires an instantaneous 75,000,000 FE burst from linked ZPM infrastructure, then consumes 50,000 FE/t from linked ZPM infrastructure while the wormhole remains open. It still requires an installed Eighth Chevron Crystal for extended-address dialing.

ZPM loot uses the custom `lanteacraft:set_zpm_energy` loot function, which accepts any vanilla number provider under `energy`. The built-in chest injections make ZPMs very rare and heavily weighted toward low remaining charge. Datapacks can use the same function, for example:

```json
{
  "function": "lanteacraft:set_zpm_energy",
  "energy": {
    "type": "minecraft:uniform",
    "min": 5000000,
    "max": 25000000
  }
}
```

## CC:Tweaked / ComputerCraft Support

If CC:Tweaked is present, assembled Stargate blocks expose a `stargate` peripheral.

Current Lua-facing functions include:

- `getAddress()`
- `getStatus()`
- `isConnected()`
- `isDialing()`
- `getRemoteAddress()`
- `dial(address)`
- `disconnect()`

Attached computers receive `stargate_event` events for gate activity such as dialing, opening, closing, timeout, disconnects, and entity transit/local gate interactions.

Active Stargate wormholes also act as CC:Tweaked wireless repeaters. A wireless modem within range of one connected gate can transmit a `modem_message` through the wormhole; the packet is re-broadcast as a fresh wireless hop from the remote gate, allowing interdimensional links or long-distance same-dimension links between computers near each gate.

ComputerCraft support is optional. LanteaCraft compiles against the CC:Tweaked API and declares the mod dependency as optional.

## OpenComputers Support

If OpenComputers is present, place an Adapter next to an assembled Stargate base. The Adapter exposes a `stargate` component with the same gate-control, status, iris, and GDO methods listed above for CC:Tweaked, including `hasIris()`, `getIrisType()`, `getIrisState()`, `openIris()`, `closeIris()`, `isIrisRedstoneEnabled()`, `isIrisRedstoneLocked()`, `setIrisRedstoneEnabled(enabled)`, `getGdoCode()`, and `setGdoCode(code)`.

OpenComputers programs receive `stargate_event` signals with the event payload table. GDO activity also emits `stargate_idc_received` with the receiver address, code, acceptance state, action, iris state flags, and full payload.

Active Stargate wormholes also repeat OpenComputers wireless network packets across the connection. A wireless network card in range of one gate can reach receivers within 64 blocks of the remote gate, including across dimensions; packet source, destination, port, payload, and TTL semantics are preserved.

The optional `LanteaCraft Stargate API` loot disk provides an OpenOS convenience wrapper and a real `/bin/stargate` dialing program. Copy `/lib/stargate.lua` and `/bin/stargate.lua` from the disk to the computer, then run `stargate <address>`, `stargate status`, `stargate disconnect`, or `stargate iris open|close`. The library also provides `pullEvent()` and `pullIdcEvent()` helpers; pass a component address as the final argument when controlling a non-primary gate.

OpenComputers support is optional. Builds compile against the `li.cil.oc:opencomputers:1.9.4-2:api` Maven artifact. To test the integration in a local game, install the matching OpenComputers: Rebooted and ScalableCatsForce jars alongside LanteaCraft.

## Known Prototype Areas

The following areas are expected to change:

- Destination busy handling is incomplete.
- Gate visual state and saved network state can drift if dimensions are unavailable during cleanup.
- Save/reload behavior for active wormholes needs more testing.
- Old rewrite snapshot address data is not migrated.
- Transport Rings are functional but very early.
- Multiplayer/dedicated-server behavior needs more broad testing.
