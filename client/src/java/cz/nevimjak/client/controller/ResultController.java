package cz.nevimjak.client.controller;

import cz.nevimjak.client.App;
import cz.nevimjak.client.Main;
import cz.nevimjak.client.SceneEnum;
import cz.nevimjak.client.model.GameModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for result view
 */
public class ResultController extends AbstractController {

    @FXML
    Label winnerLabel;

    /**
     * Initialization method
     */
    @Override
    public void initialize() {
        GameModel gameModel = App.INSTANCE.gameModel;
        if(gameModel.winnerColor == gameModel.getPlayerColor()) {
            winnerLabel.setText(App.INSTANCE.player.getName());
        } else if(gameModel.winnerColor == gameModel.getPlayerColor().getOppoPlayerColor()) {
            winnerLabel.setText(gameModel.opponentName);
        }
    }

    /**
     * Method that handle continue button
     */
    @FXML
    public void handleContinue() {
        Platform.runLater(() -> {
            App.INSTANCE.setScene(SceneEnum.LOBBY);
            App.INSTANCE.gameModel = null;
        });
    }
}
