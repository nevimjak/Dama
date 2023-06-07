package cz.nevimjak.client.controller;

import cz.nevimjak.client.App;
import cz.nevimjak.client.Main;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller for Room view
 */
public class RoomController extends AbstractController {

    @FXML
    Label userNameLabel;

    /**
     * Method to first initialization
     */
    @FXML
    @Override
    public void initialize() {
        userNameLabel.setText(App.INSTANCE.player.getName());
    }

    /**
     * Method to handle leave room button
     */
    @FXML
    public void handleLeave() {
        App.sendMessage("room_leave_req");
        App.INSTANCE.getStage().getScene().setCursor(Cursor.WAIT);
    }
}
