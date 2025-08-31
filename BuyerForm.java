package myapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BuyerForm extends JFrame {
    private JTextField nameField, emailField, phoneField, addressField;
    private JTable table;
    private DefaultTableModel model;

    public BuyerForm() {
        setTitle("Manage Buyers");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        inputPanel.add(addressField);

        JButton addBtn = new JButton("Add Buyer");
        JButton delBtn = new JButton("Delete Buyer");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        model = new DefaultTableModel(new String[]{"ID","Name","Email","Phone","Address"},0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        loadBuyers();

        addBtn.addActionListener(e -> addBuyer());
        delBtn.addActionListener(e -> deleteBuyer());
    }

    private void loadBuyers() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM buyers");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                    rs.getString("phone"), rs.getString("address")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error loading buyers: " + ex.getMessage());
        }
    }

    private void addBuyer() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO buyers (name,email,phone,address) VALUES (?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, phoneField.getText());
            stmt.setString(4, addressField.getText());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,"Buyer added!");
            loadBuyers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }

    private void deleteBuyer() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Select a buyer to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM buyers WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,"Buyer deleted!");
            loadBuyers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }
}
