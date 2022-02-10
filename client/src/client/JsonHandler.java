/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import client.gui.SignupScreen;
import com.google.gson.JsonObject;
import java.io.DataInputStream;

/**
 *
 * @author Hassan
 */
class JsonHandler {
    private App app;
    private DataInputStream dataInputStream;
    SignupScreen signupScreen;
    
    JsonHandler(App a) {
        app = a;
        signupScreen = (SignupScreen) app.getScreen("signup");    }
    
    
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
        }
    }
    
}
