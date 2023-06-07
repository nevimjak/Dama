package cz.nevimjak.client.model;

import cz.nevimjak.client.AlertFactory;
import cz.nevimjak.client.App;
import cz.nevimjak.client.Main;
import cz.nevimjak.client.SceneEnum;
import cz.nevimjak.client.controller.ConnectController;
import cz.nevimjak.client.controller.GameController;
import cz.nevimjak.client.controller.LobbyController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that recieve message and its run specifics actions
 */
public class MessageHandler {

    Window window;
	public int invalidMessages = 0;

    /**
     * Contructor to create instance
     * @param window
     */
    public MessageHandler(Window window) {
        this.window = window;
    }

    /**
     * Process the messages and make necessary actions depending on the message content
     * @param line message line
     * @throws IOException
     */
    public void proccessMessage(String line) throws IOException {
        if(line.length() == 0) return;

        System.out.println(line);

        String[] message = line.split("\\|");
        if(message[0].equalsIgnoreCase("hello")) {
            this.invalidMessages = 0;
            App.sendMessage("login_req|" + App.INSTANCE.player.getName());
            return;
        }

        if(message[0].equalsIgnoreCase("login_ok")) {
            this.invalidMessages = 0;
            if(message.length <= 2) {
                return;
            }

            if(App.INSTANCE.getSceneEnum() != SceneEnum.CONNECT) {
                return;
            }

            int state = Integer.parseInt(message[2]);

            Platform.runLater(() -> {
                App.INSTANCE.player = new Player(message[1], state);
                if(state == 2) {
                    App.INSTANCE.gameModel = new GameModel();
                    App.INSTANCE.setScene(SceneEnum.ROOM);
                } else if(state == 3 || state == 4) {
                    App.INSTANCE.gameModel = new GameModel();
                    App.INSTANCE.setScene(SceneEnum.GAME);
                    App.sendMessage("game_info_req");
                } else {
                    App.INSTANCE.setScene(SceneEnum.LOBBY);
                }
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("login_err")) {
            this.invalidMessages = 0;
            App.INSTANCE.disconnect();
            AlertFactory.sendErrorMessageOutside("Login error", "An error occurred while logging in.");
			
            return;
        }

        if(message[0].equalsIgnoreCase("room_list_data")) {

            this.invalidMessages = 0;
            if(message.length % 2 != 1) {
                AlertFactory.sendErrorMessageOutside("Server data error", "An error occurred in the server message");
				
				return;
			}

            if(message.length == 1) {
                App.INSTANCE.getStage().getScene().setCursor(Cursor.DEFAULT);
				
                return;
            }

            if(!(App.INSTANCE.getController() instanceof LobbyController)) {
                return;
            }
            LobbyController controller = (LobbyController) App.INSTANCE.getController();
            Platform.runLater(() -> {

                //controller.e
                ArrayList<Room> rooms = new ArrayList<Room>();
                for(int i = 2; i < message.length; i = i + 2) {
                    rooms.add(new Room(message[i], Integer.parseInt(message[i - 1])));
                }

                Platform.runLater(() -> {
                    controller.loadList(rooms);
                });
                App.INSTANCE.getStage().getScene().setCursor(Cursor.DEFAULT);

            });

            return;

        }

        if(message[0].equalsIgnoreCase("room_create_ok")) {
            this.invalidMessages = 0;

            App.INSTANCE.gameModel = new GameModel();

            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.ROOM);
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("game_info_data")) {
            this.invalidMessages = 0;
            if(message.length <= 4) {
				
                return;
            }

            if(message[1].equalsIgnoreCase("white")) {
                App.INSTANCE.gameModel.setPlayerColor(GameModel.PlayerColor.WHITE);
            } else if(message[1].equalsIgnoreCase("black")){
                App.INSTANCE.gameModel.setPlayerColor(GameModel.PlayerColor.BLACK);
            }
            if(message[2].equalsIgnoreCase("1")) {
                App.INSTANCE.gameModel.setPlaying(true);
            }

            App.INSTANCE.gameModel.setOpponentName(message[3]);

            PieceType[][] board = new PieceType[8][8];
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    board[i][j] = PieceType.EMPTY;
                }
            }

            for(int i = 4; i < message.length; i++) {
                int data = Integer.parseInt(message[i]);
                PieceType piece;
                int x = data/100;
                int y = (data%100)/10;

                switch (data%10) {
                    case 0: piece = PieceType.EMPTY; break;
                    case 1: piece = PieceType.WHITE; break;
                    case 2: piece = PieceType.WHITE_DAME; break;
                    case 3: piece = PieceType.BLACK_DAME; break;
                    case 4: piece = PieceType.BLACK; break;
                    default: piece = PieceType.EMPTY; break;
                }

                board[x][y] = piece;
            }

            App.INSTANCE.gameModel.board = board;

            Platform.runLater(() -> {
                ((GameController) App.INSTANCE.getController()).repaint();
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("room_join_ok")) {
            this.invalidMessages = 0;
            if(message.length < 2) {
				
                return;
            }

            App.INSTANCE.gameModel = new GameModel();
            App.INSTANCE.getGameModel().setOpponentName(message[1]);


            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.ROOM);
            });
			
