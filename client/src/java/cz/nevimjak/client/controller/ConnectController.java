package cz.nevimjak.client.controller;

import cz.nevimjak.client.*;
import cz.nevimjak.client.model.ConnectionModel;
import cz.nevimjak.client.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller to handle Connect view requests
 */
public class ConnectController extends AbstractController {

    @FXML
    TextField addressInput;

    @FXML
    TextField portInput;

    @FXML
    TextField usernameInput;

    @FXML
    Button connectButton;

    @FXML
    VBox vbox;

    @FXML
    StackPane stackPane;

    /**
     * Initialization method
     */
    @FXML
    public void initialize() {
        if(App.INSTANCE.lastEnteredHostname != null) {
            this.addressInput.setText(App.INSTANCE.lastEnteredHostname);
        }
        if(App.INSTANCE.lastEnteredPort != null) {
            this.portInput.setText(App.INSTANCE.lastEnteredPort);
        }
        if(App.INSTANCE.lastEnteredUsername != null) {
            this.usernameInput.setText(App.INSTANCE.lastEnteredUsername);
        }
    }

    /**
     * Method to handle connect button. Connects to the server.
     * @throws IOException
     */
    @FXML
    public void connect() throws IOException {

        if(App.INSTANCE.connectionModel != null && App.INSTANCE.connectionModel.isConnected()) {
            return;
        }
        String address = "localhost";
        int port = 9123;

        if(addressInput.getText().length() > 0) {
            address = addressInput.getText();
        }

        if(portInput.getText().length() != 0) {
            try {
                port = Integer.parseInt(portInput.getText());
                if(port < 0 || port > 65535) {
                    AlertFactory.sendWarningMessage("Invalid Format", "Port have to be in range from 0 to 25535");
                    return;
                }
            } catch (NumberFormatException e) {
                AlertFactory.sendWarningMessage("Invalid Format", "Port have to be a number");
                return;
            }
        }

        if(usernameInput.getText().length() == 0) {
            AlertFactory.sendWarningMessage("Invalid Format", "Username is required");
            return;
        }

        App.INSTANCE.player = new Player(usernameInput.getText());

        VBox box = new VBox(new ProgressIndicator());
        box.setAlignment(Pos.CENTER);
        vbox.setDisable(true);
        stackPane.getChildren().add(box);

        if(App.INSTANCE.connectionModel != null) {
            try {
                App.INSTANCE.connectionModel.close();
                App.INSTANCE.connectionModel = null;
            } catch (IOException e) {
                stackPane.getChildren().remove(box);
                vbox.setDisable(false);
                AlertFactory.sendErrorMessage("An Error Occurred", "An error occurred while terminating the previous connection");
                return;
            }
        }

        if(addressInput.getText().length() > 0) {
            App.INSTANCE.lastEnteredHostname = addressInput.getText();
        }
        if(portInput.getText().length() > 0) {
            App.INSTANCE.lastEnteredPort = portInput.getText();
        }
        if(usernameInput.getText().length() > 0) {
            App.INSTANCE.lastEnteredUsername = usernameInput.getText();
        }

        try {
            App.INSTANCE.connect(address, port);
        } catch (IOException e) {
            stackPane.getChildren().remove(box);
            vbox.setDisable(false);
            AlertFactory.sendErrorMessage("Connection error", "The connection was not established");
            return;
        }

    }


}
