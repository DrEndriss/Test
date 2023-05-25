package com.example.demo.handler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHandler {

    /**
    // user: cc
    // password: m+vbmCwLEFw3XF?7

    public List<String[]> retrieveData(String username, String password) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String[]> data = new ArrayList<>();

        try {
            // Load the JDBC driver class
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Create the connection URL
            String url = "jdbc:sqlserver://amaffmsql517:1433;encrypt=false;databaseName=Kufer_Reporting";

            // Connect to the SQL Server database
            conn = DriverManager.getConnection(url, username, password);

            // Create a statement object for executing SQL queries
            stmt = conn.createStatement();

            // Execute the SQL query to select all data from the qry_OP_mit_Details table
            rs = stmt.executeQuery("SELECT * FROM qry_OP_mit_Details");

            // Get the result from the query
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Create an array for the column headers
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = rsmd.getColumnName(i);
            }
            data.add(headers);

            // Create an array for each row of data
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                data.add(row);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources in reverse order of creation
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }**/

    public List<String[]> executeQuery(String username, String password, int queryNumber) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<String[]> data = new ArrayList<>();

        System.out.println("start");

        try {
            // Load the JDBC driver class
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Create the connection URL
            String url = "jdbc:sqlserver://amaffmsql517:1433;encrypt=false;databaseName=Kufer_Reporting";

            // Connect to the SQL Server database
            conn = DriverManager.getConnection(url, username, password);

            // Create a statement object for executing SQL queries
            stmt = conn.createStatement();

            // Select the query based on the queryNumber
            String query = getQueryByNumber(queryNumber);

            System.out.println(query);

            // Execute the SQL query to select all data from the qry_OP_mit_Details table
            rs = stmt.executeQuery(query);


            // Get the result from the query
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Create an array for the column headers
            String[] headers = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                headers[i - 1] = rsmd.getColumnName(i);
            }
            data.add(headers);

            // Create an array for each row of data
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                data.add(row);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close the resources in reverse order of creation
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    private String getQueryByNumber(int queryNumber) {
        // Define your four pre-existing queries here
        String query1 = "SELECT * FROM qry_OP_mit_Details";
        String query2 = "SELECT * FROM BELEGUNG";
        String query3 = "SELECT * FROM TN";


        // Return the query based on the queryNumber
        switch (queryNumber) {
            case 1:
                return query1;
            case 2:
                return query2;
            case 3:
                return query3;
            default:
                throw new IllegalArgumentException("Invalid query number. Please choose a number between 1 and 3.");
        }
    }
}
