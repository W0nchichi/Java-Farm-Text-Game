package farm;

import farm.core.DuplicateCustomerException;
import farm.core.Farm;
import farm.core.FarmManager;
import farm.core.ShopFront;
import farm.customer.AddressBook;
import farm.customer.Customer;
import farm.inventory.FancyInventory;
import farm.inventory.Inventory;

import java.util.List;


/**
 * Execute the Farm MVP program.
 * This file is for you to execute your program,
 * it will not be marked.
 */
public class Main {

    /**
     * Start the farm program.
     * @param args Parameters to the program, currently not supported.
     */
    public static void main(String[] args) throws DuplicateCustomerException {

        System.out.println("".equals(' '));

        AddressBook addressBook = new AddressBook();
        Customer customer = new Customer("Ali", 33651111, "UQ");
        addressBook.addCustomer(customer);
        for (String name : List.of("James", "Alex", "Lauren")) {
            addressBook.addCustomer(new Customer(name, 1234, "1st Street"));
        }

        Inventory inventory = new FancyInventory();
        // these lines populate the inventory with extra things if you would like to
//        for (Barcode barcode : List.of(Barcode.MILK, Barcode.EGG, Barcode.WOOL, Barcode.EGG)) {
//            for (Quality quality : List.of(Quality.REGULAR, Quality.SILVER, Quality.REGULAR,
//                    Quality.GOLD, Quality.REGULAR, Quality.REGULAR, Quality.IRIDIUM)) {
//                inventory.addProduct(barcode, quality);
//            }
//        }

        // these lines are what runs the actual programn
        FarmManager manager = new FarmManager(new Farm(inventory, addressBook),
                new ShopFront(), true);
        manager.run();
    }
}