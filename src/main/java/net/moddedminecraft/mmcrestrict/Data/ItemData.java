package net.moddedminecraft.mmcrestrict.Data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class ItemData extends ItemDataUtil {

    public ItemData(Boolean hidden, String itemid, String itemname, String banreason, Boolean usagebanned, Boolean breakingbanned, Boolean placingbanned, Boolean ownershipbanned, Boolean dropbanned, Boolean craftbanned, Boolean worldbanned) {
        super(hidden, itemid, itemname, banreason, usagebanned, breakingbanned, placingbanned, ownershipbanned, dropbanned, craftbanned, worldbanned);
    }

    public static class ItemDataSerializer implements TypeSerializer<ItemData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<ItemData>> token = new TypeToken<List<ItemData>>() {};

        @Override
        public ItemData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new ItemData(
                    node.getNode("hidden").getBoolean(),
                    node.getNode("itemid").getString(),
                    node.getNode("itemname").getString(),
                    node.getNode("banreason").getString(),
                    node.getNode("usagebanned").getBoolean(),
                    node.getNode("breakingbanned").getBoolean(),
                    node.getNode("placingbanned").getBoolean(),
                    node.getNode("ownershipbanned").getBoolean(),
                    node.getNode("dropbanned").getBoolean(),
                    node.getNode("craftbanned").getBoolean(),
                    node.getNode("worldbanned").getBoolean());
        }

        @Override
        public void serialize(TypeToken<?> token, ItemData itemdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("hidden").setValue(itemdata.hidden);
            node.getNode("itemid").setValue(itemdata.itemid);
            node.getNode("itemname").setValue(itemdata.itemname);
            node.getNode("banreason").setValue(itemdata.banreason);
            node.getNode("usagebanned").setValue(itemdata.usagebanned);
            node.getNode("breakingbanned").setValue(itemdata.breakingbanned);
            node.getNode("placingbanned").setValue(itemdata.placingbanned);
            node.getNode("ownershipbanned").setValue(itemdata.ownershipbanned);
            node.getNode("dropbanned").setValue(itemdata.dropbanned);
            node.getNode("craftbanned").setValue(itemdata.craftbanned);
            node.getNode("worldbanned").setValue(itemdata.worldbanned);
        }
    }


}
