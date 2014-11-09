package ac.hw.personis.notification;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationPanelClosedListener;

public class TimedDObfPanel extends DObfPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DobfTimer hurdlerTimer;
	private JLabel lblCountdown;

	/**
	 * Create the panel.
	 */
	public TimedDObfPanel(PersonisHelper helper, String text, final String uuid, NotificationPanelClosedListener listener,String dataType, final int obfuscationLevel) {
		super(helper, text, uuid, listener, dataType, obfuscationLevel);
		lblCountdown = new JLabel("Countdown: ");
		GridBagConstraints gbc_lblCountdown = new GridBagConstraints();
		gbc_lblCountdown.anchor = GridBagConstraints.WEST;
		gbc_lblCountdown.insets = new Insets(0, 0, 0, 5);
		gbc_lblCountdown.gridx = 0;
		gbc_lblCountdown.gridy = 4;
		add(lblCountdown, gbc_lblCountdown);
		hurdlerTimer = new DobfTimer(this);
		hurdlerTimer.start();
	}

	public void setCountDownLabelText(String text) {
		lblCountdown.setText(text);
	}
	
	@Override
	public void closeMe(){
		hurdlerTimer.stopTimer();
		super.setUserClicked(false);
		super.closeMe();
		
	}
}

class DobfTimer {
	private static final int TIMER_PERIOD = 1000;
	protected static final int MAX_COUNT = 30;
	private TimedDObfPanel panel; // holds a reference to the Welcome class
	private int count;
	private NotificationsPanel parentPanel;
	private Timer timer;

	public DobfTimer(TimedDObfPanel panel) {
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