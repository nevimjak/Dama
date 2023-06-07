package cz.nevimjak.client;

/**
 * Enumerate to scene views files
 */
public enum SceneEnum {

        CONNECT("/connect.fxml"),
        LOGIN("/login.fxml"),
        LOBBY("/rooms.fxml"),
        ROOM("/room.fxml"),
        GAME("/game.fxml"),
        GAME_RESULT("/result.fxml");

        public String path;

        /**
         * Constructor to enumerate
         * @param path
         */
        SceneEnum(String path) {
            this.path = path;
        }

}
