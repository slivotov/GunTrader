package ru.kupchagagroup.config.external;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "whitelist")
public class Whitelist {
    private Set<WhitelistItem> whitelistItems = new HashSet<>();

    public Whitelist() {
    }

    public Whitelist(Set<WhitelistItem> whitelistItems) {
        this.whitelistItems = whitelistItems;
    }

    public Set<WhitelistItem> getWhitelistItems() {
        return whitelistItems;
    }

    @XmlElement(name = "item")
    public void setWhitelistItems(Set<WhitelistItem> whitelistItems) {
        this.whitelistItems = whitelistItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Whitelist whitelist = (Whitelist) o;

        return whitelistItems != null ? whitelistItems.equals(whitelist.whitelistItems) :
                whitelist.whitelistItems == null;

    }

    @Override
    public String toString() {
        int size = whitelistItems != null ? whitelistItems.size() : 0;
        return "Whitelist{" + "whitelistItems size=" + size + '}';
    }

    public WhitelistItem getItemByNameAndQuality(String name, String quality) {
        if (whitelistItems == null) {
            return null;
        }
        for (WhitelistItem whitelistItem : whitelistItems) {
            if (whitelistItem.getName().equals(name) && (whitelistItem.getQuality() == null || whitelistItem
                    .getQuality().equals(quality))) {
                return whitelistItem;
            }
        }
        return null;
    }
}
