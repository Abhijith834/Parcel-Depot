package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates data loading, managing customers/parcels, fee calculations, logging, and reporting.
 */
public class Manager {
    private final QueueOfCustomers customerQueue;
    private final ParcelMap parcelMap;
    private final Worker worker;
    private final List<String> processedParcels;

    public Manager() {
        customerQueue = new QueueOfCustomers();
        parcelMap = new ParcelMap();
        worker = new Worker();
        processedParcels = new ArrayList<>();
    }

    public QueueOfCustomers getCustomerQueue() {
        return customerQueue;
    }

    public ParcelMap getParcelMap() {
        return parcelMap;
    }

    public Worker getWorker() {
        return worker;
    }

    public List<String> getProcessedParcels() {
        return processedParcels;
    }

    /**
     * Loads customers from a file (format: "Name,ParcelID").
     */
    public void loadCustomers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int seqCounter = 1;
            int count = 0;
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",", 2);
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String parcelID = parts[1].trim().toUpperCase();

                    Customer c = new Customer(seqCounter++, name, parcelID);
                    customerQueue.enqueue(c);
                    Log.getInstance().addEntry("Loaded Customer: " + c);
                    System.out.println("Loaded Customer: " + c);
                    count++;
                } else {
                    System.err.println("Invalid customer entry: " + line);
                }
            }
            System.out.println("Total Customers Loaded: " + count);
        } catch (IOException e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    /**
     * Loads parcels from a file (format: "ParcelID,Length,Width,Height,Weight,Days").
     */
    public void loadParcels(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int count = 0;
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s*,\\s*");
                if (parts.length == 6) {
                    String pid = parts[0].toUpperCase();
                    double length = Double.parseDouble(parts[1]);
                    double width = Double.parseDouble(parts[2]);
                    double height = Double.parseDouble(parts[3]);
                    double weight = Double.parseDouble(parts[4]);
                    int days = Integer.parseInt(parts[5]);

                    Parcel p = new Parcel(pid, length, width, height, weight, days);
                    parcelMap.putParcel(p);
                    Log.getInstance().addEntry("Loaded Parcel: " + p);
                    System.out.println("Loaded Parcel: " + p);
                    count++;
                } else {
                    System.err.println("Invalid parcel entry: " + line);
                }
            }
            System.out.println("Total Parcels Loaded: " + count);
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading parcels: " + e.getMessage());
        }
    }

    /**
     * Processes the next customer in the queue:
     * Removes them from the queue, finds their parcel, calculates fees,
     * removes the parcel from the map, logs everything, writes a report entry.
     */
    public void processNextCustomer() {
        if (!customerQueue.isEmpty()) {
            Customer c = customerQueue.dequeue();
            System.out.println("Processing Customer: " + c);

            String pid = c.getDesiredParcelID().toUpperCase();
            Parcel p = parcelMap.getParcel(pid);
            if (p == null) {
                String errorMsg = "Parcel " + pid + " not found for " + c.getName();
                Log.getInstance().addEntry(errorMsg);
                System.err.println(errorMsg);
                writeReport("Failed to process Parcel ID " + pid + " for " + c.getName() + " - Parcel not found.");
                return;
            }
            System.out.println("Found Parcel: " + p);

            double fee = worker.calculateFee(p);
            parcelMap.removeParcel(pid);

            String record = "Processed Parcel ID " + pid +
                    " for " + c.getName() +
                    " | Fee: $" + String.format("%.2f", fee);
            processedParcels.add(record);
            Log.getInstance().addEntry(record);
            writeReport(record + " (Action: Processed via Worker)");
        } else {
            String msg = "No customer left in queue to process.";
            Log.getInstance().addEntry(msg);
            System.out.println(msg);
            writeReport("Attempted to process parcel but no customers in queue.");
        }
    }

    /**
     * Collects a parcel directly from the Customer tab,
     * calculates fees, removes the parcel, and logs the action.
     */
    public boolean collectParcel(String customerName, String parcelID) {
        String pid = parcelID.toUpperCase();
        if (parcelMap.containsParcel(pid)) {
            Parcel p = parcelMap.getParcel(pid);
            double fee = worker.calculateFee(p);
            parcelMap.removeParcel(pid);

            String record = "Collected Parcel ID " + pid +
                    " by " + customerName +
                    " | Fee: $" + String.format("%.2f", fee);
            processedParcels.add(record);
            Log.getInstance().addEntry(record);
            writeReport(record + " (Action: Collected via Customer)");
            return true;
        }
        String errorMsg = "Parcel " + pid + " not found for collection by " + customerName;
        Log.getInstance().addEntry(errorMsg);
        System.err.println(errorMsg);
        writeReport("Failed to collect Parcel ID " + pid + " by " + customerName + " - Parcel not found.");
        return false;
    }

    /**
     * Appends a report entry to report.txt with a timestamp.
     */
    private void writeReport(String entry) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String reportEntry = "[" + timestamp + "] " + entry;

        String reportFilePath = "main/resources/report.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(reportFilePath, true))) {
            bw.write(reportEntry);
            bw.newLine();
            System.out.println("Report Entry Added: " + reportEntry);
        } catch (IOException e) {
            System.err.println("Error writing to report.txt: " + e.getMessage());
        }
    }

    /**
     * Returns a formatted string of all customers for display in the GUI.
     */
    public String getCustomerListAsString() {
        if (customerQueue.isEmpty()) return "[No customers in queue]";
        StringBuilder sb = new StringBuilder();
        for (Customer c : customerQueue.getAllCustomers()) {
            sb.append(c).append("\n");
        }
        return sb.toString();
    }

    /**
     * Returns a formatted string of all parcels for display in the GUI,
     * excluding the 'collected' attribute.
     */
    public String getParcelListAsString() {
        if (parcelMap.getAllParcels().isEmpty()) return "[No parcels loaded]";
        StringBuilder sb = new StringBuilder();
        for (Parcel p : parcelMap.getAllParcels()) {
            sb.append(formatParcelForDisplay(p)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formats parcel details without the 'collected' attribute for GUI display.
     */
    private String formatParcelForDisplay(Parcel p) {
        return "Parcel{ID='" + p.getParcelID() + "', LxWxH=" +
                p.getLength() + "x" + p.getWidth() + "x" + p.getHeight() +
                ", weight=" + p.getWeight() +
                ", days=" + p.getDaysInDepot() + "}";
    }

    /**
     * Returns a formatted string of all processed parcels for display in the GUI.
     */
    public String getProcessedListAsString() {
        if (processedParcels.isEmpty()) return "[No parcels processed yet]";
        StringBuilder sb = new StringBuilder();
        for (String record : processedParcels) {
            sb.append(record).append("\n");
        }
        return sb.toString();
    }

    /**
     * Main method for console-based testing.
     */
    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.loadCustomers("main/resources/customers.csv");
        manager.loadParcels("main/resources/parcels.csv");

        while (!manager.getCustomerQueue().isEmpty()) {
            manager.processNextCustomer();
        }

        Log.getInstance().writeToFile("main/resources/eventsLog.txt");
        System.out.println("All customers processed. Log written to eventsLog.txt.");
    }
}
