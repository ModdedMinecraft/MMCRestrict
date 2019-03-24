package net.moddedminecraft.mmcrestrict;

import net.moddedminecraft.mmcrestrict.Data.ItemData;
import net.moddedminecraft.mmcrestrict.Data.ModData;
import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStack itemStack = transaction.getFinal().createStack();

            if (itemStack.getType().equals(ItemTypes.NONE)) {
                continue;
            }

            if (checkBanned(itemStack, "own", player)) {
                event.setCancelled(true);
            }
        }

    }

    @Listener
    public void onItemDrop(DropItemEvent.Pre event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        List<ItemStackSnapshot> itemIDs = event.getDroppedItems();

        for (ItemStackSnapshot itemSnapshot : itemIDs) {
            ItemStack itemStack = itemSnapshot.createStack();

            if (itemStack.getType().equals(ItemTypes.NONE)) {
                continue;
            }

            if (checkBanned(itemStack, "drop", player)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void InteractItemEvent(InteractItemEvent event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        ItemStack itemStack = event.getItemStack().createStack();

        if (checkBanned(itemStack, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        if (event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.AIR)) {
            return;
        }
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        BlockSnapshot targetBlock = event.getTransactions().get(0).getFinal();

        if (checkBanned(targetBlock, "place", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Break event, @Root Player player) {
        if (event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.AIR)) {
            return;
        }
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        BlockSnapshot targetBlock = event.getTransactions().get(0).getFinal();

        if (checkBanned(targetBlock, "break", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockModify(ChangeBlockEvent.Modify event, @Root Player player) {
        if (event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.AIR)) {
            return;
        }
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        BlockSnapshot targetBlock = event.getTransactions().get(0).getFinal();

        if (checkBanned(targetBlock, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.MainHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        BlockSnapshot targetBlock = event.getTargetBlock();

        if (checkBanned(targetBlock, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Secondary.MainHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get().createSnapshot().createStack();

        if (checkBanned(itemStack, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Secondary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(HandTypes.OFF_HAND).get().createSnapshot().createStack();

        if (checkBanned(itemStack, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary.MainHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get().createSnapshot().createStack();

        if (checkBanned(itemStack, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityInteract(InteractEntityEvent.Primary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        ItemStack itemStack = player.getItemInHand(HandTypes.OFF_HAND).get().createSnapshot().createStack();

        if (checkBanned(itemStack, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
        boolean sneaking = player.get(Keys.IS_SNEAKING).orElse(false);
        if (player.hasPermission(Permissions.ADD_BANNED_ITEM) && event.getTargetBlock().getState().getType().equals(BlockTypes.CHEST) && sneaking) {
            if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && player.getItemInHand(HandTypes.MAIN_HAND).get().getType().getType().equals(ItemTypes.FLINT)) {

                TileEntityCarrier chest = (TileEntityCarrier) event.getTargetBlock().getLocation().get().getTileEntity().get();
                for (Inventory slot : chest.getInventory().slots()) {
                    if (slot.peek().isPresent()) {
                        DataContainer container = slot.peek().get().toContainer();
                        DataQuery query = DataQuery.of('/', "UnsafeDamage");

                        int unsafeDamage = 0;
                        if (container.get(query).isPresent()) {
                            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
                        }
                        String itemId = slot.peek().get().getType().getId();
                        if (unsafeDamage != 0) {
                            itemId = itemId + ":" + unsafeDamage;
                        }

                        String itemID = itemId;
                        String itemName = slot.peek().get().getTranslation().get();
                        plugin.checkChestItem(itemID, itemName, player.getName());
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

        BlockSnapshot targetBlock = event.getTargetBlock();

        if (checkBanned(targetBlock, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        BlockSnapshot targetBlock = event.getTargetBlock();

        if (checkBanned(targetBlock, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.OffHand event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }

        BlockSnapshot targetBlock = event.getTargetBlock();

        if (checkBanned(targetBlock, "use", player)) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onCraftItemEvent(CraftItemEvent.Preview event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        if (!event.getPreview().getFinal().isEmpty()) {
            ItemStack itemStack = event.getPreview().getFinal().createStack();
            if (checkBanned(itemStack, "craft", player)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onChangeEquipment(ChangeInventoryEvent.Equipment event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStack itemStack = transaction.getFinal().createStack();
            if (itemStack.getType().equals(ItemTypes.NONE)) {
                continue;
            }

            if (checkBanned(itemStack, "own", player)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onChangeHeld(ChangeInventoryEvent.Held event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStack itemStack = transaction.getFinal().createStack();
            if (itemStack.getType().equals(ItemTypes.NONE)) {
                continue;
            }

            if (checkBanned(itemStack, "own", player)) {
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onClickInventoryEvent(ClickInventoryEvent event, @Root Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        if (event instanceof ClickInventoryEvent.Shift) {
            for (SlotTransaction transaction : event.getTransactions()) {
                ItemStack itemStack = transaction.getFinal().createStack();
                if (checkBanned(itemStack, "own", player)) {
                    event.setCancelled(true);
                    Sponge.getScheduler().createTaskBuilder().execute(new Runnable() {
                        public void run() {
                            checkInventory(player);
                        }
                    }).delay(1, TimeUnit.SECONDS).name("mmcrestrict-s-onclickinventoryevent").submit(this.plugin);
                }
            }
        } else {
            ItemStack itemStack = event.getCursorTransaction().getFinal().createStack();
            if (checkBanned(itemStack, "own", player)) {
                event.setCancelled(true);
                Sponge.getScheduler().createTaskBuilder().execute(new Runnable() {
                    public void run() {
                        checkInventory(player);
                    }
                }).delay(1, TimeUnit.SECONDS).name("mmcrestrict-s-onclickinventoryevent").submit(this.plugin);
            }
        }
    }

    private boolean checkBanned(ItemStack itemStack, String banType, Player player) {
        final List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        final List<ModData> mods = new ArrayList<ModData>(plugin.getModData());
        DataContainer container = itemStack.toContainer();
        DataQuery query = DataQuery.of('/', "UnsafeDamage");
        String itemID = itemStack.getType().getId();

        int unsafeDamage = 0;
        if (container.get(query).isPresent()) {
            unsafeDamage = Integer.parseInt(container.get(query).get().toString());
        }
        if (unsafeDamage != 0) {
            itemID = itemID + ":" + unsafeDamage;
        }

        for (ModData mod : mods) {
            if (itemID.startsWith(mod.getMod())
                    && ((banType.equalsIgnoreCase("craft") && mod.getCraftbanned())
                    || (banType.equalsIgnoreCase("break") && mod.getBreakingbanned())
                    || (banType.equalsIgnoreCase("drop") && mod.getDropbanned())
                    || (banType.equalsIgnoreCase("own") && mod.getOwnershipbanned())
                    || (banType.equalsIgnoreCase("place") && mod.getPlacingbanned())
                    || (banType.equalsIgnoreCase("use") && mod.getUsagebanned()))) {
                if (plugin.checkPerm(player, banType.toLowerCase(), mod.getMod())) {
                    String reason = "";
                    if (!mod.getBanreason().isEmpty()) {
                        reason = " &3- &7" + mod.getBanreason();
                    }
                    plugin.logToFile("action-log", player.getName() + " tried to " + banType.toLowerCase() + " " + mod.getModname());
                    if (!banType.equalsIgnoreCase("own")) {
                        plugin.notifyOnlineStaff(plugin.fromLegacy("&8[&6MMCRestrict&8] &c" + player.getName() + " tried to " + banType.toLowerCase() + " " + itemID));
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + mod.getModname() + " mod is banned" + reason));
                    checkInventory(player);
                    return true;
                }
            }
        }

        for (ItemData item : items) {
            if (item.getItemid().equalsIgnoreCase(itemID)
                    && ((banType.equalsIgnoreCase("craft") && item.getCraftbanned())
                    || (banType.equalsIgnoreCase("break") && item.getBreakingbanned())
                    || (banType.equalsIgnoreCase("drop") && item.getDropbanned())
                    || (banType.equalsIgnoreCase("own") && item.getOwnershipbanned())
                    || (banType.equalsIgnoreCase("place") && item.getPlacingbanned())
                    || (banType.equalsIgnoreCase("use") && item.getUsagebanned()))) {
                if (plugin.checkPerm(player, banType.toLowerCase(), itemID)) {
                    String reason = "";
                    if (!item.getBanreason().isEmpty()) {
                        reason = " &3- &7" + item.getBanreason();
                    }
                    plugin.logToFile("action-log", player.getName() + " tried to " + banType.toLowerCase() + " " + item.getItemname());
                    if (!banType.equalsIgnoreCase("own")) {
                        plugin.notifyOnlineStaff(plugin.fromLegacy("&8[&6MMCRestrict&8] &c" + player.getName() + " tried to " + banType.toLowerCase() + " " + item.getItemname()));
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() + " is banned" + reason));
                    checkInventory(player);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkBanned(BlockSnapshot blockSnapshot, String banType, Player player) {
        final List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        final List<ModData> mods = new ArrayList<ModData>(plugin.getModData());
        String itemID = blockSnapshot.getExtendedState().getType().getId();
        if (!blockSnapshot.getState().getType().getItem().isPresent()) {
            return false;
        }

        for (ModData mod : mods) {
            if (itemID.startsWith(mod.getMod())
                    && ((banType.equalsIgnoreCase("craft") && mod.getCraftbanned())
                    || (banType.equalsIgnoreCase("break") && mod.getBreakingbanned())
                    || (banType.equalsIgnoreCase("drop") && mod.getDropbanned())
                    || (banType.equalsIgnoreCase("own") && mod.getOwnershipbanned())
                    || (banType.equalsIgnoreCase("place") && mod.getPlacingbanned())
                    || (banType.equalsIgnoreCase("use") && mod.getUsagebanned()))) {
                if (plugin.checkPerm(player, banType.toLowerCase(), mod.getMod())) {
                    String reason = "";
                    if (!mod.getBanreason().isEmpty()) {
                        reason = " &3- &7" + mod.getBanreason();
                    }
                    plugin.logToFile("action-log", player.getName() + " tried to " + banType.toLowerCase() + " " + mod.getModname());
                    if (!banType.equalsIgnoreCase("own")) {
                        plugin.notifyOnlineStaff(plugin.fromLegacy("&8[&6MMCRestrict&8] &c" + player.getName() + " tried to " + banType.toLowerCase() + " " + itemID));
                    }
                    player.sendMessage(plugin.fromLegacy("&c" + mod.getModname() + " mod is banned" + reason));
                    checkInventory(player);
                    return true;
                }
            }
        }

        ItemStack itemStack;
        try {
            itemStack = ItemStack.builder().fromBlockState(blockSnapshot.getState()).build();
        } catch (Exception e) {
            return false;
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
            if (item.getItemid().equalsIgnoreCase(itemID)) {

                if (item.getItemid().equalsIgnoreCase(itemID)
                        && ((banType.equalsIgnoreCase("craft") && item.getCraftbanned())
                        || (banType.equalsIgnoreCase("break") && item.getBreakingbanned())
                        || (banType.equalsIgnoreCase("drop") && item.getDropbanned())
                        || (banType.equalsIgnoreCase("own") && item.getOwnershipbanned())
                        || (banType.equalsIgnoreCase("place") && item.getPlacingbanned())
                        || (banType.equalsIgnoreCase("use") && item.getUsagebanned()))) {
                    if (plugin.checkPerm(player, banType.toLowerCase(), itemID)) {
                        String reason = "";
                        if (!item.getBanreason().isEmpty()) {
                            reason = " &3- &7" + item.getBanreason();
                        }
                        plugin.logToFile("action-log", player.getName() + " tried to " + banType.toLowerCase() + " " + item.getItemname());
                        if (!banType.equalsIgnoreCase("own")) {
                            plugin.notifyOnlineStaff(plugin.fromLegacy("&8[&6MMCRestrict&8] &c" + player.getName() + " tried to " + banType.toLowerCase() + " " + item.getItemname()));
                        }
                        player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() + " is banned" + reason));
                        checkInventory(player);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void checkInventory(Player player) {
        if (player.hasPermission(Permissions.ITEM_BYPASS)) {
            return;
        }
        final List<ItemData> items = new ArrayList<ItemData>(plugin.getItemData());
        for (Inventory s : player.getInventory().slots()) {
            if (s.peek().isPresent()) {
                ItemStack itemStack = s.peek().get();
                String itemID = itemStack.getType().getId();

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
                    if (item.getItemid().equalsIgnoreCase(itemID) && item.getOwnershipbanned()) {
                        if (plugin.checkPerm(player, "own", itemID)) {
                            s.clear();
                            String reason = "";
                            if (!item.getBanreason().isEmpty()) {
                                reason = " &3- &7" + item.getBanreason();
                            }
                            plugin.logToFile("action-log", item.getItemname()+ " was removed from " +player.getName()+ "'s inventory");
                            player.sendMessage(plugin.fromLegacy("&c" + item.getItemname() + " is banned and has been removed from your inventory" + reason));
                        }
                    }
                }
            }
        }
    }
}
