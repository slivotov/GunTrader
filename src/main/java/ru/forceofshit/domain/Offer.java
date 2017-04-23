package ru.forceofshit.domain;

import java.util.Objects;

public class Offer {
    private Item item;
    private int discount;
    private double price;
    private String addToCartId;

    public Offer(String name, String quality, int discount, double price, String addToCartId) {
        item = new Item(name, quality);
        this.discount = discount;
        this.price = price;
        this.addToCartId = addToCartId;
    }

    public String getName() {
        return item.getName();
    }

    public int getDiscount() {
        return discount;
    }

    public double getPrice() {
        return price;
    }

    public String getAddToCartId() {
        return addToCartId;
    }

    public String getQuality() {
        return item.getQuality();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Offer offer = (Offer) o;

        if (discount != offer.discount) {
            return false;
        }
        if (Double.compare(offer.price, price) != 0) {
            return false;
        }
        if (!Objects.equals(addToCartId, offer.addToCartId)) {
            return false;
        }
        if (item.getName() != null ? !item.getName().equals(offer.getName()) : offer.getName() != null) {
            return false;
        }
        return item.getQuality() != null ? item.getQuality().equals(offer.getQuality()) : offer.getQuality() == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = item.getName() != null ? item.getName().hashCode() : 0;
        result = 31 * result + (item.getQuality() != null ? item.getQuality().hashCode() : 0);
        result = 31 * result + discount;
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = addToCartId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Offer{" + "name='" + item.getName() + '\'' + ", quality='" + item.getQuality() + '\'' + ", discount=" +
                discount
                + ", price=" + price + ", addToCartId=" + addToCartId + '}';
    }
}
