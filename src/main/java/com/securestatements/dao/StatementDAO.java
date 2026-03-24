
package com.securestatements.dao;

import java.sql.*;

public class StatementDAO {

    private static final String DB = "jdbc:sqlite:statements.db";

    static {
        try (Connection conn = DriverManager.getConnection(DB)) {

            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS statements (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customer_id INTEGER," +
                "customer_name TEXT," +
                "file_path TEXT)"
            );

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int save(int customerId, String customerName, String filePath) throws Exception {

        Connection conn = DriverManager.getConnection(DB);

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO statements(customer_id,customer_name,file_path) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, customerId);
        ps.setString(2, customerName);
        ps.setString(3, filePath);

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();

        return rs.getInt(1);
    }

    public static String getFile(int id) throws Exception {

        Connection conn = DriverManager.getConnection(DB);

        PreparedStatement ps = conn.prepareStatement(
                "SELECT file_path FROM statements WHERE id=?");

        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) return rs.getString("file_path");

        return null;
    }
}
