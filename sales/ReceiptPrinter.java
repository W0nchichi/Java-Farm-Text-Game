package farm.sales;

import farm.core.ShopFront;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for pretty-printing a transaction as a receipt. 
 * Essentially a wrapper around an unusually complex toString, to avoid clutter. 
 * <br>
 * Please note that there is absolutely no requirement for you to understand the private methods 
 * used in this class, so long as you can call the public methods with the correct arguments, 
 * which you should be able to do from looking at the Javadocs alone. You are of course more than
 * welcome to poke around in the helper methods if you are <em>curious,</em> but you are not expected to.
 * @provided
 */
public class ReceiptPrinter {
    private static final String FARM_NAME = "The CSSE2002 Farm";
    private static final String FARM_ADDRESS = "Building 78, University of Queensland";
    private static final String FARM_LOGO = ShopFront.CAT; // width must be less than line length
    private static final int LINE_LENGTH = 48;

    /**
     * Creates a placeholder receipt for a transaction that has not been finalised yet and 
     * therefore cannot be used to generate a true receipt.
     * @return the placeholder receipt to display. 
     */
    public static String createActiveReceipt() {
        return "-".repeat(LINE_LENGTH) + "\n"
                + "Transaction still active; cannot generate receipt.\n"
                + "-".repeat(LINE_LENGTH) + "\n";
    }

    /**
     * Creates a nicely formatted String representation of a finalised transaction by styling it 
     * as a paper receipt, used for displaying information about this transaction to the CLI.
     * <p>
     * The method works by accepting provided string components and placing them in the appropriate
     * locations to create the final receipt. Specifically, these components should be provided as follows:
     * <ol>
     *     <li><strong>Headings:</strong> Provide the headings to be displayed at the top of the 'purchases' section 
     *     of the receipt, in the order (from left to right) in which they should appear on the receipt. For example, 
     *     if a headings list is provided beginning with "Name", then the leftmost heading of the products section
     *     will be "Name". All headings will always appear on the same line.</li>
     *     <li><strong>Entries:</strong> Provide a list of the entries to be displayed in the body of the 'purchases' section, 
     *     beneath the provided headings. Each entry should be a list of strings, with each item in the list 
     *     being the piece of information corresponding to the heading at the same index.
     *     <br>
     *     For example, if the first heading in the headings list is "Name", and the first list in the entries list 
     *     is ["Milk", "$4.40"], then "Milk" is the piece of information corresponding to the "Name" heading in the first entry.
     *     <br>
     *     Each entry occupies its own line, unless that entry contains more items than there are headings; in this case, 
     *     the entry 'wraps around' to the next line. For example, if there are two headings and some entry contains three items, 
     *     then the third string in that entry would instead be shown at the start of the <em>next</em> line.
     *     </li>
     *     <li><strong>Total:</strong> The string to be displayed as the total cost of the whole transaction.</li>
     *     <li><strong>Customer name:</strong> The string to be displayed as the customer's name.</li>
     * </ol>
     * Note: This method makes no changes to the <em>formatting of any of the supplied strings.</em>
     * All supplied strings must already be in the format in which they are to be displayed.
     * @param headings the headings to be shown at the top of all purchases listed on the receipt
     * @param entries the entries to be displayed in the purchases section of the receipt. Each
     *                entry is a list of strings that will be displayed in the same column as the 
     *                headings at the corresponding index of the headings list.
     * @param total the total price reported on the receipt.
     * @param customerName the name of the customer reported on the receipt.
     * @return the styled receipt representation of the provided transaction information
     */
    public static String createReceipt(List<String> headings, List<List<String>> entries,
                                       String total, String customerName) {
        StringBuilder sb = new StringBuilder();
        buildReceiptHeader(sb);
        
        List<Integer> colLengths = getMaxLenPerCol(headings, entries);
        int spacing = getSpacingBetweenEntries(colLengths);
        buildPurchasesSectionHeadings(sb, headings, colLengths, spacing);
        buildPurchasesSectionEntries(sb, entries, headings.size(), colLengths, spacing);
        
        buildTotalsSummary(sb, total);
        buildThankYouMessage(sb, customerName);
        return sb.toString();
    }
    
