package ru.kupchagagroup.config.external;

import javax.xml.bind.annotation.XmlElement;

//@XmlRootElement(name = "item")
public class WhitelistItem {
    private String name;
    private String quality;
    private double minPrice;

    public WhitelistItem() {
    }

    public WhitelistItem(String name, String quality, double minPrice) {
        this.name = name;
        this.quality = quality;
        this.minPrice = minPrice;
    }

    public String getName() {
        return name;
    }

    @XmlElement(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getQuality() {
        return quality;
    }

    @XmlElement(name = "quality")
    public void setQuality(String quality) {
        this.quality = quality;
    }

    public double getMinPrice() {
        return minPrice;
    }

    @XmlElement(name = "min_price")
    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WhitelistItem that = (WhitelistItem) o;

        if (Double.compare(that.minPrice, minPrice) != 0) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        return quality != null ? quality.equals(that.quality) : that.quality == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        temp = Double.doubleToLongBits(minPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "WhitelistItem{" + "name='" + name + '\'' + ", quality='" + quality + '\'' + ", minPrice=" + minPrice
                + '}';
    }
}
