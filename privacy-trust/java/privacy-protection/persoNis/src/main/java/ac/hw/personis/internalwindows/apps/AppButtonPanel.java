package ac.hw.personis.internalwindows.apps;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.societies.api.schema.identity.RequestorServiceBean;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.internalwindows.apps.ServicePanel.ServiceAction;
import ac.hw.personis.services.GoogleMapsService;

public class AppButtonPanel extends ImagePanel {

	private AppDetailsPanel detpanel;
	private String details = "";

	private boolean isInstalled = false;
	private final RequestorServiceBean requestor;
	private String staticName;
	private PersonisHelper personisHelper;
	
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
	public AppButtonPanel(PersonisHelper helper, RequestorServiceBean requestor, String filename, String title, AppDetailsPanel detpanel, boolean isInstalled, String staticName) {
		this(helper, requestor, filename, title, staticName);
		
		this.detpanel = detpanel;
		this.isInstalled = isInstalled;
		        
        
		addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseEntered(MouseEvent e) {
    			AppButtonPanel.super.setBackground(Color.LIGHT_GRAY);   
    			AppButtonPanel.super.setOpaque(true);
    					
    		}
    		@Override
    		public void mouseExited(MouseEvent e) {
    			AppButtonPanel.super.setBackground(null);
    			AppButtonPanel.super.setOpaque(false);
    		}
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			//JOptionPane.showMessageDialog(AppButtonPanel.super, "a;sdlfjas;ldfjasdl;fj");
    			//AppButtonPanel.this.detpanel = AppButtonPanel.this.thisdetailpanel;
    			if (AppButtonPanel.this.isInstalled == false)
    			{
    				AppButtonPanel.this.detpanel.setVisible(true);
    				AppButtonPanel.this.detpanel.setDetailsText(AppButtonPanel.this.details);
    				AppButtonPanel.this.detpanel.changeActionDetails(AppButtonPanel.this.requestor);
    			}
    			else
    			{
    				if (AppButtonPanel.this.staticName.equalsIgnoreCase(PersonisHelper.GOOGLE_VENUE_FINDER)){
    					GoogleMapsService service = new GoogleMapsService(personisHelper);
    				}
    			}
    			
    			
    		}
    	});
        

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


}
