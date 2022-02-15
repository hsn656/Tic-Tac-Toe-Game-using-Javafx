/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.gui;
import java.util.Random;
import java.util.Vector;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import client.App;
/**
 *
 * @author Yasmine
 */
public class PlayWithComputerEasyGameBoardScreen extends Pane {

    private final App app;
    Random rand;
    boolean turn, fullBoardFlag;
    int counter, cpupos;
    String line;
    Vector<Label> l = new Vector<>();
    boolean[] textLabelflag;

    private boolean isEnded = false;
    Label label1;

    public PlayWithComputerEasyGameBoardScreen(App app) {
        this.app = app;
        setId("stackGameboard");
        for (int i = 0; i < 9; i++) {
            l.add(new Label("_"));
        }
        turn = true;
        rand = new Random();
        counter = 0;
        resetGame();
        GridPane stack = new GridPane();
        stack.setId("stack");
        stack.setPadding(new Insets(40, 0, 0, 50));
        stack.setHgap(150);
        stack.setVgap(50);
        stack.setPrefSize(750, 700);
        checkWinner();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = i * 3 + j;
                l.get(x).setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (isEnded) {
                            return;
                        }
                        if (turn && textLabelflag[x]) {
                            l.get(x).setText("X");
                            l.get(x).setId("X");
                            turn = false;
                            textLabelflag[x] = false;
                            counter++;
                            checkWinner();
                        }
                        if (turn == false) {
                            cpu();
                        }
                    }
                });
                stack.add(l.get(x), j, i);
            }
        }
        //
        setId("stackGameboard");
        label1 = new Label("Player");
        label1.setPrefWidth(150);
        Label label2 = new Label("COMPUTER");
        label2.setPrefWidth(180);

        HBox hbox = new HBox(350, label1, label2);
        hbox.setLayoutX(70);
        hbox.setLayoutY(25);
       
         ///////////////////////////////////////////////////////////
            Button exit = new Button("EXIT");
        exit.setId("ExitFromGame");
        exit.setLayoutX(280);
        exit.setLayoutY(650);
        exit.setPrefSize(110, 10);
        exit.setOnAction((t) -> {
            app.exit();
        });
        Button back = new Button("Back");
        back.setPrefSize(110, 10);
        back.setId("BackToMain");
        back.setOnAction((event) -> {
            app.setScreen("main");
            App.inMultiplayerGame = false;
            App.opposingPlayerId = -1;
            App.opposingPlayerName = "";
        });

        HBox hBox = new HBox(100, back, exit);

        VBox v = new VBox(40, hBox);
        v.setId("vbox");
        v.setLayoutX(1000);
        v.setLayoutY(150);
        ///////////////////////////////////////////////////////////
        
        getChildren().addAll(stack, hbox,v);
        stack.setId("stacklolo");
        if (!turn) {
            cpu();
        }
    }

    void cpu() {
        PauseTransition pause = new PauseTransition(Duration.seconds(.5));
        pause.setOnFinished((ActionEvent event) -> {
            while (!textLabelflag[cpupos] && counter < 9) {
                cpupos = rand.nextInt(9);
            }
            if (turn == false && textLabelflag[cpupos]) {
                counter++;
                l.get(cpupos).setText("O");
                l.get(cpupos).setId("O");
                turn = true;
                textLabelflag[cpupos] = false;
                checkWinner();
            }
        });
        pause.play();
    }

    private void resetGame() {
        for (int i = 0; i < l.size(); i++) {
            l.get(i).setText("_");
            l.get(i).setId("label");
        }
        textLabelflag = new boolean[]{true, true, true, true, true, true, true, true, true};
        counter = 0;
        fullBoardFlag = true;
        isEnded = false;
        if (!turn) {
            cpu();
        }

    }

    private void checkWinner() {
        for (int x = 0; x < 8; x++) {
            line = null;
            switch (x) {
                case 0:
                    line = l.get(0).getText() + l.get(1).getText() + l.get(2).getText();

                    break;
                case 1:
                    line = l.get(3).getText() + l.get(4).getText() + l.get(5).getText();
                    break;
                case 2:
                    line = l.get(6).getText() + l.get(7).getText() + l.get(8).getText();
                    break;
                case 3:

                    line = l.get(0).getText() + l.get(3).getText() + l.get(6).getText();
                    break;
                case 4:
                    line = l.get(1).getText() + l.get(4).getText() + l.get(7).getText();
                    break;
                case 5:
                    line = l.get(2).getText() + l.get(5).getText() + l.get(8).getText();
                    break;
                case 6:
                    line = l.get(0).getText() + l.get(4).getText() + l.get(8).getText();
                    break;
                case 7:
                    line = l.get(2).getText() + l.get(4).getText() + l.get(6).getText();
                    break;
            }
            switch (line) {
                case "XXX": {
                    fullBoardFlag = false;
                    turn = true;
                    isEnded = true;
                    for (int i = 0; i < 9; i++) {
                        if (textLabelflag[i]) {
                            textLabelflag[i] = false;
                        }
                    }
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished((ActionEvent event) -> {
                        app.addPointsLocalGame(5);
                        app.setScreen("youWin");
                        counter = 0;
                        resetGame();
                    });
                    pause.play();
                    return;
                }
                case "OOO": {
                    fullBoardFlag = false;
                    turn = true;
                    isEnded = true;
                    for (int i = 0; i < 9; i++) {
                        if (textLabelflag[i]) {
                            textLabelflag[i] = false;
                        }
                    }
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished((ActionEvent event) -> {
                        app.setScreen("hardLuck");
                        counter = 0;
                        resetGame();
                    });
                    pause.play();
                    return;
                }
            }
        }
        if (counter == 9 && fullBoardFlag) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished((ActionEvent event) -> {
                app.setScreen("nooneIsTheWinner");
                counter = 0;
                resetGame();
            });
            pause.play();
        }
    }
    public void setPlayerName(String playerName) {
        label1.setText(playerName);
    }
}

