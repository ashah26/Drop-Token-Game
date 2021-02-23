package com._98point6.droptoken.DO;

public class Move {

    public enum Type{
        MOVE, QUIT;
    }

    String moveLink;
    String player;
    int column;
    String type;

    public String getMoveLink() {
        return moveLink;
    }

    public void setMoveLink(String moveLink) {
        this.moveLink = moveLink;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
