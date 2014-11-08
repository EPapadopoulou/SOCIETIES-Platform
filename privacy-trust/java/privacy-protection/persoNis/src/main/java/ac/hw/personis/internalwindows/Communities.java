package ac.hw.personis.internalwindows;

import java.awt.EventQueue;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class Communities extends JInternalFrame {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Communities frame = new Communities();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Communities() {
		setBounds(100, 100, 450, 300);
		
		JLabel lblMyCommunities = new JLabel("My Communities");
		getContentPane().add(lblMyCommunities, BorderLayout.CENTER);

	}

}
