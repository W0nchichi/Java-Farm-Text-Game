package farm.core;

import farm.core.farmgrid.*;
import farm.customer.Customer;
import farm.debugged.farmgrid.FarmGrid;
import farm.files.FileLoader;
import farm.files.FileSaver;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;
import farm.sales.TransactionHistory;
import farm.sales.transaction.CategorisedTransaction;
import farm.sales.transaction.SpecialSaleTransaction;
import farm.sales.transaction.Transaction;

import java.io.IOException;
import java.util.*;

/**
 * Controller class, coordinating information between the model and view/UI of the program.
 */
public class FarmManager {
    private final Farm farm;
    private final ShopFront shop;
    private final boolean enableFancy;
    private final FileLoader loader;
    private final FileSaver saver;
    private Grid grid;
    private GridConstructor gridConstructor;


//    // note: farmType is perhaps not the most elegant design choice,
//    // but has been included here to give you more flexibility.
//    // Feel free to modify FarmManager if you wish, as long as run()
//    // instantiates/loads/saves the grid with the same commands, and
//    // getCurrentGrid() allows us to retrieve the grid for testing.
//    private String farmType;


    /**
     * Create a new FarmManager instance with a farm and shop provided.
     * @param farm the model for the program.
     * @param shop the UI/view for the program.
     */
    public FarmManager(Farm farm, ShopFront shop) {
        this(farm, shop, false); // disable fancy behaviour until later stage
    }

    /**
     * Create a new FarmManager instance with a farm and shop provided, and the ability to enable the inventory type.
     * @param farm the model for the program.
     * @param shop the UI/view for the program.
     * @param enableFancy flag indicating whether to use the FancyInventory inventory type (Stage 2)
     */
    public FarmManager(Farm farm, ShopFront shop, boolean enableFancy) {
        this.farm = farm;
        this.shop = shop;
        this.enableFancy = enableFancy;
        this.loader = new FileLoader();
        this.saver = new FileSaver();
        this.gridConstructor = new GridConstructor();
    }

    /**
     * Begins the running of the UI and interprets user input to begin the appropriate mode.
     */
    public void run() {
        boolean running = true;
        this.startDisplay();
        this.promptGridCreation();
        while (running) {
            switch (this.getModeSelection()) {
                case "q" -> running = false;
                case "inventory" -> this.launchInventoryMode();
                case "address" -> this.launchAddressBookMode();
                case "sales" -> this.launchSalesMode();
                case "history" -> this.launchHistoryMode();
                case "farm" -> this.launchFarmingMode();
            }
        }
    }

    /**
     * Retrieves the current grid associated with the FarmGame.
     * @return the current Grid instance.
     */
    public Grid getCurrentGrid() {
        return this.grid;
    }

    /**
     * Create a farm grid of the specified type and dimensions.
     * @param input List of Strings containing the farm type, number of rows, number of columns.
     *              Position 0 not used in this method.
     * @return an instantiated Grid.
     */
    private Grid createFarmType(List<String> input) {
        int row, col;
        try {
            row = Integer.parseInt(input.get(2));
            col = Integer.parseInt(input.get(3));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row/col numbers. " +
                    "Are they actually numbers?\n");
        }

        if (row <= 0 || col <= 0) {
            throw new IllegalArgumentException("Dimensions cannot be negative!\n");
        }

