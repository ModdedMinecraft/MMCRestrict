package net.moddedminecraft.mmcrestrict.Data;

public class ItemDataUtil {

    protected String itemid, itemname, banreason;
    protected Boolean ownershipbanned, usagebanned, breakingbanned, placingbanned, dropbanned, worldbanned;

    public ItemDataUtil(String itemid, String itemname, String banreason, Boolean usagebanned, Boolean breakingbanned, Boolean placingbanned, Boolean ownershipbanned, Boolean dropbanned, Boolean worldbanned) {
        this.itemid = itemid;
        this.itemname = itemname;
        this.banreason = banreason;
        this.ownershipbanned = ownershipbanned;
        this.usagebanned = usagebanned;
        this.breakingbanned = breakingbanned;
        this.placingbanned = placingbanned;
        this.dropbanned = dropbanned;
        this.worldbanned = worldbanned;
    }

    public String getItemname() {
        return itemname;
    }

    public String getItemid() {
        return itemid;
    }

    public String getBanreason() {
        return banreason;
    }

    public Boolean getOwnershipbanned() {
        return ownershipbanned;
    }

    public Boolean getUsagebanned() {
        return usagebanned;
    }

    public Boolean getBreakingbanned() {
        return breakingbanned;
    }

    public Boolean getPlacingbanned() {
        return placingbanned;
    }

    public Boolean getDropbanned() {
        return dropbanned;
    }

    public Boolean getWorldbanned() {
        return worldbanned;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public void setBanreason(String banreason) {
        this.banreason = banreason;
    }

    public void setOwnershipbanned(Boolean ownershipbanned) {
        this.ownershipbanned = ownershipbanned;
    }

    public void setUsagebanned(Boolean usagebanned) {
        this.usagebanned = usagebanned;
    }

    public void setBreakingbanned(Boolean breakingbanned) {
        this.breakingbanned = breakingbanned;
    }

    public void setPlacingbanned(Boolean placingbanned) {
        this.placingbanned = placingbanned;
    }

    public void setDropbanned(Boolean dropbanned) {
        this.dropbanned = dropbanned;
    }

    public void setWorldbanned(Boolean worldbanned) {
        this.worldbanned = worldbanned;
    }

}
