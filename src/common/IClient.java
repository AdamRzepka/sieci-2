package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	void onGameStart(Player opponent, Mark myMark, boolean myTurn) throws RemoteException;
	void onOpponentMove(int x, int y) throws RemoteException;
	void onGameEnd(EndType type) throws RemoteException;
}
