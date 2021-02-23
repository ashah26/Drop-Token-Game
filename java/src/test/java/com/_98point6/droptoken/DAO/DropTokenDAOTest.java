package com._98point6.droptoken.DAO;

import com._98point6.droptoken.DO.Game;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class DropTokenDAOTest {

    private  DropTokenDAO dropTokenDAO;

    @Before
    public void setup(){
        dropTokenDAO = new DropTokenDAO();
        MockitoAnnotations.openMocks(DropTokenDAOTest.class);
    }

    @Test
    public void testSave() {
        Game game = new Game();
        game.setGameId("1");
        game.setState(Game.State.IN_PROGRESS.toString());
        game.setPlayers(Arrays.asList("p1", "p2"));
        game.setGrid(new String[4][4]);
        dropTokenDAO.save(game);

        Game actualGame = dropTokenDAO.getGame(game.getGameId());
        assertEquals(game, actualGame);
    }

    @Test
    public void testGameIds(){
        for(int i=1; i<11; i++){
            Game game = new Game();
            game.setGameId(String.valueOf(i));
            if(i < 8){
                game.setState(Game.State.IN_PROGRESS.toString());
            }else{
                game.setState(Game.State.DONE.toString());
            }
            dropTokenDAO.save(game);
        }
        List<String> actualGameIdList = dropTokenDAO.getGameIds(Game.State.IN_PROGRESS.toString());
        assertEquals(7, actualGameIdList.size());

        actualGameIdList = dropTokenDAO.getGameIds(Game.State.DONE.toString());
        assertEquals(3, actualGameIdList.size());

        actualGameIdList = dropTokenDAO.getGameIds(null);
        assertEquals(10, actualGameIdList.size());
    }
}
