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
