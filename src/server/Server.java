package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
		} else if (players[1] == null) {
			players[1] = new PlayerRecord(player, client, Mark.X);
			System.out.println("PVP match starts");
			startGame();
			return true;
		} else {
			System.out.println("Server alrady full");
			return false;
		}
	}

	@Override
	public void move(Player player, int x, int y) throws RemoteException, InvalidState {
		if (activePlayer != -1
				&& player.getPlayerName().equals(
						players[activePlayer].player.getPlayerName())) {
			activePlayer = (activePlayer + 1) % 2;
			players[activePlayer].client.onOpponentMove(x, y);
		} else {
			throw new InvalidState();
		}
	}

	private void startGame() {
		try {
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
