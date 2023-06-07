package cz.nevimjak.client.model;

/**
 * Enumerate for pieces types
 */
public enum PieceType {
    WHITE, BLACK, WHITE_DAME, BLACK_DAME, EMPTY;

    /**
     * Is piece white
     * @return is piece white
     */
    public boolean isWhite() {
        return this == WHITE || this == WHITE_DAME;
    }

    /**
     * Is piece black
     * @return is piece black
     */
    public boolean isBlack() {
        return this == BLACK || this == BLACK_DAME;
    }

    /**
     * Is piece a dame
     * @return is piece a dame
     */
    public boolean isDame() {
        return this == WHITE_DAME || this == BLACK_DAME;
    }

    /**
     * Is piece a real piece
     * @return is it a piece
     */
    public boolean isPiece() {
        return this != EMPTY;
    }

    /**
     * Getter for player color
     * @return player color
     */
    public GameModel.PlayerColor getPlayerColor() {
        if(this.isWhite()) return GameModel.PlayerColor.WHITE;
        if(this.isBlack()) return GameModel.PlayerColor.BLACK;
        return null;
    }
}
