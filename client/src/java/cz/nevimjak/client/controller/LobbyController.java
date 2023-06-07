package cz.nevimjak.client.controller;

import cz.nevimjak.client.AlertFactory;
import cz.nevimjak.client.App;
import cz.nevimjak.client.model.Room;
import cz.nevimjak.client.model.Stats;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller to handle Lobby view requests
 */
public class LobbyController extends AbstractController {

    @FXML
    ListView<Room> roomList;

    @FXML
    Label userNameLabel;

    @FXML
    Button joinButton;

    /**
     * Method to first initialization
     */
    @FXML
    @Override
    public void initialize() {
        userNameLabel.setText(App.INSTANCE.player.getName());

        joinButton.setDisable(true);

        roomList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Room>() {
             public void changed(ObservableValue<? extends Room> observable,
                                 Room oldValue, Room newValue) {
                 joinButton.setDisable(newValue == null);

             }
         });

        handleRefresh();
    }


    /**
     * Method to handle logout request. Sends request to log out to server
     */
    @FXML
    public void handleLogOut() {
        if(AlertFactory.sendConfirmation("Log Out", "Are you sure you want to log out?")) {
            App.sendMessage("logout_req");
        }
    }

    /**
     * Method to handle refresh button. Sends request to server
     */
    @FXML
    public void handleRefresh() {
        App.INSTANCE.getStage().getScene().setCursor(Cursor.WAIT);
        App.sendMessage("room_list_req");
    }

    /**
     * Method to handle join button. Sends request to server
     */
    @FXML
    public void handleJoin() {
        Room room = roomList.getSelectionModel().getSelectedItem();

        App.sendMessage("room_join_req|" + room.getId());
    }

    /**
     * Method to handle room create button. Sends request to server
     */
    @FXML
    public void handleRoomCreate() {
        App.sendMessage("room_create_req");
    }

    /**
     * Fills room list from argument list
     * @param rooms
     */
    public void loadList(ArrayList<Room> rooms) {
        roomList.getItems().clear();

        for(Room r : rooms) {
            roomList.getItems().add(r);
        }
    }

    /**
     * Handle stats button. Shows statistics of the connection
     */
    @FXML
    public void handleStats() {
        AlertFactory.sendMessage(Alert.AlertType.INFORMATION, "Connection statistics",
                "Recieved messages: " + Stats.INSTANCE.recievedMessages +
                "\nRecieved bytes: " + Stats.INSTANCE.recievedBytes +
                "\nSent messages: " + Stats.INSTANCE.sentMessages +
                "\nSent bytes: " + Stats.INSTANCE.sentBytes);
    }




}
