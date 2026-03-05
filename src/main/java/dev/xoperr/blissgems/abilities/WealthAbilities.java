package dev.xoperr.blissgems.abilities;

import dev.xoperr.blissgems.BlissGems;
import dev.xoperr.blissgems.utils.ParticleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

public class WealthAbilities {
    private final BlissGems plugin;
    private final Map<UUID, Boolean> autoSmeltEnabled = new HashMap<>();
    private final Map<UUID, Map<Integer, Map<Enchantment, Integer>>> storedAmplificationEnchants = new HashMap<>();
    private static final Set<UUID> unfortunatePlayers = new HashSet<>();
    private static final Map<UUID, ItemStack> lockedItems = new HashMap<>();
    private static final Set<UUID> richRushPlayers = new HashSet<>();

    public WealthAbilities(BlissGems plugin) {
        this.plugin = plugin;
    }

    public void onRightClick(Player player, int tier) {
        if (tier < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        if (player.isSneaking()) {
            this.itemLock(player);
        } else {
            this.unfortunate(player);
        }
    }

    public void durabilityChip(Player player) {
        // Legacy alias; 3.8 parity route uses Unfortunate.
        this.unfortunate(player);
    }

    public void pockets(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        // Kept exactly as current behavior: open real Ender Chest.
        player.openInventory(player.getEnderChest());
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }

    public void unfortunate(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "wealth-unfortunate";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            15.0,
            entity -> entity instanceof Player && entity != player
        );
        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof Player)) {
            player.sendMessage("\u00a7cNo player target found!");
            return;
        }

        Player target = (Player) rayTraceResult.getHitEntity();
        int duration = this.plugin.getConfigManager().getAbilityDuration("wealth-unfortunate");
        UUID targetUuid = target.getUniqueId();
        unfortunatePlayers.add(targetUuid);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            unfortunatePlayers.remove(targetUuid);
            if (target.isOnline()) {
                target.sendMessage("\u00a7a\u00a7oUnfortunate has worn off.");
            }
        }, duration * 20L);

        Particle.DustOptions greenDust = new Particle.DustOptions(ParticleUtils.WEALTH_GREEN, 1.5f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.5, 0.5, 0.0, greenDust, true);
        target.getWorld().spawnParticle(Particle.SMOKE, target.getLocation().add(0.0, 1.0, 0.0), 20, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 1.0f, 0.8f);
        target.sendMessage("\u00a7c\u00a7oYou've been afflicted with Unfortunate! Actions disabled for " + duration + "s!");
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Unfortunate"));
    }

    public void itemLock(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "wealth-item-lock";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
            player.getEyeLocation(),
            player.getEyeLocation().getDirection(),
            15.0,
            entity -> entity instanceof Player && entity != player
        );
        if (rayTraceResult == null || !(rayTraceResult.getHitEntity() instanceof Player)) {
            player.sendMessage("\u00a7cNo player target found!");
            return;
        }

        Player target = (Player) rayTraceResult.getHitEntity();
        ItemStack heldItem = target.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType().isAir()) {
            player.sendMessage("\u00a7cTarget isn't holding an item!");
            return;
        }

        UUID targetUuid = target.getUniqueId();
        lockedItems.put(targetUuid, heldItem.clone());
        int duration = this.plugin.getConfig().getInt("abilities.durations.wealth-item-lock", 10);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            lockedItems.remove(targetUuid);
            if (target.isOnline()) {
                target.sendMessage("\u00a7a\u00a7oItem Lock has worn off.");
            }
        }, duration * 20L);

        Particle.DustOptions greenDust = new Particle.DustOptions(ParticleUtils.WEALTH_GREEN, 1.5f);
        target.getWorld().spawnParticle(Particle.DUST, target.getLocation().add(0.0, 1.0, 0.0), 30, 0.5, 0.5, 0.5, 0.0, greenDust, true);
        target.playSound(target.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 0.5f);

        String itemName = heldItem.getType().name().toLowerCase().replace('_', ' ');
        if (heldItem.hasItemMeta() && heldItem.getItemMeta().hasDisplayName()) {
            itemName = heldItem.getItemMeta().getDisplayName();
        }
        target.sendMessage("\u00a7c\u00a7oYour " + itemName + " has been locked for " + duration + "s!");
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Item Lock"));
    }

    public void richRush(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "wealth-rich-rush";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        int duration = this.plugin.getConfigManager().getAbilityDuration("wealth-rich-rush");
        UUID uuid = player.getUniqueId();
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration * 20, 2, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, duration * 20, 3, false, true));
        richRushPlayers.add(uuid);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            richRushPlayers.remove(uuid);
            Player online = Bukkit.getPlayer(uuid);
            if (online != null && online.isOnline()) {
                online.sendMessage("\u00a7e\u00a7oRich Rush has worn off.");
            }
        }, duration * 20L);

        Particle.DustOptions greenDust = new Particle.DustOptions(ParticleUtils.WEALTH_GREEN, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.spawnParticle(Particle.DUST, player.getLocation().add(0.0, 1.0, 0.0), 50, 0.5, 0.5, 0.5, 0.0, greenDust, true);
        player.spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0.0, 1.0, 0.0), 40, 0.5, 0.5, 0.5);
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Rich Rush"));
        player.sendMessage("\u00a76\u00a7lRich Rush! \u00a7eMob and ore drops doubled for " + duration + "s!");
    }

    public void amplification(Player player) {
        if (this.plugin.getGemManager().getGemTier(player) < 2) {
            player.sendMessage("\u00a7c\u00a7oThis ability requires Tier 2!");
            return;
        }
        String abilityKey = "wealth-amplification";
        if (!this.plugin.getAbilityManager().canUseAbility(player, abilityKey)) {
            return;
        }

        int duration = this.plugin.getConfigManager().getAbilityDuration("wealth-amplification");
        UUID uuid = player.getUniqueId();
        Map<Integer, Map<Enchantment, Integer>> snapshot = new HashMap<>();
        ItemStack[] armor = player.getInventory().getArmorContents();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (piece == null || piece.getType().isAir() || piece.getEnchantments().isEmpty()) {
                continue;
            }
            Map<Enchantment, Integer> original = new HashMap<>(piece.getEnchantments());
            snapshot.put(i, original);
            for (Map.Entry<Enchantment, Integer> entry : original.entrySet()) {
                piece.addUnsafeEnchantment(entry.getKey(), entry.getValue() + 1);
            }
        }
        player.getInventory().setArmorContents(armor);

        if (mainHand != null && !mainHand.getType().isAir() && !mainHand.getEnchantments().isEmpty()) {
            Map<Enchantment, Integer> originalMain = new HashMap<>(mainHand.getEnchantments());
            snapshot.put(100, originalMain);
            for (Map.Entry<Enchantment, Integer> entry : originalMain.entrySet()) {
                mainHand.addUnsafeEnchantment(entry.getKey(), entry.getValue() + 1);
            }
        }
        if (offHand != null && !offHand.getType().isAir() && !offHand.getEnchantments().isEmpty()) {
            Map<Enchantment, Integer> originalOff = new HashMap<>(offHand.getEnchantments());
            snapshot.put(101, originalOff);
            for (Map.Entry<Enchantment, Integer> entry : originalOff.entrySet()) {
                offHand.addUnsafeEnchantment(entry.getKey(), entry.getValue() + 1);
            }
        }

        this.storedAmplificationEnchants.put(uuid, snapshot);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Map<Integer, Map<Enchantment, Integer>> original = this.storedAmplificationEnchants.remove(uuid);
            if (original == null) {
                return;
            }
            Player online = Bukkit.getPlayer(uuid);
            if (online == null || !online.isOnline()) {
                return;
            }

            ItemStack[] onlineArmor = online.getInventory().getArmorContents();
            for (int i = 0; i < onlineArmor.length; i++) {
                Map<Enchantment, Integer> pieceOriginal = original.get(i);
                ItemStack piece = onlineArmor[i];
                if (pieceOriginal == null || piece == null || piece.getType().isAir()) {
                    continue;
                }
                for (Enchantment enchantment : new ArrayList<>(piece.getEnchantments().keySet())) {
                    piece.removeEnchantment(enchantment);
                }
                for (Map.Entry<Enchantment, Integer> entry : pieceOriginal.entrySet()) {
                    piece.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                }
            }
            online.getInventory().setArmorContents(onlineArmor);

            Map<Enchantment, Integer> mainOriginal = original.get(100);
            ItemStack onlineMain = online.getInventory().getItemInMainHand();
            if (mainOriginal != null && onlineMain != null && !onlineMain.getType().isAir()) {
                for (Enchantment enchantment : new ArrayList<>(onlineMain.getEnchantments().keySet())) {
                    onlineMain.removeEnchantment(enchantment);
                }
                for (Map.Entry<Enchantment, Integer> entry : mainOriginal.entrySet()) {
                    onlineMain.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                }
            }

            Map<Enchantment, Integer> offOriginal = original.get(101);
            ItemStack onlineOff = online.getInventory().getItemInOffHand();
            if (offOriginal != null && onlineOff != null && !onlineOff.getType().isAir()) {
                for (Enchantment enchantment : new ArrayList<>(onlineOff.getEnchantments().keySet())) {
                    onlineOff.removeEnchantment(enchantment);
                }
                for (Map.Entry<Enchantment, Integer> entry : offOriginal.entrySet()) {
                    onlineOff.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                }
            }
            online.sendMessage("\u00a7e\u00a7oAmplification has worn off. Enchantments restored.");
        }, duration * 20L);

        Particle.DustOptions greenDust = new Particle.DustOptions(ParticleUtils.WEALTH_GREEN, 1.5f);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.5f);
        player.spawnParticle(Particle.DUST, player.getLocation().add(0.0, 1.0, 0.0), 100, 0.5, 1.0, 0.5, 0.0, greenDust, true);
        player.spawnParticle(Particle.ENCHANT, player.getLocation().add(0.0, 1.0, 0.0), 80, 0.5, 1.0, 0.5);
        this.plugin.getAbilityManager().useAbility(player, abilityKey);
        player.sendMessage(this.plugin.getConfigManager().getFormattedMessage("ability-activated", "ability", "Amplification"));
    }

    public static boolean isRichRushActive(UUID uuid) {
        return richRushPlayers.contains(uuid);
    }

    public static boolean isUnfortunateActive(UUID uuid) {
        return unfortunatePlayers.contains(uuid) && Math.random() < 0.5;
    }

    public static boolean isItemLocked(UUID uuid) {
        return lockedItems.containsKey(uuid);
    }

    public static ItemStack getLockedItem(UUID uuid) {
        return lockedItems.get(uuid);
    }

    public Inventory getPocketsInventory(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player != null ? player.getEnderChest() : null;
    }

    public boolean isAutoSmeltEnabled(Player player) {
        return this.autoSmeltEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public void setAutoSmelt(Player player, boolean enabled) {
        this.autoSmeltEnabled.put(player.getUniqueId(), enabled);
    }
}
