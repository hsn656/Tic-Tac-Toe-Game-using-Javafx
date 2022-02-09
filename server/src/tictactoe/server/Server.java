/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server;

import com.google.gson.JsonArray;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import tictactoe.server.db.DatabaseManager;
import tictactoe.server.models.Player;

/**
 *
 * @author Hassan
 */
public class Server extends Thread {
    
    private ServerSocket serverSocket;

    private final HashMap<Integer, User> onlinePlayers = new HashMap<>();
    private final HashMap<Integer, User> offlinePlayers = new HashMap<>();
    private ServerMain app;
    private DatabaseManager databaseManager;
    private JsonHandler jsonHandler = null;
    public final Set<ClientThread> clientThreads = new HashSet<ClientThread>();

    Comparator<Player> playerComparatorByPoints = (o1, o2) -> {
        int diff = o2.getPoints() - o1.getPoints();
        if (diff == 0) {
            diff = o1.getId() - o2.getId();
        }
        return diff;
    };
    
    private final TreeSet<Player> sortedOnlinePlayersbyPoints = new TreeSet<>(playerComparatorByPoints);
    private final TreeSet<Player> sortedOfflinePlayersbyPoints = new TreeSet<>(playerComparatorByPoints);
   
    
    public Server(ServerMain app) {
        this.app = app;
        try {
            this.databaseManager = new DatabaseManager();
            this.jsonHandler = new JsonHandler(this);
            Collection<Player> players = databaseManager.getAllPlayers();
            sortedOfflinePlayersbyPoints.addAll(players);
            Iterator<Player> iterator = players.iterator();

            while (iterator.hasNext()) {
                Player player = iterator.next();
                offlinePlayers.put(player.getId(), new User(player));
            }
            serverSocket = new ServerSocket(20081);
            setPlayerList();
        } catch (Exception ex) {
            app.guiLog("issue has been fitched with the server connection.");
        }
    }
    
    public void turnOff() {
        for (ClientThread clientThread : clientThreads) {
            clientThread.closeClient();
        }
        try {
            serverSocket.close();
        } catch (IOException ex) {
            app.guiLog("An issue has occurred while trying to turn off the server.");
        }
    }
    
    public void setPlayerList() {
        Platform.runLater(() -> {
            app.clearPlayersListPane();
            app.setPlayersListCounter(0);
            app.addPlayersToList(getSortedOnlinePlayersAsJson(), Color.GREEN);
            app.addPlayersToList(getSortedOfflinePlayersAsJson(), Color.RED);
        });
    }
    
    public JsonArray getSortedOnlinePlayersAsJson() {
        JsonArray players = new JsonArray();
        sortedOnlinePlayersbyPoints.forEach((player) -> {
            players.add(player.asJson());
        });
        return players;
    }
     public JsonArray getSortedOfflinePlayersAsJson() {
        JsonArray players = new JsonArray();
        sortedOfflinePlayersbyPoints.forEach((player) -> {
            players.add(player.asJson());
        });
        return players;
    }
    
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
        
    public class User {

        private Socket socket;
        private Player player;
        private DataOutputStream dataOutputStream;

        public User(Socket socket, Player player) {
            this.socket = socket;
            this.player = player;
            try {
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                app.guiLog("A new player has logged on.");
            } catch (IOException ex) {
                app.guiLog("issue has been fitched with establishing the data output stream.");
            }
        }

        public User() {
        }

        public User(Socket socket) {
            this.socket = socket;
            try {
                app.guiLog("A new connection has been established.");
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                app.guiLog("issue has been fetched with establishing the data output stream.");
            }
        }

        public User(Player player) {
            this.player = player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public Socket getSocket() {
            return socket;
        }

        public Player getPlayer() {
            return player;
        }

        public DataOutputStream getDataOutputStream() {
            return dataOutputStream;
        }

    }

    private class ClientThread extends Thread {

        private final Socket socket;
        private DataInputStream dataInputStream;
        private User user;

        public ClientThread(User user) {
            this.user = user;
            this.socket = user.socket;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
            } catch (Exception ex) {
                app.guiLog("issue has been fitched with establishing the data input stream.");
            }
        }
        
        public void closeClient() {
            try {
                app.guiLog("one of the users has logged off");
                dataInputStream.close();
                user.dataOutputStream.close();
                socket.close();
                stop();

            } catch (IOException ex) {
                app.guiLog("issue has been fetched with closing the data output/input stream.");
            }
        }

        
    }
    
}
