package ac.hw.personis;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class test extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JButton btnAdd;
	private JButton btnRemove;
	private Accordion accordionLeft;
	private Accordion accordionRight;
	private int counter = 0;
	SystemTray systemTray = SystemTray.getSystemTray();
	private List<String> barNamesAdded;
	private TrayIcon trayIcon;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test frame = new test();
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
	public test() {
		try {
			String current = new java.io.File( "." ).getCanonicalPath();
			System.out.println("Current dir:"+current);
			trayIcon = new TrayIcon(createImage("file://"+current+"\\images\\bulb.gif", "PersoNIS"));
			systemTray.add(trayIcon);
			
			
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.barNamesAdded = new ArrayList<String>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 793, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		accordionLeft = new Accordion();
		accordionLeft.setBounds(5, 5, 772, 282);


		contentPane.setLayout(null);
		contentPane.add(accordionLeft);

		accordionRight = new Accordion();
		accordionRight.setBounds(15, 299, 752, 282);
		contentPane.add(accordionRight);

		btnAdd = new JButton("Add");
		btnAdd.addActionListener(this);
		btnAdd.setBounds(44, 610, 89, 23);
		contentPane.add(btnAdd);

		btnRemove = new JButton("remove");
		btnRemove.addActionListener(this);
		btnRemove.setBounds(436, 592, 89, 23);
		contentPane.add(btnRemove);
	}

	//Obtain the image URL
	protected static Image createImage(String path, String description) throws MalformedURLException {
		URL imageURL = new URL(path);

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.btnAdd)){
			counter++;
			String barName = "Bar "+counter;
			this.accordionLeft.addBar(barName, new JLabel("label "+counter));
			this.barNamesAdded.add(barName);
		}else if (e.getSource().equals(this.btnRemove)){
			if (!this.barNamesAdded.isEmpty()){
				String barName = this.barNamesAdded.remove(0);
				this.accordionLeft.removeBar(barName);
				
				trayIcon.displayMessage("caption", "text", MessageType.INFO);
			}
		}

	}
}
