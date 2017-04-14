package net.moddedminecraft.mmcrestrict;

import net.moddedminecraft.mmcrestrict.Data.ItemData;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.AffectSlotEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class EventListener {

    private Main plugin;
    public EventListener(Main instance) {
        plugin = instance;
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
        checkInventory(player);
    }

    @Listener
    public void onItemPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());

        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStack itemStack = transaction.getFinal().createStack();
            String itemID = itemStack.getItem().getId();

            if (itemStack.getItem().equals(ItemTypes.NONE)) {
                continue;
            }

            DataContainer container = itemStack.toContainer();
            DataQuery query = DataQuery.of('/', "UnsafeDamage");

            int unsafeDamage = 0;
            if (container.get(query).isPresent()) {
                unsafeDamage = Integer.parseInt(container.get(query).get().toString());
            }
            if (unsafeDamage != 0) {
                itemID = itemID + ":" + unsafeDamage;
            }

            for (ItemData item : items) {
                if (item.getItemid().equals(itemID) && item.getOwnershipbanned()) {
                    if (plugin.checkPerm(player, "own", itemID)) {
                        checkInventory(player);
                        event.setCancelled(true);
                    }
                }
            }
        }

    }

    @Listener
    public void onItemDrop(DropItemEvent.Pre event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        List<ItemStackSnapshot> itemIDs = event.getDroppedItems();

        for (ItemStackSnapshot itemSnapshot : itemIDs) {
            String itemID = itemSnapshot.getType().getId();

            DataContainer container = itemSnapshot.toContainer();
            DataQuery query = DataQuery.of('/', "UnsafeDamage");

            int unsafeDamage = 0;
            if (container.get(query).isPresent()) {
                unsafeDamage = Integer.parseInt(container.get(query).get().toString());
            }
            if (unsafeDamage != 0) {
                itemID = itemID + ":" + unsafeDamage;
            }

            for (ItemData item : items) {
                if (item.getItemid().equals(itemID) && item.getOwnershipbanned()) {
                    if (plugin.checkPerm(player, "own", itemID)) {
                        checkInventory(player);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @Listener
    public void InteractItemEvent(InteractItemEvent event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        String itemID = event.getItemStack().getType().getId();

        DataContainer container = event.getItemStack().toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    checkInventory(player);
                    event.setCancelled(true);
                }
            }
        }
    }


    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        String itemID = event.getTransactions().get(0).getFinal().getState().getId();

        DataContainer container = event.getTransactions().get(0).getFinal().toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    checkInventory(player);
                    event.getTransactions().get(0).setValid(false);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        String itemID = event.getTransactions().get(0).getFinal().getState().getId();

        DataContainer container = event.getTransactions().get(0).getFinal().toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.getTransactions().get(0).setValid(false);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onBlockModify(ChangeBlockEvent.Modify event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        String itemID = event.getTransactions().get(0).getFinal().getState().getId();

        DataContainer container = event.getTransactions().get(0).getFinal().toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.getTransactions().get(0).setValid(false);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.MainHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        BlockSnapshot targetBlock = event.getTargetBlock();

        String itemID = targetBlock.getState().getId();
        DataContainer container = targetBlock.toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
        boolean sneaking = player.get(Keys.IS_SNEAKING).orElse(false);
        if (player.hasPermission(Permissions.ADD_BANNED_ITEM) && event.getTargetBlock().getState().getType().equals(BlockTypes.CHEST) && sneaking) {
            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getItem().getType().equals(ItemTypes.FLINT)) {

                TileEntityCarrier chest = (TileEntityCarrier) event.getTargetBlock().getLocation().get().getTileEntity().get();
                for (Inventory slot : chest.getInventory().slots()) {
                    if (slot.peek().isPresent()) {
                        DataContainer container = slot.peek().get().toContainer();
                        DataQuery query = DataQuery.of('/', "UnsafeDamage");

                        int unsafeDamage = 0;
                        if (container.get(query).isPresent()) {
                            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
                        }
                        String itemId = slot.peek().get().getItem().getId();
                        if (unsafeDamage != 0) {
                            itemId = itemId + ":" + unsafeDamage;
                        }

                        String itemID = itemId;
                        String itemName = slot.peek().get().getTranslation().get();
                        plugin.checkChestItem(itemID, itemName);
                    }
                }
                try {
                    plugin.saveData();
                } catch (Exception e) {
                    player.sendMessage(Text.of("Data was not saved correctly."));
                    e.printStackTrace();
                }
                player.sendMessage(Text.of("Chest items were added to the list."));
                return;
            }
        }

        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        BlockSnapshot targetBlock = event.getTargetBlock();

        String itemID = targetBlock.getState().getId();
        DataContainer container = targetBlock.toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        BlockSnapshot targetBlock = event.getTargetBlock();

        String itemID = targetBlock.getState().getId();
        DataContainer container = targetBlock.toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        BlockSnapshot targetBlock = event.getTargetBlock();

        String itemID = targetBlock.getState().getId();
        DataContainer container = targetBlock.toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ItemData item : items) {
            if (item.getItemid().equals(itemID) && item.getUsagebanned()) {
                if (plugin.checkPerm(player, "use", itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" +item.getBanreason();
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() +" is banned" + reason));
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onAffectSlotEvent(AffectSlotEvent event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        checkInventory(player);
    }

    private void checkInventory(Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        final java.util.List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        for (Inventory s : player.getInventory().slots()) {
            if (s.peek().isPresent()) {
                ItemStack itemStack = s.peek().get();
                String itemID = itemStack.getItem().getId();

                DataContainer container = s.peek().get().toContainer();
                DataQuery query = DataQuery.of('/', "UnsafeDamage");

                int unsafeDamage = 0;
                if (container.get(query).isPresent()) {
                    unsafeDamage = Integer.parseInt(container.get(query).get().toString());
                }
                if (unsafeDamage != 0) {
                    itemID = itemID + ":" + unsafeDamage;
                }
                for (ItemData item : items) {
                    if (item.getItemid().equals(itemID) && item.getOwnershipbanned()) {
                        if (plugin.checkPerm(player, "own", itemID)) {
                            s.clear();
                            String reason = "";
                            if (!item.getBanreason().isEmpty()) {
                                reason = " &3- &7" + item.getBanreason();
                            }
                            player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() + " is banned and has been removed from your inventory" + reason));
                        }
                    }
                }
            }
        }
    }
}
