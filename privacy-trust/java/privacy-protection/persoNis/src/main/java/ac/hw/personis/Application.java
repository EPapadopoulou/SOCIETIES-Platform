package ac.hw.personis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.RequestorBean;

import ac.hw.personis.dataInit.DataInitialiser;
import ac.hw.personis.preference.AllPreferencesGUI;

public class Application implements WindowListener{

	private JFrame frame;
	private Apps appsPage;
	private JDesktopPane desktopPane;
	//private HomeFrame homeFrame;
	private PersonisHelper helper;
    private Logger logging = LoggerFactory.getLogger(this.getClass());
    private JButton btnGoHome;
	private AllPreferencesGUI preferencesPage;
	private List<String> installedApps;
	private List<String> storeApps;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application(new PersonisHelper());
					window.frame.setVisible(true);
					String current = new java.io.File( "." ).getCanonicalPath();
					System.out.println("Current dir:"+current);
					String currentDir = System.getProperty("user.dir");
					System.out.println("Current dir using System:" +currentDir);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application(PersonisHelper helper) {
		//UIManager.put("ClassLoader", getClass().getClassLoader());
		this.helper=helper;

		initialize();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		
		frame = new JFrame();
		//centers the window on startup
		frame.setLocationRelativeTo(null);
		frame.setBounds(100, 100, 957, 749);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(this);
		
    	DataInitialiser initialiser = new DataInitialiser(helper.getCtxBroker(), helper.getCommsMgr().getIdManager().getThisNetworkNode(), this.frame);
    	if (!initialiser.dataExists()){
    		boolean initialised = initialiser.isInitialised();
    	}

		btnGoHome = new JButton();
		btnGoHome.setActionCommand("home");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel topPanel = new JPanel();
		GridBagConstraints gbc_topPanel = new GridBagConstraints();
		gbc_topPanel.insets = new Insets(0, 0, 5, 0);
		gbc_topPanel.fill = GridBagConstraints.BOTH;
		gbc_topPanel.gridx = 0;
		gbc_topPanel.gridy = 0;
		frame.getContentPane().add(topPanel, gbc_topPanel);
		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.columnWidths = new int[]{0, 0, 0};
		gbl_topPanel.rowHeights = new int[]{0, 0, 0};
		gbl_topPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_topPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		topPanel.setLayout(gbl_topPanel);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 0;
		topPanel.add(panel_3, gbc_panel_3);

		JLabel lblNewLabel = new JLabel("New label");
		panel_3.add(lblNewLabel);

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.EAST;
		gbc_panel_4.fill = GridBagConstraints.VERTICAL;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 0;
		topPanel.add(panel_4, gbc_panel_4);

		JLabel lblHelloUser = new JLabel("Hello, user");
		panel_4.add(lblHelloUser);

		JPanel bottomPanel = new JPanel();
		GridBagConstraints gbc_bottomPanel = new GridBagConstraints();
		gbc_bottomPanel.insets = new Insets(0, 0, 5, 0);
		gbc_bottomPanel.fill = GridBagConstraints.BOTH;
		gbc_bottomPanel.gridx = 0;
		gbc_bottomPanel.gridy = 2;
		frame.getContentPane().add(bottomPanel, gbc_bottomPanel);
		GridBagLayout gbl_bottomPanel = new GridBagLayout();
		gbl_bottomPanel.columnWidths = new int[]{0};
		gbl_bottomPanel.rowHeights = new int[]{0};
		gbl_bottomPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_bottomPanel.rowWeights = new double[]{Double.MIN_VALUE};
		bottomPanel.setLayout(gbl_bottomPanel);

		desktopPane = new JDesktopPane();
		GridBagConstraints gbc_desktopPane = new GridBagConstraints();
		gbc_desktopPane.insets = new Insets(0, 0, 5, 0);
		gbc_desktopPane.fill = GridBagConstraints.BOTH;
		gbc_desktopPane.gridx = 0;
		gbc_desktopPane.gridy = 1;
		frame.getContentPane().add(desktopPane, gbc_desktopPane);


		this.storeApps = new ArrayList<String>();
		storeApps.add(PersonisHelper.GOOGLE_VENUE_FINDER);
		storeApps.add(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
		installedApps = new ArrayList<String>();
		appsPage = new Apps(helper, this.installedApps, this.storeApps);
		appsPage.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		appsPage.setClosable(true);
		appsPage.setVisible(true);	
		desktopPane.add(appsPage);
		
		
		preferencesPage = new AllPreferencesGUI(helper);
		preferencesPage.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		preferencesPage.setClosable(true);
		preferencesPage.setVisible(true);
		desktopPane.add(preferencesPage);
		
		
		try {
			appsPage.setMaximum(true);
			preferencesPage.setMaximum(true);
			JMenuBar menuBar = new JMenuBar();
			GridBagConstraints gbc_menuBar = new GridBagConstraints();
			gbc_menuBar.gridx = 0;
			gbc_menuBar.gridy = 3;
			frame.setJMenuBar(menuBar);
			
			JMenu mnNewMenu = new JMenu("Windows");
			menuBar.add(mnNewMenu);
			
			JMenuItem prefsMenuItem = new JMenuItem("Privacy Preferences");
			prefsMenuItem.setActionCommand("preferences");
			prefsMenuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent ev) {
					//JOptionPane.showMessageDialog(frame, "Starting prefs page");
					preferencesPage.refreshData();
					preferencesPage.show();
					appsPage.hide();
					//JOptionPane.showMessageDialog(frame, "Shown page");
				}
			});
			mnNewMenu.add(prefsMenuItem);
			
