package com._98point6.droptoken.service;

import com._98point6.droptoken.DAO.DropTokenDAO;
import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.DO.Move;
import com._98point6.droptoken.error.BadRequestException;
import com._98point6.droptoken.error.GoneException;
import com._98point6.droptoken.error.NotFoundException;
import com._98point6.droptoken.factory.DropTokenFactory;
import com._98point6.droptoken.model.*;
import com._98point6.droptoken.validator.DropTokenValidator;
import com.google.common.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DropTokenService {

    DropTokenValidator dropTokenValidator;
    DropTokenFactory dropTokenFactory;
    DropTokenDAO dropTokenDAO;

    public DropTokenService(){
        dropTokenValidator = new DropTokenValidator();
        dropTokenFactory = new DropTokenFactory();
        dropTokenDAO = new DropTokenDAO();
    }

    // Added for unit test
    @VisibleForTesting
    DropTokenService(DropTokenDAO dropTokenDAO){
        this.dropTokenDAO = dropTokenDAO;
        dropTokenValidator = new DropTokenValidator();
        dropTokenFactory = new DropTokenFactory();
    }

    public CreateGameResponse createNewGame(CreateGameRequest request) throws BadRequestException {
        dropTokenValidator.validateCreateNewGameRequest(request);
        Game newGame = dropTokenFactory.buildGame(request);
        dropTokenDAO.save(newGame);
        return new CreateGameResponse.Builder().gameId(newGame.getGameId()).build();
    }

    public GetGamesResponse getGames() {

        return new GetGamesResponse.Builder()
                .games(dropTokenDAO.getGameIds(Game.State.IN_PROGRESS.toString()))
                .build();
    }

    public  GameStatusResponse getGameStatus(String gameId) throws BadRequestException {
        // TODO: Need to validate about winner key - it should not appear it state is in progress
        dropTokenValidator.validateGameStatus(gameId);
        Game game = dropTokenDAO.getGame(gameId);
        return dropTokenFactory.buildGameStatusResponse(game);

    }


    /**
     * When a player wants to make a move, he/she needs to pass a column a number in the request
     * And will validate the move and return the output
     */
    public PostMoveResponse postMove(String gameId, String playerId, PostMoveRequest request) throws BadRequestException, NotFoundException {
        dropTokenValidator.validatePostMoveRequest(gameId, playerId, request);
        Game game = dropTokenDAO.getGame(gameId);
        dropTokenValidator.validateMove(game, playerId, request);
        Move move = dropTokenFactory.buildMove(game, playerId, request);
        dropTokenDAO.save(game);
        return new PostMoveResponse.Builder()
                .moveLink(move.getMoveLink())
                .build();
    }


    public GetMovesResponse getMoves(String gameId, Integer start, Integer until) throws BadRequestException, NotFoundException {
        dropTokenValidator.validateMovesRequest(gameId, start, until);
        List<GetMoveResponse> movesList = new ArrayList<>();
        Game game = dropTokenDAO.getGame(gameId);
        start = Optional.ofNullable(start).orElse(1);
        until = Optional.ofNullable(until).orElse(game.getMovesList().size());
        dropTokenValidator.validateMoveQueryParam(game, start, until);


        // MoveLink 1 represents index 0 in MoveList
        for(int i=start; i <= until; i++){
            movesList.add(getMove(gameId, i));
        }

        return new GetMovesResponse.Builder()
                .moves(movesList)
                .build();
    }

    public GetMoveResponse getMove(String gameId, Integer moveId) throws BadRequestException, NotFoundException {
        // TODO: if type is quit no column should appear
        dropTokenValidator.validateMoveRequest(gameId, moveId);
        Game game = dropTokenDAO.getGame(gameId);
        dropTokenValidator.validateMoveId(game, moveId);

        return  new GetMoveResponse.Builder()
                .player(game.getMovesList().get(moveId-1).getPlayer())
                .column(game.getMovesList().get(moveId-1).getColumn())
                .type(game.getMovesList().get(moveId-1).getType())
                .build();
    }

    /**
     * If one player quits game, other player is declared as winner
     */
    public void playerQuit(String gameId, String playerId) throws GoneException, BadRequestException {
        dropTokenValidator.validatePlayerQuitRequest(gameId, playerId);
        Game game = dropTokenDAO.getGame(gameId);
        dropTokenValidator.validateGame(game);
        game.setState(Game.State.DONE.toString());
        String winner =  "";
        for(String player : game.getPlayers()) {
            if(!playerId.equals(player)) {
                winner = player;
            }
        }

        List<Move> moveList = game.getMovesList();
        Move move = new Move();
        move.setPlayer(playerId);
        move.setType(Move.Type.QUIT.toString());
        move.setMoveLink(String.valueOf(moveList.size()+1));
        moveList.add(move);
        game.setMovesList(moveList);

        game.setWinner(winner);
        dropTokenDAO.save(game);
    }

}
