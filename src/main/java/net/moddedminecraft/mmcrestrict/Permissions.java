package net.moddedminecraft.mmcrestrict;

public class Permissions {

    public static final String LIST_BANNED_ITEMS = "mmcrestrict.commands.list";

    //For specific items, Use as: mmcrestrict.bypass.[use/own/craft/drop/place/break].[ItemID/*] , ItemID can be found using /restrict whatsthis or /whatsthis with an item in hand.
    //Remeber to replace any of : with .
    public static final String ITEM_BYPASS = "mmcrestrict.bypass";

    public static final String NOTIFY = "mmcrestrict.notify";

    public static final String LIST_HIDDEN = "mmcrestrict.hidden.list";
    public static final String LIST_HIDDEN_EDIT = "mmcrestrict.hidden.edit";
    public static final String LIST_EXTRA = "mmcrestrict.extra.list";
    public static final String EDIT_BANNED_ITEM = "mmcrestrict.commands.edit";
    public static final String REMOVE_BANNED_ITEM = "mmcrestrict.commands.remove";
    public static final String ADD_BANNED_ITEM = "mmcrestrict.commands.add";
    public static final String SEARCH_BANNED_ITEM = "mmcrestrict.commands.search";
    public static final String WHATS_THIS = "mmcrestrict.commands.whatsthis";
    public static final String CHECK_CHUNKS = "mmcrestrict.commands.checkchunks";
    public static final String SEND_TO_CHEST = "mmcrestrict.commands.sendtochest";

}
