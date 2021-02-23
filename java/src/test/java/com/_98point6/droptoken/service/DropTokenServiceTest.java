package com._98point6.droptoken.service;

import com._98point6.droptoken.DAO.DropTokenDAO;
import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.error.BadRequestException;
import com._98point6.droptoken.error.GoneException;
import com._98point6.droptoken.error.NotFoundException;
import com._98point6.droptoken.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DropTokenServiceTest {

    private DropTokenService dropTokenService;

    private DropTokenDAO dropTokenDAO;

    @Before
    public void setup() {
        dropTokenDAO = new DropTokenDAO();
        dropTokenService = new DropTokenService(dropTokenDAO);
        MockitoAnnotations.openMocks(DropTokenServiceTest.class);
    }

    @Test
    public void testCreateNewGame() throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest.Builder()
                .columns(4)
                .players(Arrays.asList("p1", "p2"))
                .rows(4)
                .build();

        CreateGameResponse response = dropTokenService.createNewGame(request);
        assertNotNull(response.getGameId());
    }

    @Test
    public void testPostMove_horizontal() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4, Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 4, Game.State.DONE.toString());

        Game actualGame = dropTokenDAO.getGame(gameId);
        assertEquals("p1", actualGame.getWinner());
    }

    @Test
    public void testPostMove_vertical() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4, Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.DONE.toString());

        Game actualGame = dropTokenDAO.getGame(gameId);
        assertEquals("p1", actualGame.getWinner());
    }

    @Test
    public void testPostMove_backslashDiagonal() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4, Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1", 4, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 4, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.DONE.toString());

        Game actualGame = dropTokenDAO.getGame(gameId);
        assertEquals("p1", actualGame.getWinner());
    }

    @Test
    public void testPostMove_slashDiagonal() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4, Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 3, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 4, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 4, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2", 4, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1", 4, Game.State.DONE.toString());

        Game actualGame = dropTokenDAO.getGame(gameId);
        assertEquals("p1", actualGame.getWinner());
    }

    private PostMoveRequest buildPostMoveRequest(int col) {
        return new PostMoveRequest.Builder()
                .column(col)
                .build();
    }

    private String createGame(int column, int row, List<String> players) throws BadRequestException {
        CreateGameRequest request = new CreateGameRequest.Builder()
                .columns(4)
                .players(Arrays.asList("p1", "p2"))
                .rows(4)
                .build();

        return dropTokenService.createNewGame(request).getGameId();
    }

    private void verifyGameStatueOnPostMove(String gameId, String player, int col, String expectedGameStatus) throws BadRequestException, NotFoundException {
        dropTokenService.postMove(gameId, player, buildPostMoveRequest(col));
        assertEquals(expectedGameStatus, dropTokenService.getGameStatus(gameId).getState());
    }

    @Test
    public void testGetGameStatus() throws BadRequestException {
        Game game = new Game();
        game.setGameId("1");
        game.setPlayers(Arrays.asList("p1", "p2"));
        game.setMovesList(new ArrayList<>());
        game.setState(Game.State.IN_PROGRESS.toString());
        game.setWinner("");
        dropTokenDAO.save(game);
        GameStatusResponse response = dropTokenService.getGameStatus(game.getGameId());
        assertEquals(game.getPlayers(), response.getPlayers());
        assertEquals(game.getState(), response.getState());
    }

    @Test
    public void testGetMove() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4,Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1" , 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2" , 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1" , 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2" , 2, Game.State.IN_PROGRESS.toString());
        Game game = dropTokenDAO.getGame(gameId);
        dropTokenDAO.save(game);

        GetMoveResponse actualResponse = dropTokenService.getMove(game.getGameId(),1);

        assertEquals(game.getMovesList().get(0).getPlayer(), actualResponse.getPlayer());
        assertEquals(game.getMovesList().get(0).getType(), actualResponse.getType());
        assertTrue(actualResponse.getColumn().isPresent());
        assertEquals(game.getMovesList().get(0).getColumn(), actualResponse.getColumn().get().intValue());
    }

    @Test
    public void testGetMovesResponse() throws BadRequestException, NotFoundException {
        String gameId = createGame(4, 4,Arrays.asList("p1", "p2"));

        verifyGameStatueOnPostMove(gameId, "p1" , 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2" , 2, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p1" , 1, Game.State.IN_PROGRESS.toString());
        verifyGameStatueOnPostMove(gameId, "p2" , 2, Game.State.IN_PROGRESS.toString());

        Game game = dropTokenDAO.getGame(gameId);
        dropTokenDAO.save(game);

        GetMovesResponse actualResponse = dropTokenService.getMoves(game.getGameId(), null, null);
        assertEquals(game.getMovesList().size(), actualResponse.getMoves().size());
    }

    @Test
    public void testPlayerQuit() throws GoneException, BadRequestException {

        String gameId = createGame(4,4,Arrays.asList("p1", "p2"));
        Game game = dropTokenDAO.getGame(gameId);
        dropTokenDAO.save(game);
        dropTokenService.playerQuit(gameId, "p1");
        String winner =  "";
        for(String player : game.getPlayers()) {
            if(!"p1".equals(player)) {
                winner = player;
            }
        }
        assertEquals(winner, game.getWinner());
        assertEquals(game.getState(), Game.State.DONE.toString());
    }


}
