# Commands and Permissions

## Base Command

- `/bliss`
- Aliases: `/gems`, `/bg`

## Common Player Subcommands

- `/bliss info`
- `/bliss ability:main`
- `/bliss ability:secondary`
- `/bliss trust <player>`
- `/bliss untrust <player>`
- `/bliss trustlist`
- `/bliss pockets`
- `/bliss amplify`
- `/bliss autosmelt`

Note: exact subcommand availability depends on gem, tier, and permissions.

## Permissions

- `blissgems.*`: full access
- `blissgems.admin`: admin command access
- `blissgems.user`: standard user access
- `blissgems.use`: use gem abilities
- `blissgems.info`: use info command
- `blissgems.pockets`: use pockets command
- `blissgems.amplify`: use amplification command
- `blissgems.toggle`: use click-toggle command
- `blissgems.trust`: use trust system commands
- `blissgems.ability`: use ability commands
- `blissgems.autosmelt`: use autosmelt command

## Admin Notes

- Keep `blissgems.admin` restricted to staff.
- Give `blissgems.user` to default player groups.
- If using a permissions plugin, inherit from `blissgems.user` first and then grant extras.
