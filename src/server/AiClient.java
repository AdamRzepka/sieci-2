package server;

import java.rmi.RemoteException;

import common.EndType;
import common.IClient;
import common.IServer;
import common.InvalidState;
import common.Mark;
import common.Player;

public class AiClient implements IClient {
	Mark[][] board = new Mark[3][3];
	Mark myMark;
	IServer server;
	private Player player;
	
	public AiClient(IServer server, Player player) {
		this.server = server;
		this.player = player;
	}

	@Override
	public void onGameStart(Player opponent, Mark myMark, boolean myTurn)
			throws RemoteException {
		this.myMark = myMark;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				board[i][j] = Mark.NONE;
			}
		}
	}

	@Override
	public void onOpponentMove(int x, int y) throws RemoteException {
		board[x][y] = (myMark == Mark.O ? Mark.X : Mark.O);
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				if (board[i][j] == Mark.NONE) {
					try {
						server.move(player, i, j);
						board[i][j] = myMark;
					} catch (InvalidState e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
		}
	}

	@Override
	public void onGameEnd(EndType type) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
