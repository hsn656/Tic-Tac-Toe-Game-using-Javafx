/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server.db;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import tictactoe.server.models.Game;
import tictactoe.server.models.Game.Status;
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
    
     public Player signUp(String first_name, String last_name, String email, String password) throws SQLException, ClassNotFoundException {
        Player newPlayer = null;
        
        if (isEmailExists(email)) {
            System.out.println("Your email is already registered..");
        } else {
            establishConnection();
            
            PreparedStatement preparedStatment = connection.prepareStatement("INSERT INTO player (first_name, last_name, email, password) VALUES (?, ?, ?, ?);");
            preparedStatment.setString(1, first_name);
            preparedStatment.setString(2, last_name);
            preparedStatment.setString(3, email);
            preparedStatment.setString(4, password);
            preparedStatment.executeUpdate();
            
            newPlayer = getLastPlayer();
            
            preparedStatment.close();
            connection.close();
        }
        return newPlayer;
    }
// sign in by kelany------
    public Player signIn(String email, String password) {
        Player playerSignIn = null;
        if (email != null && password != null) {
            try {
                establishConnection();
                statment = connection.createStatement();
                resultSet = statment.executeQuery("SELECT * FROM player WHERE email='" + email + "' AND password='" + password + "';");
                
                if (resultSet.first() == true) {
                    System.out.println("Login successed..");
                    playerSignIn = new Player();
                    playerSignIn.setId(resultSet.getInt("id"));
                    playerSignIn.setFirstName(resultSet.getString("first_name"));
                    playerSignIn.setLastName(resultSet.getString("last_name"));
                    playerSignIn.setEmail(resultSet.getString("email"));
                    playerSignIn.setImg(resultSet.getString("image"));
                    playerSignIn.setPoints(resultSet.getInt("points"));
                    statment.close();
                    connection.close();
                    return playerSignIn;
                } else {
                    statment.close();
                    connection.close();
                    System.out.println("Login failed.");
                    return playerSignIn;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Empty fields..");
            return playerSignIn;
        }
        return playerSignIn;
    }
//updatePlayerScore
    public Player updatePlayerScore(Player player) throws ClassNotFoundException {
        PreparedStatement pst;
        int playerPoints = player.getPoints();
        try {
            establishConnection();
            player.setPoints(player.getPoints());
            
            pst = connection.prepareStatement("UPDATE player SET points=? WHERE id=?");
            pst.setInt(1, player.getPoints());
            pst.setInt(2, player.getId());
            
            pst.executeUpdate();
            pst.close();
            connection.close();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return player;
    }
    
    public boolean isEmailExists(String email) throws SQLException {
        
        try {
            establishConnection();
            statment = connection.createStatement();
            resultSet = statment.executeQuery("SELECT email FROM player WHERE email='" + email + "';");

            // The email is in the DB.
            if (resultSet.first() == true) {
                statment.close();
                connection.close();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        statment.close();
        connection.close();
        return false;
    }

    public Player getLastPlayer() {
        Player lastPlayer = new Player();
        
        try {
            establishConnection();
            statment = connection.createStatement();
            resultSet = statment.executeQuery("SELECT * FROM player ORDER BY id DESC LIMIT 0, 1");
            
            if (resultSet.first()) {
                int newUserId = Integer.parseInt(resultSet.getString("id"));
                lastPlayer.setId(newUserId);
                lastPlayer.setFirstName(resultSet.getString("first_name"));
                lastPlayer.setLastName(resultSet.getString("last_name"));
                lastPlayer.setPoints(0);
                lastPlayer.setEmail(resultSet.getString("email"));
                lastPlayer.setCurrentGame(null);
            } else {
                lastPlayer = null;
                throw new SQLException("Getting the last user failed");
            }
            
            statment.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return lastPlayer;
    }

    public Game getTerminatedGame(int firstPlayerId, int secondPlayerId) throws SQLException {
        Game terminatedGame = null;
        
        try {
            establishConnection();
            statment = connection.createStatement();
            resultSet = statment.executeQuery("select * from game where player1_id ='" + firstPlayerId
                    + "' and player2_id ='" + secondPlayerId + "' and session_status='terminated';");
            if (resultSet.last()) {
                terminatedGame = new Game(null, null);
                Status gameStatus = Status.valueOf(resultSet.getString("session_status"));
                String coordinatesDB = resultSet.getString("coordinates");
                int playerXId = resultSet.getInt("player1_id");
                int playerYId = resultSet.getInt("player2_id");
                int gameId = resultSet.getInt("id");
                JsonObject request = JsonParser.parseString(coordinatesDB).getAsJsonObject();
                terminatedGame.setGameStatus(gameStatus);
                terminatedGame.setGameCoordinates(request);
                terminatedGame.setPlayerXId(playerXId);
                terminatedGame.setPlayerOId(playerYId);
                terminatedGame.setGameId(gameId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return terminatedGame;
    }

    public boolean insertGame(Game gameToSave) throws ClassNotFoundException {
        boolean success = false;
        Integer playerXId = gameToSave.getPlayerX().getId();
        Integer playerOId = gameToSave.getPlayerO().getId();
        String gameStatus = gameToSave.getStatus();
        String coordinatesToSave = gameToSave.getCoordinates().toString();
        
        try {
            establishConnection();
            
            PreparedStatement preparedStatment = connection.prepareStatement("INSERT INTO game (player1_id, player2_id, session_status, coordinates) VALUES (?, ?, ?, ?);");
            preparedStatment.setString(1, playerXId.toString());
            preparedStatment.setString(2, playerOId.toString());
            preparedStatment.setString(3, gameStatus);
            preparedStatment.setString(4, coordinatesToSave);
            preparedStatment.executeUpdate();
            
            preparedStatment.close();
            connection.close();
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return success;
    }

    public boolean updateGame(Game gameToUpdate) {
        boolean updated = false;
        PreparedStatement pst;
        int playerXId = gameToUpdate.getPlayerX().getId();
        int playerOId = gameToUpdate.getPlayerO().getId();
        int gameId = gameToUpdate.getGameId();
        String sessionStatus = gameToUpdate.getGameStatus().toString();
        String coordinates = gameToUpdate.getCoordinates().toString();
        
        try {
            establishConnection();
            
            pst = connection.prepareStatement("UPDATE game SET player1_id = ?, player2_id = ?, session_status = ?, coordinates = ? WHERE id = ?;");
            pst.setInt(1, playerXId);
            pst.setInt(2, playerOId);
            pst.setString(3, sessionStatus);
            pst.setString(4, coordinates);
            pst.setInt(5, gameId);
            
            pst.executeUpdate();
            pst.close();
            updated = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return updated;
    }


}
