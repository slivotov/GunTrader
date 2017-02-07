package ru.kupchagagroup.config.external;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "blacklist")
public class Blacklist {

    private List<String> blacklist;

    public Blacklist() {
    }

    public Blacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    @XmlElement(name = "entry")
    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Blacklist blacklist1 = (Blacklist) o;

        return blacklist != null ? blacklist.equals(blacklist1.blacklist) : blacklist1.blacklist == null;

    }

    @Override
    public String toString() {
        int blacklistSize = blacklist != null ? blacklist.size() : 0;
        return "Blacklist{" + "blacklist size=" + blacklistSize + '}';
    }
}
