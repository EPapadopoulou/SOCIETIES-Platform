package ac.hw.personis.dataInit.policies;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PolicyFileLoader extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton btnContinue;
	private JButton btnBrowse;
	private File file;
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PolicyFileLoader dialog = new PolicyFileLoader(new JFrame(), "bla");
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PolicyFileLoader(JFrame frame, String requestor) {
		super(frame, "Load privacy policy files", true);
		this.frame = frame;
		frame.setAlwaysOnTop(true);
		this.setBounds(100, 100, 406, 205);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 376, 103);
		getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Please select a privacy policy file for "+requestor);
		lblNewLabel.setBounds(10, 11, 581, 38);
		panel.add(lblNewLabel);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBounds(10, 121, 376, 42);
		getContentPane().add(buttonsPanel);
		buttonsPanel.setLayout(null);

		btnBrowse = new JButton("Browse for file");
		btnBrowse.setBounds(26, 11, 157, 23);
		btnBrowse.addActionListener(this);
		buttonsPanel.add(btnBrowse);

		btnContinue = new JButton("Continue");
		btnContinue.addActionListener(this);
		btnContinue.setBounds(259, 11, 89, 23);
		buttonsPanel.add(btnContinue);
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(PolicyFileLoader.this.btnBrowse)){
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showDialog(PolicyFileLoader.this, "Select");

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				this.file =  fc.getSelectedFile();
				
			} 
		}else if (event.getSource().equals(PolicyFileLoader.this.btnContinue)){
			if (this.file == null){
				JOptionPane.showMessageDialog(PolicyFileLoader.this, "You must choose a file", "Error - file not valid", JOptionPane.ERROR_MESSAGE, null);
				
			}else{
				PolicyFileLoader.this.dispose();
			}
		}
	}
	
	public File getFile(){
		this.setVisible(true);
		return this.file;
	}
}
