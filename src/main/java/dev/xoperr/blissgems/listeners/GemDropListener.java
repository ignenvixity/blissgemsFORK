package dev.xoperr.blissgems.listeners;

import dev.xoperr.blissgems.BlissGems;
import dev.xoperr.blissgems.utils.CustomItemManager;
import dev.xoperr.blissgems.utils.GemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

/**
 * Prevents dropping of locked gems and force-returns them on any desync edge case.
 */
public class GemDropListener implements Listener {
    private final BlissGems plugin;

    public GemDropListener(BlissGems plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!plugin.getConfig().getBoolean("gems.prevent-drop", true)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        boolean isLocked = CustomItemManager.isUndroppable(droppedItem);

        if (!isLocked) {
            return;
        }

        event.setCancelled(true);

        String message = plugin.getConfigManager().getFormattedMessage("cannot-drop-gem");
        if (message != null && !message.isEmpty()) {
            player.sendMessage(message);
        } else {
            player.sendMessage("\u00A7c\u00A7lYou cannot drop your gem!");
        }

        ItemStack droppedClone = droppedItem.clone();
        if (event.getItemDrop() != null && event.getItemDrop().isValid()) {
            event.getItemDrop().remove();
        }

        // Safety net in case of inventory desync after cancel.
        Bukkit.getScheduler().runTask(plugin, () -> ensureGemReturned(player, droppedClone));
    }

    private void ensureGemReturned(Player player, ItemStack droppedGem) {
        if (player == null || !player.isOnline() || droppedGem == null || droppedGem.getType() == Material.AIR) {
            return;
        }

        int currentAmount = countSimilarAmount(player.getInventory(), droppedGem);
        int missing = droppedGem.getAmount() - currentAmount;
        if (missing <= 0) {
            return;
        }

        ItemStack toReturn = droppedGem.clone();
        toReturn.setAmount(missing);

        Map<Integer, ItemStack> overflow = player.getInventory().addItem(toReturn);
        if (overflow.isEmpty()) {
            return;
        }

        for (ItemStack remainder : overflow.values()) {
            forcePlaceGem(player, remainder);
        }
    }

    private int countSimilarAmount(PlayerInventory inv, ItemStack sample) {
        int total = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.isSimilar(sample)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    private void forcePlaceGem(Player player, ItemStack gemStack) {
        PlayerInventory inv = player.getInventory();

        int empty = inv.firstEmpty();
        if (empty != -1) {
            inv.setItem(empty, gemStack);
            return;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack slotItem = inv.getItem(i);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inv.setItem(i, gemStack);
                return;
            }
            String id = CustomItemManager.getIdByItem(slotItem);
            if (id == null || !GemType.isGem(id)) {
                ItemStack replaced = slotItem.clone();
                inv.setItem(i, gemStack);
                player.getWorld().dropItemNaturally(player.getLocation(), replaced);
                return;
            }
        }

        for (int i = 0; i < 9; i++) {
            ItemStack slotItem = inv.getItem(i);
            if (slotItem == null || slotItem.getType() == Material.AIR) {
                inv.setItem(i, gemStack);
                return;
            }
            String id = CustomItemManager.getIdByItem(slotItem);
            if (id == null || !GemType.isGem(id)) {
                ItemStack replaced = slotItem.clone();
                inv.setItem(i, gemStack);
                player.getWorld().dropItemNaturally(player.getLocation(), replaced);
                return;
            }
        }
    }
}