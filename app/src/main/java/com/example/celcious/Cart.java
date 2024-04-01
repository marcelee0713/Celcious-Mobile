package com.example.celcious;

public class Cart {
    private String image;
    private String name;
    private String price;
    private int quantity;
    private int stock;

    public Cart(String image, String name, String price, int quantity, int stock) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
    }
    public Cart(){

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                ", stock=" + stock +
                '}';
    }
}
