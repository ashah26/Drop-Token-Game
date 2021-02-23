package com._98point6.droptoken.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;

import java.util.List;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetMovesResponse {
    private List<GetMoveResponse> moves;

    public GetMovesResponse() {}

    private GetMovesResponse(Builder builder) {
        this.moves = Preconditions.checkNotNull(builder.moves);
    }

    public List<GetMoveResponse> getMoves() {
        return moves;
    }

    public static class Builder {
        private List<GetMoveResponse> moves;

        public Builder moves(List<GetMoveResponse> moves) {
            this.moves = moves;
            return this;
        }

        public Builder fromPrototype(GetMovesResponse prototype) {
            moves = prototype.moves;
            return this;
        }

        public GetMovesResponse build() {
            return new GetMovesResponse(this);
        }
    }
}
