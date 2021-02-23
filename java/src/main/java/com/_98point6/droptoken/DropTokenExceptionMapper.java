package com._98point6.droptoken;

import com._98point6.droptoken.error.BadRequestException;
import com._98point6.droptoken.error.GoneException;
import com._98point6.droptoken.error.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 *
 */
public class DropTokenExceptionMapper implements ExceptionMapper<RuntimeException>  {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenExceptionMapper.class);
    public Response toResponse(RuntimeException e) {
        if(e instanceof BadRequestException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        if (e instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
        if(e instanceof GoneException){
            return Response.status(Response.Status.GONE).entity(e.getMessage()).build();
        }
        logger.error("Unhandled exception.", e);
        return Response.status(500).build();
    }
}
