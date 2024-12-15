package farm.customer;

import farm.core.CustomerNotFoundException;
import farm.core.DuplicateCustomerException;

import java.util.ArrayList;
import java.util.List;

/**
 * The address book where the farmer stores their customers' details.
 * <p>
 * Keeps track of all the customers that come and visit the Farm.
 * @multistage
 * @stage0
 * @stage1
 */
public class AddressBook {
    private final List<Customer> customers = new ArrayList<>();

    /**
     * Add a new customer to the address book.
     * <p>
     * <span style="color:#2E75B2;">Stage 1:</span> If the address book already contains the customer to be added, throws a duplicate customer
     * exception with a message containing the string representation ({@link Customer#toString()})
     * of the customer identified as a duplicate.
     * @multistage
     * @stage0
     * @stage1
     * @hint <span style="color:#2E75B2;">Stage 1:</span> A duplicate customer is one where they share the same phone number and name.
     * @param customer The customer to be added.
     * @ensures The address book contains no duplicate customers .
     * @throws DuplicateCustomerException <span style="color:#2E75B2;">Stage 1:</span> If the customer already exists in the address book. Contains a message of the Customers representation.
     */
    public void addCustomer(Customer customer) throws DuplicateCustomerException {
        if (this.containsCustomer(customer)) {
            throw new DuplicateCustomerException(customer.toString());
        }
        this.customers.add(customer);
    }

    /**
     * Retrieve all customer records stored in the address book.
     * @return A list of all customers in the address book
     * @ensures The returned list is a shallow copy and cannot modify the original address book
     */
    public List<Customer> getAllRecords() {
        return new ArrayList<>(this.customers);
    }

    /**
     * Check to see if a customer is already in the address book.
     * @stage1
     * @param customer The customer to check.
     * @return true iff the customer already exists, else false
     */
    public boolean containsCustomer(Customer customer) {
        return this.customers.contains(customer);
    }

    /**
     * Lookup a customer in address book, if they exist using their details.
     * @stage1
     * @param name  The name of the customer to lookup.
     * @param phoneNumber The phone number of the customer.
     * @return The Customer iff they exist in the address book.
     * @throws CustomerNotFoundException If there is no customer matching the information in the address book.
     * @requires That the name is non-empty and has been stripped of its trailing whitespace and that the phone number is a positive number.
     */
    public Customer getCustomer(String name, int phoneNumber) throws CustomerNotFoundException {
        for (Customer customer : this.customers) {
            if (customer.getPhoneNumber() == phoneNumber && customer.getName().equals(name)) {
                return customer;
            }
        }
        throw new CustomerNotFoundException();
    }
}