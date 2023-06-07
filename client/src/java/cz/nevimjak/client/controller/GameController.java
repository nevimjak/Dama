package cz.nevimjak.client.controller;

import cz.nevimjak.client.AlertFactory;
import cz.nevimjak.client.App;
import cz.nevimjak.client.Main;
import cz.nevimjak.client.model.GameModel;
import cz.nevimjak.client.model.PieceType;
import cz.nevimjak.client.model.Player;
import cz.nevimjak.client.model.Point;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class GameController extends AbstractController {

    @FXML
    Label userNameLabel;

    @FXML
    Canvas boardCanvas;

    @FXML
    ToolBar toolBar;

    @FXML
    AnchorPane anchorPane;

    @FXML
    AnchorPane sideBar;

    @FXML
    BorderPane borderPane;

    @FXML
    Text textLabel;

    @FXML
    TextArea protocol;

    Affine originalTransform;
    final double PAINT_WIDTH = 400.0;

    Point selected;
    GameModel gameModel = App.INSTANCE.getGameModel();

    LinkedList<Point> path = new LinkedList<Point>();

    /**
     * Initialization method to do init procedures.
     * Resizes canvas to fit window
     */
    @FXML
    @Override
    public void initialize() {

        //boardCanvas.widthProperty().bind(canvasPane.widthProperty());
        //boardCanvas.heightProperty().bind(canvasPane.heightProperty());

        boardCanvas.setWidth(anchorPane.getWidth());
        boardCanvas.setHeight(anchorPane.getHeight());

        GraphicsContext gc = boardCanvas.getGraphicsContext2D();


        borderPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if(!oldVal.equals(newVal)) {
                boardCanvas.setWidth(newVal.doubleValue() - sideBar.getWidth());
                repaint();
            }
        });
        borderPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if(!oldVal.equals(newVal)) {
                boardCanvas.setHeight(newVal.doubleValue() - toolBar.getPrefHeight());
                repaint();
            }
        });

        boardCanvas.setWidth(borderPane.getWidth() - sideBar.getWidth());
        boardCanvas.setHeight(borderPane.getHeight() - toolBar.getPrefHeight());
        userNameLabel.setText(App.INSTANCE.player.getName());

        repaint();
    }

    /**
     * Handle leaves button. Sends request to the server.
     */
    @FXML
    public void handleLeave() {
        if(AlertFactory.sendConfirmation("Leave The Game", "Are you sure you want to leave the game? The game will end.")) {
            App.sendMessage("room_leave_req");
        }
    }

    /**
     * Method to repaint canvas
     */
    public void repaint() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
        drawBoard(gc);
    }

    /**
     * Method to add message to the protocol pane
     * @param s
     */
    public void protocolAdd(String s) {
        protocol.appendText(s + "\n");
    }

    /**
     * Method to handle click on canvas.
     * Transform coordinations
     * @param event
     */
    @FXML
    public void handleCanvasClick(MouseEvent event) {
        if(!gameModel.isPlaying()) return;
        Point p = transform(event.getX(), event.getY());
        if(p == null) return;
        if(gameModel.isBlackBox(p)) {
            if(gameModel.canBeMoved(p.getX(), p.getY())) {
                path.clear();
                this.selected = p;
                repaint();
                return;
            }

            if(selected != null ) {

                if(gameModel.haveToJump()) {
                    if (gameModel.canBeJumpedTo(selected.getX(), selected.getY(), p.getX(), p.getY(), gameModel.getBoard()[selected.getX()][selected.getY()])) {
                        if(path.isEmpty()) {
                            path.add(new Point(selected.getX(), selected.getY()));
                        }
                        path.add(new Point(p.getX(), p.getY()));

                        gameModel.jump(selected.getX(), selected.getY(), p.getX(), p.getY());
                        if(gameModel.haveToJumpPiece(p.getX(), p.getY())) {
                            selected = new Point(p.getX(), p.getY());
                        } else {
                            gameModel.setPlaying(false);
                            StringBuilder pathString = new StringBuilder();
                            StringBuilder pathProtocol = new StringBuilder();
                            pathString.append("game_jump_req");
                            pathProtocol.append("[" + path.get(0).getX() + ";" + path.get(0).getY() + "]");
                            for(int i = 1; i < path.size(); i++) {
                                pathProtocol.append(" -> [" + path.get(i).getX() + ";" + path.get(i).getY() + "]");
                            }

                            for(Point point : path) {
                                pathString.append("|" + point.getX() + "" + point.getY());
                            }

                            this.protocolAdd("You played jump " + pathProtocol.toString());
                            App.sendMessage(pathString.toString());

                            path.clear();
                            selected = null;

                            //gameModel.setPlayerColor(gameModel.getPlayerColor().getOppoPlayerColor());
                        }
                        repaint();
                        return;
                    }
                } else {
                    if (gameModel.canBeMovedTo(selected.getX(), selected.getY(), p.getX(), p.getY())) {
                        if (path.isEmpty() && this.selected != null) {
                            gameModel.move(selected.getX(), selected.getY(), p.getX(), p.getY());
                            //System.out.printf("MOVE: FX=%d, FY=%d, TX=%d, TY=%d%n", selected.getX(), selected.getY(), p.getX(), p.getY());

                            protocolAdd("You played move [" + selected.getX() + ";" + selected.getY() + "] -> [" + p.getX() + ";" + p.getY() + "]");
                            App.sendMessage(String.format("game_move_req|%d%d|%d%d", selected.getX(), selected.getY(), p.getX(), p.getY()));

                            gameModel.setPlaying(false);
                            selected = null;
                            //gameModel.setPlayerColor(gameModel.getPlayerColor().getOppoPlayerColor());

                            repaint();
                            return;
                        }
                    }
                }

            }
        }

        repaint();

    }

    /**
     * Method that paint board on canvas
     * @param gc graphic context
     */
    public void drawBoard(GraphicsContext gc) {
        this.originalTransform = gc.getTransform();
        fillBackground(gc);

        double width = this.boardCanvas.getWidth();
        double height = this.boardCanvas.getHeight();

        double size = Math.min(width, height);
        double max = Math.max(width, height);

        double tx = 0, ty = 0;
        double scale = 1;

        if(width > height) {
            tx = (width - size) * 0.5;
        } else {
            ty = (height - size) * 0.5;
        }


		System.out.println("1");

        scale = size / PAINT_WIDTH;


        gc.translate(tx, ty);
        gc.scale(scale, scale);

        if(this.gameModel.getPlayerColor() == GameModel.PlayerColor.BLACK) {
            gc.translate(PAINT_WIDTH, PAINT_WIDTH);
            gc.rotate(180);
        }

        Affine defaultTransform = gc.getTransform();

		System.out.println("2");

        double oneBox = PAINT_WIDTH * 0.125; // jedna osmina

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if((i + j) % 2 == 1) {
                    gc.setFill(Color.GRAY);
                } else {
                    gc.setFill(Color.WHITE);
                }

                gc.fillRect(i * oneBox, j * oneBox, oneBox, oneBox);
            }
            //gc.translate(-PAINT_WIDTH, oneBox);
        }

		
		System.out.println("3");
        if(gameModel.isPlaying()) {
            List<Point> moveablePieces;
            gc.setFill(Color.GREEN);
            boolean haveToJump = gameModel.haveToJump();

            if (haveToJump) {
                moveablePieces = gameModel.getJumpablePieces();
            } else {
                moveablePieces = gameModel.getMoveablePieces();
            }

            for (Point p : moveablePieces) {
                gc.fillRect(p.getX() * oneBox, (7 - p.getY()) * oneBox, oneBox, oneBox);
            }
			
			System.out.println("4");

            if (this.selected != null) {
                gc.setFill(Color.YELLOW);
                gc.fillRect(selected.getX() * oneBox, (7 - selected.getY()) * oneBox, oneBox, oneBox);

                if (haveToJump) {
                    gc.setFill(Color.CORAL);
                    for (Point p : gameModel.getPossibleJumps(this.selected.getX(), this.selected.getY())) {
                        gc.fillRect(p.getX() * oneBox, (7 - p.getY()) * oneBox, oneBox, oneBox);

                    }
                } else {
                    gc.setFill(Color.INDIANRED);
                    for (Point p : gameModel.getPossibleMoves(this.selected)) {
                        gc.fillRect(p.getX() * oneBox, (7 - p.getY()) * oneBox, oneBox, oneBox);

                    }
                }
            }
			
			System.out.println("5");
        }

        gc.setTransform(defaultTransform);

        drawPieces(gc);

		System.out.println("6");

        gc.setTransform(this.originalTransform);
    }

    /**
     * Method that draws pieces on the board on the canvas
     * @param gc graphic context
     */
    void drawPieces(GraphicsContext gc) {

        double oneBoxSize = PAINT_WIDTH * 0.125;

        PieceType[][] board = gameModel.getBoard();

        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                drawPiece(gc, i, j, oneBoxSize, board[i][j]);
            }
        }

    }

    /**
     * Method that draws one single piece on the board on the canvas
     * @param gc graphic context
     * @param x x coordination
     * @param y y coordination
     * @param size default width of one box on the board
     * @param pieceType type of the piece
     */
    void drawPiece(GraphicsContext gc, int x, int y, double size, PieceType pieceType) {

        if(pieceType == null || !pieceType.isPiece()) {
            return;
        }

        Affine transform = gc.getTransform();

        y = 7 - y;

        gc.translate(x * size, y * size);
        if(pieceType.isWhite()) {
            gc.setFill(Color.WHITE);
        } else {
            gc.setFill(Color.BLACK);
        }

        gc.translate(size * 0.1, size * 0.1);
        gc.fillOval(0, 0, size * 0.8, size * 0.8);

        if(pieceType.isWhite()) {
            gc.setFill(Color.LIGHTGRAY);
        } else {
            gc.setFill(Color.DARKGRAY);
        }
        gc.translate(size * 0.1, size * 0.1);
        gc.fillOval(0, 0, size * 0.6, size * 0.6);

        if(pieceType.isDame()) {
            if(pieceType.isWhite()) {
                gc.setFill(Color.GRAY);
            } else {
                gc.setFill(Color.LIGHTGRAY);
            }
            gc.translate(size * 0.2, size * 0.2);
            gc.fillOval(0, 0, size * 0.2, size * 0.2);

        }

        gc.setTransform(transform);

    }

    /**
     * Method to paint background on the canvas
     * @param gc graphic context
     */
    public void fillBackground(GraphicsContext gc) {
        Affine transform = gc.getTransform();
        gc.setTransform(this.originalTransform);
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.setTransform(transform);
    }

    /**
     * Method to transform coordination from canvas to game coordination
     * @param x x coordinate
     * @param y y coordinate
     * @return instance of Point that includes game coordination
     */
    public Point transform(double x, double y) {

        double width = this.boardCanvas.getWidth();
        double height = this.boardCanvas.getHeight();

        double size = Math.min(width, height);

        double tx = 0, ty = 0;
        double scale = 1;

        if(width > height) {
            tx = (width - size) * 0.5;
        } else {
            ty = (height - size) * 0.5;
        }

        scale = PAINT_WIDTH / size;
        double boxSize = PAINT_WIDTH * 0.125;

        x -= tx;
        y -= ty;

        x *= scale;
        y *= scale;

        x /= boxSize;
        y /= boxSize;

        int nx = (int) Math.floor(x);
        int ny = 7 - (int) Math.floor(y);

        if(nx > 7 || nx < 0) return null;
        if(ny > 7 || ny < 0) return null;

        if(this.gameModel.getPlayerColor() == GameModel.PlayerColor.BLACK) {
            nx = 7 - nx;
            ny = 7 - ny;
        }

        return new Point(nx, ny);
    }
}
