package farm.customer;

import farm.sales.Cart;

import java.util.Objects;

/**
 * A customer who interacts with the farmer's business.
 * <p>
 * Keeps a record of the customer's information.
 * @multistage
 * @stage0
 * @stage1
 */
public class Customer {

    private final Cart cart;
    private int phone;
    private String name;
    private String address;

    /**
     * Create a new customer instance with their details.
     * @param name The name of the customer.
     * @param phoneNumber The customer's phone number.
     * @param address The address of the customer.
     * @requires That the name and address is non-empty.<br>That the phone number is a positive number.<br>That the name and address are stripped of trailing whitespaces.
     */
    public Customer(String name, int phoneNumber, String address) {
        this.name = name;
        this.phone = phoneNumber;
        this.address = address;
        this.cart = new Cart();
    }

    /**
     * Retrieve the name of the customer.
     * @return The customers name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Update the current name of the customer with a new one.
     * @param newName The new name to override the current name.
     * @requires That the name is non-empty and that its stripped of trailing whitespaces.
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Retrieve the phone number of the customer.
     * @return The customer's phone number.
     */
    public int getPhoneNumber() {
        return this.phone;
    }

    /**
     * Set the current phone number of the customer to be newPhone.
     * @param newPhone The phone number to override the current phone number.
     * @requires The phone number is a positive number.
     */
    public void setPhoneNumber(int newPhone) {
        this.phone = newPhone;
    }


    /**
     * Retrieve the address of the customer.
     * @return The customer address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Set the current address of the customer to be newAddress.
     * @param newAddress The address to override the current address.
     * @requires That the address is non-empty and that its stripped of trailing whitespaces.
     */
    public void setAddress(String newAddress) {
        this.address = newAddress;
    }

    /**
     * Retrieves the customers cart.
     * @stage1
     * @return Their shopping cart.
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * Returns a string representation of this customer class. The representation contains the name of the customer, followed by their phone number and address separated by ' | '.
     * <p>
     * <b>Example output: </b>
     * <p>
     * <pre>{@code new Customer("James Smith", 12345632, "1 Second Street").toString()}</pre>
     * <pre style="color:#00CC00">"Name: James Smith | Phone Number: 12345632 | Address: 1 Second Street"</pre>
     * @return The formatted string representation of the customer.
     */
    @Override
    public String toString() {
        return String.format("Name: %s | Phone Number: %s | Address: %s", name, phone, address);
    }

    /**
     * Determines whether the provided object is equal to this customer instance. 
     * <br>
     * For customers, equality is defined by having the same phone number and name; addresses are 
     * not considered.
     * <br><strong>Note that customer names are <em>case sensitive.</em></strong> That is,
     * a customer called "Emily Lee" is <em>not</em> equal to a customer called "emily lee".
     * @stage1
     * @param obj The object with which to compare
     * @return true if the other object is a customer with the same phone number and name as the current customer.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Customer other)) {
            return false;
        }
        return other.getPhoneNumber() == getPhoneNumber() && other.getName().equals(getName());
    }

    /**
     * A hashcode method that respects the {@link Customer#equals(Object)} method.
     * @stage1
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, phone);
    }
}