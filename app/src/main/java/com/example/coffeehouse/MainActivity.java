package com.example.coffeehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

//class that holds coffee details
class Coffee {
    StringBuffer type;
    StringBuffer topping;
    int quantity;
    int basePrice;
    int curPrice;

    Coffee() {
        quantity = basePrice = curPrice = 0;
    }

    @Override
    public String toString() {
        return "ITEM: " + this.type + "\n" +
                "TOPPING: " + this.topping + "\n" +
                "QUANTITY: " + this.quantity + "\n" +
                "CURRENT PRICE: ₹" + this.curPrice + "\n";
    }
}

public class MainActivity extends AppCompatActivity {
    Coffee c = new Coffee();
    //holds the list of items ordered by a particular customer
    ArrayList<Coffee> list = new ArrayList<>();

    //this method is triggered right after the app starts up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * opens up another activity when the VIEW CART button
     * is clicked
     */
    public void openList(View view) {
        String priceMsg = "";
        int totalPrice = 0;

        for (int i = 0; i < list.size(); i++) {
            priceMsg += list.get(i);
            priceMsg += "\n";
            totalPrice += list.get(i).curPrice;
        }
        priceMsg += "\nTOTAL: ₹" + totalPrice;

        Intent intent = new Intent(this, DisplayList.class);
        intent.putExtra("key", priceMsg);
        startActivity(intent);
    }

    /**
     * This method is called when the + button is clicked
     */
    public void increment(View view) {
        if (c.quantity >= 100) {
            //Show an error message as a toast
            Toast.makeText(this, "Can't order more than 100 coffees :(", Toast.LENGTH_SHORT).show();
            return;
        }
        c.quantity++;
        displayQuantity(c.quantity);
    }

    /**
     * This method is called when the - button is clicked.
     */
    public void decrement(View view) {
        if (c.quantity <= 1) {
            Toast.makeText(this, "Can't order less than 1 coffee LOL!!", Toast.LENGTH_SHORT).show();
            return;
        }
        c.quantity--;
        displayQuantity(c.quantity);
    }

    /**
     * This method is called when the ADD button is clicked.
     */
    public void addOrder(View view) {
        //c = new Coffee();
        CheckBox ColdCoffee = findViewById(R.id.cold_coffee_checkbox);
        boolean isColdCoffee = ColdCoffee.isChecked();
        CheckBox Cappuccino = findViewById(R.id.cappuccino_checkbox);
        boolean isCappuccino = Cappuccino.isChecked();
        CheckBox Espresso = findViewById(R.id.espresso_checkbox);
        boolean isEspresso = Espresso.isChecked();

        if ((!isCappuccino && !isColdCoffee && !isEspresso) || (isCappuccino && isColdCoffee)
                || (isEspresso && isColdCoffee) || (isCappuccino && isEspresso)) {
            Toast.makeText(this, "Please select a single coffee type :(", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isColdCoffee) {
            c.type = new StringBuffer("Cold Coffee");
            c.basePrice = 10;
        }
        if (isCappuccino) {
            c.type = new StringBuffer("Cappuccino");
            c.basePrice = 20;
        }
        if (isEspresso) {
            c.type = new StringBuffer("Espresso");
            c.basePrice = 15;
        }

        //check for toppings
        CheckBox whippedCreamCheckBox = findViewById(R.id.whipped_cream_checkbox);
        boolean hasWhippedCream = whippedCreamCheckBox.isChecked();

        CheckBox chocolateCheckBox = findViewById(R.id.chocolate_checkbox);
        boolean hasChocolate = chocolateCheckBox.isChecked();

        if (hasChocolate && hasWhippedCream) {
            Toast.makeText(this, "Can't select two toppings at once :(", Toast.LENGTH_SHORT).show();
            return;
        }

        if (hasWhippedCream) {
            c.topping = new StringBuffer("Whipped Cream");
            c.basePrice += 1;
        } else if (hasChocolate) {
            c.topping = new StringBuffer("Chocolate");
            c.basePrice += 2;
        } else {
            c.topping = new StringBuffer("No Topping");
        }

        if (c.quantity <= 0) {
            Toast.makeText(this, "Can't order less than 1 coffee!!", Toast.LENGTH_SHORT).show();
            return;
        }

        c.curPrice = calculatePrice();

        //add the current item to the list
        list.add(c);

        //refresh the checkboxes and quantity
        ColdCoffee.setChecked(false);
        Cappuccino.setChecked(false);
        Espresso.setChecked(false);
        whippedCreamCheckBox.setChecked(false);
        chocolateCheckBox.setChecked(false);

        Toast.makeText(this, "Order Added", Toast.LENGTH_SHORT).show();

        c = new Coffee();
        displayQuantity(c.quantity);

    }

    /**
     * This method is called when the ORDER button is clicked.
     */
    public void submitOrder(View view) {

        if (list.isEmpty()) {
            Toast.makeText(this, "Please add an item before clicking ORDER!!", Toast.LENGTH_SHORT).show();
            return;
        }

        //to get name of customer
        EditText nameField = findViewById(R.id.name_field);
        String name = nameField.getText().toString();

        //generate the order receipt
        String priceMsg = createOrderSummary(name);
        list.clear();

        //send out the receipt to an email app using intent
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Coffee order for " + name);
        intent.putExtra(Intent.EXTRA_TEXT, priceMsg);

        //check if there is an app on the device to handle this activity or not
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Calculates the price of the order
     *
     * @return total price
     */
    private int calculatePrice() {
        return c.quantity * c.basePrice;
    }

    /**
     * Creates an order summary
     *
     * @param name The customer name
     * @return order summary priceMsg
     */
    private String createOrderSummary(String name) {
        int totalPrice = 0;
        String priceMsg = "NAME: " + name + "\n" + "\n";

        for (int i = 0; i < list.size(); i++) {
            priceMsg += list.get(i);
            priceMsg += "\n";
            totalPrice += list.get(i).curPrice;
        }

        priceMsg += "\nTOTAL: ₹" + totalPrice;
        priceMsg += "\nGRAND TOTAL(INCLUDING TAXES): ₹" + String.format("%.2f", totalPrice * 1.18);

        return priceMsg;
    }

    /**
     * This method displays the given quantity value on the screen
     *
     * @param number the current quantity of the item to be ordered
     */
    private void displayQuantity(int number) {
        TextView quantityTextView = findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }

    /**
     * This method is invoked when the RESET button is clicked
     */
    public void clearList(View view) {
        list.clear();
        EditText et = findViewById(R.id.name_field);
        et.setText("");
        displayQuantity(0);

        CheckBox ColdCoffee = findViewById(R.id.cold_coffee_checkbox);
        CheckBox Cappuccino = findViewById(R.id.cappuccino_checkbox);
        CheckBox Espresso = findViewById(R.id.espresso_checkbox);
        CheckBox whippedCreamCheckBox = findViewById(R.id.whipped_cream_checkbox);
        CheckBox chocolateCheckBox = findViewById(R.id.chocolate_checkbox);

        if (ColdCoffee.isChecked())
            ColdCoffee.setChecked(false);

        if (Cappuccino.isChecked())
            Cappuccino.setChecked(false);

        if (Espresso.isChecked())
            Espresso.setChecked(false);

        if (whippedCreamCheckBox.isChecked())
            whippedCreamCheckBox.setChecked(false);

        if (chocolateCheckBox.isChecked())
            chocolateCheckBox.setChecked(false);

    }
}