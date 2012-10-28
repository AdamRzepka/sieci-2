package client;

import java.rmi.RemoteException;

import common.EndType;
import common.IClient;
import common.Mark;
import common.Player;

public class Client implements IClient {

	public Client(Main mainForm) {
		this.mainForm = mainForm;
	}

	private Main mainForm;
	private Mark myMark;
	
	@Override
	public void onGameStart(Player opponent, Mark myMark, boolean myTurn)
			throws RemoteException {
		this.myMark = myMark;
		mainForm.myMark = (myMark == Mark.O ? "o" : "x");
		mainForm.readyToMove = myTurn;
		mainForm.statusLabel.setText((myTurn ? "Your turn" : "Waiting for other player"));
	}

	@Override
	public void onOpponentMove(int x, int y) throws RemoteException {
		mainForm.opponentMove(x, y, (myMark == Mark.X ? "o" : "x"));
	}

	@Override
	public void onGameEnd(EndType type) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
