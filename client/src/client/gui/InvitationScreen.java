/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import com.google.gson.JsonObject;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import client.App;

/**
 *
 * @author Abdallah
 */
public class InvitationScreen extends StackPane{
    private int challengerId;
    private String challengerName = "";
    private App app;
    Button invitationMSG;

    public InvitationScreen(App app) {
        this.app = app;

        Region rec = new Region();
        rec.setPrefSize(498, 460);
        rec.setId("regionInvitationScreen");
        DropShadow e = new DropShadow();
        e.setOffsetX(0.0f);
        e.setOffsetY(4.0f);
        e.setBlurType(BlurType.GAUSSIAN);
        e.setColor(Color.BLACK);
        invitationMSG = new Button(challengerName + " Challenges you");
        invitationMSG.setId("playerWantsToChallengeYou");
        invitationMSG.setEffect(e);
        /////////////////accept button//////////////////////
        ToggleButton accept = new ToggleButton("Accept");
        accept.setPrefSize(200, 20);
        accept.setId("acceptInvitation");
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MultiOnlinePlayers multiOnlinePlayers = (MultiOnlinePlayers) app.getScreen("multiOnlinePlayers");
                System.out.println(multiOnlinePlayers);
                app.setScreen("multiOnlinePlayers");
                MultiOnlinePlayers.turn = false;
                JsonObject response = new JsonObject();
                JsonObject data = new JsonObject();
                response.add("data", data);
                response.addProperty("type", "accept-invitation");
                data.addProperty("inviting_player_id", challengerId);
                multiOnlinePlayers.acceptInvitationInvitedSide(challengerId, challengerName);
                try {
                    app.getDataOutputStream().writeUTF(response.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        ///////////////////////decline button//////////////////////////
        ToggleButton Decline = new ToggleButton("Decline");
        Decline.setPrefSize(200, 20);
        Decline.setId("declineInvitation");
        Decline.setOnAction((t) -> {
            app.setScreen("main");
            JsonObject response = new JsonObject();
            JsonObject data = new JsonObject();
            response.add("data", data);
            response.addProperty("type", "decline-invitation");
            data.addProperty("inviting_player_id", challengerId);
            try {
                app.getDataOutputStream().writeUTF(response.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });
        ///////////////////////////////////////////////////////////////
        HBox buttonBox = new HBox(20, accept, Decline);

        VBox vbox = new VBox(100, invitationMSG, buttonBox);
        vbox.setId("vboxInvitationScreen");
        
        
        ////////////////////////////////////////////////////////////////////////////////////////////
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

        HBox hBox = new HBox(200, back, exit);

  
        vbox.setId("vbox");

        VBox v = new VBox(120, vbox, hBox);
        v.setId("vbox");

      
        ////////////////////////////////////////////////////////////////////////

        getChildren().addAll(rec, v);
        setId("stackInvitation");
    }

    public void setInvitation(int challengerId, String challengerName) {
        this.challengerId = challengerId;
        this.challengerName = challengerName;
        app.setScreen("invitation");
        invitationMSG.setText(challengerName + " Challenges you");

    }
}
