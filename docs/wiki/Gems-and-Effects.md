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

## 3.8 Ability Parity (Fork)

This fork is aligned to 3.8 command-path behavior for all gem routes.

- Astra: `main` Daggers/Tag, `secondary` Projection/Spook, `tertiary` Drift, `quaternary` Void
- Fire: `main` Charged Fireball, `secondary` Campfire, `tertiary` Crisp, `quaternary` Meteor Shower
- Flux: `main` Ground, `secondary` Ground, `tertiary` Flashbang, `quaternary` Kinetic Burst
- Life: `main` Heart Drainer, `secondary` Circle of Life, `tertiary` Vitality Vortex, `quaternary` Heart Lock
- Puff: `main` Dash, `secondary` Breezy Bash, `tertiary` Group Breezy Bash
- Speed: `main` Right-click router, `secondary` Speed Storm, `tertiary` Terminal Velocity
- Strength: `main` Nullify, `secondary` Frailer, `tertiary` Shadow Stalker (tracker flow)
- Wealth: `main` Unfortunate, `secondary` Rich Rush, `tertiary` Item Lock, `quaternary` Amplification

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

Wealth `pockets` is intentionally preserved as Ender Chest access on this fork.
