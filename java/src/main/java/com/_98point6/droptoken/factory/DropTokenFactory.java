package com._98point6.droptoken.factory;

import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.DO.Move;
import com._98point6.droptoken.model.CreateGameRequest;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.PostMoveRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DropTokenFactory {
    /**
     * Builder for new game
     */
    public Game buildGame(CreateGameRequest request) {
        Game game = new Game();
        game.setGrid(new String[request.getRows()][request.getColumns()]);
        for (String[] row : game.getGrid()) {
            Arrays.fill(row, "");
        }
        game.setPlayers(request.getPlayers());
        game.setState(Game.State.IN_PROGRESS.toString());
        game.setMovesList(new ArrayList<>());
        return game;
    }

    public Move buildMove(Game game, String playerId, PostMoveRequest request) {

        String[][] grid = game.getGrid();
        int rows = grid.length;
        boolean canInsert = false;
        int rowNum = -1;
        int colNum = request.getColumn();

        // Fill each cell with empty string to start with


        for (int i=0; i<rows; i++) {
            if(StringUtils.isBlank(grid[i][colNum-1])){
                canInsert = true;
                rowNum = i;
                grid[i][colNum-1] = playerId;
                game.setGrid(grid);
                break;
            }
        }

//        printGrid(grid);
        Move move = new Move();
        if(canInsert){

            move.setColumn(request.getColumn());
            move.setPlayer(playerId);
            move.setType("MOVE");


            List<Move> moveList = game.getMovesList();
            move.setMoveLink(String.valueOf(moveList.size()+1));

            moveList.add(move);
            game.setMovesList(moveList);
        }

        if (canInsert && game.getMovesList().size() > 3) {
            String streak = String.format("%s%s%s%s", playerId, playerId, playerId, playerId);

            if (contains(horizontal(rowNum, grid), streak)  ||
                    contains(vertical(colNum, grid), streak) ||
                    contains(slashDiagonal(rowNum, colNum, grid), streak) ||
                    contains(backslashDiagonal(rowNum, colNum, grid), streak)
            ) {
                game.setState(Game.State.DONE.toString());
                game.setWinner(playerId);
            }
        }
        return move;
    }

    private boolean contains(String gridString, String streak){
        return gridString.contains(streak);
    }
    private String horizontal(int rowNum, String[][] grid){
        StringBuilder sb = new StringBuilder(grid[0].length);
        for(int i=0; i<grid[0].length; i++){
            sb.append(grid[rowNum][i]);
        }
        return sb.toString();
    }
    private String vertical(int colNum, String[][] grid){
        StringBuilder sb = new StringBuilder(grid.length);
        for (String[] strings : grid) {
            sb.append(strings[colNum-1]);
        }
        return sb.toString();
    }

    private String slashDiagonal(int rowNum, int colNum, String[][] grid){
        StringBuilder sb = new StringBuilder(grid.length);
        for(int i=0; i<grid.length; i++){
            int diagonal = rowNum + colNum-1 - i;
            if(0 <= diagonal && diagonal < grid[0].length){
                sb.append(grid[i][diagonal]);
            }
        }
        return sb.toString();
    }

    private String backslashDiagonal(int rowNum, int colNum, String[][] grid){
        StringBuilder sb = new StringBuilder(grid.length);
        for(int i=0; i<grid.length; i++){
            int diagonal = colNum-1 - rowNum + i;
            if(0 <= diagonal && diagonal < grid[0].length){
                sb.append(grid[i][diagonal]);
            }
        }
        return sb.toString();
    }

//    private void printGrid(String[][] grid) {
//        System.out.println("Grid output");
//        System.out.print("\t\t");
//        for (int i=0; i<grid.length; i++) {
//            System.out.print("Col" + i + "\t");
//        }
//        System.out.println();
//
//        for(int i=0; i<grid.length; i++){
//            System.out.print("row" + i + "\t");
//
//            for(int j=0; j<grid[0].length; j++){
//                System.out.print(grid[i][j] + "\t");
//            }
//            System.out.println("\n");
//        }
//    }

    public GameStatusResponse buildGameStatusResponse(Game game) {
        if (StringUtils.isBlank(game.getWinner())) {
            return new GameStatusResponse.Builder()
                    .players(game.getPlayers())
                    .state(game.getState())
                    .moves(game.getMovesList().size())
                    .build();
        } else {
            return new GameStatusResponse.Builder()
                    .players(game.getPlayers())
                    .state(game.getState())
                    .winner(game.getWinner())
                    .moves(game.getMovesList().size())
                    .build();
        }
    }
}
