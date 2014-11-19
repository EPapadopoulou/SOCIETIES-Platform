package ac.hw.personis.internalwindows.apps;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.identity.RequestorBean;

import ac.hw.personis.PersonisHelper;

public class Appsv2 extends JInternalFrame {

	private ArrayList<AppButtonPanel> AvailApps;
	private ArrayList<AppButtonPanel> InstalledApps;

	private AppDetailsPanel detailspanel;
	private JPanel pnlavapps;
	private JButton btnNewButton;
	private JPanel pnlinstapps;
	private AppButtonPanel p1;
	private AppButtonPanel p2;
	private AppButtonPanel p3;
	private AppButtonPanel p4;
	private List<String> installedAppNames;
	private List<String> storeAppNames;
	private PersonisHelper helper;



	public Appsv2(PersonisHelper helper, List<String> installedAppNames,
			List<String> storeAppNames) {
		this.helper = helper;
		this.installedAppNames = installedAppNames;
		this.storeAppNames = storeAppNames;

		setBounds(100, 100, 813, 729);
		setLocation(0, 0);
		getContentPane().setLayout(null);

		ImagePanel pnlmain = new ImagePanel("/appsback1.png");
		pnlmain.setBounds(0, 0, 800, 700);
		getContentPane().add(pnlmain);

		detailspanel = new AppDetailsPanel(helper);
		detailspanel.setBounds(50, 470, 700, 180);
		detailspanel.setVisible(false);
		pnlmain.add(detailspanel);


		    	
		


		AvailApps = new ArrayList<AppButtonPanel>();
		InstalledApps = new ArrayList<AppButtonPanel>();

		pnlmain.setLayout(null);
		SelectedPanelConfigurator selectedPanelConfigurator = new SelectedPanelConfigurator();
		
		if (storeAppNames.contains(PersonisHelper.GOOGLE_VENUE_FINDER)){
			
			p1 = new AppButtonPanel(helper, helper.getGoogleRequestor(), "/gvf.png", "Google Venue<br />Finder",detailspanel, false, PersonisHelper.GOOGLE_VENUE_FINDER, selectedPanelConfigurator);    	
			AvailApps.add(p1);
		}else{
			p1 = new AppButtonPanel(helper,helper.getGoogleRequestor(), "/gvf.png", "Google Venue<br />Finder",detailspanel, true, PersonisHelper.GOOGLE_VENUE_FINDER, selectedPanelConfigurator);
			InstalledApps.add(p1);
		}
		
		if (storeAppNames.contains(PersonisHelper.HWU_CAMPUS_GUIDE_APP)){
			p2 = new AppButtonPanel(helper,helper.getHwuRequestor(), "/hwucampus.png","HWU Campus<br />Guide",detailspanel, false, PersonisHelper.HWU_CAMPUS_GUIDE_APP, selectedPanelConfigurator);    				
			AvailApps.add(p2);
		}else{
			p2 = new AppButtonPanel(helper,helper.getHwuRequestor(), "/hwucampus.png","HWU Campus<br />Guide",detailspanel, true, PersonisHelper.HWU_CAMPUS_GUIDE_APP, selectedPanelConfigurator);    	
			InstalledApps.add(p2);
		}
		if (storeAppNames.contains(PersonisHelper.BBC_NEWS_APP)){
			p3 = new AppButtonPanel(helper,helper.getBBCNewsRequestor(), "/bbcnews.png", "BBC <br />News",detailspanel, false, PersonisHelper.BBC_NEWS_APP, selectedPanelConfigurator);
			AvailApps.add(p3);
		}else{
			p3 = new AppButtonPanel(helper,helper.getBBCNewsRequestor(), "/bbcnews.png", "BBC <br />News",detailspanel, true, PersonisHelper.BBC_NEWS_APP, selectedPanelConfigurator);
			InstalledApps.add(p3);
		}
		if (storeAppNames.contains(PersonisHelper.BBC_WEATHER_APP)){
			p4 = new AppButtonPanel(helper,helper.getBbcWeatherRequestor(), "/bbcweather.png", "BBC <br /> Weather",detailspanel, false, PersonisHelper.BBC_WEATHER_APP, selectedPanelConfigurator);
			AvailApps.add(p4);
		}else{
			p4 = new AppButtonPanel(helper,helper.getBbcWeatherRequestor(), "/bbcweather.png", "BBC <br /> Weather",detailspanel, true, PersonisHelper.BBC_WEATHER_APP, selectedPanelConfigurator);
			InstalledApps.add(p4);
		}
		selectedPanelConfigurator.addPanel(p1);
		selectedPanelConfigurator.addPanel(p2);
		selectedPanelConfigurator.addPanel(p3);
		selectedPanelConfigurator.addPanel(p4);
/*		if (storeAppNames.contains(PersonisHelper.ITUNES_MUSIC_APP)){
			p4 = new AppButtonPanel(helper,helper.getItunesRequestor(), "/itunesapp.png", "iTunes",detailspanel, false, PersonisHelper.ITUNES_MUSIC_APP);
			AvailApps.add(p4);
		}else{
			p4 = new AppButtonPanel(helper,helper.getItunesRequestor(), "/itunesapp.png", "iTunes",detailspanel, true, PersonisHelper.ITUNES_MUSIC_APP);
			InstalledApps.add(p4);
		}*/

		//pnlmain.add(p1);

		pnlavapps = new JPanel();
		pnlavapps.setBounds(50, 285, 700, 140);
		pnlavapps.setLayout(null);
		pnlavapps.setBackground(new Color(214,214,214));
		pnlmain.add(pnlavapps);    	

		pnlinstapps = new JPanel();
		pnlinstapps.setBounds(50, 60, 700, 140);
		pnlinstapps.setLayout(null);
		pnlinstapps.setBackground(new Color(214,214,214));
		pnlmain.add(pnlinstapps);

/*		btnNewButton = new JButton("New button");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				//EXAMPLE WHAT HAPPENS WHEN AN APP IS INSTALLED
				AvailApps.remove(p3);
				p3.setInstalled(true);    			
				InstalledApps.add(p3);

				DrawButtons();
				Appsv2.this.detailspanel.setVisible(false);

			}
		});
		btnNewButton.setBounds(665, 211, 89, 23);
		pnlmain.add(btnNewButton);*/



		DrawButtons();

	}
	
