package models;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class Purchase {
    private final Product product;
    private int count;

    public Purchase(Product product, int count) {
        this.product = product;
        this.count = count;
    }

    /**
     * parses purchase summary information from a textLine with format: barcode, amount
     * @param textLine
     * @param products  a list of products ordered and searchable by barcode
     *                  (i.e. the comparator of the ordered list shall consider only the barcode when comparing products)
     * @return  a new Purchase instance with the provided information
     *          or null if the textLine is corrupt or incomplete
     */
    public static Purchase fromLine(String textLine, List<Product> products) {
        Purchase newPurchase;

        String[] purchaseArray = textLine.split(", ");
        final int TOTAL_ITEMS_IN_ARRAY = 2;

        if (purchaseArray.length != TOTAL_ITEMS_IN_ARRAY) {
            return null;
        }

        String barcode = purchaseArray[0];
        int soldItems = Integer.parseInt(purchaseArray[1]);

        Product soldProduct = null;

        for (Product product : products) {
            if (Long.toString(product.getBarcode()).equals(barcode)) {
                soldProduct = product;
            }
        }

        if (soldProduct == null) {
            return null;
        }

        newPurchase = new Purchase(soldProduct, soldItems);

        return newPurchase;
    }

    /**
     * add a delta amount to the count of the purchase summary instance
     *
     * @param delta
     */
    public void addCount(int delta) {
        this.count += delta;
    }

    public long getBarcode() {
        return this.product.getBarcode();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public String calculateRevenue(Product product) {
        DecimalFormat df = new DecimalFormat("#.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return df.format(product.getPrice() * this.getCount());
    }

    @Override
    public String toString() {
        return String.format("%d/%s/%d/%s", this.getBarcode(), this.getProduct().getTitle(), this.getCount(), this.calculateRevenue(this.getProduct()));
    }
}
