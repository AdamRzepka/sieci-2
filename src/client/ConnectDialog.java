package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

import common.GameType;
import common.Player;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField serverNameText;
	private JTextField loginText;
	private JRadioButton pvpRadio;
	private JRadioButton pvcRadio;
	Main mainForm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ConnectDialog dialog = new ConnectDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConnectDialog() {
		setBounds(100, 100, 423, 194);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		serverNameText = new JTextField();
		serverNameText.setColumns(10);
		serverNameText.setText("localhost");
		
		ButtonGroup group = new ButtonGroup();
		
		pvpRadio = new JRadioButton("Player vs Player");
		pvpRadio.setSelected(true);
		pvcRadio = new JRadioButton("Player vs Computer");

		group.add(pvpRadio);
		group.add(pvcRadio);
		
		loginText = new JTextField();
		loginText.setColumns(10);
		loginText.setText("Player");
		
		JLabel lblPlayerName = new JLabel("Player name");
		
		JLabel lblServer = new JLabel("Server");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(pvpRadio)
							.addGap(67)
							.addComponent(pvcRadio))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(17)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
									.addComponent(lblPlayerName)
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(lblServer)
									.addGap(54)))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(loginText, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(serverNameText, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(80, Short.MAX_VALUE))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServer)
						.addComponent(serverNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(26)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPlayerName)
						.addComponent(loginText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(33)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(pvpRadio)
						.addComponent(pvcRadio))
					.addContainerGap(83, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ConnectDialog.this.setVisible(false);
						mainForm.connectToServer(serverNameText.getText(), loginText.getText(), pvcRadio.isSelected() ? GameType.PVC : GameType.PVP);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ConnectDialog.this.setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