            return;

        }

        if(message[0].equalsIgnoreCase("room_join_opp")) {
            this.invalidMessages = 0;
            if(message.length < 2) {
				
                return;
            }

            if(App.INSTANCE.getSceneEnum() != SceneEnum.GAME && App.INSTANCE.getSceneEnum() != SceneEnum.ROOM) {
				
                return;
            }

            App.INSTANCE.getGameModel().setOpponentName(message[1]);
            if(App.INSTANCE.getSceneEnum() == SceneEnum.GAME) {
                ((GameController) App.INSTANCE.getController()).protocolAdd("Teammate " + App.INSTANCE.getGameModel().getOpponentName() + " joined the game.");
            }
			
			return;
        }

        if(message[0].equalsIgnoreCase("room_leave_ok")) {
            this.invalidMessages = 0;
            App.INSTANCE.gameModel = null;

            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.LOBBY);
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("game_start")) {
            this.invalidMessages = 0;
            if(message.length < 2) {
				
                return;
            }

            if(App.INSTANCE.getSceneEnum() != SceneEnum.LOBBY && App.INSTANCE.getSceneEnum() != SceneEnum.ROOM) {
				
                return;
            }

            GameModel.PlayerColor color;

            if (message[1].equalsIgnoreCase("white")) {
                color = GameModel.PlayerColor.WHITE;
            } else if(message[1].equalsIgnoreCase("black")) {
                color = GameModel.PlayerColor.BLACK;
            } else {
				
                return;
            }

            App.INSTANCE.getGameModel().setPlayerColor(color);
            App.INSTANCE.getGameModel().init();

            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.GAME);
            });

			
            return;
        }

        if(message[0].equalsIgnoreCase("game_turn")) {
            this.invalidMessages = 0;
            App.INSTANCE.getGameModel().setPlaying(true);
            Platform.runLater(() -> {
                ((GameController) App.INSTANCE.getController()).repaint();
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("game_end")) {
            this.invalidMessages = 0;
            if(message.length < 1) {
                return;
            }

            if(App.INSTANCE.getSceneEnum() != SceneEnum.GAME) {
                return;
            }

            App.INSTANCE.gameModel.winnerColor = GameModel.PlayerColor.getPlayerColor(message[1]);

            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.GAME_RESULT);
            });
			
            return;
        }

        if(message[0].equalsIgnoreCase("game_move_data")) {
            this.invalidMessages = 0;
            if(message.length < 3) return;

            int fx = message[1].charAt(0) - 48;
            int fy = message[1].charAt(1) - 48;
            int x = message[2].charAt(0) - 48;
            int y = message[2].charAt(1) - 48;

            App.INSTANCE.getGameModel().makeMove(fx, fy, x, y);
            ((GameController) App.INSTANCE.getController()).protocolAdd("Opponent played move [" + fx + "," + fy + "] -> [" + x + "," + y + "]");
            ((GameController) App.INSTANCE.getController()).repaint();

			
            return;
        }
        if(message[0].equalsIgnoreCase("game_jump_data")) {
            this.invalidMessages = 0;
            if(message.length < 3) return;

            int fx = message[1].charAt(0) - 48;
            int fy = message[1].charAt(1) - 48;
            StringBuilder path = new StringBuilder();
            path.append("[" + fx + ";" + fy + "]");
            for(int i = 2; i < message.length; i++) {
                int x = message[i].charAt(0) - 48;
                int y = message[i].charAt(1) - 48;

                App.INSTANCE.getGameModel().makeJump(fx, fy, x, y);
                path.append(" -> [" + x + ";" + y + "]");
                fx = x;
                fy = y;

                ((GameController) App.INSTANCE.getController()).repaint();
            }

            ((GameController) App.INSTANCE.getController()).protocolAdd("Opponent played jump " + path.toString());

			
            return;
        }

        if(message[0].equalsIgnoreCase("game_board_data")) {
            this.invalidMessages = 0;
            PieceType[][] board = new PieceType[8][8];
            for(int i = 0; i < 8; i++) {
                for(int j = 0; j < 8; j++) {
                    board[i][j] = PieceType.EMPTY;
                }
            }

            for(int i = 1; i < message.length; i++) {
                if(message[i].length() == 3) {
                    int x = message[i].charAt(0) - 48;
                    int y = message[i].charAt(1) - 48;
                    int piece = message[i].charAt(2) - 48;
                    PieceType pieceType;

                    if(x > 7 || x < 0 || y > 7 || y < 0) {
                        return;
                    }

                    switch (piece) {
                        case 1: pieceType = PieceType.WHITE; break;
                        case 2: pieceType = PieceType.WHITE_DAME; break;
                        case 3: pieceType = PieceType.BLACK_DAME; break;
                        case 4: pieceType = PieceType.BLACK; break;
                        default: pieceType = PieceType.EMPTY; break;
                    }

                    board[x][y] = pieceType;

                }
            }

            App.INSTANCE.getGameModel().updateBoard(board);
            ((GameController) App.INSTANCE.getController()).repaint();
			
            return;
        }

        if(message[0].equalsIgnoreCase("logout_ok")) {
            this.invalidMessages = 0;
            App.INSTANCE.reciever.setRunning(false);
            App.INSTANCE.connectionModel.close();
            App.INSTANCE.connectionModel = null;
            Platform.runLater(() -> {
                App.INSTANCE.setScene(SceneEnum.CONNECT);
            });

			
            return;
        }

        if(message[0].equalsIgnoreCase("game_jump_err")) {
            this.invalidMessages = 0;
            ((GameController) App.INSTANCE.getController()).protocolAdd("The server returned a jump as invalid");
            App.sendMessage("game_info_req");
			
			return;
        }

        if(message[0].equalsIgnoreCase("game_move_err")) {
            this.invalidMessages = 0;
            ((GameController) App.INSTANCE.getController()).protocolAdd("The server returned a move as invalid");
            App.sendMessage("game_info_req");
			
			return;
        }
        if(message[0].equalsIgnoreCase("game_jump_ok")) {
            this.invalidMessages = 0;
			
			return;
		}

		if(message[0].equalsIgnoreCase("game_move_ok")) {
            this.invalidMessages = 0;
			
			return;
		}

		if(message[0].equalsIgnoreCase("game_join_err")) {
            this.invalidMessages = 0;
			
			return;
		}
		

		if(message[0].equalsIgnoreCase("logout_err")) {
            this.invalidMessages = 0;
			
			return;
		}

        if(message[0].equalsIgnoreCase("room_leave_opp")) {
            this.invalidMessages = 0;
            if(App.INSTANCE.getSceneEnum() == SceneEnum.GAME) {
                ((GameController) App.INSTANCE.getController()).protocolAdd("Teammate " + App.INSTANCE.getGameModel().getOpponentName() + " left the game.");
            }
        }

		this.invalidMessages++;
		if(this.invalidMessages > App.MAX_INVALID_MESSAGES) {
			App.INSTANCE.disconnect();
		};
    }
}
