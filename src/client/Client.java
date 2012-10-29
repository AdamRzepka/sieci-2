package client;

import java.awt.EventQueue;
import java.rmi.RemoteException;

import javax.swing.JOptionPane;

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
		String markText = (myMark == Mark.O ? "o" : "x");
		String markTextOp = (myMark == Mark.X ? "o" : "x");
		mainForm.playerLabel.setText(markText + ": " + mainForm.player.getPlayerName());
		mainForm.opponentLabel.setText(markTextOp + ": " + opponent.getPlayerName());
	}

	@Override
	public void onOpponentMove(int x, int y) throws RemoteException {
		mainForm.opponentMove(x, y, (myMark == Mark.X ? "o" : "x"));
	}

	@Override
	public void onGameEnd(EndType type, int lastX, int lastY) throws RemoteException {
		if (mainForm.fields[lastX][lastY].getText().isEmpty()) {
			mainForm.fields[lastX][lastY].setText((myMark == Mark.X ? "o" : "x"));
		}
		String msg = "";
		switch (type) {
		case DISCONNECTED:
			msg = "Other player disconnected";
			break;
		case LOOSE:
			msg = "You loose!";
			break;
		case WIN:
			msg = "You win!";
			break;
		case TIE:
			msg = "Tie!";
			break;
		default:
			break;
		}

		final String message = msg;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(mainForm.frame, message);
				mainForm.reset();				
			}
		});



	}

}