    /**
     * Creates a nicely formatted String representation of a finalised transaction by styling it 
     * as a paper receipt, used for displaying information about this transaction to the CLI.
     * <p>
     * The method works by accepting provided string components and placing them in the appropriate
     * locations to create the final receipt. Specifically, these components should be provided as follows:
     * <ol>
     *     <li><strong>Headings:</strong> Provide the headings to be displayed at the top of the 'purchases' section 
     *     of the receipt, in the order (from left to right) in which they should appear on the receipt. For example, 
     *     if a headings list is provided beginning with "Name", then the leftmost heading of the products section
     *     will be "Name". All headings will always appear on the same line.</li>
     *     <li><strong>Entries:</strong> Provide a list of the entries to be displayed in the body of the 'purchases' section, 
     *     beneath the provided headings. Each entry should be a list of strings, with each item in the list 
     *     being the piece of information corresponding to the heading at the same index.
     *     <br>
     *     For example, if the first heading in the headings list is "Name", and the first list in the entries list 
     *     is ["Milk", "$4.40"], then "Milk" is the piece of information corresponding to the "Name" heading in the first entry.
     *     <br>
     *     Each entry occupies its own line, unless that entry contains more items than there are headings; in this case, 
     *     the entry 'wraps around' to the next line. For example, if there are two headings and some entry contains three items, 
     *     then the third string in that entry would instead be shown at the start of the <em>next</em> line.
     *     </li>
     *     <li><strong>Total:</strong> The string to be displayed as the total cost of the whole transaction.</li>
     *     <li><strong>Customer name:</strong> The string to be displayed as the customer's name.</li>
     *     <li><strong>Total saved:</strong> The string to be displayed as the total savings incurred from the whole transaction.</li>
     * </ol>
     * Note: This method makes no changes to the <em>formatting of any of the supplied strings.</em>
     * All supplied strings must already be in the format in which they are to be displayed.
     * @param headings the headings to be shown at the top of all purchases listed on the receipt
     * @param entries the entries to be displayed in the purchases section of the receipt. Each
     *                entry is a list of strings that will be displayed in the same column as the 
     *                headings at the corresponding index of the headings list.
     * @param total the total price reported on the receipt.
     * @param customerName the name of the customer reported on the receipt.
     * @param totalSaved the total savings reported on the receipt.                    
     * @return the styled receipt representation of the provided transaction information
     */
    public static String createReceipt(List<String> headings, List<List<String>> entries,
                                       String total, String customerName, String totalSaved) {
        StringBuilder sb = new StringBuilder();
        buildReceiptHeader(sb);

        List<Integer> colLengths = getMaxLenPerCol(headings, entries);
        int spacing = getSpacingBetweenEntries(colLengths);
        buildPurchasesSectionHeadings(sb, headings, colLengths, spacing);
        buildPurchasesSectionEntries(sb, entries, headings.size(), colLengths, spacing);
        
        buildTotalsSummary(sb, total);
        buildSavingsSummary(sb, totalSaved);
        buildThankYouMessage(sb, customerName);
        return sb.toString();
    }
    
    private static void buildReceiptHeader(StringBuilder sb) {
        sb.append("=".repeat(LINE_LENGTH)).append("\n");
        sb.append(ShopFront.centreStringComponent(FARM_NAME, LINE_LENGTH));
        sb.append(ShopFront.centreStringComponent(FARM_ADDRESS, LINE_LENGTH)).append("\n");
        sb.append(ShopFront.centreStringComponent(FARM_LOGO, LINE_LENGTH)).append("\n");
        
    }
    
    private static void buildPurchasesSectionHeadings(StringBuilder sb, List<String> headings, List<Integer> colLengths, int spacing) {
        sb.append("=".repeat(LINE_LENGTH)).append("\n");
        distribute(sb, colLengths, spacing, headings);
        sb.append("-".repeat(LINE_LENGTH)).append("\n");
    }
    
    private static void buildPurchasesSectionEntries(StringBuilder sb, List<List<String>> entries, int numHeadings, List<Integer> colLengths, int spacing) {
        
        for (List<String> entry : entries) {
            List<List<String>> lines = new ArrayList<>();
            if (entry.size() <= numHeadings) {
                lines.add(entry);
            } else {
                int partitions = (int) Math.ceil((double) entry.size() / numHeadings);
                for (int i = 0; i < partitions; i++) {
                    lines.add(entry.subList(i * numHeadings, Math.min(i * numHeadings
                            + numHeadings, entry.size())));
                }
            }
            
            for (List<String> line : lines) {
                distribute(sb, colLengths, spacing, line);
            }
        }
    }

    private static void distribute(StringBuilder sb, List<Integer> colLengths, int spacing, List<String> line) {
        for (int i = 0; i < line.size() - 1; i++) {
            String part = line.get(i);
            sb.append(part).append(" ".repeat(spacing + colLengths.get(i) - part.length()));
        }
        sb.append(line.getLast());
        sb.append("\n"); // only one linebreak between same-entry lines
    }

    private static List<Integer> getMaxLenPerCol(List<String> headings, List<List<String>> entries) {
        List<Integer> colLengths = headings.stream().map(String::length).collect(Collectors.toList());
        for (List<String> entry : entries) {
            for (int i = 0; i < colLengths.size(); i++) { // for loop to check only first line (no wrap)
                colLengths.set(i, Math.max(colLengths.get(i), entry.get(i).length()));
            }
        }
        return colLengths;
    }
    
    private static int getSpacingBetweenEntries(List<Integer> maxLengths) {
        return Math.max((LINE_LENGTH - maxLengths.stream().mapToInt(Integer::intValue).sum()) / (maxLengths.size() - 1), 1);
    }
    
    private static void buildTotalsSummary(StringBuilder sb, String total) {
        sb.append("-".repeat(LINE_LENGTH)).append("\n");
        sb.append("Total:").append(" ".repeat(Math.max(LINE_LENGTH / 2 - "Total:".length(), 2))).append(total).append("\n");
    }
    
    private static void buildSavingsSummary(StringBuilder sb, String totalSaved) {
        sb.append("-".repeat(LINE_LENGTH)).append("\n");
        sb.append("***** TOTAL SAVINGS: ").append(totalSaved).append(" *****").append("\n");
    }
    
    private static void buildThankYouMessage(StringBuilder sb, String customerName) {
        sb.append("-".repeat(LINE_LENGTH)).append("\n");
        String msg = "Thank you for shopping with us, " + customerName + "!";
        sb.append(msg.indent((LINE_LENGTH - msg.length()) / 2)).append("\n");
        sb.append("=".repeat(LINE_LENGTH)).append("\n");
    }
}