	public void setAppInstalled(RequestorBean requestor){
		if (RequestorUtils.equals(requestor, p1.getRequestor())){
			AvailApps.remove(p1);
			p1.setInstalled(true);    			
			InstalledApps.add(p1);

			DrawButtons();
			Appsv2.this.detailspanel.setVisible(false);
		}else if (RequestorUtils.equals(requestor, p2.getRequestor())){
			AvailApps.remove(p2);
			p2.setInstalled(true);    			
			InstalledApps.add(p2);

			DrawButtons();
			Appsv2.this.detailspanel.setVisible(false);
		}else if (RequestorUtils.equals(requestor, p3.getRequestor())){
			AvailApps.remove(p3);
			p3.setInstalled(true);    			
			InstalledApps.add(p3);

			DrawButtons();
			Appsv2.this.detailspanel.setVisible(false);
		}else if (RequestorUtils.equals(requestor, p4.getRequestor())){
			AvailApps.remove(p4);
			p4.setInstalled(true);    			
			InstalledApps.add(p4);

			DrawButtons();
			Appsv2.this.detailspanel.setVisible(false);
		}
	}


	private void DrawButtons()
	{
		pnlavapps.removeAll();
		pnlavapps.repaint();

		int xloc =0;
		for (AppButtonPanel btn : AvailApps)
		{
			btn.setLocation(xloc, 0);
			pnlavapps.add(btn);
			xloc+=110;    		
		}

		pnlinstapps.removeAll();
		pnlinstapps.repaint();

		xloc=0;
		for (AppButtonPanel btn : InstalledApps)
		{
			btn.setLocation(xloc, 0);
			pnlinstapps.add(btn);
			xloc+=110;    		
		}
	}

}
