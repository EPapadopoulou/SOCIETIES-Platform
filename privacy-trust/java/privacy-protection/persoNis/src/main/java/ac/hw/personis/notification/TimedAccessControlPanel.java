package ac.hw.personis.notification;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationPanelClosedListener;

import javax.swing.JLabel;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimedAccessControlPanel extends AccessControlPanel {

	private JLabel lblCountdown;
	private HurdlerTimer hurdlerTimer;

	/**
	 * Create the panel.
	 * @param helper 
	 * @param message 
	 * @param uuid 
	 * @param listener 
	 * @param parentPanel 
	 */
	public TimedAccessControlPanel(PersonisHelper helper, String message, String uuid, NotificationPanelClosedListener listener, PrivacyOutcomeConstantsBean preferenceEffect) {
		super(helper, message, uuid, listener, preferenceEffect);
		lblCountdown = new JLabel("Countdown: ");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 3;
		add(lblCountdown, gbc_lblNewLabel);
		hurdlerTimer = new HurdlerTimer(this);
		hurdlerTimer.start();
	}

	public void setCountDownLabelText(String text) {
		lblCountdown.setText(text);
	}
	private static void createAndShowUI() {
		JFrame frame = new JFrame("Welcome");
		frame.getContentPane().add(new TimedAccessControlPanel(null, "MyMessage", "uuid", null, null));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				createAndShowUI();
			}
		});
	}

	@Override
	public void closeMe(){
		hurdlerTimer.stopTimer();
		super.setUserClicked(false);
		super.closeMe();
		
	}
	
}

class HurdlerTimer {
	private static final int TIMER_PERIOD = 1000;
	protected static final int MAX_COUNT = 30;
	private TimedAccessControlPanel panel; // holds a reference to the Welcome class
	private int count;
	private NotificationsPanel parentPanel;
	private Timer timer;

	public HurdlerTimer(TimedAccessControlPanel panel) {
		this.panel = panel; 
		String text = "(" + (MAX_COUNT - count) + ") seconds left";
		panel.setCountDownLabelText(text);
	}

	public void start() {
		timer = new Timer(TIMER_PERIOD, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (count < MAX_COUNT) {
					count++;
					String text = "(" + (MAX_COUNT - count) + ") seconds left";
					panel.setCountDownLabelText(text); // uses the reference to Welcome
				} else {
					((Timer) e.getSource()).stop();
					try{
						panel.closeMe();
					}
					catch(Exception ex){
						ex.printStackTrace();
					}

				}
			}
		});
		timer.start();
	}

	public void stopTimer(){
		try{
			timer.stop();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}