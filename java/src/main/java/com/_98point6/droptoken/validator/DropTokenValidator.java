package com._98point6.droptoken.validator;

import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.DO.Move;
import com._98point6.droptoken.error.BadRequestException;
import com._98point6.droptoken.error.GoneException;
import com._98point6.droptoken.error.NotFoundException;
import com._98point6.droptoken.model.CreateGameRequest;
import com._98point6.droptoken.model.PostMoveRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DropTokenValidator {

    public void validateCreateNewGameRequest(CreateGameRequest request) throws BadRequestException {

        if(request.getPlayers().size() != 2){
            throw new BadRequestException("Invalid input for players. There must be 2 players");
        }
        if(request.getColumns() < 4){
            throw new BadRequestException("Invalid input for columns. Columns can not be less than 4");
        }
        if(request.getRows() < 4){
            throw new BadRequestException("Invalid input for rows. Rows can not be less then 4");
        }
    }

    public void validateGameStatus(String gameId) throws BadRequestException {
        if(StringUtils.isBlank(gameId)){
            throw new BadRequestException("Invalid input for gameId. GameId cannot be empty");
        }
    }

    public void validatePostMoveRequest(String gameId, String playerId, PostMoveRequest request) throws BadRequestException {
        if(StringUtils.isBlank(gameId)){
            throw new BadRequestException("Invalid input for gameId. GameId cannot be empty");
        }
        if(playerId.equals("")){
            throw new BadRequestException("Invalid input for playerId. PlayerId cannot be empty");
        }
        if(request.getColumn() <= 0){
            throw new BadRequestException("Invalid input for column. Column cannot be less than or equal to 0");
        }
    }

    public void validateMoveRequest(String gameId, Integer moveId) throws BadRequestException {
        if(StringUtils.isBlank(gameId)){
            throw new BadRequestException("Invalid input for gameId. GameId cannot be empty");
        }
        if(moveId <= 0){
            throw new BadRequestException("Invalid input for moveId. MoveId cannot be less than or equal to 0");
        }
    }

    public void validateMovesRequest(String gameId, Integer start, Integer until) throws BadRequestException {
        if(StringUtils.isBlank(gameId)){
            throw new BadRequestException("Invalid input for gameId. GameId cannot be empty");
        }
        if(start != null && start <= 0){
            throw new BadRequestException("Invalid input for start. Start cannot be less than or equal to 0");
        }
        if(start != null && until != null) {
            if (until <= 0 || until < start) {
                throw new BadRequestException("Invalid input for until. Until cannot be less than 0 or start");
            }
        }
    }

    public void validateMoveQueryParam(Game game, Integer start, Integer until) throws BadRequestException {
        int size = game.getMovesList().size();

        if(start != null && start > size ){
            throw  new BadRequestException("Invalid input for start. Start cannot be greater than moves made");
        }
        if(until != null && until > size){
            throw  new BadRequestException("Invalid input for until. Until cannot be greater than moves made");
        }
    }

    public void validatePlayerQuitRequest(String gameId, String playerId) throws BadRequestException {
        if(StringUtils.isBlank(gameId)){
            throw new BadRequestException("Invalid input for gameId. GameId cannot be empty");
        }
        if(StringUtils.isBlank(playerId)){
            throw new BadRequestException("Invalid input for playerId. PlayerId cannot be empty");
        }

    }

    public void validateGame(Game game) throws GoneException {
        if(game.getState().equals(Game.State.DONE.toString())){
            throw new GoneException("Game is already over.");
        }
    }

    public void validateMove(Game game, String playerId, PostMoveRequest request) throws BadRequestException, NotFoundException {
        List<Move> movesList = game.getMovesList();
        if(movesList.size() > 0){
            if(playerId.equals(movesList.get(movesList.size()-1).getPlayer())){
                throw new BadRequestException("Wait for other player to finish the turn");
            }
        }
        if(!game.getPlayers().contains(playerId)){
            throw new NotFoundException("Invalid input for playerId. No such player found for this game");
        }
        int column = request.getColumn();
        int cols = game.getGrid()[0].length;
        int rows = game.getGrid().length;

        if(column > cols){
            throw new BadRequestException("Invalid input for column. Available input range is 1 to "+cols);
        }
        if(!StringUtils.isBlank(game.getGrid()[rows-1][column-1])){
            throw new BadRequestException("Invalid input for column. Column " +column+ " is full.");
        }
        if(game.getState().equals(Game.State.DONE.toString())){
            throw new BadRequestException("Invalid input. Game is over so cannot make move");
        }

    }

    public void validateMoveId(Game game, Integer moveId) throws NotFoundException {
        boolean moveFound = false;
        for(Move move : game.getMovesList()){
            if(move.getMoveLink().equals(String.valueOf(moveId))){
                moveFound = true;
                break;
            }
        }
        if(!moveFound){
            throw new NotFoundException("Invalid input for moveId. No such move found for this game");
        }
    }



}
