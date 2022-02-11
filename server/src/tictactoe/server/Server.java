/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import tictactoe.server.models.Game;
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
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(new User(socket));
                clientThread.start();
                clientThreads.add(clientThread);
            } catch (IOException ex) {
                app.guiLog("issue has been fitched with server socket.");
            }
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
    
    public void addNewOfflinePlayer(Player player) {
        offlinePlayers.put(player.getId(), new User(player));
        sortedOfflinePlayersbyPoints.add(player);
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
        
         @Override
        public void run() {
            app.guiLog("server is listening");
            try {
                while (true) {
                    String line = dataInputStream.readUTF();
                    if (line != null) {
                        JsonObject request = JsonParser.parseString(line).getAsJsonObject();
                        app.guiLog("Incoming JSON:\t" + line);
                        if (request.get("type").getAsString().equals("signout")) {

                            app.guiLog("user" + user.toString() + " player:" + user.player + " logging off");

                            handleClosedPlayer();
                            break;
                        } else {
                            jsonHandler.handle(request, user);
                        }
                    }
                }
            } catch (IOException ex) {
                app.guiLog("NO DATA.");
                handleClosedPlayer();
            }
        }

        private void handleClosedPlayer() {
            if (user.player != null) {
                if (user.player.getCurrentGame() != null) {
                    handleTerminatedGame(user);
                }
                removeFromOnlinePlayers(user.player.getId()); // call here
            }
            setPlayerList();
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
    
    public void handleTerminatedGame(User user) {
        System.out.print("handleTerminatedGame function is not implemented yet");
    }
    public void addToOnlinePlayers(int id, User newUser) {
        User user = offlinePlayers.remove(id);
        newUser.player.setOnline(true);
        onlinePlayers.put(id, newUser);
        sortedOfflinePlayersbyPoints.remove(user.player);
        sortedOnlinePlayersbyPoints.add(newUser.player);
        newUser.player.setOnline(true);
        sendUpdatedPlayerList();
        app.guiLog("New player has been added to the online players list");
        setChatPlayerStatus("online", user.getPlayer().getFirstName());
    }
   public User getOnlinePlayerById(int id) {
        return onlinePlayers.get(id);
    }

    
    public void removeFromOnlinePlayers(int id) {
        User user = onlinePlayers.remove(id);
        user.socket = null;
        offlinePlayers.put(id, user);
        sortedOnlinePlayersbyPoints.remove(user.player);
        sortedOfflinePlayersbyPoints.add(user.player);
        user.player.setOnline(false);
        sendUpdatedPlayerList();
        setChatPlayerStatus("offline", user.getPlayer().getFirstName());
    }
    public void sendUpdatedPlayerList() {
        setPlayerList();
        /* update server gui player list */
        JsonObject data = new JsonObject();
        JsonObject response = new JsonObject();
        response.addProperty("type", "update-player-list");
        response.add("data", data);
        JsonArray onlineUsers = getSortedOnlinePlayersAsJson();
        JsonArray offlineUsers = getSortedOfflinePlayersAsJson();
        data.add("online-players", onlineUsers);
        data.add("offline-players", offlineUsers);
        sendToAllOnlinePlayers(response);
    }
    
    
    public void sendToAllOnlinePlayers(JsonObject req) {
        onlinePlayers.forEach((key, value) -> {
            try {
                app.guiLog("k:" + key + " v:" + value);
                value.dataOutputStream.writeUTF(req.toString());
            } catch (Exception ex) {
                app.guiLog("Issue fitched with writing into the output stream");
            }
        });
    }

    public void setChatPlayerStatus(String status, String playerName) {
        // [player is now online.]
        // [player is now offline.]
        JsonObject request = new JsonObject();
        JsonObject data = new JsonObject();
        request.add("data", data);
        request.addProperty("type", "global_chat_message");
        data.addProperty("sender", "");

        if (status.equals("offline")) {
            data.addProperty("message", "[" + playerName + " is now offline.]");
        } else if (status.equals("online")) {
            data.addProperty("message", "[" + playerName + " is now online.]");
        }

        sendToAllOnlinePlayers(request);
    }

    
}
