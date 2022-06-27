package models;

public class Product {
    private long barcode;
    private String title;
    private double price;

    public Product(long barcode) {
        this.barcode = barcode;
    }

    public Product(long barcode, String title, double price) {
        this(barcode);
        this.title = title;
        this.price = price;
    }

    /**
     * parses product information from a textLine with format: barcode, title, price
     *
     * @param textLine
     * @return a new Product instance with the provided information
     * or null if the textLine is corrupt or incomplete
     */
    public static Product fromLine(String textLine) {
        Product newProduct = null;
        if (textLine != null) {
            String[] productText = textLine.split(",\s");

            newProduct = new Product(Long.parseLong(productText[0]), productText[1], Double.parseDouble(productText[2]));
        }
        return newProduct;
    }

    public long getBarcode() {
        return barcode;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Product)) {
            return false;
        }
        return this.getBarcode() == ((Product) other).getBarcode();
    }

    @Override
    public String toString() {
        return barcode + "/" + title + "/" + price;
    }
}
