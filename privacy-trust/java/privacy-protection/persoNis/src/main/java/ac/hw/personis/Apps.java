package ac.hw.personis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorServiceBean;

import ac.hw.personis.ServicePanel.ServiceAction;
import ac.hw.personis.event.ButtonActionListener;

public class Apps extends JInternalFrame {
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	private RequestorServiceBean hwuRequestor;
	private RequestorServiceBean googleRequestor;
	private RequestorServiceBean bbcRequestor;
	private RequestorServiceBean itunesRequestor;
	
	private PersonisHelper personisHelper;
	private Accordion accordion_StoreApps;
	private Accordion accordion_InstalledApps;
	
	private ServicePanel googleServicePanel;
	private ServicePanel hwuServicePanel;
	private ServicePanel bbcServicePanel;
	private ServicePanel itunesServicePanel;
	
	private ButtonActionListener googleListener;
	private ButtonActionListener hwuListener;
	private ButtonActionListener bbcListener;
	private ButtonActionListener itunesListener;

	/**
	 * Create the frame.
	 * @param storeApps 
	 * @param installedApps 
	 */
	public Apps(PersonisHelper personisHelper, List<String> installedApps, List<String> storeApps) {
		this.logging.debug("Initialising Apps with "+installedApps.size()+" and "+storeApps.size());
		this.personisHelper = personisHelper;
		this.googleRequestor = personisHelper.getGoogleRequestor();
		this.hwuRequestor = personisHelper.getHwuRequestor();
		bbcRequestor = personisHelper.getBbcRequestor();
		itunesRequestor = personisHelper.getItunesRequestor();
		//this.addInternalFrameListener(new MyInternalFrameListener(personisHelper.getApplication()));
		setBounds(100, 100, 839, 577);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{823, 0};
		gridBagLayout.rowHeights = new int[]{548, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JSplitPane splitPane = new JSplitPane();
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 0;
		getContentPane().add(splitPane, gbc_splitPane);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);

		JLabel lblMyInstalledApps = new JLabel("My Installed Apps");
		GridBagConstraints gbc_lblMyInstalledApps = new GridBagConstraints();
		gbc_lblMyInstalledApps.insets = new Insets(0, 0, 5, 0);
		gbc_lblMyInstalledApps.gridx = 0;
		gbc_lblMyInstalledApps.gridy = 0;
		panel.add(lblMyInstalledApps, gbc_lblMyInstalledApps);

		accordion_InstalledApps = new Accordion();

		GridBagConstraints gbc_accordion_InstalledApps = new GridBagConstraints();
		gbc_accordion_InstalledApps.fill = GridBagConstraints.BOTH;
		gbc_accordion_InstalledApps.gridx = 0;
		gbc_accordion_InstalledApps.gridy = 1;
		panel.add(accordion_InstalledApps, gbc_accordion_InstalledApps);


		JPanel panel_1 = new JPanel();
		splitPane.setRightComponent(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);

		JLabel lblAppsFromPersonis = new JLabel("Apps from PersoNIS store");
		GridBagConstraints gbc_lblAppsFromPersonis = new GridBagConstraints();
		gbc_lblAppsFromPersonis.insets = new Insets(0, 0, 5, 0);
		gbc_lblAppsFromPersonis.gridx = 0;
		gbc_lblAppsFromPersonis.gridy = 0;
		panel_1.add(lblAppsFromPersonis, gbc_lblAppsFromPersonis);

		accordion_StoreApps = new Accordion();


		GridBagConstraints gbc_accordion_StoreApps = new GridBagConstraints();
		gbc_accordion_StoreApps.fill = GridBagConstraints.BOTH;
		gbc_accordion_StoreApps.gridx = 0;
		gbc_accordion_StoreApps.gridy = 1;
		panel_1.add(accordion_StoreApps, gbc_accordion_StoreApps);

		if (storeApps.contains(PersonisHelper.GOOGLE_VENUE_FINDER)){
			googleListener = new ButtonActionListener(this.personisHelper, this.googleRequestor);
			googleServicePanel = new ServicePanel(PersonisHelper.GOOGLE_VENUE_FINDER,this.googleRequestor, this.personisHelper, ServiceAction.INSTALL, googleListener);
			accordion_StoreApps.addBar(PersonisHelper.GOOGLE_VENUE_FINDER, googleServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.googleRequestor.getRequestorServiceId()));
		}else {
			googleListener = new ButtonActionListener(this.personisHelper, this.googleRequestor);
			googleServicePanel = new ServicePanel(PersonisHelper.GOOGLE_VENUE_FINDER,this.googleRequestor, this.personisHelper, ServiceAction.LAUNCH, googleListener);
			accordion_InstalledApps.addBar(PersonisHelper.GOOGLE_VENUE_FINDER, googleServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.googleRequestor.getRequestorServiceId()));
		}



		if (storeApps.contains(PersonisHelper.HWU_CAMPUS_GUIDE_APP)){
			hwuListener = new ButtonActionListener(this.personisHelper, this.hwuRequestor);
			hwuServicePanel = new ServicePanel(PersonisHelper.HWU_CAMPUS_GUIDE_APP,this.hwuRequestor, this.personisHelper, ServiceAction.INSTALL, hwuListener);
			accordion_StoreApps.addBar(PersonisHelper.HWU_CAMPUS_GUIDE_APP, hwuServicePanel);	
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.hwuRequestor.getRequestorServiceId()));
		}else{
			hwuListener = new ButtonActionListener(this.personisHelper, this.hwuRequestor);
			hwuServicePanel = new ServicePanel(PersonisHelper.HWU_CAMPUS_GUIDE_APP,this.hwuRequestor, this.personisHelper, ServiceAction.LAUNCH, hwuListener);
			accordion_InstalledApps.addBar(PersonisHelper.HWU_CAMPUS_GUIDE_APP, hwuServicePanel);	
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.hwuRequestor.getRequestorServiceId()));
		}

		if (storeApps.contains(PersonisHelper.BBC_NEWS_APP)){
			bbcListener = new ButtonActionListener(this.personisHelper, this.bbcRequestor);
			bbcServicePanel = new ServicePanel(PersonisHelper.BBC_NEWS_APP, this.bbcRequestor, this.personisHelper, ServiceAction.INSTALL, bbcListener);
			accordion_StoreApps.addBar(PersonisHelper.BBC_NEWS_APP, bbcServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.bbcRequestor.getRequestorServiceId()));
		}else{
			bbcListener = new ButtonActionListener(this.personisHelper, this.bbcRequestor);
			bbcServicePanel = new ServicePanel(PersonisHelper.BBC_NEWS_APP, this.bbcRequestor, this.personisHelper, ServiceAction.LAUNCH, bbcListener);
			accordion_InstalledApps.addBar(PersonisHelper.BBC_NEWS_APP, bbcServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.bbcRequestor.getRequestorServiceId()));
		}


		if (storeApps.contains(PersonisHelper.ITUNES_MUSIC_APP)){
			itunesListener = new ButtonActionListener(this.personisHelper, this.itunesRequestor);
			itunesServicePanel = new ServicePanel(PersonisHelper.ITUNES_MUSIC_APP, this.itunesRequestor, this.personisHelper, ServiceAction.INSTALL, itunesListener);
			accordion_StoreApps.addBar(PersonisHelper.ITUNES_MUSIC_APP, itunesServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.itunesRequestor.getRequestorServiceId()));
		}else{
			itunesListener = new ButtonActionListener(this.personisHelper, this.itunesRequestor);
			itunesServicePanel = new ServicePanel(PersonisHelper.ITUNES_MUSIC_APP, this.itunesRequestor, this.personisHelper, ServiceAction.LAUNCH, itunesListener);
			accordion_InstalledApps.addBar(PersonisHelper.ITUNES_MUSIC_APP, itunesServicePanel);
			logging.debug("Added tab for:"+ServiceModelUtils.serviceResourceIdentifierToString(this.itunesRequestor.getRequestorServiceId()));
		}

	}



}
