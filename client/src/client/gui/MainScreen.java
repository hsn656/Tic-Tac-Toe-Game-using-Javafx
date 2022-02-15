/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import client.App;
import client.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Abdallah
 */

public class MainScreen extends Pane {

    Comparator<Player> playerComparatorByPoints = (o1, o2) -> {
        int diff = o1.getPoints() - o2.getPoints();
        if (diff == 0) {
            if (o1.getId() < o2.getId()) {
                return 1;
            } else {
                return -1;
            }
        }
        return diff;
    };

    private int playersListCounter;
    private GridPane gridPane;
    private App app;
    private TextArea chatMessageArea, chatTextArea;
    private Player currentPlayer;
    private Label welcome;
    private Label points;
    private Label playerName2;
    private Label points2;

    public MainScreen(App app) {
        this.app = app;
        Button challengeComp = new Button("Vs Computer");
        challengeComp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                app.setScreen("levels");
            }
        });
        challengeComp.setPrefSize(190, 50);
        challengeComp.setStyle("-fx-text-fill: green");
        Button challengePlayer = new Button("Vs Player");
        challengePlayer.setStyle("-fx-text-fill: red");
        challengePlayer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                app.setScreen("playerList");
            }
        });
        challengePlayer.setPrefSize(190, 50);
        challengePlayer.setId("playerButton");
        HBox buttonBox = new HBox(20, challengeComp, challengePlayer);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setLayoutX(43);
        buttonBox.setLayoutY(676);
        Button exit = new Button("EXIT  ;___;  o/");
        exit.setId("ExitFromGame");
        exit.setPrefSize(402, 50);
        exit.setStyle("-fx-text-fill: blue");
        exit.setOnAction((t) -> {
            app.exit();
        });
        HBox hBox = new HBox(exit);
        hBox.setLayoutX(43);
        hBox.setLayoutY(750);
        challengeComp.setId("main_button");
        challengePlayer.setId("main_button");
        exit.setId("main_button");
/////////////////////////////////////////////////////////

        gridPane = new GridPane();
        gridPane.setId("GridMain");
        gridPane.setHgap(30);
        gridPane.setPrefSize(516, 224.0);


        //////////////////////////////////////////////////////////
        
        /////////////////////////////////////////////////////////////////

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setId("scrollPane1");
        scrollPane.setFocusTraversable(false);

        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);

        VBox v = new VBox();
        v.getChildren().add(scrollPane);
        v.setLayoutX(338);
        v.setLayoutY(305);

        playerName2 = new Label();
        playerName2.setLayoutX(200);
        playerName2.setLayoutY(558);
        playerName2.setId("welcome");

        welcome = new Label();
        welcome.setId("welcome");
        welcome.setLayoutX(43);
        welcome.setLayoutY(558);

        Label tictactoe = new Label("Tic Tac Toe");
        tictactoe.setLayoutX(470);
        tictactoe.setLayoutY(2);
        tictactoe.setId("tictactoe");

      
        Image image = new Image(getClass().getResourceAsStream("/images/Tic.png"));
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setLayoutX(446);
        imageView.setLayoutY(68);
        imageView.setFitWidth(306);
        imageView.setFitHeight(225);


        points2 = new Label();
        points2.setLayoutX(200);
        points2.setLayoutY(606);
        points2.setId("welcome");

        points = new Label();
        points.setId("welcome");
        points.setLayoutX(47);
        points.setLayoutY(606);

        //String variable="WELCOME " + ;
        chatTextArea = new TextArea("");
        chatTextArea.setId("ta");
        chatTextArea.setEditable(false);
        chatTextArea.setLayoutX(862);
        chatTextArea.setLayoutY(550);
        chatTextArea.setPrefSize(297, 180);
        chatTextArea.setWrapText(true);

        chatMessageArea = new TextArea("");

        chatMessageArea.setPromptText("Enter your Msg ");
        chatMessageArea.setLayoutX(862);
        chatMessageArea.setLayoutY(750);
        chatMessageArea.setPrefWidth(225.0);
        chatMessageArea.setPrefHeight(38.0);
        chatMessageArea.setMaxWidth(225.0);
        chatMessageArea.setMaxHeight(38.5);
        chatMessageArea.setWrapText(true);

        Button send = new Button();
        send.setText("send");
        send.setId("sendChatMainScreen");
        send.setLayoutX(1101);
        send.setLayoutY(750);
        send.setPrefSize(58, 38);

        send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sendEvent();
            }
        });
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    sendEvent();
                }
            }
        }
        );
        getChildren().addAll(buttonBox, chatMessageArea, chatTextArea, send, v, hBox, welcome, points, playerName2, points2,tictactoe, imageView);
        setId("MainScreenPane");
    }

    public void setPlayersListCounter(int playersListCounter) {
        this.playersListCounter = playersListCounter;
    }

    public void clearPlayersListPane() {
        gridPane.getChildren().clear();
    }

    public void addPlayersToList(JsonArray playerList, String type, Color color) {
        for (int i = 0; i < playerList.size(); i++) {
            JsonObject jsonPlayer = playerList.get(i).getAsJsonObject();
            if (jsonPlayer.get("id").getAsInt() == app.getCurrentPlayer().getId()) {
                /*skips iteration if the player is me*/
                points2.setText(jsonPlayer.get("points").getAsString());
                continue;
            }
            Player player = new Player();
            player.setFirstName(jsonPlayer.get("firstName").getAsString());
            player.setPoints(jsonPlayer.get("points").getAsInt());
            player.setId(jsonPlayer.get("id").getAsInt());
            Button invite2 = new Button("Challenge");
            if (type.equals("online")) {
                invite2.setId("challengeScrolPaneMainScreen");
            } else if (type.equals("offline")) {
                invite2.setId("offlineChallengeScrolPaneMainScreen");
            }
            invite2.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    app.sendInvitation(player.getId());
                }
            });

            Label score2 = new Label(Integer.toString(player.getPoints()));
            Label playerName = new Label(player.getFirstName());
            playerName.setPrefWidth(100);
            Circle cir2 = new Circle(150.0f, 150.0f, 5.f);
            cir2.setFill(color);
            gridPane.add(cir2, 1, playersListCounter);
            gridPane.add(invite2, 9, playersListCounter);
            gridPane.add(score2, 4, playersListCounter);
            gridPane.add(playerName, 2, playersListCounter);
            playersListCounter++;
        }
    }

    public void sendEvent() {
        sendGlobalMsg(chatMessageArea.getText());
        chatMessageArea.clear();
    }

    public void sendGlobalMsg(String msg) {
        JsonObject request = new JsonObject();
        JsonObject data = new JsonObject();
        request.add("data", data);
        request.addProperty("type", "global_chat_message");
        data.addProperty("sender", app.getCurrentPlayer().getFirstName().toString());
        data.addProperty("message", msg.toString());

        try {
            System.out.println("SENT JSON GLOBAL MESSAGE: " + request);
            app.getDataOutputStream().writeUTF(request.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setGlobalMsgFromServer(String sender, String msg) {
        chatTextArea.appendText(sender + ": " + msg + "\n");
    }

    public void setWelcomePlayer(String playerName, int totalPoints) {
        playerName2.setText(playerName);
        points2.setText(Integer.toString(totalPoints));
        welcome.setText("Welcome:");
        points.setText("Score: ");

    }
}
