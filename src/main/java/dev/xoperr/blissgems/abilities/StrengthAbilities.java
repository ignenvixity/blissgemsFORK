package dev.xoperr.blissgems.abilities;

import dev.xoperr.blissgems.BlissGems;
import dev.xoperr.blissgems.utils.ParticleUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class StrengthAbilities {
    private final BlissGems plugin;
    private final Map<UUID, BukkitTask> activeTrackerTasks = new HashMap<>();

    public StrengthAbilities(BlissGems plugin) {
        this.plugin = plugin;
    }

    public void onRightClick(Player player, int tier) {
        if (tier == 2 && player.isSneaking()) {
            this.chadStrength(player);
        } else if (tier == 2) {
            // Tier 2 without sneak: try Frailer first (single target), fallback to Tracker.
            LivingEntity target = this.getTargetEntity(player, 15);
            if (target != null) {
                this.frailerPower(player);
            } else {
                this.playerTracker(player);
            }
        } else {
            // Tier 1: Player Tracker.
            this.playerTracker(player);
        }
    }

    public void playerTracker(Player player) {
        String abilityKey = "strength-tracker";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 0.8f, 1.3f);

        BukkitTask existing = activeTrackerTasks.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }

        final int durationSeconds = this.plugin.getConfig().getInt("abilities.durations.strength-tracker", 15);
        BukkitTask task = new BukkitRunnable() {
            int secondsLeft = durationSeconds;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    activeTrackerTasks.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                Player target = findNearestUntrustedPlayer(player);
                if (target != null) {
                    spawnTrackerTrail(player, target, secondsLeft);
                    int blocks = (int) Math.round(Math.sqrt(target.getLocation().distanceSquared(player.getLocation())));
                    String popup = formatTrackerActionBar(target.getName(), blocks, secondsLeft);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(popup));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(formatTrackerActionBar("No target", 0, secondsLeft)));
                }

                secondsLeft--;
                if (secondsLeft <= 0) {
                    activeTrackerTasks.remove(player.getUniqueId());
                    this.cancel();
                    playTrackerFadeOut(player);
                }
            }
        }.runTaskTimer(this.plugin, 0L, 20L);

        activeTrackerTasks.put(player.getUniqueId(), task);
    }

    private Player findNearestUntrustedPlayer(Player source) {
        return source.getWorld().getPlayers().stream()
            .filter(other -> other != source)
            .filter(other -> !other.isDead())
            .filter(other -> !this.plugin.getTrustedPlayersManager().isTrusted(source, other))
            .min(Comparator.comparingDouble(other -> other.getLocation().distanceSquared(source.getLocation())))
            .orElse(null);
    }

    private void spawnTrackerTrail(Player source, Player target, int secondsLeft) {
        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.2f);
        Location start = source.getEyeLocation();
        Location end = target.getLocation().add(0.0, 1.0, 0.0);

        double distance = start.distance(end);
        int points = Math.max(6, (int) (distance * 3));
        int particleCount = secondsLeft <= 5 ? 1 : 2;

        for (int i = 0; i <= points; i++) {
            double t = (double) i / (double) points;
            Location point = start.clone().add(end.clone().subtract(start).toVector().multiply(t));
            source.getWorld().spawnParticle(Particle.DUST, point, particleCount, 0.03, 0.03, 0.03, 0.0, redDust, true);
        }
    }

    private String formatTrackerActionBar(String targetName, int distanceMeters, int secondsLeft) {
        String color;
        if (secondsLeft > 5) {
            color = "\u00a7c";
        } else if (secondsLeft > 2) {
            color = "\u00a77";
        } else {
            color = "\u00a78";
        }
        if ("No target".equals(targetName)) {
            return color + "No target - Distance --";
        }
        return color + targetName + " - Distance " + distanceMeters + "m";
    }

    private void playTrackerFadeOut(Player player) {
        new BukkitRunnable() {
            int step = 0;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (step == 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a77Tracker fading..."));
                } else if (step == 1) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a78Tracker fading..."));
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
                    this.cancel();
                    return;
                }
                step++;
            }
        }.runTaskTimer(this.plugin, 0L, 10L);
    }

    public void frailerPower(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "strength-frailer";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }
        LivingEntity target = this.getTargetEntity(player, 15);
        if (target == null) {
            player.sendMessage("\u00a7cNo target found!");
            return;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1, false, true));

        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.5f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.5, 0.5, 0.0, redDust, true);
        target.getWorld().spawnParticle(Particle.SMOKE, target.getLocation().add(0.0, 1.0, 0.0), 20, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1.0f, 0.8f);
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Frailer Power"));
    }

    public void chadStrength(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "strength-chad";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 1, false, true));

        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
        player.spawnParticle(Particle.DUST, player.getLocation().add(0.0, 2.0, 0.0), 30, 0.5, 0.5, 0.5, 0.0, redDust, true);
        player.spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation().add(0.0, 2.0, 0.0), 20, 0.5, 0.5, 0.5);
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Chad Strength"));
    }

    private LivingEntity getTargetEntity(Player player, int range) {
        org.bukkit.util.RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            (double) range,
            entity -> entity instanceof LivingEntity && entity != player
        );
        return result != null ? (LivingEntity) result.getHitEntity() : null;
    }
}
