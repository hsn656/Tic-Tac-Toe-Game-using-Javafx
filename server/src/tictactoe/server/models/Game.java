/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe.server.models;

/**
 *
 * @author Hassan
 */
public class Game {
    private Player playerX;
    private Player playerO;

    private int playerXId;
    private int playerOId;
    private int winnerId;
    private Status gameStatus;
    private Move nextMove;

    private int gameId;
    
    
    
    public static enum Status {
        inProgress, terminated, finished;
    }
    
    public static enum Move {
        X, O
    }
}
