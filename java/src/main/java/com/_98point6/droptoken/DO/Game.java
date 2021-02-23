package com._98point6.droptoken.DO;

import java.util.List;

public class Game {

    public enum State{
        IN_PROGRESS, DONE;
    }

    String gameId;
    List<String> players;
    String state;
    String winner;
    List<Move> movesList;
    String[][] grid;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<Move> getMovesList() {
        return movesList;
    }

    public void setMovesList(List<Move> movesList) {
        this.movesList = movesList;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setGrid(String[][] grid) {
        this.grid = grid;
    }
}
