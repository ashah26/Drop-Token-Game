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

    /**
     * Creates new game with columns, rows and a list of players passed in request.
     * After creating the game, saves it to DB
     */
    public CreateGameResponse createNewGame(CreateGameRequest request) throws BadRequestException {
        dropTokenValidator.validateCreateNewGameRequest(request);
        Game newGame = dropTokenFactory.buildGame(request);
        dropTokenDAO.save(newGame);
        return new CreateGameResponse.Builder().gameId(newGame.getGameId()).build();
    }

    /**
     * Gets a list of all gameId which are in progress at the moment
     */
    public GetGamesResponse getGames() {

        return new GetGamesResponse.Builder()
                .games(dropTokenDAO.getGameIds(Game.State.IN_PROGRESS.toString()))
                .build();
    }

    /**
     * Gets details of a particular game like number of players, total moves, state (Done or in-progress)
     * and winner if game is over
     */
    public  GameStatusResponse getGameStatus(String gameId) throws BadRequestException {
        dropTokenValidator.validateGameStatus(gameId);
        Game game = dropTokenDAO.getGame(gameId);
        return new GameStatusResponse.Builder()
                .players(game.getPlayers())
                .state(game.getState())
                .winner(game.getWinner())
                .moves(game.getMovesList().size())
                .build();


    }


    /**
     * When a player wants to make a move, he/she needs to pass a column a number in the request
     * And will validate the move, save the move in DB and return the move link number
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

    /**
     * To get a detailed list which includes playerId and column of all the moves of a particular game
     * We can also get a filtered list in a particular range by providing start and until params
     */
    public GetMovesResponse getMoves(String gameId, Integer start, Integer until) throws BadRequestException, NotFoundException {
        dropTokenValidator.validateMovesRequest(gameId, start, until);
        List<GetMoveResponse> movesList = new ArrayList<>();
        Game game = dropTokenDAO.getGame(gameId);
        if(game.getMovesList().size() > 0){
            start = Optional.ofNullable(start).orElse(1);
            until = Optional.ofNullable(until).orElse(game.getMovesList().size());
            dropTokenValidator.validateMoveQueryParam(game, start, until);
            // MoveLink 1 represents index 0 in MoveList
            for(int i=start; i <= until; i++){
                movesList.add(getMove(gameId, i));
            }
        }

        return new GetMovesResponse.Builder()
                .moves(movesList)
                .build();
    }

    /**
     *  To get details of a particular move in a particular game
     */
    public GetMoveResponse getMove(String gameId, Integer moveId) throws BadRequestException, NotFoundException {
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
