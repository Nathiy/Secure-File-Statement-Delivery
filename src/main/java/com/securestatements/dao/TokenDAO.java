package com.securestatements.dao;

import java.sql.*;

import com.securestatements.util.DBConnection;

public class TokenDAO {

    public void saveToken(int statementId,
                          String token,
                          long expiry) {

        try{

            Connection conn =
                    DBConnection.getConnection();

            String sql =
                    "INSERT INTO tokens(statement_id,token,expiry_time) VALUES(?,?,?)";

            PreparedStatement ps =
                    conn.prepareStatement(sql);

            ps.setInt(1, statementId);
            ps.setString(2, token);
            ps.setLong(3, expiry);

            ps.executeUpdate();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
