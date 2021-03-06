package com.example.demo.utility;

import com.example.demo.models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class Database {

    public Database() {
    }

    private Connection connection = null;

    public boolean setConnection() {
        final String url = "jdbc:mysql://eu-cdbr-west-01.cleardb.com/heroku_ae18e19e8feb955?reconnect=true&autoReconnect=true"; // TODO FIX LOGIN
        boolean res = false;
        try {
            connection = DriverManager.getConnection(url, "b9ccce1d85d850", "b4af5854");
            res = true;
            System.out.println("Connection made!");
        } catch (SQLException ioerr) {
            System.out.println(ioerr);
            throw new RuntimeException(ioerr);
        } 
        return res;
    }

    public String createWishList(String name, String description) {
        setConnection();
        String insstr = "INSERT INTO Wishlist(name, description) values (?, ?)";
        PreparedStatement preparedStatement;
        String result = "";
        try {
            // Result bliver brugt til at skaffe det korrekte ID efter at der bliver indsat
            preparedStatement = connection.prepareStatement(insstr, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name.replace("[","").replace("]",""));
            preparedStatement.setString(2, description.replace("[","").replace("]",""));
            preparedStatement.executeUpdate();

            ResultSet column = preparedStatement.getGeneratedKeys();
            if (column.next()) {
                result = column.getString(1);
                System.out.println("Created column " + result);
            }

        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
            return "400";
        }
        System.out.println("Wishlist created successfully");
        return result;
    }
    
    public ArrayList <String[]> getWishlistItems(int id) {
        System.out.println("Start of Wishlist Items");
        setConnection();
        String insstr = "SELECT itemID FROM Combo WHERE wishlistID = ?";
        PreparedStatement preparedStatement;
        
        ArrayList<Integer> items = new ArrayList<>();
        ArrayList<String[]> stringArrayResult = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(insstr);
            preparedStatement.setInt(1, id);

            ResultSet rs = preparedStatement.executeQuery();
             while (rs.next()) {
                 items.add(rs.getInt("itemID"));
             }

            for (int item : items) {
                System.out.println("Entered for each");
                insstr = "SELECT * FROM Items WHERE id = ?";
                preparedStatement = connection.prepareStatement(insstr);
                preparedStatement.setInt(1, item);

               rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String[] result = {rs.getString("id"), rs.getString("name"),  rs.getString("price"), rs.getString("link")};
                    stringArrayResult.add(result);
                }

                
            }

        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
            return null;
        }
        System.out.println("Succesfully fetched wishlist: " + id);
        System.out.println(stringArrayResult);
        return stringArrayResult;
    }

    public String[] getWishlist(String id) {
        setConnection();
        String insstr = "SELECT * FROM Wishlist WHERE ID = ?";
        PreparedStatement preparedStatement;
        try {
            // Result bliver brugt til at skaffe det korrekte ID efter at der bliver indsat
            preparedStatement = connection.prepareStatement(insstr);
            preparedStatement.setInt(1, Integer.parseInt(id));

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // Alt bliver hentet som String[]
                String[] result = {rs.getString("id"), rs.getString("name"),
                        rs.getString("description")
                };
                return result;
            }

        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
            String[] result = {"error"};
            return result;
        }
        // Hvis Wishlist ikke bliver fundet returnere den null
        return null;
    }
    public int addItem (String name, int id, double price,String link) {
        setConnection();
        String insstr = "INSERT INTO Items(name, price, link) values (?, ?, ?)";
        PreparedStatement preparedStatement;
        String result = "";
        try {
            // Result bliver brugt til at skaffe det korrekte ID efter at der bliver indsat
            preparedStatement = connection.prepareStatement(insstr, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setString(3, link);
            System.out.println(insstr);
            preparedStatement.executeUpdate();

            ResultSet column = preparedStatement.getGeneratedKeys();
            if (column.next()) {
                result = column.getString(1);
                System.out.println("Created column " + result);

                insstr = "INSERT INTO Combo(wishlistID, itemID) values (?, ?)";
                preparedStatement = connection.prepareStatement(insstr, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, Integer.parseInt(result));
                preparedStatement.executeUpdate();

            }

        } catch (SQLException err) {
            System.out.println("bad happened:" + err.getMessage());
            return 400;
        }
        System.out.println("good happened");
        System.out.println(result);
        return 200;
    }
}