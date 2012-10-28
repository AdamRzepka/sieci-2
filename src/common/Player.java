package common;

import java.io.Serializable;

public class Player implements Serializable {

	public Player(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String playerName;

	public String getPlayerName() {
		return playerName;
	}
}
