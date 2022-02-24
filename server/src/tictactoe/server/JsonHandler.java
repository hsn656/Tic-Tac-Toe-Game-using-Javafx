/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.SQLException;
import tictactoe.server.Server.User;
import tictactoe.server.db.DatabaseManager;
import tictactoe.server.models.Game;
import tictactoe.server.models.Player;

/**
 *
 * @author Hassan
 */
public class JsonHandler {

    private DatabaseManager databaseManager;
    private final Server server;

    public JsonHandler(Server server) {
        this.server = server;
        try {
            this.databaseManager = server.getDatabaseManager();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void handle(JsonObject request, User user) throws IOException {
        String requestType = request.get("type").getAsString();

        JsonObject requestData = request.getAsJsonObject("data");
        JsonObject response = null;
        switch (requestType) {
            case "signup":
                response = handleSignup(requestData, user);
                break;
            case "signin":
                response = handleSignin(requestData, user);
                break;
            case "invitation":
                response = handleInvitation(requestData, user);
                break;
            case "accept-invitation":
                response = handleInvitationAccept(requestData, user);
                break;
            case "decline-invitation":
                //{"type": "decline-invitation", "data":{"inviting_player_id": 123}}
                break;
            case "global_chat_message":
                server.sendToAllOnlinePlayers(request);
                response = null;
                break;
            case "game-message":
                response = handleGameMessage(request, user);
                break;
            case "game-move":
                response = handleGameMove(request, user);
                break;
            case "pause-game":
                handlePauseGame(requestData, user);
                break;
        }
    }

    private JsonObject handleSignup(JsonObject requestData, User user) throws IOException {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.add("data", data);

        String firstName = requestData.get("firstName").getAsString();
        String lastName = requestData.get("lastName").getAsString();
        String email = requestData.get("email").getAsString();
        String password = requestData.get("password").getAsString();
        {
            try {
                Player player = null;
                try {
                    player = databaseManager.signUp(firstName, lastName, email, password);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                if (player != null) {
                    response.addProperty("type", "signup-success");
                    server.addNewOfflinePlayer(player);
                    server.setPlayerList();
                } else {
                    response.addProperty("type", "signup-error");
                }
                user.getDataOutputStream().writeUTF(response.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        return response;
    }

    private JsonObject handleSignin(JsonObject requestData, User user) {
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.add("data", data);

        String email = requestData.get("email").getAsString();
        String password = requestData.get("password").getAsString();
        Player player = databaseManager.signIn(email, password);
        if (player == null) {
            response.addProperty("type", "signin-error");
            data.addProperty("msg", "wrong email or password");
            try {
                user.getDataOutputStream().writeUTF(response.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            if (server.getOnlinePlayerById(player.getId()) != null) {
                response.addProperty("type", "signin-error");
                data.addProperty("msg", "You are logged in from another device");
                try {
                    user.getDataOutputStream().writeUTF(response.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
            user.setPlayer(player);
            response.addProperty("type", "signin-success");
            data.add("my-data", player.asJson());
            try {
                user.getDataOutputStream().writeUTF(response.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            server.addToOnlinePlayers(player.getId(), user);
            server.setPlayerList();
        }
        return null;
    }

    private JsonObject handleInvitation(JsonObject requestData, User user) {
        User opponentUser = server.getOnlinePlayerById(requestData.get("invited_player_id").getAsInt());
        if (opponentUser == null) {
            return null;
        }
        if (opponentUser.getPlayer().isOnline() && opponentUser.getPlayer().getCurrentGame() == null) {
            JsonObject response = new JsonObject();
            JsonObject data = new JsonObject();
            response.add("data", data);
            response.addProperty("type", "invitation");

            data.addProperty("inviter_player_id", user.getPlayer().getId());
            data.addProperty("inviter_player_name", user.getPlayer().getFirstName());
            try {
                opponentUser.getDataOutputStream().writeUTF(response.toString());
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }
        return null;
    }

    private JsonObject handleInvitationAccept(JsonObject requestData, User user) {
        User invitingPlayer = server.getOnlinePlayerById(requestData.get("inviting_player_id").getAsInt());

        if (invitingPlayer.getPlayer().isOnline() && invitingPlayer.getPlayer().getCurrentGame() == null) {
            JsonObject response = new JsonObject();
            JsonObject data = new JsonObject();
            response.add("data", data);
            response.addProperty("type", "invitation-accepted");

            data.addProperty("invited_player_id", user.getPlayer().getId());
            data.addProperty("invited_player_name", user.getPlayer().getFirstName());

            try {
                invitingPlayer.getDataOutputStream().writeUTF(response.toString());
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            Game game = null;
            try {
                game = databaseManager.getTerminatedGame(
                        user.getPlayer().getId(), invitingPlayer.getPlayer().getId());
                if (game == null) {
                    game = databaseManager.getTerminatedGame(invitingPlayer.getPlayer().getId(),
                            user.getPlayer().getId());
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            if (game == null) {
                game = new Game(invitingPlayer.getPlayer(), user.getPlayer());
            } else {
                game.setGameStatus(Game.Status.inProgress);
                game.setPlayerX(server.getOnlinePlayerById(game.getPlayerXId()).getPlayer());
                game.setPlayerO(server.getOnlinePlayerById(game.getPlayerOId()).getPlayer());
                JsonObject terminatedGameResponse = new JsonObject();
                terminatedGameResponse.addProperty("type", "terminated-game-data");

                JsonObject terminatedGameData = new JsonObject();
                terminatedGameResponse.add("data", terminatedGameData);
                terminatedGameData.add("game-coordinates", game.getGameCoordinates());
                terminatedGameData.addProperty("playerX_id", game.getPlayerXId());
                terminatedGameData.addProperty("playerO_id", game.getPlayerOId());
                try {
                    user.getDataOutputStream().writeUTF(terminatedGameResponse.toString());
                    invitingPlayer.getDataOutputStream().writeUTF(terminatedGameResponse.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            invitingPlayer.getPlayer().setCurrentGame(game);
            user.getPlayer().setCurrentGame(game);
        }
        return null;
    }
    private JsonObject handleGameMessage(JsonObject request, User user) {
        if (user.getPlayer().getCurrentGame() != null) {
            try {
                Game game = user.getPlayer().getCurrentGame();
                Player opponentPlayer = game.getPlayerX().getId() == user.getPlayer().getId()
                        ? game.getPlayerO() : game.getPlayerX();
                server.getOnlinePlayerById(opponentPlayer.getId()).getDataOutputStream()
                        .writeUTF(request.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    private JsonObject handleGameMove(JsonObject request, User user) {
        JsonObject reqData = request.get("data").getAsJsonObject();

        if (user.getPlayer().getCurrentGame() != null) {
            Game game = user.getPlayer().getCurrentGame();
            Game.Position position = Game.Position.valueOf(reqData.get("position").getAsString());
            Game.Move move = Game.Move.valueOf(reqData.get("move").getAsString());

            Player opponentPlayer = move.equals(Game.Move.X) ? game.getPlayerO() : game.getPlayerX();
            game.setNextMove(position, move);

            JsonObject response = new JsonObject();
            JsonObject data = new JsonObject();
            response.add("data", data);
            response.addProperty("type", "game-move");
            data.addProperty("position", position.toString());
            data.addProperty("move", move.toString());
            System.out.println("TEST BEFORE HANDLE MOVE TRY");
            try {
                System.out.println("TEST AFTER HANDLE MOVE TRY");
                server.getOnlinePlayerById(opponentPlayer.getId()).getDataOutputStream().writeUTF(response.toString());
                System.out.println("SERVERSIDE opponentPlayer: " + opponentPlayer);
                /*getDataOutputStream().writeUTF(response.toString()); */
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    private void handlePauseGame(JsonObject requestData, User user) {
        if (user.getPlayer().getCurrentGame() != null) {
            server.handleTerminatedGame(user);
        }
    }

}
