package cz.nevimjak.client.model;

/**
 * Class to save player data
 */
public class Player {

    String name;
    int state;

    /**
     * Constructor for new instance of player
     * @param name player name
     */
    public Player(String name) {
        this.name = name;
        this.state = 1;
    }

    /**
     * Constructor for new instance of player
     * @param name player name
     * @param state player state
     */
    public Player(String name, int state) {
        this.name = name;
        this.state = state;
    }

    /**
     * Getter for player name
     * @return player name
     * @return player name
     */
    public String getName() {
        return this.name;
    }

}
