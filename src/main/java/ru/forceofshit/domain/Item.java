package ru.forceofshit.domain;

public class Item {
    private String name;
    private String quality;

    public Item(String name, String quality) {
        this.name = name;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public String getQuality() {
        return quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Item item = (Item) o;

        if (!name.equals(item.name)) {
            return false;
        }
        return quality != null ? quality.equals(item.quality) : item.quality == null;

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        return result;
    }
}
