# BlissGemsFORK

A maintained fork of `ItzJustNett/blissgems` for modern Paper/Spigot servers, with inventory safety fixes, balanced gem text updates, expanded config coverage, and release-ready jar packaging.

## Compatibility

- Minecraft: `1.21.x` (Paper/Spigot API `1.21`)
- Java: `21` recommended for build/runtime

## Installation

1. Download the latest jar from [Releases](../../releases).
2. Place `BlissGemsFORKED-1.0.jar` in your server `plugins/` folder.
3. Start or restart the server.
4. Edit `plugins/BlissGems/config.yml` as needed.
5. Run `/bliss reload` (or restart) after config changes.

## Commands

- Main command: `/bliss`
- Aliases: `/gems`, `/bg`

## Gems (Detailed)

Each player can hold one active gem. Most gems have:
- A passive effect set (always-on while energy state allows passives)
- A primary ability (`/bliss ability:main` or right-click)
- Tier 2 upgrades that unlock stronger passives and additional abilities

### Astra Gem
- Theme: mobility, tracking, and ability denial.
- Passive highlights:
  - Chance to phase through incoming hits.
- Primary:
  - **Astral Daggers**: ranged/targeted damage ability.
- Tier 2 unlocks:
  - **Astral Projection** (secondary): temporary projection state.
  - **Dimensional Drift** (tertiary): stealth/mobility tool.
  - **Dimensional Void** (quaternary): area suppression/pressure tool.

### Fire Gem
- Theme: sustained pressure and area control.
- Passive highlights:
  - Auto-smelt support.
  - Fire resistance enabled by config for both tiers.
- Primary:
  - **Charged Fireball**: charge-based projectile damage.
- Tier 2 unlocks:
  - **Cozy Campfire** (secondary): supportive zone with hostile pressure.
  - **Crisp** (tertiary): water denial/scorch utility.
  - **Meteor Shower** (quaternary): AoE burst/control.

### Flux Gem
- Theme: stun/control and armor pressure.
- Passive highlights:
  - Shocking arrow chance.
  - Charged creeper damage reduction.
- Primary:
  - **Flux Beam**: charged beam with scaling damage.
- Tier 2 unlocks:
  - **Ground** (secondary): disables/grounds target mobility.
  - **Flashbang** (tertiary): AoE blind/disorient.
  - **Kinetic Burst** (quaternary): radial knockback control.

### Life Gem
- Theme: sustain, healing, and team support.
- Passive highlights:
  - Periodic healing.
  - Better anti-undead damage.
  - Better food/saturation value.
- Primary:
  - **Heart Drainer**: life-steal pressure ability.
- Tier 2 unlocks:
  - **Circle of Life** (secondary): team/area HP modulation.
  - **Vitality Vortex** (tertiary): sustained support pressure.
  - **Heart Lock** (quaternary): targeted control utility.

### Puff Gem
- Theme: vertical control and displacement.
- Passive highlights:
  - Double jump.
  - Fall-damage safety.
  - Tier 2 adds sculk immunity.
- Primary:
  - **Dash**: short mobility burst.
- Tier 2 unlocks:
  - **Breezy Bash** (secondary): launch/displace a target.
  - **Group Breezy Bash** (tertiary): AoE displacement.

### Speed Gem
- Theme: tempo and rapid engagement.
- Passive highlights:
  - Soul sand movement immunity.
  - Speed effect scaling by tier.
- Primary:
  - **Blur**: burst-strike mobility damage.
- Tier 2 unlocks:
  - **Speed Storm** (secondary): short high-speed window.
  - **Terminal Velocity** (special burst mode support in ability set).

### Strength Gem
- Theme: raw PvP pressure and melee dominance.
- Passive highlights:
  - Strength scaling by tier.
- Primary:
  - **Bloodthorns**: direct combat pressure.
- Tier 2 unlocks:
  - **Frailer** (secondary): offensive debuff/pressure tool.
  - **Chad Strength** (secondary route): high-power melee amplification.

### Wealth Gem
- Theme: utility, loot control, and stat amplification.
- Passive highlights:
  - Luck + villager discount scaling by tier.
  - Configurable Netherite Scrap furnace multiplier (`passives.wealth.netherite-scrap-multiplier`).
- Primary:
  - **Unfortunate**: target debuff pressure.
- Tier 2 unlocks:
  - **Pockets** command (`/bliss pockets` or `/gems pocket`): opens your Ender Chest directly.
  - **Rich Rush**: temporary utility/combat boost.
  - **Amplification** (`/bliss amplify`): increases active potion effect amplifiers.
  - **Auto-smelt toggle** (`/bliss autosmelt`).

## Energy States

- Gem behavior is tied to energy.
- At low thresholds, passives disable first; at broken state, ability use is blocked.
- Optional zero-energy ban behavior is configurable.

## Permissions

- `blissgems.*` (admin umbrella)
- `blissgems.admin`
- `blissgems.user`
- `blissgems.use`
- `blissgems.info`
- `blissgems.pockets`
- `blissgems.amplify`
- `blissgems.toggle`
- `blissgems.trust`
- `blissgems.ability`
- `blissgems.autosmelt`

## Documentation

Full documentation is available in the [Wiki](../../wiki).

Quick links:

- [Home](../../wiki)
- [Installation](../../wiki/Installation)
- [Configuration](../../wiki/Configuration)
- [Gems and Effects](../../wiki/Gems-and-Effects)
- [Commands and Permissions](../../wiki/Commands-and-Permissions)
- [FAQ](../../wiki/FAQ)

If your wiki is not enabled yet, starter page content is included in [`docs/wiki/`](docs/wiki).

## Release Files

Each tagged release publishes:

- `BlissGemsFORKED-1.0.jar` (server jar)
- `BlissGemsFORKED-1.0-obfuscated.jar` (obfuscated build)

## Credits

- Original project: `ItzJustNett/blissgems`
- Fork maintenance and server-specific improvements: `ignenvixity/blissgemsFORK`
