package ac.hw.personis.internalwindows.notification;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationAccCtrlEvent.NotificationType;
import org.societies.api.internal.privacytrust.privacyprotection.model.event.NotificationDobfEvent;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyOutcomeConstantsBean;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.event.NotificationsListener;
import ac.hw.personis.notification.NotificationsPanel;

public class Notifications extends JInternalFrame {

	private NotificationsListener listener;
	private static final Logger logging = LoggerFactory.getLogger(Notifications.class);
	private NotificationsPanel notificationsPanel;

	/**
	 * Create the frame.
	 * @param personisHelper 
	 * @param y 
	 * @param x 
	 */
	public Notifications(PersonisHelper personisHelper) {
		setBounds(100, 100, 375, 729);
		setLocation(820, 0);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{334, 0};
		gridBagLayout.rowHeights = new int[]{700, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		notificationsPanel = new NotificationsPanel(personisHelper);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(notificationsPanel, gbc_panel);
		listener = new NotificationsListener(personisHelper, notificationsPanel);

	}

	public NotificationsPanel getNotificationsPanel() {
		return notificationsPanel;
	}

	public void setNotificationsPanel(NotificationsPanel notificationsPanel) {
		this.notificationsPanel = notificationsPanel;
	}

	

	/**
	 * Launch the application.
	 */
/*	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					try {
					    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					    	logging.debug("Found LookAndFeel: "+info.getName());
					        if ("Nimbus".equals(info.getName())) {
					            UIManager.setLookAndFeel(info.getClassName());
					            
					            break;
					        }
					    }
					} catch (Exception e) {
					    // If Nimbus is not available, you can set the GUI to another look and feel.
					}
					//UIManager.put("control", new Color(242,242,189));
					//UIManager.put("Button.background", new Color(10,10,10));
					UIManager.put("TextField.font", new Font("Tahoma", Font.PLAIN,11));
					//UIManager.put("Label[Enabled].font", new Font("Tahoma", Font.PLAIN,14));
					UIManager.put("ComboBox.font", new Font("Tahoma", Font.PLAIN,11));
					//UIManager.put("Button.font", new FontUIResource("Tahoma",Font.PLAIN,2));
					UIManager.getLookAndFeelDefaults().put("Button.font", new FontUIResource("Tahoma",Font.BOLD,12));
					UIManager.getLookAndFeelDefaults().put("Label.font", new FontUIResource("Tahoma",Font.BOLD,12));
					UIManager.getLookAndFeelDefaults().put("Button.background", new ColorUIResource(Color.GREEN));
					JFrame frmPersonismEvaluationTool = new JFrame();
					frmPersonismEvaluationTool.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					frmPersonismEvaluationTool.setTitle("PersoNISM Evaluation Tool");
					//centers the window on startup
					frmPersonismEvaluationTool.setLocationRelativeTo(null);
					frmPersonismEvaluationTool.setBounds(100, 100, 1200, 800);
					SwingUtilities.updateComponentTreeUI(frmPersonismEvaluationTool);


					GridBagLayout gridBagLayout = new GridBagLayout();
					gridBagLayout.columnWidths = new int[]{0, 0};
					gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
					gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
					gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
					frmPersonismEvaluationTool.getContentPane().setLayout(gridBagLayout);
					JPanel bottomPanel = new JPanel();
					GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
					gbc_bottomPanel.insets = new Insets(0, 0, 5, 0);
					gbc_bottomPanel.fill = GridBagConstraints.BOTH;
					gbc_bottomPanel.gridx = 0;
					gbc_bottomPanel.gridy = 2;
					frmPersonismEvaluationTool.getContentPane().add(bottomPanel, gbc_bottomPanel);
					GridBagLayout gbl_bottomPanel = new GridBagLayout();
					gbl_bottomPanel.columnWidths = new int[]{0};
					gbl_bottomPanel.rowHeights = new int[]{0};
					gbl_bottomPanel.columnWeights = new double[]{Double.MIN_VALUE};
					gbl_bottomPanel.rowWeights = new double[]{Double.MIN_VALUE};
					bottomPanel.setLayout(gbl_bottomPanel);

					JDesktopPane desktopPane = new JDesktopPane();
					GridBagConstraints gbc_desktopPane = new GridBagConstraints();
					gbc_desktopPane.insets = new Insets(0, 0, 5, 0);
					gbc_desktopPane.fill = GridBagConstraints.BOTH;
					gbc_desktopPane.gridx = 0;
					gbc_desktopPane.gridy = 1;
					frmPersonismEvaluationTool.getContentPane().add(desktopPane, gbc_desktopPane);
					final Notifications notifications = new Notifications(null);
					notifications.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
					notifications.setClosable(false);
					notifications.setVisible(true);
					desktopPane.add(notifications);
					frmPersonismEvaluationTool.setVisible(true);
					
					JButton btnAdd = new JButton("Add notification");
					btnAdd.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							notifications.getNotificationsPanel().addAccessControlNotification(new NotificationAccCtrlEvent("uuid", "asldfjasl;dfja;sldfjkas;ldfkjasl;fjas;flkjasd;flajsd;lk;lkjl;kj lkj ;lkj l;jk  k l;jkl;jk;lkjl;jk lkjhugbyibu u iu ghuil uikg ilu  gu ukk", NotificationType.SIMPLE, PrivacyOutcomeConstantsBean.ALLOW));
							notifications.getNotificationsPanel().addAccessControlNotification(new NotificationAccCtrlEvent("uuid", "asldfjasl;dfja;sldfjkas;ldfkjasl;fjas;flkjasd;flajsd;lk;lkjl;kj lkj ;lkj l;jk  k l;jkl;jk;lkjl;jk lkjhugbyibu u iu ghuil uikg ilu  gu ukk", NotificationType.TIMED, PrivacyOutcomeConstantsBean.ALLOW));
							notifications.getNotificationsPanel().addDobfNotification(new NotificationDobfEvent("uuid", "asldfjasl;dfja;sldfjkas;ldfkjasl;fjas;flkjasd;flajsd;lk;lkjl;kj lkj ;lkj l;jk  k l;jkl;jk;lkjl;jk lkjhugbyibu u iu ghuil uikg ilu  gu ukk", NotificationType.SIMPLE, 1, CtxAttributeTypes.BIRTHDAY));
							notifications.getNotificationsPanel().addDobfNotification(new NotificationDobfEvent("uuid", "asldfjasl;dfja;sldfjkas;ldfkjasl;fjas;flkjasd;flajsd;lk;lkjl;kj lkj ;lkj l;jk  k l;jkl;jk;lkjl;jk lkjhugbyibu u iu ghuil uikg ilu  gu ukk", NotificationType.TIMED, 1, CtxAttributeTypes.BIRTHDAY));
							
							
						}
					});
					
					bottomPanel.add(btnAdd);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
*/
}
