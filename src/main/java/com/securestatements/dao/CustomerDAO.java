package com.securestatements.dao;

import com.securestatements.model.Customer;
import com.securestatements.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Create Customer
    public void addCustomer(Customer customer) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO customers(name, email) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get Customer by ID
    public Customer getCustomerById(int id) {

        Customer customer = null;

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM customers WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                customer = new Customer();

                customer.setId(rs.getInt("id"));
                customer.setFullName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customer;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {

        List<Customer> customers = new ArrayList<>();

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM customers";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Customer customer = new Customer();

                customer.setId(rs.getInt("id"));
                customer.setFullName(rs.getString("name"));
                customer.setEmail(rs.getString("email"));

                customers.add(customer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    // Delete customer
    public void deleteCustomer(int id) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM customers WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
