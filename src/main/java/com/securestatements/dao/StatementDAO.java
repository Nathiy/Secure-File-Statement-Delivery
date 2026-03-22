package com.securestatements.dao;

import com.securestatements.model.Statement;
import com.securestatements.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatementDAO {

    // Save statement
    public void addStatement(Statement statement) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO statements(customer_id, file_path) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, statement.getCustomerId());
            ps.setString(2, statement.getFilePath());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get statement by ID
    public Statement getStatementById(int id) {

        Statement statement = null;

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM statements WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                statement = new Statement();

                statement.setId(rs.getInt("id"));
                statement.setCustomerId(rs.getInt("customer_id"));
                statement.setFilePath(rs.getString("file_path"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return statement;
    }

    // Get statements for a customer
    public List<Statement> getStatementsByCustomer(int customerId) {

        List<Statement> statements = new ArrayList<>();

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM statements WHERE customer_id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, customerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Statement statement = new Statement();

                statement.setId(rs.getInt("id"));
                statement.setCustomerId(rs.getInt("customer_id"));
                statement.setFilePath(rs.getString("file_path"));

                statements.add(statement);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return statements;
    }

    // Delete statement
    public void deleteStatement(int id) {

        try {

            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM statements WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
