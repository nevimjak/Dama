package cz.nevimjak.client;

import cz.nevimjak.client.model.*;
import cz.nevimjak.client.controller.*;
import cz.nevimjak.client.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * Main class of the application
 */
public class App extends Application {

    public static App INSTANCE;
    public ConnectionModel connectionModel;
    private MessageHandler messageHandler;
    public Reciever reciever;
    private FXMLLoader fxmlLoader;
    private Stage stage;
    public SceneEnum actualScene;
    public Player player;
    public GameModel gameModel;
    private SceneEnum sceneEnum;
    public String lastEnteredUsername;
    public String lastEnteredHostname;
    public String lastEnteredPort;

    public static final int MAX_INVALID_MESSAGES = 5;

    /**
     * Method that starts the application
     * @param stage initial stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        INSTANCE = this;
        this.stage = stage;

        stage.setTitle("Checkers");
        this.messageHandler = new MessageHandler(stage);
        this.setScene(SceneEnum.CONNECT);

    }

    /**
     * Getter for controller of actual scene
     * @return controller of actual scene
     */
    public AbstractController getController() {
        return this.fxmlLoader.getController();
    }

    /**
     * Sets scene from outside of JavaFX thread
     * @param scene scene type
     */
    public void setSceneOutside(SceneEnum scene) {
        Platform.runLater(() -> {
            setScene(scene);
        });
    }

    /**
     * Sets application scene
     * @param scene
     */
    public void setScene(SceneEnum scene) {

        URL url = getClass().getResource(scene.path);
        Parent root = null;
        SceneEnum bakScene = this.actualScene;

        this.actualScene = scene;
        this.fxmlLoader = new FXMLLoader(url);
        try {

            root = this.fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            this.actualScene = bakScene;
            return;
        }
        Scene s = new Scene(root);
        stage.setScene(s);
        this.sceneEnum = scene;
        stage.show();
        if(scene == SceneEnum.GAME) {
            AbstractController controller = (AbstractController) this.getController();
            controller.initialize();
        }
    }

    /**
     * Method to create new connection to the server
     * @param address address of the server
     * @param port port of the service
     * @throws IOException
     */
    public void connect(String address, int port) throws IOException {
        this.connectionModel = new ConnectionModel(address, port);
        this.reciever = new Reciever(this.connectionModel, this.messageHandler);
        this.reciever.start();
    }

    /**
     * Entry point of the application
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Method to close the application
     * @param event
     */
    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Method to stop the program
     */
    @Override
    public void stop(){
        System.exit(0);
        // Save file
    }

    /**
     * Getter for stage
     * @return stage of the application
     */
    public Stage getStage() {
        return this.stage;
    }

    /**
     * Method that provides disconnection from the server
     */
    public void disconnect() {
        this.reciever.setRunning(false);
        try {
            this.connectionModel.close();
        } catch (IOException e) {
            this.connectionModel = null;
        }
        this.connectionModel = null;

        this.setSceneOutside(SceneEnum.CONNECT);
    }

    /**
     * Getter for the game model instance
     * @return game model
     */
    public GameModel getGameModel() {
        return this.gameModel;
    }

    /**
     * Getter for actual scene type
     * @return
     */
    public SceneEnum getSceneEnum() {
        return this.sceneEnum;
    }

    /**
     * Sends a message to a server
     * @param message
     */
    public static void sendMessage(String message) {
        if(App.INSTANCE.connectionModel != null) {
            App.INSTANCE.connectionModel.sendMessage(message);
        }
    }
}