package com._98point6.droptoken.validator;

import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.DO.Move;
import com._98point6.droptoken.error.BadRequestException;
import com._98point6.droptoken.error.GoneException;
import com._98point6.droptoken.error.NotFoundException;
import com._98point6.droptoken.model.CreateGameRequest;
import com._98point6.droptoken.model.PostMoveRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DropTokenValidatorTest {
    private DropTokenValidator dropTokenValidator;

    @Before
    public void setup() {
        dropTokenValidator = new DropTokenValidator();
    }

    @Test
    public void testValidateCreateNewGameRequest() {
        CreateGameRequest request = new CreateGameRequest.Builder()
                .players(Arrays.asList("p1", "p2", "p3"))
                .columns(4)
                .rows(4)
                .build();

        try {
            dropTokenValidator.validateCreateNewGameRequest(request);
            fail();
        } catch (BadRequestException e) {
            assertTrue(e.getMessage().contains("Invalid input for players. There must be 2 players"));
        }

        request = new CreateGameRequest.Builder()
                .players(Arrays.asList("p1", "p2"))
                .columns(3)
                .rows(4)
                .build();

        try {
            dropTokenValidator.validateCreateNewGameRequest(request);
            fail();
        } catch (BadRequestException e) {
            assertTrue(e.getMessage().contains("Invalid input for columns. Columns can not be less than 4"));
        }

        request = new CreateGameRequest.Builder()
                .players(Arrays.asList("p1", "p2"))
                .columns(4)
                .rows(3)
                .build();

        try {
            dropTokenValidator.validateCreateNewGameRequest(request);
            fail();
        } catch (BadRequestException e) {
            assertTrue(e.getMessage().contains("Invalid input for rows. Rows can not be less then 4"));
        }
    }

    @Test
    public void testValidGameStatus(){

        try {
            dropTokenValidator.validateGameStatus("");
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for gameId. GameId cannot be empty"));
        }
    }

    @Test
    public void testValidatePostMoveRequest(){
        PostMoveRequest request = new PostMoveRequest.Builder()
                .column(2).build();
        try{
            dropTokenValidator.validatePostMoveRequest("", "1", request);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for gameId. GameId cannot be empty"));
        }
        try{
            dropTokenValidator.validatePostMoveRequest("11", "", request);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for playerId. PlayerId cannot be empty"));
        }
        request = new PostMoveRequest.Builder().column(0).build();
        try{
            dropTokenValidator.validatePostMoveRequest("11", "1", request);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for column. Column cannot be less than or equal to 0"));
        }
    }

    @Test
    public void testValidateMoveRequest(){

        try{
            dropTokenValidator.validateMoveRequest("", 1);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for gameId. GameId cannot be empty"));
        }
        try{
            dropTokenValidator.validateMoveRequest("1", 0);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for moveId. MoveId cannot be less than or equal to 0"));
        }
    }

    @Test
    public void testValidateMovesRequest(){
        try{
            dropTokenValidator.validateMovesRequest("", 1, 2);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for gameId. GameId cannot be empty"));
        }
        try{
            dropTokenValidator.validateMovesRequest("1", -1, 2);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for start. Start cannot be less than or equal to 0"));
        }
        try{
            dropTokenValidator.validateMovesRequest("1", 1, -2);
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for until. Until cannot be less than 0 or start"));
        }
    }

    @Test
    public  void testValidateMoveQueryParam() {
        Game game = new Game();
        game.setMovesList(new ArrayList<>());

        try {
            dropTokenValidator.validateMoveQueryParam(game, 2, 5);
        } catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for start. Start cannot be greater than moves made"));
        }

        Move move = new Move();
        List<Move> movesList = game.getMovesList();
        movesList.add(move);
        game.setMovesList(movesList);
        try{
            dropTokenValidator.validateMoveQueryParam(game, 1, 5);
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for until. Until cannot be greater than moves made"));
        }

    }

    @Test
    public void testValidatePlayerQuitRequest() {
        try{
            dropTokenValidator.validatePlayerQuitRequest("", "1");
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for gameId. GameId cannot be empty"));
        }
        try{
            dropTokenValidator.validatePlayerQuitRequest("1", "");
            fail();
        }catch (BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for playerId. PlayerId cannot be empty"));
        }
    }

    @Test
    public void testValidateGame() {
        Game game = new Game();
        game.setState(Game.State.DONE.toString());
        try {
            dropTokenValidator.validateGame(game);
            fail();
        }catch (GoneException e){
            assertTrue(e.getMessage().contains("Game is already over."));
        }
    }

    @Test
    public void testValidateMove() {
        Move move = new Move();
        move.setPlayer("p1");
        Game game = new Game();
        game.setPlayers(Arrays.asList("p1", "p2"));
        game.setGrid(new String[4][4]);
        game.setState(Game.State.IN_PROGRESS.toString());
        List<Move> movesList = new ArrayList<>();
        movesList.add(move);
        game.setMovesList(movesList);
        PostMoveRequest request = new PostMoveRequest.Builder()
                .column(2).build();
        try{
            dropTokenValidator.validateMove(game, "p1", request);
        } catch (BadRequestException | NotFoundException e){
            assertTrue(e.getMessage().contains("Wait for other player to finish the turn"));
        }
        try{
            dropTokenValidator.validateMove(game, "p3", request);
        } catch (NotFoundException | BadRequestException e){
            assertTrue(e.getMessage().contains("Invalid input for playerId. No such player found for this game"));
        }
        request = new PostMoveRequest.Builder()
                .column(5).build();
        int cols = game.getGrid()[0].length;
        try{
            dropTokenValidator.validateMove(game, "p2", request);
        }catch (BadRequestException | NotFoundException e){
            assertTrue(e.getMessage().contains("Invalid input for column. Available input range is 1 to "+cols));
        }
        String[][] grid = game.getGrid();
        grid[3][2] = "p1";
        grid[2][2] = "p2";
        grid[1][2] = "p2";
        grid[0][2] = "p1";
        game.setGrid(grid);
        request = new PostMoveRequest.Builder()
                .column(2).build();
        try{
            dropTokenValidator.validateMove(game, "p2", request);
        }catch (BadRequestException | NotFoundException e){
            assertTrue(e.getMessage().contains("Invalid input for column. Column " +request.getColumn()+ " is full."));
        }

        request = new PostMoveRequest.Builder()
                .column(2).build();
        game.setState(Game.State.DONE.toString());
        try{
            dropTokenValidator.validateMove(game, "p2", request);
        }catch (BadRequestException | NotFoundException e){
            assertTrue(e.getMessage().contains("Invalid input. Game is over so cannot make move"));
        }
    }

    @Test
    public void testValidateMoveId(){
        Move move = new Move();
        move.setMoveLink("1");
        Game game = new Game();
        List<Move> movesList = new ArrayList<>();
        movesList.add(move);
        game.setMovesList(movesList);

        try{
            dropTokenValidator.validateMoveId(game, 2);
        }catch (NotFoundException e){
            assertTrue(e.getMessage().contains("Invalid input for moveId. No such move found for this game"));
        }


    }
}
