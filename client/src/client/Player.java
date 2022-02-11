/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author Hassan
 */

import com.google.gson.JsonObject;

public class Player {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String img;
    private String password;
    private int points;

    public Player() {
        id = 0;
        firstName = null;
        lastName = null;
        email = null;
        img = null;
        password = null;

    }

    Player(String firstName, String email, String password) {    //TODO FIX DB AND SIGNUP GUI
        this.id = 0;
        this.firstName = firstName;
        this.lastName = null;
        this.email = email;
        this.img = img;
        this.password = password;

    }

    public Player(int id, String firstName, String email, int points) {
        this.id = id;
        this.firstName = firstName;
        this.email = email;
        this.points = points;
    }    public Player(int id, String firstName,String lastName, String email, int points) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.points = points;
    }

    Player(String firstName, String lastName, String email, String password) {
        this.id = 0;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.img = img;
        this.password = password;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    //TODO TEMP!
    public Player setPlayer(String firstName, String email, String password) {
        Player p = new Player(firstName, lastName, email, password);
        return p;
    }

    public Player setPlayer(String firstName, String lastName, String email, String password) {
        Player p = new Player(firstName, lastName, email, password);
        return p;
    }

    public JsonObject getPlayerAsJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("firstName", firstName);
        jsonObject.addProperty("lastName", lastName);
        jsonObject.addProperty("email", email);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("points", points);
        return jsonObject;
    }

}
