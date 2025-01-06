package view;

import model.Manager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

/**
 * Swing-based GUI with:
 *  - Customer tab (Collect Parcel)
 *  - Worker tab (Add Customer, Add Parcel, Process Parcel)
 *  - Text areas for Customer List, Parcel List, and Processed List
 */
public class Gui extends JFrame {
    private final Manager manager;

    private final JButton btnCollectParcel;    // Customer tab
    private final JButton btnAddCustomer;      // Worker tab
    private final JButton btnAddParcel;        // Worker tab
    private final JButton btnProcessParcel;    // Worker tab

    private final JTextArea txtCustomerList;
    private final JTextArea txtParcelList;
    private final JTextArea txtProcessedList;

    private final Color primaryColor = new Color(103, 63, 212);
    private final Color accentColor  = new Color(63, 202, 212);

    public Gui(Manager manager) {
        super("Depot Application");
        this.manager = manager;

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabbedPane = new JTabbedPane();
        UIManager.put("TabbedPane.tabInsets", new Insets(20, 60, 20, 60));

        // Customer tab
        JPanel customerTab = new JPanel(new BorderLayout(5, 5));
        customerTab.setBorder(new EmptyBorder(10, 10, 10, 10));
        btnCollectParcel = new JButton("Collect Parcel");
        styleButton(btnCollectParcel);
        addHoverEffect(btnCollectParcel);

        JPanel customerButtonPanel = new JPanel(new FlowLayout());
        customerButtonPanel.add(btnCollectParcel);
        customerTab.add(customerButtonPanel, BorderLayout.CENTER);

        // Worker tab
        JPanel workerTab = new JPanel(new BorderLayout(5, 5));
        workerTab.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel workerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        btnAddCustomer = new JButton("Add Customer");
        btnAddParcel   = new JButton("Add Parcel");
        btnProcessParcel = new JButton("Process Parcel");

        styleButton(btnAddCustomer);
        styleButton(btnAddParcel);
        styleButton(btnProcessParcel);

        addHoverEffect(btnAddCustomer);
        addHoverEffect(btnAddParcel);
        addHoverEffect(btnProcessParcel);

        workerButtonPanel.add(btnAddCustomer);
        workerButtonPanel.add(btnAddParcel);
        workerButtonPanel.add(btnProcessParcel);

        workerTab.add(workerButtonPanel, BorderLayout.NORTH);

        // Text areas
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        // Customer List
        txtCustomerList = new JTextArea();
        txtCustomerList.setEditable(false);
        JScrollPane scCustomer = new JScrollPane(txtCustomerList);
        JPanel panelCustomer = new JPanel(new BorderLayout(5, 5));
        panelCustomer.add(new JLabel("Customer List", SwingConstants.CENTER), BorderLayout.NORTH);
        panelCustomer.add(scCustomer, BorderLayout.CENTER);

        // Parcel List
        txtParcelList = new JTextArea();
        txtParcelList.setEditable(false);
        JScrollPane scParcel = new JScrollPane(txtParcelList);
        JPanel panelParcel = new JPanel(new BorderLayout(5, 5));
        panelParcel.add(new JLabel("Parcel List", SwingConstants.CENTER), BorderLayout.NORTH);
        panelParcel.add(scParcel, BorderLayout.CENTER);

        // Processed List
        txtProcessedList = new JTextArea();
        txtProcessedList.setEditable(false);
        JScrollPane scProcessed = new JScrollPane(txtProcessedList);
        JPanel panelProcessed = new JPanel(new BorderLayout(5, 5));
        panelProcessed.add(new JLabel("Processed List", SwingConstants.CENTER), BorderLayout.NORTH);
        panelProcessed.add(scProcessed, BorderLayout.CENTER);

        centerPanel.add(panelCustomer);
        centerPanel.add(panelParcel);
        centerPanel.add(panelProcessed);

        workerTab.add(centerPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Customer", customerTab);
        tabbedPane.addTab("Worker", workerTab);
        tabbedPane.setBackground(accentColor);
        tabbedPane.setForeground(Color.WHITE);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void refreshViews() {
        txtCustomerList.setText(manager.getCustomerListAsString());
        txtParcelList.setText(manager.getParcelListAsString());
        txtProcessedList.setText(manager.getProcessedListAsString());
    }

    // Button Listener Hooks
    public void addCollectParcelListener(ActionListener listener) { btnCollectParcel.addActionListener(listener); }
    public void addAddCustomerListener(ActionListener listener)   { btnAddCustomer.addActionListener(listener); }
    public void addAddParcelListener(ActionListener listener)     { btnAddParcel.addActionListener(listener); }
    public void addProcessParcelListener(ActionListener listener) { btnProcessParcel.addActionListener(listener); }

    // Utility Prompts
    public String promptCustomerName() {
        return JOptionPane.showInputDialog(this, "Enter customer name:");
    }

    public String promptParcelID() {
        return JOptionPane.showInputDialog(this, "Enter parcel ID:");
    }

    public double promptDouble(String message) {
        while (true) {
            String input = JOptionPane.showInputDialog(this, message);
            if (input == null) return -1; // user canceled
            try {
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                showError("Invalid numeric input: " + input);
            }
        }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Private Styling
    private void styleButton(JButton button) {
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            private final Color originalColor = button.getBackground();
            private final Color hoverColor = originalColor.brighter();

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }
}
