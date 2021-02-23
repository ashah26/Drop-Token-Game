package com._98point6.droptoken;

import com._98point6.droptoken.model.CreateGameRequest;
import com._98point6.droptoken.model.PostMoveRequest;
import com._98point6.droptoken.service.DropTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class DropTokenResource {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenResource.class);

    DropTokenService dropTokenService;

    public DropTokenResource() {
        dropTokenService = new DropTokenService();
    }

    @GET
    public Response getGames() {
        return Response.ok(dropTokenService.getGames()).build();
    }

    @POST
    public Response createNewGame(CreateGameRequest request) {
        logger.info("request={}", request);
        return Response.ok(dropTokenService.createNewGame(request)).build();
    }

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) {
        logger.info("gameId = {}", gameId);
        return Response.ok(dropTokenService.getGameStatus(gameId)).build();
    }

    @Path("/{id}/{playerId}")
    @POST
    public Response postMove(@PathParam("id")String gameId, @PathParam("playerId") String playerId, PostMoveRequest request) {
        logger.info("gameId={}, playerId={}, move={}", gameId, playerId, request);
        return Response.ok(dropTokenService.postMove(gameId, playerId, request)).build();
    }

    @Path("/{id}/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id")String gameId, @PathParam("playerId") String playerId) {
        logger.info("gameId={}, playerId={}", gameId, playerId);
        dropTokenService.playerQuit(gameId, playerId);
        return Response.status(202).build();
    }

    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId, @QueryParam("start") Integer start, @QueryParam("until") Integer until) {
        logger.info("gameId={}, start={}, until={}", gameId, start, until);
        return Response.ok(dropTokenService.getMoves(gameId, start, until)).build();
    }

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @PathParam("moveId") Integer moveId) {
        logger.info("gameId={}, moveId={}", gameId, moveId);
        return Response.ok(dropTokenService.getMove(gameId, moveId)).build();
    }
}
