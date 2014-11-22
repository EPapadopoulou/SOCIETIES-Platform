package ac.hw.personis.internalwindows.apps;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.societies.api.schema.identity.RequestorServiceBean;

import ac.hw.personis.PersonisHelper;

public class AppButtonPanel extends ImagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AppDetailsPanel detpanel;
	private String details = "";

	private boolean isInstalled = false;
	private final RequestorServiceBean requestor;
	private final String staticName;
	private PersonisHelper personisHelper;
	private boolean selected = false;
	private SelectedPanelConfigurator selectedPanelConfigurator;

	/**
	 * Create the panel.
	 */

	public AppButtonPanel(PersonisHelper helper, RequestorServiceBean requestor, String filename, String title, String staticName){
		super(filename);
		this.personisHelper = helper;
		this.requestor = requestor;
		this.staticName = staticName;				
		JLabel label = new JLabel("<html><center>" + title + "</center></html>");
		super.setSize(100, 140);
		super.setLayout(null);

		label.setForeground(Color.black);
		label.setLocation(0, 100);
		label.setSize(100, 40);
		label.setFont(new Font("Tahoma",Font.BOLD,11));
		label.setHorizontalAlignment(SwingConstants.CENTER);

		super.setOpaque(false);

		super.add(label);
	}

	public AppButtonPanel(PersonisHelper helper, RequestorServiceBean requestor, String filename, String title, AppDetailsPanel detpanel, boolean isInstalled, String staticName, SelectedPanelConfigurator selectedPanelConfigurator) {
		this(helper, requestor, filename, title, staticName);
		this.selectedPanelConfigurator = selectedPanelConfigurator;
		this.setDetails();
		this.detpanel = detpanel;
		this.isInstalled = isInstalled;


		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				if (AppButtonPanel.this.isInstalled){
					highlight();
				}else{
					if (!selected){
						highlight();
					}
				}

			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (AppButtonPanel.this.isInstalled){
					dehighlight();
				}else{
					if (!selected){
						dehighlight();
					}
				}
			}


			@Override
			public void mouseClicked(MouseEvent e) {
				//JOptionPane.showMessageDialog(AppButtonPanel.super, "a;sdlfjas;ldfjasdl;fj");
				//AppButtonPanel.this.detpanel = AppButtonPanel.this.thisdetailpanel;
				if (!AppButtonPanel.this.isInstalled)
				{
					AppButtonPanel.this.detpanel.setVisible(true);
					AppButtonPanel.this.detpanel.setDetailsText(AppButtonPanel.this.details);
					AppButtonPanel.this.detpanel.changeActionDetails(AppButtonPanel.this.requestor);
					AppButtonPanel.this.selectedPanelConfigurator.mouseClickedOn(AppButtonPanel.this);
				}
				else
				{
					if (AppButtonPanel.this.staticName.equalsIgnoreCase(PersonisHelper.GOOGLE_VENUE_FINDER)){
						personisHelper.startGoogleService();
					}else if (AppButtonPanel.this.staticName.equalsIgnoreCase(PersonisHelper.HWU_CAMPUS_GUIDE_APP)){
						personisHelper.startHWUService();
					}else if (AppButtonPanel.this.staticName.equalsIgnoreCase(PersonisHelper.BBC_NEWS_APP)){
						personisHelper.startBBCNewsService();
					}else if (AppButtonPanel.this.staticName.equalsIgnoreCase(PersonisHelper.BBC_WEATHER_APP)){
						personisHelper.startBBCWeatherService();
					}

				}


			}
		});
	}

	public void highlight() {
		AppButtonPanel.super.setBackground(Color.LIGHT_GRAY);   
		AppButtonPanel.super.setOpaque(true);
	}
	public void dehighlight() {
		this.selected = false;
		AppButtonPanel.super.setBackground(null);
		AppButtonPanel.super.setOpaque(false);
	}
	public void select(){
		AppButtonPanel.super.setBackground(Color.LIGHT_GRAY);   
		AppButtonPanel.super.setOpaque(true);
		this.selected= true;
	}
	private void setDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		if (staticName.equals(PersonisHelper.GOOGLE_VENUE_FINDER)){
			sb.append("The Google Venue finder app for Android phones and tablets makes navigating your world faster and easier. Find the best spots in town and the information you need to get there.");

		}else if (staticName.equals(PersonisHelper.HWU_CAMPUS_GUIDE_APP)){
			sb.append("The HWU Campus Guide helps you find your way around the campus and explore local amenities.");
		}else if (staticName.equals(PersonisHelper.BBC_NEWS_APP)){
			sb.append("The official BBC News app for UK audiences.");
			sb.append("<br />");
			sb.append("Get the latest, breaking news from our trusted global network of journalists.");

		}else if (staticName.equals(PersonisHelper.BBC_WEATHER_APP)){
			sb.append("Wherever you are, and whatever your plans, you're always prepared with the latest weather forecast from BBC Weather.");
		}
		sb.append("</html>");
		details = sb.toString();

	}
	public boolean isInstalled() {
		return isInstalled;
	}
	public boolean setInstalled(boolean isInstalled) {
		this.isInstalled = isInstalled;
		return isInstalled;
	}
	public RequestorServiceBean getRequestor() {
		return requestor;
	}
	public String getStaticName() {
		return staticName;
	}


}
