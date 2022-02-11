/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.gui.MainScreen;
import client.gui.SigninScreen;
import client.gui.SignupScreen;
import com.google.gson.JsonObject;
import java.io.DataInputStream;
//import Player.java;

/**
 *
 * @author Hassan
 */
class JsonHandler {
    private App app;
    private DataInputStream dataInputStream;
    SignupScreen signupScreen;
    SigninScreen signinScreen;
    MainScreen mainScreen;


    JsonHandler(App a) {
        app = a;
        signupScreen = (SignupScreen) app.getScreen("signup");    
        signinScreen = (SigninScreen) a.getScreen("signin");
        mainScreen = (MainScreen) app.getScreen("main");


}
    
    
    public void handle(JsonObject request) {
        System.out.println(request);
        String requestType = request.get("type").getAsString();
        JsonObject requestData = request.getAsJsonObject("data");
        
        switch (requestType) {
            case "signup-error":
                signupScreen.showSignupFailedPopup();
                break;
            case "signup-success":
                app.showAlert("Welcome :D", "Sign up successful.\nLogin to play :D");
                app.setScreen("signin");
                break;
            case "signin-success":
                JsonObject myData = requestData.getAsJsonObject("my-data");
                app.setCurrentPlayer(new Player(
                        myData.get("id").getAsInt(),
                        myData.get("firstName").getAsString(),
                        myData.get("lastName").getAsString(),
                        myData.get("email").getAsString(),
                        myData.get("points").getAsInt()
                ));

                mainScreen.setWelcomePlayer(app.getCurrentPlayer().getFirstName(), app.getCurrentPlayer().getPoints());
//
//                playWithComputerEasy.setPlayerName(app.getCurrentPlayer().getLastName());
//                playWithComputerNormal.setPlayerName(app.getCurrentPlayer().getLastName());
//                playWithComputerHard.setPlayerName(app.getCurrentPlayer().getLastName());

                app.setScreen("main");
                break;
            case "signin-error":
                app.showAlert("Could not login", requestData.get("msg").getAsString());
                signinScreen.showSigninButton();
                break;
        }
    }
    
}
