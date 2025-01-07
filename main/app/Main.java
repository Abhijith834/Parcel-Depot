package app;

import model.*;
import view.Gui;

import javax.swing.*;

/**
 * Entry point for the Parcel Depot application.
 * Run with "console" argument for console-based processing,
 * or no argument for GUI mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("console")) {
            runConsoleMode();
        } else {
            runGuiMode();
        }
    }

    /**
     * Loads data, processes all customers in queue, writes log to file, then exits.
     */
    private static void runConsoleMode() {
        System.out.println("Running in CONSOLE mode...");

        Manager manager = new Manager();
        manager.loadCustomers("main/resources/customers.csv");
        manager.loadParcels("main/resources/parcels.csv");

        while (!manager.getCustomerQueue().isEmpty()) {
            manager.processNextCustomer();
        }

        Log.getInstance().writeToFile("main/resources/eventsLog.txt");
        System.out.println("All customers processed. Log written to eventsLog.txt.");
    }

    /**
     * Loads data, launches the GUI, and attaches button listeners for user interactions.
     */
    private static void runGuiMode() {
        System.out.println("Running in GUI mode...");

        Manager manager = new Manager();
        manager.loadCustomers("main/resources/customers.csv");
        manager.loadParcels("main/resources/parcels.csv");

        SwingUtilities.invokeLater(() -> {
            Gui gui = new Gui(manager);

            // Customer tab: "Collect Parcel" button
            gui.addCollectParcelListener(e -> {
                String parcelID = gui.promptParcelID();
                if (parcelID == null || parcelID.trim().isEmpty()) {
                    gui.showError("No parcel ID entered. Cancelled.");
                    return;
                }
                String customerName = gui.promptCustomerName();
                if (customerName == null || customerName.trim().isEmpty()) {
                    gui.showError("No customer name entered. Cancelled.");
                    return;
                }

                boolean success = manager.collectParcel(customerName.trim(), parcelID.trim());
                if (success) {
                    gui.showMessage("Parcel " + parcelID + " has been collected by " + customerName + ".");
                } else {
                    gui.showError("Parcel " + parcelID + " not found!");
                }
                gui.refreshViews();
            });

            // Worker tab: "Add Customer"
            gui.addAddCustomerListener(e -> {
                String name = gui.promptCustomerName();
                if (name == null || name.trim().isEmpty()) {
                    gui.showError("No customer name entered.");
                    return;
                }
                String parcelID = gui.promptParcelID();
                if (parcelID == null || parcelID.trim().isEmpty()) {
                    gui.showError("No parcel ID entered.");
                    return;
                }
                if (!manager.getParcelMap().containsParcel(parcelID.trim())) {
                    gui.showError("Parcel ID " + parcelID + " doesn't exist in the parcel list.");
                    return;
                }
                int seqNo = manager.getCustomerQueue().size() + 1;
                Customer c = new Customer(seqNo, name.trim(), parcelID.trim());
                manager.getCustomerQueue().enqueue(c);
                Log.getInstance().addEntry("Worker added new customer: " + c);

                gui.refreshViews();
                gui.showMessage("Customer " + name + " added successfully!");
            });

            // Worker tab: "Add Parcel"
            gui.addAddParcelListener(e -> {
                String pid = gui.promptParcelID();
                if (pid == null || pid.trim().isEmpty()) {
                    gui.showError("No parcel ID entered.");
                    return;
                }
                double length = gui.promptDouble("Enter parcel length:");
                if (length < 0) return;
                double width  = gui.promptDouble("Enter parcel width:");
                if (width < 0) return;
                double height = gui.promptDouble("Enter parcel height:");
                if (height < 0) return;
                double weight = gui.promptDouble("Enter parcel weight:");
                if (weight < 0) return;
                double days   = gui.promptDouble("Enter days in depot:");
                if (days < 0) return;

                Parcel p = new Parcel(pid.trim().toUpperCase(), length, width, height, weight, (int)days);
                manager.getParcelMap().putParcel(p);
                Log.getInstance().addEntry("Worker added new parcel: " + p);

                gui.refreshViews();
                gui.showMessage("Parcel " + pid + " added successfully.");
            });

            // Worker tab: "Process Parcel"
            gui.addProcessParcelListener(e -> {
                if (manager.getCustomerQueue().isEmpty()) {
                    gui.showError("No customers in queue to process.");
                    return;
                }
                manager.processNextCustomer();
                gui.showMessage("Processed the next customer in the queue.");
                gui.refreshViews();
            });

            gui.setVisible(true);
            gui.refreshViews();
        });
    }
}
