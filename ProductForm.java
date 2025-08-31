package myapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProductForm extends JFrame {
    private JTextField nameField, categoryField, priceField, qtyField, descField;
    private JTable table;
    private DefaultTableModel model;

    public ProductForm() {
        setTitle("Manage Products");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        inputPanel.add(new JLabel("Quantity:"));
        qtyField = new JTextField();
        inputPanel.add(qtyField);

        inputPanel.add(new JLabel("Description:"));
        descField = new JTextField();
        inputPanel.add(descField);

        JButton addBtn = new JButton("Add Product");
        JButton delBtn = new JButton("Delete Product");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);

        model = new DefaultTableModel(new String[]{"ID","Name","Category","Price","Quantity","Description"},0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        loadProducts();

        addBtn.addActionListener(e -> addProduct());
        delBtn.addActionListener(e -> deleteProduct());
    }

    private void loadProducts() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("category"),
                    rs.getDouble("price"), rs.getInt("quantity"), rs.getString("description")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error loading products: " + ex.getMessage());
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        String category = categoryField.getText();
        String price = priceField.getText();
        String qty = qtyField.getText();
        String desc = descField.getText();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO products (name, category, price, quantity, description) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setDouble(3, Double.parseDouble(price));
            stmt.setInt(4, Integer.parseInt(qty));
            stmt.setString(5, desc);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,"Product added!");
            loadProducts();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,"Select a product to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM products WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this,"Product deleted!");
            loadProducts();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: " + ex.getMessage());
        }
    }
}
