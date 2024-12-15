package farm.sales;

import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.sales.transaction.CategorisedTransaction;
import farm.sales.transaction.SpecialSaleTransaction;
import farm.sales.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * A record of all past transactions.
 * <p>
 * Handles retrieval of statistics about past transactions, such as earnings and popular products.
 * @stage2
 */
public class TransactionHistory {
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Adds the given transaction to the record of all past transactions.
     * @param transaction the transaction to add to the record.
     * @requires the transaction to be recorded has been finalised
     */
    public void recordTransaction(Transaction transaction) {
        transactions.addLast(transaction);
    }

    /**
     * Retrieves the most recent transaction.
     * @return the most recent transaction added to the record.
     */
    public Transaction getLastTransaction() {
        return transactions.getLast();
    }

    /**
     * Calculates the gross earnings, i.e. total income, from all transactions.
     * <p>
     * Total income refers to the sum of all totals reported by each individual completed 
     * transaction stored in the history, as calculated by that particular transaction's 
     * {@link Transaction#getTotal()}; if the history contains transaction instances that 
     * calculate their totals in a particular manner, such as {@link SpecialSaleTransaction}, 
     * it should nonetheless use the total exactly as it is reported by that instance. 
     * <p>For example, suppose a particular {@link SpecialSaleTransaction} reports its total as 
     * 150c because a 50c discount has been applied to the original purchase price of 200c. This 
     * method should add the reported 150c total to the sum of gross earnings, not the undiscounted
     * 200c, because that is the total reported by that particular transaction.
     * <p><em>Note: returns the calculated total in integer cents.</em>
     * @return the gross earnings from all transactions in history, in cents.
     */
    public int getGrossEarnings() { 
        int total = 0;
        for (Transaction sale : transactions) {
            total += sale.getTotal();
        }
        return total;
    }

    /**
     * Calculates the gross earnings, i.e. total income, from all sales of a particular product type.
     * <p>
     * Total income is as defined in {@link TransactionHistory#getGrossEarnings()}.
     * <p><em>Note: returns the calculated total in integer cents.</em>
     * @param type the Barcode of the item of interest.
     * @return the gross earnings from all sales of the product type, in cents.
     */
    public int getGrossEarnings(Barcode type) { 
        int total = 0;
        for (Transaction sale : transactions) {
            if (sale instanceof CategorisedTransaction catSale) {
                total += catSale.getPurchaseSubtotal(type);
            } else {
                total += getBasicTransactionSubtotalByType(sale, type);
            }
        }
        return total; 
    }

    /**
     * Calculates the number of transactions made.
     * @return the number of transactions in total.
     */
    public int getTotalTransactionsMade() {
        return transactions.size();
    }

    /**
     * Calculates the number of products sold over all transactions.
     * @return the total number of products sold.
     */
    public int getTotalProductsSold() {
        int total = 0;
        for (Transaction sale : transactions) {
            total += sale.getPurchases().size();
        }
        return total; 
    }

    /**
     * Calculates the number of sold of a particular product type, over all transactions.
     * @param type the Barcode for the product of interest
     * @return the total number of products sold, for that particular product.
     */
    public int getTotalProductsSold(Barcode type) { 
        int quantity = 0;
        for (Transaction sale : transactions) {
            if (sale instanceof CategorisedTransaction catSale) {
                quantity += catSale.getPurchaseQuantity(type);
            } else {
                quantity += getBasicTransactionQuantityByType(sale, type);
            }
        }
        return quantity; 
    }

    /**
     * Retrieves the transaction with the highest gross earnings, i.e. reported total. If there are multiple return the one that first was recorded.
     * @return the transaction with the highest gross earnings.
     */
    public Transaction getHighestGrossingTransaction() { 
        Transaction best = transactions.getFirst();
        for (Transaction candidate : transactions) {
            if (candidate.getTotal() > best.getTotal()) {
                best = candidate;
            }
        }
        return best;
    }

