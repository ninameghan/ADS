import models.Purchase;
import models.PurchaseTracker;

import java.util.Comparator;

public class SupermarketStatisticsMain {

    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Supermarket Statistics processor\n");

        PurchaseTracker purchaseTracker = new PurchaseTracker();

        purchaseTracker.importProductsFromVault("/products.txt");

        purchaseTracker.importPurchasesFromVault("/purchases");

        purchaseTracker.showTops(5, "worst sales volume",
                Comparator.comparing(Purchase::getCount)
        );

        purchaseTracker.showTops(5, "best sales revenue",
                (p1, p2) -> {
            double value1 = p1.getCount()*p1.getProduct().getPrice();
            double value2 = p2.getCount()*p2.getProduct().getPrice();
            return (int) Math.signum(value2-value1);
                }
        );

        purchaseTracker.showTotals();
    }
}
