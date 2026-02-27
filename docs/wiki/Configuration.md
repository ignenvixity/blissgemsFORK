# Configuration

Main file: `plugins/BlissGems/config.yml`

## Major Sections

- `energy`: gain/loss, caps, and zero-energy behavior
- `gems`: gem enable flags and global gem behavior
- `crafting`: custom recipe toggles
- `passives`: passive effect settings and tier values
- `abilities`: cooldowns, durations, ranges, damage
- `auto-enchant`: tier-2 auto-enchant toggles
- `upgrader`, `trader`, `energy-bottle`, `repair-kit`, `revive-beacon`
- `messages`: all customizable player/admin messages

## Notable Customizable Values

- `passives.wealth.netherite-scrap-multiplier`
- `passives.fire.tier1.fire-resistance`
- `passives.fire.tier2.fire-resistance`
- `abilities.cooldowns.*`
- `abilities.durations.*`
- `messages.*`

## Best Practices

- Keep one backup of your previous config before editing.
- Change one section at a time and test in-game.
- Prefer using `/bliss reload` only for simple text/number changes.
- Restart fully after large config edits.

## Message Keys

All message strings are in `messages.*` in `config.yml`.

Recommended format:

- Keep placeholders unchanged (examples: `{player}`, `{amount}`, `{seconds}`).
- Keep color formatting consistent with your server style.
- Do not remove keys that are actively used by commands/listeners.
