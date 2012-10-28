package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.event.ActionEvent;

import common.GameType;
import common.IClient;
import common.IServer;
import common.InvalidState;
import common.Player;

import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JLabel;

public class Main {

	private JFrame frame;
	private ConnectDialog dialog = new ConnectDialog();
	private JButton[][] fields = new JButton[3][3];
	String myMark;
	boolean readyToMove = false;
	IServer server;
	Client client;
	Player player;
	JLabel statusLabel;
	
	public void opponentMove(int x, int y, String mark) {
		if (!readyToMove && fields[x][y].getText().isEmpty()) {
			fields[x][y].setText(mark);
			readyToMove = true;
			statusLabel.setText("Your turn");
		}
	}
	
	public void connectToServer(String address, String playerName, GameType type) {
//		if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new SecurityManager());
//        }
        try {
        	readyToMove = false;
        	for (JButton[] row: fields) {
        		for (JButton field: row) {
        			field.setText("");
        		}
        	}
            String name = "Server";
            Registry registry = LocateRegistry.getRegistry(address);
            server = (IServer) registry.lookup(name);
            System.out.println("Server located");
            player = new Player(playerName);
            client = new Client(this);
            IClient stub = (IClient)UnicastRemoteObject.exportObject(client, 0);
            if (server.join(stub, player, type)) {
            	if (!readyToMove) {
            		statusLabel.setText("Waiting for other player");
            	}
            } else {
            	statusLabel.setText("Server full");
            }
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }	
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	private class FieldListener implements ActionListener {

		public FieldListener(int x, int y) {
			this.x = x;
			this.y = y;
		}

		private int x, y;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fields[x][y].getText().isEmpty() && server != null && readyToMove) {
				try {
					server.move(player, x, y);
					fields[x][y].setText(myMark);
					readyToMove = false;
					statusLabel.setText("Waiting for other player");
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidState e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}		
		}
		
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		dialog.mainForm = this;
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);
		
		JMenuItem mntmJoin = new JMenuItem("Join");
		mntmJoin.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(true);
				
			}
		});
		mnGame.add(mntmJoin);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		mnGame.add(mntmExit);

		GridLayout grid = new GridLayout(3, 3);
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(grid);
		
		for (int i = 0; i < fields.length; ++i) {
			for (int j = 0; j < fields[i].length; ++j) {
				fields[i][j] = new JButton();
				fields[i][j].addActionListener(new FieldListener(i, j));
				gamePanel.add(fields[i][j]);
			}
		}
		
		statusLabel = new JLabel("Select Game->Join to join the game");

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(gamePanel, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(12)
							.addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(gamePanel, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(statusLabel)
					.addContainerGap(83, Short.MAX_VALUE))
		);
		
		frame.getContentPane().setLayout(groupLayout);
	}
}
