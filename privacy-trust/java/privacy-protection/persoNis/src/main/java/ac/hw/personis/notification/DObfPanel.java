package ac.hw.personis.notification;

import javax.swing.JPanel;
import javax.swing.JSlider;

import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.UserResponseDObfEvent;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.privacytrust.privacyprotection.api.dataobfuscation.ObfuscationLevels;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationPanelClosedListener;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DObfPanel extends NotificationPanel implements ChangeListener {

	private boolean userClicked;
	private JSlider slider;
	private PersonisHelper personisHelper;
	private JButton btnContinue;
	private final String uuid;
	private String dataType;
	
	private static final String[] locations = new String[]{"EM1.69, MACS, Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"MACS, Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"Riccarton, EH14 4AS, Edinburgh, Scotland, UK",
		"EH14 4AS, Edinburgh, Scotland, UK",
		"Edinburgh, Scotland, UK",
		"Scotland, UK",
		"UK",
		"Europe",
		"Earth"};
	private static final String[] names = new String[]{"John Smith", "J. Smith", "John S.", "J.S", "user"};
	private static final String[] birthdays = new String[]{"5/8/1995","July 1995", "1995", "Before 1994"};
	private static final String[] emails = new String[]{"j.smith@hw.ac.uk","anonymous21312@societies.eu"};
	private JLabel obfValueLabel;
	


	/**
	 * Create the panel.
	 */
	public DObfPanel(PersonisHelper helper, String text, final String uuid, NotificationPanelClosedListener listener, final String dataType, final int obfuscationLevel) {

		this.personisHelper = helper;
		this.uuid = uuid;
		this.dataType = dataType;
		this.userClicked = true;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{88, 200, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{136, 23, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		JLabel lblMessage = new JLabel(text);
		GridBagConstraints gbc_messageLabel = new GridBagConstraints();
		gbc_messageLabel.gridwidth = 3;
		gbc_messageLabel.insets = new Insets(0, 0, 5, 0);
		gbc_messageLabel.gridx = 0;
		gbc_messageLabel.gridy = 0;
		add(lblMessage, gbc_messageLabel);

		slider = new JSlider();
		slider.setMajorTickSpacing(1);
		slider.setMinorTickSpacing(1);
		slider.setMinimum(0);
		slider.setMaximum(ObfuscationLevels.getApplicableObfuscationLevels(dataType)-1);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		if (obfuscationLevel>=0){
			slider.setValue(obfuscationLevel);
		}
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.gridwidth = 3;
		gbc_slider.insets = new Insets(0, 0, 5, 0);
		gbc_slider.fill = GridBagConstraints.BOTH;
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 1;
		add(slider, gbc_slider);

		btnContinue = new JButton("Continue");
		btnContinue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (userClicked){
					UserResponseDObfEvent dobfEvent = new UserResponseDObfEvent(uuid, slider.getValue(), true);
					InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION__DOBF_RESPONSE, "", getClass().getName(), dobfEvent);
					try {
						personisHelper.getEventMgr().publishInternalEvent(event);
					} catch (EMSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}else{
					UserResponseDObfEvent dobfEvent = new UserResponseDObfEvent(uuid, obfuscationLevel, false);
					InternalEvent event = new InternalEvent(EventTypes.PERSONIS_NOTIFICATION__DOBF_RESPONSE, "", getClass().getName(), dobfEvent);
					try {
						personisHelper.getEventMgr().publishInternalEvent(event);
					} catch (EMSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		btnContinue.setActionCommand(uuid);
		btnContinue.addActionListener(listener);
		
		obfValueLabel = new JLabel("New label");
		GridBagConstraints gbc_obfValueLabel = new GridBagConstraints();
		gbc_obfValueLabel.fill = GridBagConstraints.BOTH;
		gbc_obfValueLabel.anchor = GridBagConstraints.WEST;
		gbc_obfValueLabel.gridwidth = 3;
		gbc_obfValueLabel.insets = new Insets(0, 0, 5, 5);
		gbc_obfValueLabel.gridx = 0;
		gbc_obfValueLabel.gridy = 2;
		add(obfValueLabel, gbc_obfValueLabel);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 3;
		add(btnContinue, gbc_btnNewButton);
		
		slider.addChangeListener(this);
		slider.setValue(0);
	}

	public void setUserClicked(boolean userClicked) {
		this.userClicked = userClicked;
	}
	
	public void closeMe(){
		btnContinue.doClick();
	}

	@Override
	public String getUuid() {
		// TODO Auto-generated method stub
		return uuid;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		
		
		if (dataType.equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			this.obfValueLabel.setText(locations[slider.getValue()]);
		}else if (dataType.equalsIgnoreCase(CtxAttributeTypes.NAME)){
			this.obfValueLabel.setText(names[slider.getValue()]);
		}if (dataType.equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
			this.obfValueLabel.setText(birthdays[slider.getValue()]);
		}if (dataType.equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
			this.obfValueLabel.setText(emails[slider.getValue()]);
		}
		

	}
}
