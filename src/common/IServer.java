package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	boolean join(IClient client, Player player, GameType type) throws RemoteException;
	void move(Player player, int x, int y) throws RemoteException, InvalidState;
}
