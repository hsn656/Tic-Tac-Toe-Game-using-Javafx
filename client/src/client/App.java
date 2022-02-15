/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.gui.InvitationScreen;
import client.gui.LevelsScreen;
import client.gui.MainScreen;
import client.gui.MultiOnlinePlayers;
import client.gui.PlayWithComputerEasyGameBoardScreen;

import client.gui.SigninScreen;
import client.gui.SignupScreen;
import client.gui.YouWinScreen;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author Hassan
 */
public class App extends Application {

    private HashMap<String, Pane> screens = new HashMap<>();
    private Scene mainScene;
    private Socket s;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private JsonHandler jsonHandler;
    private Stage pStage;
    private Player currentPlayer;
    private double xOffset;
    private double yOffset;
    public static boolean inMultiplayerGame = false;
    public static int opposingPlayerId = -1;
    public static String opposingPlayerName = "";

    public App() {

        addScreens();
        jsonHandler = new JsonHandler(this);
        try {
            s = new Socket("127.0.0.1", 20081);
            dataInputStream = new DataInputStream(s.getInputStream());
            dataOutputStream = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            showAlert("Connection Error.", "Please check your connection and try again.");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (true) {

                        String line = dataInputStream.readUTF();
                        if (line != null) {
                            JsonObject obj = JsonParser.parseString(line).getAsJsonObject();
                            System.out.println(obj);
                            jsonHandler.handle(obj);
                        }
                    }
                } catch (IOException ex) {
                    showAlert("You are disconnected!", "Please check your connection and try again.");
                    setScreen("signin");
                    Platform.exit();
                    ex.printStackTrace();

                }

            }
        }).start();
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        pStage = primaryStage;
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("TIC TAC TOE!");

//        mainScene = new Scene(screens.get("signup"), 1350, 700);
        mainScene = new Scene(screens.get("signin"), 1350, 1200);

        mainScene.getStylesheets().add(getClass().getResource("/css/style.css").toString());
        primaryStage.setScene(mainScene);
        // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        mainScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                makePaneDraggable(primaryStage);
            }
        });
        pStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (inMultiplayerGame) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", "terminated-game");
                    try {
                        dataOutputStream.writeUTF(jsonObject.toString());

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                exit();
            }
        });
    }

    public Pane getScreen(String screen) {
        return screens.get(screen);
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        System.out.println("current player" + this.currentPlayer);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void addScreens() {
        screens.put("signup", new SignupScreen(this));
        screens.put("signin", new SigninScreen(this));
        screens.put("main", new MainScreen(this));
        screens.put("levels", new LevelsScreen(this));
        screens.put("youWin", new YouWinScreen(this));
        screens.put("invitation", new InvitationScreen(this));
        screens.put("multiOnlinePlayers", new MultiOnlinePlayers(this));
        screens.put("playWithComputerEasyGameBoard", new PlayWithComputerEasyGameBoardScreen(this));

//                
    }

    public void showAlert(String title, String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle(title);
                a.setHeaderText("");
                a.setContentText(msg);
                a.show();
            }
        });

    }

    public void setScreen(String screenName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainScene.setRoot(screens.get(screenName));
            }
        });
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void sendInvitation(int playerId) {
        JsonObject request = new JsonObject();
        JsonObject data = new JsonObject();
        request.add("data", data);
        request.addProperty("type", "invitation");
        data.addProperty("invited_player_id", playerId);
        try {
            System.out.println("SENT JSON INVITATION: " + request);
            getDataOutputStream().writeUTF(request.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void makePaneDraggable(Stage primaryStage) {
        screens.forEach((key, value) -> {
            value.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            value.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });
        });
    }

    public void exit() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "signout");
        try {
            dataOutputStream.writeUTF(jsonObject.toString());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Platform.exit();
        System.exit(0);
    }

    public void addPointsLocalGame(int points) {
        JsonObject request = new JsonObject();
        JsonObject data = new JsonObject();
        request.add("data", data);
        request.addProperty("type", "won-local-game");
        data.addProperty("added-points", points);
        try {
            getDataOutputStream().writeUTF(request.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
