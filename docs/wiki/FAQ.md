# FAQ

## The plugin loaded, but commands do not work

- Check startup console for errors.
- Confirm the jar is in `plugins/`.
- Confirm permissions are granted correctly.
- Test with `/bliss` as OP first.

## Why are effects different from old lore text?

This fork aligns visible strings with balancing behavior and config-driven values. Final effect strength is controlled by `config.yml`.

## Is the obfuscated jar required?

No. Use the normal jar for standard deployment:

- `BlissGemsFORKED-1.0.jar`

The obfuscated jar is optional:

- `BlissGemsFORKED-1.0-obfuscated.jar`

## How do I update safely?

1. Back up existing plugin data.
2. Replace jar.
3. Start server and verify logs.
4. Compare and merge any new config keys.

## Where should I report issues?

Use GitHub Issues on this repository with:

- Server version
- Plugin version/tag
- Steps to reproduce
- Relevant config section
- Console errors (if any)
