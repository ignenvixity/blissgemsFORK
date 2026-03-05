package dev.xoperr.blissgems.listeners;

import dev.xoperr.blissgems.BlissGems;
import dev.xoperr.blissgems.utils.CustomItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

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
        ItemStack itemStack = event.getItemDrop().getItemStack();
        boolean locked = CustomItemManager.isUndroppable(itemStack);

        if (locked) {
            event.setCancelled(true);
            String message = plugin.getConfigManager().getFormattedMessage("cannot-drop-gem");
            if (message != null && !message.isEmpty()) {
                player.sendMessage(message);
            } else {
                player.sendMessage("\u00a7c\u00a7lYou cannot drop your gem!");
            }
        }
    }
}
