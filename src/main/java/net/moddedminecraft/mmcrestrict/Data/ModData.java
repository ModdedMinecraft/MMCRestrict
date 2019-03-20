package net.moddedminecraft.mmcrestrict.Data;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;

public class ModData extends ModDataUtil {

    public ModData(Boolean hidden, String mod, String modname, String banreason, Boolean usagebanned, Boolean breakingbanned, Boolean placingbanned, Boolean ownershipbanned, Boolean dropbanned, Boolean craftbanned) {
        super(hidden, mod, modname, banreason, usagebanned, breakingbanned, placingbanned, ownershipbanned, dropbanned, craftbanned);
    }

    public static class ModDataSerializer implements TypeSerializer<ModData> {
        @SuppressWarnings("serial")
        final public static TypeToken<List<ModData>> token = new TypeToken<List<ModData>>() {};

        @Override
        public ModData deserialize(TypeToken<?> token, ConfigurationNode node) throws ObjectMappingException {
            return new ModData(
                    node.getNode("hidden").getBoolean(),
                    node.getNode("mod").getString(),
                    node.getNode("modname").getString(),
                    node.getNode("banreason").getString(),
                    node.getNode("usagebanned").getBoolean(),
                    node.getNode("breakingbanned").getBoolean(),
                    node.getNode("placingbanned").getBoolean(),
                    node.getNode("ownershipbanned").getBoolean(),
                    node.getNode("dropbanned").getBoolean(),
                    node.getNode("craftbanned").getBoolean());
        }

        @Override
        public void serialize(TypeToken<?> token, ModData itemdata, ConfigurationNode node) throws ObjectMappingException {
            node.getNode("hidden").setValue(itemdata.hidden);
            node.getNode("mod").setValue(itemdata.mod);
            node.getNode("modname").setValue(itemdata.modname);
            node.getNode("banreason").setValue(itemdata.banreason);
            node.getNode("usagebanned").setValue(itemdata.usagebanned);
            node.getNode("breakingbanned").setValue(itemdata.breakingbanned);
            node.getNode("placingbanned").setValue(itemdata.placingbanned);
            node.getNode("ownershipbanned").setValue(itemdata.ownershipbanned);
            node.getNode("dropbanned").setValue(itemdata.dropbanned);
            node.getNode("craftbanned").setValue(itemdata.craftbanned);
        }
    }


}