    /**
     * Calculates which type of product has had the highest quantity sold overall.
     * If two products have sold the same quantity resulting in a tie, return the one appearing first in the Barcode enum. See {@link Barcode}
     * @hint tiebreak using ({@link Barcode#values()})
     * @return the identifier for the product type of most popular product.
     */
    public Barcode getMostPopularProduct() {
        Barcode bestSeller = null;
        int bestAmount = 0;
        
        for (Barcode type : Barcode.values()) {
            int candidateAmount = getTotalProductsSold(type);
            if (candidateAmount > bestAmount) {
                bestSeller = type;
                bestAmount = candidateAmount;
            }
        }
        
        if (bestSeller == null) {
            return Barcode.values()[0];
        } else {
            return bestSeller;
        }
    }

    /**
     * Calculates the average amount spent by customers across all transactions.
     * <p><em>Note: returns the calculated average price in cents, but as a double, 
     * since it is possible that the average may not be a round number of cents. For example,
     * if the total amount earned is 1100c and the total number of transactions is 3, then the 
     * average spend per visit is ~366.6666666666667c.</em>
     * You do not need to return the result at a particular float precision or control the number 
     * of decimal places, so long as it is accurate to at least the first three decimal places. 
     * If you are rounding the result, ensure your rounding does not affect these places. 
     * If there have been no products sold, return 0.0d.
     * @return the average amount spent overall, in cents (with decimals). 
     */
    public double getAverageSpendPerVisit() {
        if (getTotalProductsSold() == 0) {
            return 0;
        }
        return (double) getGrossEarnings() / getTotalTransactionsMade();
    }

    /**
     * Calculates the average amount a product has been discounted by, across all sales of that product.
     * <p><em>Note: returns the calculated average discount for this product type in cents, but as a
     * double, since it is possible that the average may not be a round number of cents. For example,
     * if the total amount earned is 1100c and the total number of transactions is 3, then the 
     * average spend per visit is ~366.6666666666667c.</em>
     * You do not need to return the result at a particular float precision or control the number 
     * of decimal places, so long as it is accurate to at least the first three decimal places. 
     * If you are rounding the result, ensure your rounding does not affect these places. 
     * If there have been no products sold, return 0.0d.
     * @hint Where do discounts come in?
     * @param type identifier of the product of interest.
     * @return the average discount for the product, in cents (with decimals). 
     */
    public double getAverageProductDiscount(Barcode type) {
        if (getTotalProductsSold(type) == 0) {
            return 0;
        }
        double discount = 0;
        for (Transaction sale : transactions) {
            if (sale instanceof SpecialSaleTransaction specSale) {
                discount += specSale.getPurchaseQuantity(type) *
                        specSale.getDiscountAmount(type) * type.getBasePrice() / 100.0;
            } // otherwise no discount, so don't add anything to the discount sum
        }
        return (double) discount / getTotalProductsSold(type);
    }
    
    /* Alternative solution that satisfies the original bugged tests, for reference.
    */
    private double getAverageProductDiscountPercent(Barcode type) {
        if (getTotalProductsSold(type) == 0) {
            return 0;
        }

        int discount = 0;
        for (Transaction sale : transactions) {
            if (sale instanceof SpecialSaleTransaction specSale) {
                discount += specSale.getDiscountAmount(type) * specSale.getPurchaseQuantity(type);
            } // otherwise no discount, so don't add anything to the discount sum
        }
        return (double) discount / getTotalProductsSold(type);
    }


    /* private helpers - for basic transactions, which don't already sort by type */
    private int getBasicTransactionSubtotalByType(Transaction sale, Barcode type) {
        int subtotal = 0;
        for (Product product : sale.getPurchases()) {
            if (product.getBarcode() == type) {
                subtotal += product.getBasePrice();
            }
        }
        return subtotal;
    }
    
    private int getBasicTransactionQuantityByType(Transaction sale, Barcode type) {
        int quantity = 0;
        for (Product product : sale.getPurchases()) {
            if (product.getBarcode() == type) {
                quantity++;
            }
        }
        return quantity;
    }
}
