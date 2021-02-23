package com._98point6.droptoken.DAO;

import com._98point6.droptoken.DO.Game;
import com._98point6.droptoken.error.NotFoundException;
import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DropTokenDAO {

    private Map<String, Game> gamesMap = null;

    public DropTokenDAO(){
        gamesMap = new HashMap<>();
    }

    public void save(Game game) {
        if (StringUtils.isBlank(game.getGameId())) {
            game.setGameId(UUID.randomUUID().toString());
        }
        gamesMap.put(game.getGameId(), game);
    }

    public List<String> getGameIds(String gameStatus) {
        return gamesMap.values()
                .stream()
                .filter(game -> StringUtils.isBlank(gameStatus) || gameStatus.equals(game.getState()))
                .map(Game::getGameId)
                .collect(Collectors.toList());
    }

    public Game getGame(String gameId){
        if(gamesMap.get(gameId) == null){
            throw new NotFoundException("Invalid input for gameId.Game not found");
        }
        return gamesMap.get(gameId);
    }
}
