package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;

public class PurchaseTracker {
    private final String PURCHASE_FILE_PATTERN = ".*\\.txt";

    private OrderedList<Product> products;        // the reference list of all Products available from the SuperMarket chain
    private OrderedList<Purchase> purchases;      // the aggregated volumes of all purchases of all products across all branches

    public PurchaseTracker() {
        products = new OrderedArrayList<>(Comparator.comparing(Product::getBarcode));
        purchases = new OrderedArrayList<>(Comparator.comparing(Purchase::getBarcode));
    }

    /**
     * imports all products from a resource file that is common to all branches of the Supermarket chain
     *
     * @param resourceName
     */
    public void importProductsFromVault(String resourceName) {
        this.products.clear();

        // load all products from the text file
        importItemsFromFile(this.products,
                PurchaseTracker.class.getResource(resourceName).getPath(),
                Product::fromLine);

        this.products.sort();

        System.out.printf("Imported %d products from %s.\n", products.size(), resourceName);
    }

    /**
     * imports and merges all raw purchase data of all branches from the hierarchical file structure of the vault
     *
     * @param resourceName
     */
    public void importPurchasesFromVault(String resourceName) {
        this.purchases.clear();

        mergePurchasesFromFileRecursively(
                PurchaseTracker.class.getResource(resourceName).getPath());

        System.out.printf("Accumulated purchases of %d products from files in %s.\n", this.purchases.size(), resourceName);
    }

    /**
     * traverses the purchases vault recursively and processes every data file that it finds
     *
     * @param filePath
     */
    private void mergePurchasesFromFileRecursively(String filePath) {
        File file = new File(filePath);

        if (file.isDirectory()) {
            File[] filesInDirectory = Objects.requireNonNullElse(file.listFiles(), new File[0]);
            for (File value : filesInDirectory) {
                mergePurchasesFromFileRecursively(value.getAbsolutePath());
            }

        } else if (file.getName().matches(PURCHASE_FILE_PATTERN)) {
            this.mergePurchasesFromFile(file.getAbsolutePath());
        }
    }

    /**
     * show the top n purchases according to the ranking criterium specified by ranker
     *
     * @param n        the number of top purchases to be shown
     * @param subTitle some title text that clarifies the list
     * @param ranker   the comparator used to rank the purchases
     */
    public void showTops(int n, String subTitle, Comparator<Purchase> ranker) {
        System.out.printf("%d purchases with %s:\n", n, subTitle);
        OrderedList<Purchase> tops = new OrderedArrayList<>(ranker);

        // add all purchases to the new tops list
        tops.addAll(this.purchases);
        // sort the list
        tops.sort();

        // show the top items
        for (int rankItem = 0; rankItem < n && rankItem < tops.size(); rankItem++) {
            System.out.printf("%d: %s\n", rankItem + 1, tops.get(rankItem));
        }
    }

    public static Double totalVolume(List<Purchase> purchases) {
        double volume = 0;
        for (Purchase purchase : purchases) {
            volume += purchase.getCount();
        }
        return volume;
    }

    public Double totalRevenue(List<Purchase> purchases) {
        double revenue = 0;
        for (Purchase purchase : purchases) {
            revenue += (purchase.getCount() * purchase.getProduct().getPrice());
        }
        return revenue;
    }

    /**
     * shows total volume and total revenue sales statistics
     */
    public void showTotals() {
        System.out.printf("Total volume of all purchases: %.0f\n",
                this.totalVolume(this.getPurchases()));
        System.out.printf("Total revenue from all purchases: %.2f\n",
                this.totalRevenue(this.getPurchases()));
    }

    /**
     * imports a collection of items from a text file which provides one line for each item
     *
     * @param items     the list to which imported items shall be added
     * @param filePath  the file path of the source text file
     * @param converter a function that can convert a text line into a new item instance
     * @param <E>       the (generic) type of each item
     */
    public static <E> void importItemsFromFile(List<E> items, String filePath, Function<String, E> converter) {
        int originalNumItems = items.size();

        Scanner scanner = createFileScanner(filePath);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            E convertLine = converter.apply(line);
            items.add(convertLine);
        }
    }

    /**
     * imports another batch of raw purchase data from the filePath text file
     * and merges the purchase amounts with the earlier imported and accumulated collection in this.purchases
     *
     * @param filePath
     */
    private void mergePurchasesFromFile(String filePath) {
        OrderedList<Purchase> newPurchases = new OrderedArrayList<>(this.purchases.getOrdening());
        this.purchases.sort();

        importItemsFromFile(newPurchases, filePath,
                item -> Purchase.fromLine(item, this.products)
        );

        for (Purchase purchase : newPurchases) {
            if (Objects.isNull(purchase)) {
                continue;
            }
            this.purchases.merge(purchase, this::add);
        }
    }

    public Purchase add(Purchase p1, Purchase p2) {
        p1.addCount(p2.getCount());
        return p1;
    }

    /**
     * helper method to create a scanner on a file an handle the exception
     *
     * @param filePath
     * @return
     */
    private static Scanner createFileScanner(String filePath) {
        try {
            return new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound exception on path: " + filePath);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }
}
