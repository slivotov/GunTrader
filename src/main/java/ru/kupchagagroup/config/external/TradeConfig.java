package ru.kupchagagroup.config.external;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class TradeConfig {
    private String login;
    private String password;
    private int minDiscount;
    private double minPrice;
    private double maxPrice;
    private Blacklist blacklist;
    private Whitelist whitelist;

    public String getLogin() {
        return login;
    }

    @XmlElement(name = "login")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    @XmlElement(name = "password")
    public void setPassword(String password) {
        this.password = password;
    }

    public int getMinDiscount() {
        return minDiscount;
    }

    @XmlElement(name = "min_discount")
    public void setMinDiscount(int minDiscount) {
        this.minDiscount = minDiscount;
    }

    public double getMinPrice() {
        return minPrice;
    }

    @XmlElement(name = "min_price")
    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    @XmlElement(name = "max_price")
    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Blacklist getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(Blacklist blacklist) {
        this.blacklist = blacklist;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TradeConfig that = (TradeConfig) o;

        if (minDiscount != that.minDiscount) {
            return false;
        }
        if (Double.compare(that.minPrice, minPrice) != 0) {
            return false;
        }
        if (Double.compare(that.maxPrice, maxPrice) != 0) {
            return false;
        }
        if (login != null ? !login.equals(that.login) : that.login != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) : that.password != null) {
            return false;
        }
        if (blacklist != null ? !blacklist.equals(that.blacklist) : that.blacklist != null) {
            return false;
        }
        return whitelist != null ? whitelist.equals(that.whitelist) : that.whitelist == null;

    }

    @Override
    public String toString() {
        return "TradeConfig{" + "login='" + login + '\'' + ", password='" + password + '\'' + ", minDiscount="
                + minDiscount + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + ", blacklist=" + blacklist
                + ", whitelist=" + whitelist + '}';
    }
}