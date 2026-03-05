package dev.xoperr.blissgems.abilities;

import dev.xoperr.blissgems.BlissGems;
import dev.xoperr.blissgems.utils.ParticleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class StrengthAbilities {
    private final BlissGems plugin;
    private final Map<UUID, UUID> trackedTargets = new HashMap<>();
    private final Map<UUID, BukkitTask> trackingTasks = new HashMap<>();

    public StrengthAbilities(BlissGems plugin) {
        this.plugin = plugin;
    }

    public void onRightClick(Player player, int tier) {
        if (tier >= 2 && player.isSneaking()) {
            this.frailerPower(player);
        } else {
            this.nullify(player);
        }
    }

    public void nullify(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "strength-nullify";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        int range = this.plugin.getConfig().getInt("abilities.strength-nullify.range", 20);
        LivingEntity target = this.getTargetEntity(player, range);
        if (target == null) {
            player.sendMessage("\u00a7cNo target found!");
            return;
        }
        if (target instanceof Player targetPlayer && this.plugin.getTrustedPlayersManager().isTrusted(player, targetPlayer)) {
            player.sendMessage("\u00a7cYou cannot nullify a trusted player!");
            return;
        }

        int removed = 0;
        for (PotionEffect effect : new ArrayList<>(target.getActivePotionEffects())) {
            target.removePotionEffect(effect.getType());
            removed++;
        }

        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.5f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.8, 0.5, 0.0, redDust, true);
        target.getWorld().spawnParticle(Particle.SMOKE, target.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.8, 0.5, 0.05);
        target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0.0, 1.0, 0.0), 10, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.8f, 1.2f);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 0.8f, 1.2f);

        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        String targetName = target instanceof Player targetPlayer ? targetPlayer.getName() : "target";
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Nullify")
            + " \u00a77(Stripped " + removed + " effects from " + targetName + ")");
        if (target instanceof Player targetPlayer) {
            targetPlayer.sendMessage("\u00a7c\u00a7oYour potion effects have been nullified!");
        }
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

        int range = this.plugin.getConfig().getInt("abilities.strength-frailer.range", 15);
        LivingEntity target = this.getTargetEntity(player, range);
        if (target == null) {
            player.sendMessage("\u00a7cNo target found!");
            return;
        }
        if (target instanceof Player targetPlayer && this.plugin.getTrustedPlayersManager().isTrusted(player, targetPlayer)) {
            player.sendMessage("\u00a7cYou cannot use Frailer on a trusted player!");
            return;
        }

        for (PotionEffect effect : new ArrayList<>(target.getActivePotionEffects())) {
            target.removePotionEffect(effect.getType());
        }

        int weaknessDuration = this.plugin.getConfig().getInt("abilities.durations.strength-frailer-weakness", 20) * 20;
        int witherDuration = this.plugin.getConfig().getInt("abilities.durations.strength-frailer-wither", 40) * 20;
        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weaknessDuration, 0, false, true));
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration, 0, false, true));

        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.5f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.8, 0.5, 0.0, redDust, true);
        target.getWorld().spawnParticle(Particle.SMOKE, target.getLocation().add(0.0, 1.0, 0.0), 25, 0.5, 0.8, 0.5, 0.05);
        target.getWorld().spawnParticle(Particle.CRIMSON_SPORE, target.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1.0f, 0.8f);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_WITHER_HURT, 1.0f, 0.8f);

        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Frailer Power"));
        if (target instanceof Player targetPlayer) {
            targetPlayer.sendMessage("\u00a7c\u00a7oYou have been weakened by Frailer Power!");
        }
    }

    public void playerTracker(Player player) {
        this.shadowStalker(player);
    }

    public void shadowStalker(final Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "strength-shadow-stalker";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        if (this.trackedTargets.containsKey(player.getUniqueId())) {
            this.stopTracking(player);
            player.sendMessage("\u00a7c\u00a7oStopped tracking.");
            return;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType() == Material.AIR) {
            player.sendMessage("\u00a7c\u00a7oHold a player head!");
            return;
        }

        UUID targetUuid = null;
        if (held.getType() == Material.PLAYER_HEAD && held.hasItemMeta()) {
            SkullMeta skullMeta = (SkullMeta) held.getItemMeta();
            if (skullMeta.getOwningPlayer() != null) {
                targetUuid = skullMeta.getOwningPlayer().getUniqueId();
            }
        }
        if (targetUuid == null) {
            player.sendMessage("\u00a7c\u00a7oThis is not a valid player head!");
            return;
        }
        if (targetUuid.equals(player.getUniqueId())) {
            player.sendMessage("\u00a7c\u00a7oYou cannot track yourself!");
            return;
        }

        Player target = Bukkit.getPlayer(targetUuid);
        if (target == null || !target.isOnline()) {
            player.sendMessage("\u00a7c\u00a7oTarget player is not online!");
            return;
        }

        if (held.getAmount() > 1) {
            held.setAmount(held.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        int durationSeconds = this.plugin.getConfig().getInt("abilities.strength-shadow-stalker.duration", 60);
        int maxTicks = durationSeconds * 20;
        int invisibilityMaxRange = this.plugin.getConfig().getInt("abilities.strength-shadow-stalker.invisibility-max-range", 2500);
        UUID trackerUuid = player.getUniqueId();
        final UUID huntedUuid = targetUuid;

        this.trackedTargets.put(trackerUuid, huntedUuid);
        target.sendMessage("\u00a7c\u00a7l\u26a0 \u00a7c\u00a7oYou are being hunted...");
        target.playSound(target.getLocation(), Sound.ENTITY_WARDEN_NEARBY_CLOSER, 0.7f, 1.5f);

        Particle.DustOptions redDust = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 1.5f);
        player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.5, 0.5, 0.0, redDust, true);
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 1.0f, 1.2f);

        BukkitTask task = new BukkitRunnable() {
            int elapsedTicks = 0;

            @Override
            public void run() {
                Player tracker = Bukkit.getPlayer(trackerUuid);
                Player hunted = Bukkit.getPlayer(huntedUuid);
                if (!(tracker != null && tracker.isOnline() && hunted != null && hunted.isOnline()
                    && this.elapsedTicks < maxTicks && trackedTargets.containsKey(trackerUuid))) {
                    stopTracking(tracker != null ? tracker : player);
                    this.cancel();
                    return;
                }

                this.elapsedTicks++;
                if (this.elapsedTicks % 20 != 0) {
                    return;
                }

                if (tracker.getWorld().equals(hunted.getWorld())) {
                    double distance = tracker.getLocation().distance(hunted.getLocation());
                    boolean targetInvisible = hunted.hasPotionEffect(PotionEffectType.INVISIBILITY);
                    if (targetInvisible && distance > invisibilityMaxRange) {
                        tracker.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("\u00a7c\u00a7lTRACKING \u00a77... \u00a7c" + hunted.getName() + " \u00a77(invisible, too far)"));
                    } else {
                        String arrow = this.getDirectionArrow(tracker.getLocation(), hunted.getLocation());
                        tracker.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            new TextComponent("\u00a7c\u00a7lTRACKING \u00a77" + arrow + " \u00a7c" + hunted.getName() + " \u00a77" + (int) distance + "m"));

                        for (UUID trusted : plugin.getTrustedPlayersManager().getTrustedPlayers(tracker)) {
                            Player trustedPlayer = Bukkit.getPlayer(trusted);
                            if (trustedPlayer == null || !trustedPlayer.isOnline()) {
                                continue;
                            }
                            if (!trustedPlayer.getWorld().equals(tracker.getWorld())) {
                                continue;
                            }
                            if (trustedPlayer.getLocation().distance(tracker.getLocation()) >= 50.0) {
                                continue;
                            }
                            trustedPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent("\u00a78[\u00a7cTRACK\u00a78] \u00a77" + arrow + " \u00a7c" + hunted.getName() + " \u00a77" + (int) distance + "m"));
                        }
                    }

                    if (this.elapsedTicks % 40 == 0) {
                        Particle.DustOptions faintRed = new Particle.DustOptions(ParticleUtils.STRENGTH_RED, 0.6f);
                        tracker.getWorld().spawnParticle(Particle.DUST, tracker.getLocation().add(0.0, 2.2, 0.0), 3, 0.15, 0.1, 0.15, 0.0, faintRed, true);
                    }
                } else {
                    tracker.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("\u00a7c\u00a7lTRACKING \u00a77... \u00a7c" + hunted.getName() + " \u00a77(different dimension)"));
                }
            }

            private String getDirectionArrow(Location from, Location to) {
                Vector direction = to.toVector().subtract(from.toVector()).normalize();
                double targetAngle = Math.atan2(direction.getZ(), direction.getX());
                double yaw = Math.toRadians(from.getYaw());
                double angle = targetAngle - yaw;
                while (angle > Math.PI) {
                    angle -= Math.PI * 2;
                }
                while (angle < -Math.PI) {
                    angle += Math.PI * 2;
                }
                if (angle > -0.7853981633974483 && angle <= 0.7853981633974483) {
                    return "\u2192";
                }
                if (angle > 0.7853981633974483 && angle <= 2.356194490192345) {
                    return "\u2193";
                }
                if (angle > -2.356194490192345 && angle <= -0.7853981633974483) {
                    return "\u2191";
                }
                return "\u2190";
            }
        }.runTaskTimer(this.plugin, 0L, 1L);

        this.trackingTasks.put(trackerUuid, task);
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage("\u00a7c\u00a7l\u2620 Shadow Stalker \u00a77Tracking \u00a7c" + target.getName() + " \u00a77for " + durationSeconds + " seconds.");
    }

    private void stopTracking(Player player) {
        if (player == null) {
            return;
        }
        UUID trackerUuid = player.getUniqueId();
        UUID targetUuid = this.trackedTargets.remove(trackerUuid);
        BukkitTask task = this.trackingTasks.remove(trackerUuid);
        if (task != null) {
            task.cancel();
        }
        if (player.isOnline()) {
            player.sendMessage("\u00a7c\u00a7oShadow Stalker tracking ended.");
        }
        if (targetUuid != null) {
            Player target = Bukkit.getPlayer(targetUuid);
            if (target != null && target.isOnline()) {
                target.sendMessage("\u00a7a\u00a7oYou are no longer being hunted.");
            }
        }
    }

    public boolean isTracking(Player player) {
        return this.trackedTargets.containsKey(player.getUniqueId());
    }

    public void stopTrackingManually(Player player) {
        this.stopTracking(player);
    }

    private LivingEntity getTargetEntity(Player player, int range) {
        RayTraceResult result = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            range,
            entity -> entity instanceof LivingEntity && entity != player
        );
        return result != null ? (LivingEntity) result.getHitEntity() : null;
    }
}
