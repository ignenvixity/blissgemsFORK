# Gems and Effects

BlissGems has 8 gem types:

- Astra
- Fire
- Flux
- Life
- Puff
- Speed
- Strength
- Wealth

Each gem has:

- Tier 1 and Tier 2 behavior
- Passive effects
- Ability kit with cooldowns from `config.yml`

## Balancing Source of Truth

For this fork, balancing values should be treated as config-driven:

- Passive values: `passives.*`
- Ability values: `abilities.*`
- Cooldowns: `abilities.cooldowns.*`
- Durations: `abilities.durations.*`

Item text/lore has been aligned to match balancing intent, but final behavior is always determined by runtime config values.

## Wealth-Specific Note

Netherite scrap furnace bonus is configurable:

- `passives.wealth.netherite-scrap-multiplier`
- `1.0` = no bonus
- `2.0` = double output
- `3.0` = triple output
