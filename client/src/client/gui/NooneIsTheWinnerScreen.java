package client.gui;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import client.App;

/**
 *
 * @author Hagar
 */
public class NooneIsTheWinnerScreen extends StackPane {

    private final App app;

    public NooneIsTheWinnerScreen(App app) {
        this.app = app;
        Region rec = new Region();
        rec.setPrefSize(498, 460);
        rec.setId("recSignin");

        Region over = new Region();
        over.setId("noOneIsTheWinner");
        over.setPrefSize(130, 130);

        DropShadow e = new DropShadow();
        e.setOffsetX(0.0f);
        e.setOffsetY(4.0f);
        e.setBlurType(BlurType.GAUSSIAN);
        e.setColor(Color.BLACK);

        Button noOneWine = new Button("No one Is The Winner");
        noOneWine.setId("lose");
        noOneWine.setEffect(e);
  
//        ToggleButton playAgain = new ToggleButton("Play Again");
//        playAgain.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                if (App.inMultiplayerGame) {
//                    app.sendInvitation(App.opposingPlayerId);
//                } else {
//                    app.setScreen("levels");
//                }
//                App.inMultiplayerGame = false;
//                App.opposingPlayerId = -1;
//                App.opposingPlayerName = "";
//            }
//        });
//        playAgain.setPrefSize(180, 20);
//        playAgain.setId("playAgain");
//        HBox buttonBox = new HBox(50, playAgain);
         
        /////////////////////////////////////////////////////
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
        
        
/////////////////////////////////////////////////////
        VBox vbox = new VBox(30, over, noOneWine);
        vbox.setId("vbox");

         VBox v = new VBox(100, vbox,hBox);
        v.setId("vbox"); 
        
        getChildren().addAll(rec, v);
        setId("stackGameResultScreen");
    }
}
