/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import client.App;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author Abdallah
 */
public class MultiOnlinePlayers extends Pane {
    GridPane stack;
    private final App app;
    Random rand;
    public static boolean turn = true;
    int counter, cpupos;
    private String line, thisPlayerLetter, opponenetPlayerLetter, challengerName;
    Vector<Label> l = new Vector<>();
    private boolean isEnded = false;
    Label label1, label2;
    private TextArea chatTextArea;
    private TextArea chatMessageArea;

    public MultiOnlinePlayers(App app) {
        stack = new GridPane();
        this.app = app;
        setId("stackGameboard");
        for (int i = 0; i < 9; i++) {
            l.add(new Label("_"));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = i * 3 + j;
                stack.add(l.get(x), j, i);
            }
        }

        rand = new Random();
        counter = 0;
//        resetGame();
        stack.setId("stack");
        stack.setPadding(new Insets(40, 0, 0, 50));
        stack.setHgap(150);
        stack.setVgap(-20);
        stack.setPrefSize(750, 700);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = i * 3 + j;
                l.get(x).setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (isEnded) {
                            return;
                        }
                        Label currentLabel = (Label) event.getSource();
                        if (turn && currentLabel.getText().equals("_")) {
                            currentLabel.setText(thisPlayerLetter);
                            currentLabel.setId(thisPlayerLetter);
                            turn = false;
                            counter++;
                            stack.requestLayout();
//                            sendMoveToServer(x);
//                            checkWinner();
                        }
                    }
                });

            }
        }
        setId("stackGameboard");
        label1 = new Label("PLAYER1");
        label1.setPrefWidth(150);
        label2 = new Label("PLAYER2");
        label2.setPrefWidth(150);

        HBox hbox = new HBox(350, label1, label2);
        hbox.setLayoutX(70);
        hbox.setLayoutY(25);

        chatTextArea = new TextArea(" ");
        chatTextArea.setId("ta");
        chatTextArea.setEditable(false);
        chatTextArea.setLayoutX(890);
        chatTextArea.setLayoutY(420);
        chatTextArea.setMaxWidth(220.0);
        chatTextArea.setMaxHeight(250.0);
        chatTextArea.setWrapText(true);

        chatMessageArea = new TextArea("");
        chatMessageArea.setId("text");
        chatMessageArea.setPromptText("Enter your Msg ");
        chatMessageArea.setLayoutX(890);
        chatMessageArea.setLayoutY(680);
        chatMessageArea.setMaxWidth(220.0);
        chatMessageArea.setMaxHeight(10.5);
        chatMessageArea.setWrapText(true);

        Button send = new Button();
        send.setText("send");
        send.setId("send");
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
//                    sendEvent();
                }
            }
        });
        send.setLayoutX(1140);
        send.setLayoutY(680);
        ///////////////////////////////////////////////////////////
        Button exit = new Button("EXIT");
        exit.setId("ExitFromGame");
        exit.setLayoutX(280);
        exit.setLayoutY(650);
        exit.setPrefSize(110, 10);
        exit.setOnAction((t) -> {
            app.exit();
        });
        Button pause = new Button("PAUSE");
        pause.setPrefSize(110, 10);
        pause.setId("BackToMain");
        pause.setOnAction((event) -> {
            JsonObject request = new JsonObject();
            JsonObject data = new JsonObject();
            request.addProperty("type", "pause-game");
            request.add("data", data);

            app.setScreen("main");
            app.showAlert(App.opposingPlayerName + " left the game", "Switching to main screen.");

            try {
                app.getDataOutputStream().writeUTF(request.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        HBox hBox = new HBox(100, pause, exit);

        VBox v = new VBox(40, hBox);
        v.setId("vbox");
        v.setLayoutX(1000);
        v.setLayoutY(150);
        ///////////////////////////////////////////////////////////
        getChildren().addAll(stack, hbox, v, chatMessageArea, chatTextArea, send);
        stack.setId("stacklolo");
    }

    public void acceptInvitationInvitedSide(int challengerId, String challengerName) {
        /*Invited Side the O*/
        App.inMultiplayerGame = true;
        App.opposingPlayerName = challengerName;
        App.opposingPlayerId = challengerId;
        this.challengerName = challengerName;
        label1.setText(challengerName);
        label2.setText(app.getCurrentPlayer().getFirstName());
        isEnded = false;
        MultiOnlinePlayers.turn = false;
        chatTextArea.setText("");
        chatMessageArea.setText("");
        opponenetPlayerLetter = "X";
        thisPlayerLetter = "O";
        System.out.println("Accept this letter: " + thisPlayerLetter);
        System.out.println("turn: " + turn + "thisPlayerLetter: " + thisPlayerLetter + "opponentPlayerLetter: " + opponenetPlayerLetter);
    }

    public void invitationAcceptedSetInviterSide(String challengerName, int opposingPlayerId) {
        App.inMultiplayerGame = true;
        App.opposingPlayerId = opposingPlayerId;
        App.opposingPlayerName = challengerName;
        label1.setText(app.getCurrentPlayer().getFirstName());
        label2.setText(challengerName);
        /*Inviter Side the X*/
        System.out.println("test 1");
        this.challengerName = challengerName;
        System.out.println("test 2");
        System.out.println("test 3");
        opponenetPlayerLetter = "O";
        thisPlayerLetter = "X";
        System.out.println("test 4" + thisPlayerLetter);
        isEnded = false;
        MultiOnlinePlayers.turn = true;
        chatTextArea.setText("");
        chatMessageArea.setText("");
        System.out.println("test 5");
        System.out.println("turn: " + turn + "thisPlayerLetter: " + thisPlayerLetter + "opponentPlayerLetter: " + opponenetPlayerLetter);
        System.out.println("test 6");
        app.setScreen("multiOnlinePlayers");
    }
     private void sendEvent() {

        if (chatMessageArea.getText().isEmpty()) {
            return;
        }
        JsonObject response = new JsonObject();
        JsonObject data = new JsonObject();
        response.add("data", data);
        response.addProperty("type", "game-message");
        data.addProperty("msg", chatMessageArea.getText());
        chatTextArea.setText(chatTextArea.getText()+app.getCurrentPlayer().getFirstName() 
                + ": " + chatMessageArea.getText() + "\n");
        chatMessageArea.setText("");
        try {
            app.getDataOutputStream().writeUTF(response.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
     public void setNewMsg(String msg) {
        chatTextArea.appendText(challengerName + ": " + msg+"\n"  );
    }

    
}