        String farmType = input.get(1);
        return gridConstructor.constructGrid(row, col, farmType);
    }

    /**
     * Get the user to determine the farm to use and set it up for usage.
     */
    private void promptGridCreation() {
        while (this.grid == null) {
            List<String> input = this.newOrLoad();

            switch (input.getFirst()) {
                case "new" -> {
                    if (input.size() != 4) {
                        shop.displayMessage("Incorrect number of arguments.");
                        continue;
                    }
                    try {
                        this.grid = createFarmType(input);
                    } catch (IllegalArgumentException e) {
                        shop.displayMessage(e.getMessage());
                    }
                }

                case "load" -> {
                    if (input.size() != 2) {
                        shop.displayMessage("Incorrect number of arguments. Did you give the " +
                                "filepath to load?");
                        continue;
                    }
                    loadFarm(input.get(1));
                }

                default -> shop.displayMessage("Unexpected value entered.\n");
            }
        }
    }

    /**
     * Load in a farm from given file and set it as grid
     * @param filename save file to load
     */
    private void loadFarm(String filename) {
        try {
            this.grid = loader.load(filename);
        } catch (IOException | IllegalArgumentException exception) {
            shop.displayMessage(exception.getMessage());
        }
    }

    /**
     * Adds a single product with corresponding name to the Farm's inventory.
     * @param productName the name of the product to add to the farm.
     */
    protected void addToInventory(String productName) {
        try {
            farm.stockProduct(convertProductName(productName), Quality.REGULAR);
            shop.displayProductAddSuccess();
        } catch (InvalidStockRequestException e) {
            shop.displayProductAddFailed(e.getMessage());
        }
    }

    /**
     * Adds a certain number of the given product with corresponding name to the Farm's inventory.
     * @param productName the name of the product to add to the farm.
     * @param quantity the amount of the product to add.
     */
    protected void addToInventory(String productName, int quantity) {
        try {
            farm.stockProduct(convertProductName(productName), Quality.REGULAR, quantity);
            shop.displayProductAddSuccess();
        } catch (InvalidStockRequestException | IllegalArgumentException e) {
            shop.displayProductAddFailed(e.getMessage()); 
        }
    }

    /**
     * Prompt the user to create a new customer and then save it to the farms address book for later usage.
     */
    protected void createCustomer() {
        try {
            farm.saveCustomer(new Customer(shop.promptForCustomerName(),
                    shop.promptForCustomerNumber(), shop.promptForCustomerAddress()));
        } catch (DuplicateCustomerException e) {
            shop.displayDuplicateCustomer();
        } catch (NumberFormatException e) {
            shop.displayInvalidPhoneNumber();
        }
    }

    /**
     * Start a new transaction for the transaction manager to manage. There are three types of
     * transactions that can be made that correspond to different values being passed in.
     * @param transactionType the type of transaction to make
     */
    protected void initiateTransaction(String transactionType) {
        Customer customer;
        try {
            customer = farm.getCustomer(shop.promptForCustomerName(),
                    shop.promptForCustomerNumber());
        } catch (CustomerNotFoundException e) {
            shop.displayCustomerNotFound();
            return;
        } catch (NumberFormatException e) {
            shop.displayInvalidPhoneNumber();
            return;
        }

        Transaction transaction = switch (transactionType) {
            case "-s", "-specialsale" -> new SpecialSaleTransaction(customer, getDiscounts());
            case "-c", "-categorised" -> new CategorisedTransaction(customer);
            default -> new Transaction(customer);
        };

        try {
            shop.displayTransactionStart();
            farm.startTransaction(transaction);
        } catch (FailedTransactionException e) {
            shop.displayFailedToCreateTransaction();
        }
    }

    /** UI REQUESTS and UPDATES **/
    private void startDisplay() {
        shop.displayMessage("-*- WELCOME TO FARM MVP -*-");
    }

    private List<String> newOrLoad() {
        return shop.newOrLoadSelect();
    }
    
    private String getModeSelection() {
        return shop.promptModeSelect().getFirst().trim().toLowerCase();
    }
    
    // -- MODE LAUNCHERS and INPUT VALIDATORS -- //

    /**
     * Launches the inventory mode of the CLI.
     */
    private void launchInventoryMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptInventoryCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "add" -> handleInventoryAddRequest(input);
                case "list" -> {
                    int count = 1;
                    List<Product> stock = farm.getAllStock();
                    if (stock.isEmpty()) {
                        shop.displayMessage("Inventory is empty.");
                    } else {
                        StringBuilder builder = new StringBuilder("{" + stock.getFirst());
                        for (Product product : stock.subList(1, stock.size())) {
                            builder.append(",").append("\t\t");
                            if (count % 4 == 0) {
                                builder.append(System.lineSeparator());
                            }
                            builder.append(product.toString());
                            count++;
                        }
                        shop.displayMessage(builder.append("}").toString());
                    }
                }
            }
        }
    }

    /**
     * Launches the address book mode of the CLI.
     */
    private void launchAddressBookMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptAddressBookCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "add" -> createCustomer();
                case "list" -> {
                    for (Customer customer : farm.getAllCustomers()) {
                        shop.displayMessage(customer.toString());
                    }
                }
            }
        }
    }

    /**
     * Launches the sales mode of the CLI.
     */
    private void launchSalesMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptSalesCmd();
            switch (input.getFirst()) {
                case "q" -> {
                    if (farm.getTransactionManager().hasOngoingTransaction()) {
                        shop.displayMessage("You have a transaction in progress. Please check out "
                                + "before quitting sales mode or your inventory may be corrupted.");
                    } else {
                        running = false;
                    }
                }
                case "start" -> handleStartTransaction(input);
                case "add" -> handleTransactionAddRequest(input);
                case "checkout" -> handleCheckoutRequest();
            }
        }
    }

    /**
     * Launches the sales history mode of the CLI.
     */
    private void launchHistoryMode() {
        boolean running = true;
        while (running) {
            List<String> input = shop.promptHistoryCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "stats" -> handleHistoryStats(input);
                case "last" -> {
                    if (farm.getTransactionHistory().getTotalTransactionsMade() == 0) {
                        shop.displayMessage("No transactions made!");
                    } else {
                        shop.displayReceipt(farm.getLastReceipt());
                    }
                }
                case "grossing" -> {
                    if (farm.getTransactionHistory().getTotalTransactionsMade() == 0) {
                        shop.displayMessage("No transactions made!");
                    } else {
                        shop.displayReceipt(farm.getTransactionHistory()
                                .getHighestGrossingTransaction().getReceipt());
                    }
                }
                case "popular" -> shop.displayMessage(
                        farm.getTransactionHistory().getMostPopularProduct().getDisplayName()
                                + " is the most popular!!");
            }
        }
    }


    // -- INVENTORY MODE CONTROLS -- //

    private void handleInventoryAddRequest(List<String> cmdInput) {
        if (cmdInput.size() == 2) {
            String arg = cmdInput.get(1);
            if (arg.equals("-o")) {
                for (Barcode barcode : Barcode.values()) {
                    shop.displayMessage(barcode.toString().toLowerCase());
                }
            } else {
                addToInventory(arg);
            }
        } else if (cmdInput.size() == 3) {
            if (!enableFancy) {
                shop.displayQuantitiesNotSupported();
                return;
            }
            try {
                int quantity = Integer.parseInt(cmdInput.get(2));
                addToInventory(cmdInput.get(1), quantity);
            } catch (NumberFormatException e) {
                shop.displayInvalidQuantity();
            }
        } else {
            shop.displayIncorrectArguments();
        }
    }

    // -- ADDRESS BOOK MODE CONTROLS -- //


    // -- SALES MODE CONTROLS -- //

    private void handleStartTransaction(List<String> input) {
        if (input.size() == 1) {
            initiateTransaction("");
        } else if (input.size() == 2) {
            initiateTransaction(input.get(1));
        } else {
            shop.displayIncorrectArguments();
        }
    }

    private void handleCheckoutRequest() {
        try {
            boolean printReceipt = farm.checkout();
            if (printReceipt) {
                shop.displayReceipt(farm.getLastReceipt());
                return;
            }
            shop.displayMessage("Thanks for stopping by!");

        } catch (FailedTransactionException e) {
            shop.displayMessage("Checkout request failed: " + e.getMessage());
        }
    }

    private void handleTransactionAddRequest(List<String> cmdInput) {
        if (cmdInput.size() == 2 || cmdInput.size() == 3) {

            if (cmdInput.get(1).equals("-o")) {
                for (Barcode barcode : Barcode.values()) {
                    shop.displayMessage(barcode.toString().toLowerCase());
                }
                return;
            }

            // Check to see if attempted to use quantities.
            int quantity = 1;
            if (cmdInput.size() == 3) {
                if (!enableFancy) {
                    shop.displayQuantitiesNotSupported();
                    return;
                }
                try {
                    quantity = Integer.parseInt(cmdInput.get(2));
                } catch (NumberFormatException e) {
                    shop.displayInvalidQuantity();
                }
            }

            // Attempt to add to transaction
            try {

                int actualQuantity;
                if (cmdInput.size() == 3) {
                    if (!enableFancy) {
                        shop.displayQuantitiesNotSupported();
                        return;
                    }
                    actualQuantity =  farm.addToCart(convertProductName(cmdInput.get(1)), quantity);
                } else {
                    actualQuantity = farm.addToCart(convertProductName(cmdInput.get(1)));
                }

                if (enableFancy && actualQuantity < quantity) {
                    shop.displayMessage("We only had " + actualQuantity + " " + cmdInput.get(1)
                            + " to give you :(");
                } else if (actualQuantity > 0) {
                    shop.displayMessage("Item/s added to cart");
                } else {
                    shop.displayMessage("Sorry, that's out of stock!");
                }
            } catch (InvalidStockRequestException e) {
                shop.displayInvalidProductName();
            } catch (FailedTransactionException | IllegalArgumentException e) {
                shop.displayMessage("Product could not be added to transaction: " + e.getMessage());
            }

        } else {
            shop.displayIncorrectArguments();
        }
    }

    private Map<Barcode, Integer> getDiscounts() { 
        shop.displayMessage("Entering Discount Setting!");
        Map<Barcode, Integer> discounts = new HashMap<>();
        String productName;
        while (true) {
            try {
                productName = shop.promptForProductName().toLowerCase();
                if (productName.equals("q")) {
                    break;
                }
                Barcode barcode = convertProductName(productName);

                int discount = shop.promptForDiscount("Discount (%): ");
                if (discount < 0) {
                    break;
                }
                discounts.put(barcode, discount); // overwrites old discount if they enter it twice
            } catch (InvalidStockRequestException ignored) {
                shop.displayMessage("Please enter a valid product name.");
            }
        }
        shop.displayMessage("Discounts entered as follows: ");
        shop.displayMessage(discounts.toString());
        return discounts;
    }

    // -- HISTORY MODE CONTROLS -- //

    private void handleHistoryStats(List<String> input) {
        TransactionHistory history = farm.getTransactionHistory();

        if (input.size() == 2) {
            try {
                Barcode barcode = convertProductName(input.get(1));
                shop.displayMessage(String.format("""
                    |--------------------------
                    |     Stats for all
                    | Total Transactions:  %s
                    | Average Sale Price:  $%.2f
                    |--------------------------
                    |     Stats for %s
                    | Total Products Sold: %s
                    | Gross Earning        $%.2f
                    | Average Discount:    %.0f`
                    |--------------------------
                    """, history.getTotalTransactionsMade(),
                        history.getAverageSpendPerVisit() / 100.0f, barcode.getDisplayName(),
                        history.getTotalProductsSold(barcode),
                        history.getGrossEarnings(barcode) / 100.0f,
                        history.getAverageProductDiscount(barcode)
                        ).replace("`", "%")
                );
            } catch (InvalidStockRequestException e) {
                shop.displayInvalidProductName();
            }
            return;
        }

        shop.displayMessage(String.format("""
            |--------------------------
            |     Stats for all
            | Total Transactions:  %s
            | Average Sale Price:  $%.2f
            | Total Products Sold: %s
            | Gross Earning        $%.2f
            |--------------------------
            """, history.getTotalTransactionsMade(), history.getAverageSpendPerVisit() / 100.0f,
            history.getTotalProductsSold(), history.getGrossEarnings() / 100.0f));
    }

    /** Private Helper Methods **/
    private Barcode convertProductName(String productName) throws InvalidStockRequestException {
        return switch (productName) {
            case "egg" -> Barcode.EGG;
            case "milk" -> Barcode.MILK;
            case "jam" -> Barcode.JAM;
            case "wool" -> Barcode.WOOL;
            default -> throw new InvalidStockRequestException("Invalid product name provided: "
                    + productName);
        };
    }


    // -- FARMING MODE CONTROLS -- //

    private void launchFarmingMode() {
        boolean running = true;
        boolean canSave = true;
        shop.displayMessage(grid.farmDisplay());
        while (running) {
            List<String> input = shop.promptFarmingCmd();
            switch (input.getFirst()) {
                case "q" -> running = false;
                case "place" -> {
                    if (input.size() == (4)) {
                        int row = Integer.parseInt(input.get(2));
                        int col = Integer.parseInt(input.get(3));
                        try {
                            char symbol = this.nameToSymbol(input.get(1));
                            if (!this.grid.place(row, col, symbol)) {
                                shop.displayMessage(input.get(1) + " could not be placed!");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            shop.displayMessage(e.getMessage());
                        }
                    } else {
                        shop.displayMessage("Did you remember to specify the type?");
                    }
                }

                case "remove" -> {
                    canSave = false;
                    int row = Integer.parseInt(input.get(1));
                    int col = Integer.parseInt(input.get(2));
                    try {
                        this.grid.interact("remove", row, col);
                    } catch (UnableToInteractException e) {
                        shop.displayMessage("Unexpected exception thrown when removing from grid: "
                                + e.getMessage());
                    }
                }

                case "save" -> {
                    if (canSave) {
                        String filename;
                        Scanner saveInput = new Scanner(System.in);
                        shop.displayMessage("Enter the filename to save farm grid as: ");
                        filename = saveInput.nextLine().trim();
                        try {
                            saver.save(filename, this.grid);
                        } catch (IOException e) {
                            shop.displayMessage("There was an error saving your file: " + e.getMessage());
                        }
                    } else {
                        shop.displayMessage("You can only save at the start of the day!");
                    }
                }

                case "load" -> {
                    try {
                        loadFarm(input.get(1));
                    } catch (IndexOutOfBoundsException oob) {
                        shop.displayMessage("You forgot the file name to load!");
                    }
                }

                case "end-day" -> {
                    this.endDay();
                    canSave = true;
                }

                case "harvest" -> {
                    int row = Integer.parseInt(input.get(1));
                    int col = Integer.parseInt(input.get(2));
                    try {
                        canSave = false;
                        Product product = this.grid.harvest(row, col);
                        Barcode barcode = product.getBarcode();
                        Quality quality = product.getQuality();
                        this.farm.stockProduct(barcode, quality);
                    } catch (UnableToInteractException e) {
                        shop.displayMessage(e.getMessage());
                    }
                }

                case "feed" -> {
                        try {
                            int row = Integer.parseInt(input.get(1));
                            int col = Integer.parseInt(input.get(2));
                            grid.interact("feed", row, col);
                            canSave = false;
                        } catch (UnableToInteractException e) {
                            shop.displayMessage(e.getMessage());
                        }
                }

                case "stats" -> {
                    StringBuilder sb = new StringBuilder();
                    List<List<String>> stats = this.grid.getStats();

                    sb.append(System.lineSeparator());

                    int rows = this.grid.getRows();
                    int cols = this.grid.getColumns();

                    for (int i = 0; i < rows * cols; i++) {

                        sb.append(stats.get(i)).append(" ");
                        if ((i + 1) % cols == 0) {
                            sb.append(System.lineSeparator());
                        }
                    }
                    shop.displayMessage(sb.toString());
                }
            }
            shop.displayMessage(grid.farmDisplay());
        }
    }

    /**
     * End the day on the farm.
     */
    private void endDay() {
        try {
            this.grid.interact("end-day", 0, 0);
        } catch (UnableToInteractException e) {
            shop.displayMessage("Unexpected exception thrown when ending the day: "
                    + e.getMessage());
        }
    }

    /**
     * Converts the name of a plant or animal into their grid symbol representation
     * @param input name to convert
     * @return symbol representation
     */
    private char nameToSymbol(String input) {
        return switch (input) {
            case "berry" -> '.';
            case "coffee" -> ':';
            case "wheat" -> '\u1F34';
            case "chicken" -> '\u09EC';
            case "cow" -> '\u096A';
            case "sheep" -> '\u0D94';
            default -> throw new IllegalArgumentException("Invalid object to place.");
        };
    }
}

