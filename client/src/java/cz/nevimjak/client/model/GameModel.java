package cz.nevimjak.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent game login
 */
public class GameModel {

    PieceType[][] board;
    boolean playing;
    PlayerColor playerColor;
    public List<String> protocol;
    public String opponentName;
    public PlayerColor winnerColor;

    /**
     * Creates instance and initialize attributes
     */
    public GameModel() {
        board = new PieceType[8][8];
        this.playing = false;
        this.playerColor = playerColor;
        this.protocol = new ArrayList<String>();
        this.winnerColor = null;

    }

    /**
     * Initialization of the game board
     */
    public void init() {for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = PieceType.EMPTY;

                if((i + j) % 2 == 0) {
                    if(j > 4) {
                        board[i][j] = PieceType.BLACK;
                    }

                    if(j < 3) {
                        board[i][j] = PieceType.WHITE;
                    }
                }
            }
        }
    }

    /**
     * Tests if piece can be moved from source direction to destination direction
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     * @return boolean value
     */
    public boolean canBeMovedTo(int x1, int y1, int x2, int y2) {
        if(this.board[x1][y1].getPlayerColor() != this.playerColor) return false;

        if(haveToJump()) return false;

        int xdif = x2 - x1;
        int ydif = y2 - y1;
        int xdir = xdif > 0 ? 1 : -1;
        int ydir = ydif > 0 ? 1 : -1;


        if(Math.abs(xdif) != Math.abs(ydif)) return false;

        if(this.board[x1][y1].isDame()) {
            for(int i = 1; i < xdif; i++) {
                if(this.board[x1 + i * xdir][y1 + i * ydir].isPiece()) {
                    return false;
                }
            }
        } else {
            if(Math.abs(xdif) != 1) return false;
            if(this.board[x1][y1].isWhite() && ydif < 0) return false;
            if(this.board[x1][y1].isBlack() && ydif > 0) return false;

            if(this.board[x2][y2].isPiece()) return false;
        }
        return true;

    }

    /**
     * Tests if piece can be jumped from source direction to destination direction
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     * @param pieceType type of the figure
     * @return boolean value
     */
    public boolean canBeJumpedTo(int x1, int y1, int x2, int y2, PieceType pieceType) {
        int difx = x2 - x1;
        int dify = y2 - y1;

        int x3 = (int) ((x1 + x2) * 0.5);
        int y3 = (int) ((y1 + y2) * 0.5);

        if(Math.abs(difx) != Math.abs(dify)) return false;
        if(Math.abs(difx) != 2) return false;

        if(!pieceType.isPiece()) return false;
        if(pieceType.getPlayerColor() != this.playerColor) return false;
		if(this.board[x3][y3].getPlayerColor() != this.playerColor.getOppoPlayerColor()) return false;

        if(!pieceType.isDame()) {
            if(pieceType.isWhite() && dify < 0) return false;
            if(pieceType.isBlack() && dify > 0) return false;
        }

        return true;
    }

    /**
     * Provide validation and then jump
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     */
    public void jump(int x1, int y1, int x2, int y2) {
        if(canBeJumpedTo(x1, y1, x2, y2, this.board[x1][y1])) {
            makeJump(x1, y1, x2, y2);
        }
    }

    /**
     * Provide validation and then move
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     */
    public void move(int x1, int y1, int x2, int y2) {
        if(canBeMovedTo(x1, y1, x2, y2)) {
            makeMove(x1, y1, x2, y2);
        }
    }

    /**
     * Makes jump with the figure on the source to destination position
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     */
    public void makeJump(int x1, int y1, int x2, int y2) {

        this.board[x2][y2] = this.board[x1][y1];
        this.board[(int)((x1 + x2) * 0.5)][(int) ((y1 + y2) * 0.5)] = PieceType.EMPTY;
        this.board[x1][y1] = PieceType.EMPTY;

        if(!this.board[x2][y2].isDame()) {
            if(this.board[x2][y2].isWhite() && y2 == 7) {
                this.board[x2][y2] = PieceType.WHITE_DAME;
            }

            if(this.board[x2][y2].isBlack() && y2 == 0) {
                this.board[x2][y2] = PieceType.BLACK_DAME;
            }
        }

    }

    /**
     * Makes jump with the figure on the source to destination position
     * @param x1 source x coordinate
     * @param y1 source y coordinate
     * @param x2 destination x coordinate
     * @param y2 destination y coordinate
     */
    public void makeMove(int x1, int y1, int x2, int y2) {
        this.board[x2][y2] = this.board[x1][y1];
        this.board[x1][y1] = PieceType.EMPTY;

        if(!this.board[x2][y2].isDame()) {
            if(this.board[x2][y2].isWhite() && y2 == 7) {
                this.board[x2][y2] = PieceType.WHITE_DAME;
            }

            if(this.board[x2][y2].isBlack() && y2 == 0) {
                this.board[x2][y2] = PieceType.BLACK_DAME;
            }
        }
    }

    /**
     * Switch board array in model
     * @param board board
     */
    public void updateBoard(PieceType[][] board) {
        this.board = board;
    }

    /**
     * Getter for playing boolean value
     * @return boolean value
     */
    public boolean isPlaying() {
        return this.playing;
    }

    /**
     * Setter for playing value
     * @param playing boolean value playing
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * Getter for game board
     * @return game board
     */
    public PieceType[][] getBoard() {
        return this.board;
    }

    /**
     * Gets list of pieces that can make move
     * @return list of pieces
     */
    public List<Point> getMoveablePieces() {
        List<Point> moveablePieces = new ArrayList<Point>();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(!board[i][j].isPiece()) continue;
                if(board[i][j].getPlayerColor() != this.playerColor) continue;

                if(board[i][j].isDame()) {
                    if(i > 0) {
                        if(j > 0) {
                            if(!board[i - 1][j - 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }

                        if(j < 7) {
                            if(!board[i - 1][j + 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }
                    }

                    if(i < 7) {
                        if(j > 0) {
                            if(!board[i + 1][j - 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }

                        if(j < 7) {
                            if(!board[i + 1][j + 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }
                    }
                } else {
                    if (this.playerColor == PlayerColor.WHITE) {
                        if (i > 0 && j < 7) {
                            if (!board[i - 1][j + 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }

                        if (i < 7 && j < 7) {
                            if (!board[i + 1][j + 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }
                    } else if (this.playerColor == PlayerColor.BLACK) {
                        if (i > 0 && j > 0) {
                            if (!board[i - 1][j - 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }

                        if (i < 7 && j > 0) {
                            if (!board[i + 1][j - 1].isPiece()) {
                                moveablePieces.add(new Point(i, j));
                                continue;
                            }
                        }
                    }
                }
            }
        }

        return moveablePieces;
    }

    /**
     * Gets a list of pieces that can make jump
     * @return list of pieces
     */
    public List<Point> getJumpablePieces() {
        List<Point> jumpablePieces = new ArrayList<Point>();
        int dameCount = 0;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(!this.board[i][j].isPiece()) continue;
                if(!this.board[i][j].isDame()) continue;
                if(this.board[i][j].getPlayerColor().getOppoPlayerColor() == playerColor) continue;
                if(haveToJumpPiece(i, j)) {
                    jumpablePieces.add(new Point(i, j));
                }
            }
        }

        if(dameCount > 0) return jumpablePieces;

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(!this.board[i][j].isPiece()) continue;
                if(this.board[i][j].getPlayerColor().getOppoPlayerColor() == playerColor) continue;
                if(haveToJumpPiece(i, j)) {
                    jumpablePieces.add(new Point(i, j));
                }
            }
        }

        return jumpablePieces;
    }

    /**
     * Gets a list of possible jumps positions for piece on entered coordinates
     * @param x source piece x coordinate
     * @param y source piece y coordinate
     * @return list of possible jumps
     */
    public List<Point> getPossibleJumps(int x, int y) {
        List<Point> possibleJumps = new ArrayList<Point>();

        PlayerColor color = board[x][y].getPlayerColor();

        if(board[x][y].isWhite() || board[x][y].isDame()) {
            if(x > 1 && y < 6) {
                if(!board[x - 2][y + 2].isPiece() && board[x - 1][y + 1].isPiece()) {
                    if(board[x - 1][y + 1].getPlayerColor().getOppoPlayerColor() == color) {
                        possibleJumps.add(new Point(x - 2, y + 2));
                    }
                }
            }

            if(x < 6 && y < 6) {
                if(!board[x + 2][y + 2].isPiece() && board[x +1][y + 1].isPiece()) {
                    if(board[x +1][y + 1].getPlayerColor().getOppoPlayerColor() == color) {
                        possibleJumps.add(new Point(x + 2, y + 2));
                    }
                }
            }

        }

        if(board[x][y].isBlack() || board[x][y].isDame()) {

            if(x > 1 && y > 1) {
                if(!board[x - 2][y - 2].isPiece() && board[x - 1][y - 1].isPiece()) {
                    if(board[x - 1][y - 1].getPlayerColor().getOppoPlayerColor() == color) {
                        possibleJumps.add(new Point(x - 2, y - 2));
                    }
                }
            }

            if(x < 6 && y > 1) {
                if(!board[x + 2][y - 2].isPiece() && board[x + 1][y - 1].isPiece()) {
                    if(board[x + 1][y - 1].getPlayerColor().getOppoPlayerColor() == color) {
                        possibleJumps.add(new Point(x + 2, y - 2));
                    }
                }
            }

        }

        return possibleJumps;
    }

    /**
     * Gets list of possible moves for source position
     * @param p source piece position
     * @return list of possible moves
     */
    public List<Point> getPossibleMoves(Point p) {
        List<Point> possibleMoves = new ArrayList<Point>();

        if(board[p.getX()][p.getY()].isDame()) {

            boolean ul = true, ur = true, dl = true, dr = true;

            for(int i = 1; i < 8; i++) {
                if (ul) {
                    if (p.getX() - i >= 0 && p.getY() + i <= 7) {
                        if (!board[p.getX() - i][p.getY() + i].isPiece()) {
                            possibleMoves.add(new Point(p.getX() - i, p.getY() + i));
                        } else {
                            ul = false;
                        }
                    }
                }

                if (ur) {
                    if (p.getX() + i <= 7 && p.getY() + i <= 7) {
                        if (!board[p.getX() + i][p.getY() + i].isPiece()) {
                            possibleMoves.add(new Point(p.getX() + i, p.getY() + i));
                        } else {
                            ur = false;
                        }
                    } else {
                        ur = false;
                    }
                }

                if (dl) {
                    if (p.getX() - i >= 0 && p.getY() - i >= 0) {
                        if (!board[p.getX() - i][p.getY() - i].isPiece()) {
                            possibleMoves.add(new Point(p.getX() - i, p.getY() - i));
                        } else {
                            dl = false;
                        }
                    }
                }

                if (dr) {
                    if (p.getX() + i <= 7 && p.getY() - i >= 0) {
                        if (!board[p.getX() + i][p.getY() - i].isPiece()) {
                            possibleMoves.add(new Point(p.getX() + i, p.getY() - i));
                        } else {
                            dr = false;
                        }
                    }
                }
            }


            return possibleMoves;
        }

        if(board[p.getX()][p.getY()].isWhite() && this.playerColor == PlayerColor.WHITE) {

            if(p.getX() > 0 && p.getY() < 7) {
                if(!board[p.getX() - 1][p.getY() + 1].isPiece()) {
                    possibleMoves.add(new Point(p.getX() - 1, p.getY() + 1));
                }
            }

            if(p.getX() < 7 && p.getY() < 7) {
                if(!board[p.getX() + 1][p.getY() + 1].isPiece()) {
                    possibleMoves.add(new Point(p.getX() + 1, p.getY() + 1));
                }
            }

        } else if(board[p.getX()][p.getY()].isBlack() && this.playerColor == PlayerColor.BLACK) {

            if(p.getX() > 0 && p.getY() > 0) {
                if(!board[p.getX() - 1][p.getY() - 1].isPiece()) {
                    possibleMoves.add(new Point(p.getX() - 1, p.getY() - 1));
                }
            }

            if(p.getX() < 7 && p.getY() > 0) {
                if(!board[p.getX() + 1][p.getY() - 1].isPiece()) {
                    possibleMoves.add(new Point(p.getX() + 1, p.getY() - 1));
                }
            }

        }

        return possibleMoves;
    }

    /**
     * Validation that given position is black
     * @param p position
     * @return boolean value
     */
    public boolean isBlackBox(Point p) {
        return (p.getX() + p.getY()) % 2 == 0;
    }

    /**
     * Gets opponent name
     * @return opponent name
     */
    public String getOpponentName() {
        return this.opponentName;
    }

    /**
     * Sets opponents name
     * @param opponentName opponents player name
     */
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    /**
     * Gets player color
     * @return player color
     */
    public PlayerColor getPlayerColor() {
        return this.playerColor;
    }

    /**
     * Sets player color
     * @param playerColor player color
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     * Finds out if the piece have to jump
     * @param x x piece coordinate
     * @param y y piece coordinate
     * @return boolean value
     */
    public boolean haveToJumpPiece(int x, int y) {
        if(!board[x][y].isPiece()) {
            return false;
        }

        PlayerColor color = board[x][y].getPlayerColor();

        if(x > 1) {
            if((color == PlayerColor.WHITE || board[x][y].isDame()) && y < 6) {
                if(!board[x - 2][y + 2].isPiece() && board[x - 1][y + 1].getPlayerColor() == color.getOppoPlayerColor()) {
                    return true;
                }
            }

            if((color == PlayerColor.BLACK || board[x][y].isDame()) && y > 1) {
                if(!board[x - 2][y - 2].isPiece() && board[x - 1][y - 1].getPlayerColor() == color.getOppoPlayerColor()) {
                    return true;
                }
            }
        }

        if(x < 6) {
            if((color == PlayerColor.WHITE || board[x][y].isDame()) && y < 6) {
                if(!board[x + 2][y + 2].isPiece() && board[x + 1][y + 1].getPlayerColor() == color.getOppoPlayerColor()) {
                    return true;
                }
            }

            if((color == PlayerColor.BLACK || board[x][y].isDame()) && y > 1) {
                if(!board[x + 2][y - 2].isPiece() && board[x + 1][y - 1].getPlayerColor() == color.getOppoPlayerColor()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method that finds out if player have to jump
     * @return boolean value
     */
    public boolean haveToJump() {

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {

                if(!this.board[i][j].isPiece()) continue;

                if(this.board[i][j].getPlayerColor() != playerColor) continue;

                if(haveToJumpPiece(i, j)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Validation for the piece can be moved
     * @param x x piece coordinate
     * @param y y piece coordinate
     * @return
     */
    public boolean canBeMoved(int x, int y) {
        List<Point> moves = this.getJumpablePieces();
        for(Point p : moves) {
            if(p.getX() == x && p.getY() == y) return true;
        }

        if(moves.isEmpty()) {
            for (Point p : this.getMoveablePieces()) {
                if (p.getX() == x && p.getY() == y) return true;
            }
        }

        return false;

    }

    /**
     * Enumerate class for player color
     */
    public enum PlayerColor {
        WHITE, BLACK;

        /**
         * Gets color of opponent to given color
         * @param color player color
         * @return opponents color
         */
        public static PlayerColor getOppositeColor(PlayerColor color) {
            if(color == WHITE) return BLACK;
            if(color == BLACK) return WHITE;
            return null;
        }

        /**
         * Gets opponent color
         * @return opponent color
         */
        public PlayerColor getOppoPlayerColor() {
            return getOppositeColor(this);
        }

        public static PlayerColor getPlayerColor(String s) {
            switch(s) {
                case "white": return WHITE;
                case "black": return BLACK;
                default: return null;
            }
        }
    }


}
