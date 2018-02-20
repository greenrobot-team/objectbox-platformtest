package com.example.objectbox_platformtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.objectbox_platformtest.model.Customer;
import com.example.objectbox_platformtest.model.MyObjectBox;
import com.example.objectbox_platformtest.model.Order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import timber.log.Timber;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        copyDatabaseFromAssets();

        // init
        BoxStore store = MyObjectBox.builder().androidContext(getApplicationContext()).build();

        doObjectBoxThings(store);
    }

    private void copyDatabaseFromAssets() {
        // copy database file from assets if present
        File filesDir = getFilesDir();
        File targetDir = new File(filesDir, "objectbox/objectbox/");

        // delete old database files
        File[] files = targetDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    throw new RuntimeException("Could not delete old db file");
                }
            }
        }

        //noinspection ResultOfMethodCallIgnored
        targetDir.mkdirs();
        File targetFile = new File(targetDir, "data.mdb");

        try (Source source = Okio.source(getAssets().open("data.mdb"));
             BufferedSink sink = Okio.buffer(Okio.sink(new FileOutputStream(targetFile)))) {
            sink.writeAll(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void doObjectBoxThings(BoxStore store) {
        Box<Customer> customerBox = store.boxFor(Customer.class);
        Box<Order> orderBox = store.boxFor(Order.class);

        Timber.d("Existing customers: %d", customerBox.count());
        Timber.d("Existing orders: %d", orderBox.count());

        List<Customer> customers = customerBox.query().build().find();
        Timber.d("Queried customers: %d", customers.size());
        List<Order> orders = orderBox.query().build().find();
        Timber.d("Queried orders: %d", orders.size());

        // add a new order to each customer
        for (Customer customer : customers) {
            Order order = new Order();
            order.tag = customer.tag;
            order.customer.setTarget(customer);
            orderBox.put(order);
        }

        // add a new customer
        Customer newCustomer = new Customer();
        newCustomer.name = "Fred " + customers.size();
        newCustomer.tag = customers.size();
        newCustomer.size = Double.MAX_VALUE;
        newCustomer.bits = new byte[]{1, 2, 3};

        Order orderNewCustomer = new Order();
        orderNewCustomer.tag = customers.size();
        orderNewCustomer.customer.setTarget(newCustomer);
        newCustomer.orders.add(orderNewCustomer);

        customerBox.put(newCustomer);

        // validate customers
        customers = customerBox.query().build().find();
        byte[] bits = {1, 2, 3};
        for (Customer customer : customers) {
            Timber.d(customer.toString());
            logErrorIfFalse("Name valid", customer.name.startsWith("Fred"));
            logErrorIfFalse("Size matches", customer.size == Double.MAX_VALUE);
            logErrorIfFalse("Bits match", Arrays.equals(customer.bits, bits));
            logErrorIfFalse("Has orders", customer.orders.size() > 0);
        }

        orders = orderBox.query().build().find();
        for (Order order : orders) {
            Timber.d(order.toString());
            Customer customer = order.customer.getTarget();
            logErrorIfFalse("Has customer", customer != null);
            if (customer != null) {
                logErrorIfFalse("Matches customer", order.tag == customer.tag);
            }
        }
    }

    public static void logErrorIfFalse(String message, boolean condition) {
        if (condition) {
            Timber.d("%s: %s", message, true);
        } else {
            Timber.e("%s: %s", message, false);
        }
    }

    public void onClickExportDb(View view) {
        // copy database file from assets if present
        File filesDir = getFilesDir();
        File sourceFile = new File(filesDir, "objectbox/objectbox/data.mdb");

        File targetFile = new File(getExternalFilesDir(null), "data.mdb");

        try (Source source = Okio.source(new FileInputStream(sourceFile));
             BufferedSink sink = Okio.buffer(Okio.sink(new FileOutputStream(targetFile)))) {
            sink.writeAll(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
