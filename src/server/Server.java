package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import common.EndType;
import common.GameType;
import common.IClient;
import common.IServer;
import common.InvalidState;
import common.Mark;
import common.Player;

public class Server implements IServer {

	private class PlayerRecord {
		public PlayerRecord(Player player, IClient client, Mark mark) {
			this.player = player;
			this.client = client;
			this.mark = mark;
		}

		final Player player;
		final IClient client;
		final Mark mark;
	}

	private PlayerRecord[] players = new PlayerRecord[2];
	private int activePlayer = -1;
	private int[][] board = new int[3][3];

	@Override
	public boolean join(IClient client, Player player, GameType type)
			throws RemoteException {
		System.out.printf("Player %s joins a game\n", player.getPlayerName());

		if (players[0] == null) {
			players[0] = new PlayerRecord(player, client, Mark.O);
			if (type == GameType.PVC) {
				Player pl = new Player("Computer");
				players[1] = new PlayerRecord(pl,
						new AiClient(this, pl), Mark.X);
				System.out.println("PVC match starts");
				startGame();
			}
			return true;
		} else if (players[1] == null && type != GameType.PVC && !players[0].player.getPlayerName().equals(player.getPlayerName())) {
			players[1] = new PlayerRecord(player, client, Mark.X);
			System.out.println("PVP match starts");
			startGame();
			return true;
		} else {
			System.out.println("Server full or name already used");
			return false;
		}
	}

	@Override
	public void move(Player player, int x, int y) throws RemoteException, InvalidState {
		if (activePlayer != -1
				&& player.getPlayerName().equals(
						players[activePlayer].player.getPlayerName())) {
			board[x][y] = activePlayer;
			int winner = checkEndGame();
			if (winner != -1) {
				if (winner == 2) {
					System.out.println("Game ends with tie.");
					players[0].client.onGameEnd(EndType.TIE, x, y);
					players[1].client.onGameEnd(EndType.TIE, x, y);
					reset();
				} else {
					System.out.printf("Player %d wins\n", winner);
					players[winner].client.onGameEnd(EndType.WIN, x, y);
					players[1 - winner].client.onGameEnd(EndType.LOOSE, x, y);
					reset();
				}
			}
			else {
				activePlayer = (activePlayer + 1) % 2;
				try {
					players[activePlayer].client.onOpponentMove(x, y);
				} catch (RemoteException re) {
					activePlayer = (activePlayer + 1) % 2;
					players[activePlayer].client.onGameEnd(EndType.DISCONNECTED, x, y);
					reset();
				}
			}
		} else {
			throw new InvalidState();
		}
	}
	
	private void reset(){
		players[0] = null;
		players[1] = null;
		activePlayer = -1;
	}

	private int checkEndGame() {
		int candidate = -1;
		for (int i = 0; i < 3; ++i) {
			//horizontally
			candidate = board[i][0];
			boolean win = true;
			if (candidate != -1) {
				for (int j = 0; j < 3; ++j) {
					if (board[i][j] != candidate) {
						win = false;
						break;
					}
				}
				if (win)
					return candidate;
			}
			
			win = true;
			// vertically
			candidate = board[0][i];
			if (candidate != -1) {
				for (int j = 0; j < 3; ++j) {
					if (board[j][i] != candidate) {
						win = false;
						break;
					}
				}
				if (win)
					return candidate;
			}			
		}
		
		candidate = board[0][0];
		if (candidate != -1 && candidate == board[1][1] && candidate == board[2][2])
			return candidate;
		
		candidate = board[0][2];
		if (candidate != -1 && candidate == board[1][1] && candidate == board[2][0])
			return candidate;
		
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (board[i][j] == -1) {
					return -1;
				}
			}
		}
		
		return 2; //tie
	}

	private void startGame() {
		try {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					board[i][j] = -1;
				}
			}
			players[0].client.onGameStart(players[1].player, players[0].mark,
					true);
			players[1].client.onGameStart(players[0].player, players[1].mark,
					false);
			activePlayer = 0;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        try {
            String name = "Server";
            Server server = new Server();
            IServer stub = 
                (IServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server bound");
        } catch (Exception e) {
            System.err.println("Server exception:");
            e.printStackTrace();
        }

	}
}
