package net.moddedminecraft.mmcrestrict.Data;

public class ModDataUtil {

    protected String mod, modname, banreason;
    protected Boolean hidden, ownershipbanned, usagebanned, breakingbanned, placingbanned, dropbanned, craftbanned;

    public ModDataUtil(Boolean hidden, String mod, String modname, String banreason, Boolean usagebanned, Boolean breakingbanned, Boolean placingbanned, Boolean ownershipbanned, Boolean dropbanned, Boolean craftbanned) {
        this.hidden = hidden;
        this.mod = mod;
        this.modname = modname;
        this.banreason = banreason;
        this.ownershipbanned = ownershipbanned;
        this.usagebanned = usagebanned;
        this.breakingbanned = breakingbanned;
        this.placingbanned = placingbanned;
        this.dropbanned = dropbanned;
        this.craftbanned = craftbanned;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getModname() {
        return modname;
    }

    public String getMod() {
        return mod;
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

    public void setItemname(String modname) {
        this.modname = modname;
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

    public Boolean getCraftbanned() {
        return craftbanned;
    }

    public void setCraftbanned(Boolean craftbanned) {
        this.craftbanned = craftbanned;
    }
}
