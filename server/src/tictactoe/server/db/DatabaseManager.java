/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server.db;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import tictactoe.server.models.Player;

/**
 *
 * @author Hassan
 */
public class DatabaseManager {
     
    private Connection connection;
    private Statement statment;
    private ResultSet resultSet;
    private Vector<Player> usersVecDetails = new Vector();
    
    public DatabaseManager() throws ClassNotFoundException, SQLException {
        // Here to establish the connecection once creating an instance.
        establishConnection();
    }
    
    private void establishConnection() throws ClassNotFoundException, SQLException {
        try {
            // to start the connection;
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_base",
                    "abdallah","root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Player> getAllPlayers() throws SQLException {
        ArrayList<Player> allPlayers = new ArrayList<>();
        
        try {
            establishConnection();
            statment = connection.createStatement();
            resultSet = statment.executeQuery("SELECT * FROM player;");
            
            while (resultSet.next()) {
                Player tempPlayer = new Player();
                
                tempPlayer.setId(resultSet.getInt("id"));
                tempPlayer.setFirstName(resultSet.getString("first_name"));
                tempPlayer.setLastName(resultSet.getString("last_name"));
                tempPlayer.setEmail(resultSet.getString("email"));
                tempPlayer.setImg(resultSet.getString("image"));
                tempPlayer.setPoints(resultSet.getInt("points"));
                
                allPlayers.add(tempPlayer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        statment.close();
        connection.close();
        
        return allPlayers;
    }


}