			JMenuItem appsMenuItem = new JMenuItem("Applications");
			appsMenuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					appsPage.show();
					preferencesPage.hide();
				}
			});
			
			mnNewMenu.add(appsMenuItem);
			
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public Apps getAppsPage() {
		return appsPage;
	}

	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}


	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent arg0) {

		int showConfirmDialog = JOptionPane.showConfirmDialog(Application.this.getFrame(), "Are you sure you want to close this application?", "Close application.", JOptionPane.YES_NO_OPTION);
		if (showConfirmDialog==JOptionPane.YES_OPTION){
			Application.this.getFrame().dispose();
			System.exit(0);
		}


	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	public PersonisHelper getPersonisHelper() {
		// TODO Auto-generated method stub
		return this.helper;
	}

	public JButton getBtnGoHome() {
		return btnGoHome;
	}

	public AllPreferencesGUI getPreferencesPage() {
		return preferencesPage;
	}

	public void setPreferencesPage(AllPreferencesGUI preferencesPage) {
		this.preferencesPage = preferencesPage;
	}

	public void notifySuccessfulNegotiation(RequestorBean requestor) {
		int i = 0;
		if (RequestorUtils.equals(requestor, this.helper.getGoogleRequestor())){
			this.installedApps.add(PersonisHelper.GOOGLE_VENUE_FINDER);
			this.storeApps.remove(PersonisHelper.GOOGLE_VENUE_FINDER);
			this.logging.debug("step: "+(i++));
			//this.appsPage.removeInternalFrameListener(frameListener);
			//this.appsPage.dispose();
			this.appsPage = new Apps(helper, installedApps, storeApps);
			this.logging.debug("step: "+(i++));
			this.appsPage.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.logging.debug("step: "+(i++));
			this.logging.debug("step: "+(i++));
			JOptionPane.showMessageDialog(this.frame, "Google Venue Finder successfully installed");
		}else if (RequestorUtils.equals(requestor, this.helper.getHwuRequestor())){
			this.installedApps.add(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
			this.storeApps.remove(PersonisHelper.HWU_CAMPUS_GUIDE_APP);
			this.logging.debug("step: "+(i++));
			//this.appsPage.removeInternalFrameListener(frameListener);
			//this.appsPage.dispose();
			this.appsPage = new Apps(helper, installedApps, storeApps);
			this.logging.debug("step: "+(i++));
			this.appsPage.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.logging.debug("step: "+(i++));
			this.logging.debug("step: "+(i++));
			JOptionPane.showMessageDialog(this.frame, "HWU Campus Guide successfully installed");
		}

		
		this.logging.debug("step: "+(i++));
		this.appsPage.setVisible(true);
		this.logging.debug("step: "+(i++));
		this.getDesktopPane().add(appsPage);
		this.logging.debug("step: "+(i++));
		try {
			this.appsPage.setSelected(true);
			this.logging.debug("step: "+(i++));
			this.appsPage.setMaximum(true);
			this.logging.debug("step: "+(i++));
			
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	public void notifyFailedNegotiation(RequestorBean requestor) {
		if (requestor.getRequestorId().equals(this.helper.getGoogleRequestor().getRequestorId())){
			JOptionPane.showMessageDialog(this.frame, "Negotiation with "+PersonisHelper.GOOGLE_VENUE_FINDER+" failed.");
		}else if (requestor.getRequestorId().equals(this.helper.getHwuRequestor().getRequestorId())){
			JOptionPane.showMessageDialog(this.frame, "Negotiation with "+PersonisHelper.HWU_CAMPUS_GUIDE_APP+" failed.");
		}
		
	}

}
