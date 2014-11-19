package ac.hw.personis.internalwindows.profile;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.internalwindows.apps.AppButtonPanel;

import javax.swing.JButton;

import org.societies.api.privacytrust.trust.TrustException;
import org.societies.api.privacytrust.trust.TrustQuery;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.RequestorServiceBean;

public class TrustGUI extends JInternalFrame {
	private JTextField txtGoogle;
	private JSlider sliderGoogle;
	private JTextField txtHWU;
	private JTextField txtBBC;
	private JTextField txtItunes;
	private JSlider sliderHWU;
	private JSlider sliderBBC;
	private JSlider sliderItunes;
	private PersonisHelper helper;
	


	/**
	 * Create the frame.
	 */
	public TrustGUI(PersonisHelper helper) {
		this.helper = helper;
		setTitle("Trust Settings");
		setBounds(100, 100, 639, 618);
		getContentPane().setLayout(null);
		
		/*
		 * Google
		 */
		txtGoogle = new JTextField();
		txtGoogle.setEditable(false);
		txtGoogle.setBounds(575, 64, 46, 20);
		getContentPane().add(txtGoogle);
		txtGoogle.setColumns(10);
		txtGoogle.setText("0");
		
		sliderGoogle = new JSlider();
		sliderGoogle.setValue(0);
		sliderGoogle.setMinorTickSpacing(1);
		sliderGoogle.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				txtGoogle.setText(""+sliderGoogle.getValue());
				
			}
		});
		sliderGoogle.setPaintLabels(true);
		sliderGoogle.setBounds(135, 61, 425, 23);
		getContentPane().add(sliderGoogle);
		
		/*
		 * HWU
		 */
		txtHWU = new JTextField();
		txtHWU.setText("0");
		txtHWU.setEditable(false);
		txtHWU.setColumns(10);
		txtHWU.setBounds(575, 197, 46, 20);
		getContentPane().add(txtHWU);
		
		sliderHWU = new JSlider();
		sliderHWU.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				txtHWU.setText(""+ sliderHWU.getValue());
			}
		});
		sliderHWU.setValue(0);
		sliderHWU.setPaintLabels(true);
		sliderHWU.setMinorTickSpacing(1);
		sliderHWU.setBounds(135, 197, 425, 23);
		getContentPane().add(sliderHWU);
		
		/*
		 * BBC
		 */
		txtBBC = new JTextField();
		txtBBC.setText("0");
		txtBBC.setEditable(false);
		txtBBC.setColumns(10);
		txtBBC.setBounds(575, 345, 46, 20);
		getContentPane().add(txtBBC);
		
		sliderBBC = new JSlider();
		sliderBBC.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				txtBBC.setText("" + sliderBBC.getValue());
			}
		});
		sliderBBC.setValue(0);
		sliderBBC.setPaintLabels(true);
		sliderBBC.setMinorTickSpacing(1);
		sliderBBC.setBounds(135, 342, 425, 23);
		getContentPane().add(sliderBBC);
		
		/*
		 * iTunes
		 */
/*		txtItunes = new JTextField();
		txtItunes.setText("0");
		txtItunes.setEditable(false);
		txtItunes.setColumns(10);
		txtItunes.setBounds(575, 487, 46, 20);
		getContentPane().add(txtItunes);
		
		sliderItunes = new JSlider();
		sliderItunes.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				txtItunes.setText("" + sliderItunes.getValue());
			}
		});
		sliderItunes.setValue(0);
		sliderItunes.setPaintLabels(true);
		sliderItunes.setMinorTickSpacing(1);
		sliderItunes.setBounds(135, 484, 425, 23);
		getContentPane().add(sliderItunes);*/
			

		AppButtonPanel p1 = new AppButtonPanel(helper, helper.getGoogleRequestor(), "/gvf.png", "Google Venue<br />Finder", PersonisHelper.GOOGLE_VENUE_FINDER);    	
		AppButtonPanel p2 = new AppButtonPanel(helper,helper.getHwuRequestor(), "/hwucampus.png","HWU Campus<br />Guide", PersonisHelper.HWU_CAMPUS_GUIDE_APP);    	
		AppButtonPanel p3 = new AppButtonPanel(helper,helper.getBBCNewsRequestor(), "/bbcnews.png", "BBC", PersonisHelper.BBC_NEWS_APP);
		//AppButtonPanel p4 = new AppButtonPanel(helper,helper.getBbcWeatherRequestor(), "/itunesapp.png", "BBC <br /> Weather", PersonisHelper.BBC_WEATHER_APP);
		p1.setLocation(20, 20);
		p2.setLocation(20, 160);
		p3.setLocation(20, 300);
		//p4.setLocation(20, 440);
		
		getContentPane().add(p1);
		getContentPane().add(p2);
		getContentPane().add(p3);
		//getContentPane().add(p4);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Double googleValue = new Double(sliderGoogle.getValue());
				Double hwuValue = new Double(sliderHWU.getValue());
				Double bbcNewsValue = new Double(sliderBBC.getValue());
				//Double itunesValue = new Double(sliderItunes.getValue());
				
				try {
					saveTrustValue(TrustGUI.this.helper.getGoogleRequestor(), googleValue);
					saveTrustValue(TrustGUI.this.helper.getHwuRequestor(), hwuValue);
					saveTrustValue(TrustGUI.this.helper.getBBCNewsRequestor(), bbcNewsValue);
					//saveTrustValue(TrustGUI.this.helper.getItunesRequestor(), itunesValue);
				} catch (TrustException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			private void saveTrustValue(RequestorServiceBean requestor, Double trustValue) throws TrustException{
				TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());

				TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CSS, TrustGUI.this.helper.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
				TrustQuery trustQuery = new TrustQuery(trustorID);
				trustQuery.setTrusteeId(trusteeID);
				TrustGUI.this.helper.getTrustBroker().updateTrustValue(trustQuery, trustValue);
			}
		});
		btnSave.setBounds(531, 553, 90, 28);
		getContentPane().add(btnSave);
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				retrieveTrustValues();
				
			}
		});

		btnReset.setBounds(429, 553, 90, 28);
		getContentPane().add(btnReset);
		retrieveTrustValues();
	}
	
	private void retrieveTrustValues(){
		
		try {
			TrustQuery googleQuery = getQuery(TrustGUI.this.helper.getGoogleRequestor());
			TrustQuery hwuQuery = getQuery(TrustGUI.this.helper.getHwuRequestor());
			TrustQuery bbcQuery = getQuery(TrustGUI.this.helper.getBBCNewsRequestor());
			//TrustQuery itunesQuery = getQuery(TrustGUI.this.helper.getItunesRequestor());
			
			Double googleValue = TrustGUI.this.helper.getTrustBroker().retrieveTrustValue(googleQuery).get();
			sliderGoogle.setValue(googleValue.intValue());
			Double hwuValue = TrustGUI.this.helper.getTrustBroker().retrieveTrustValue(hwuQuery).get();
			sliderHWU.setValue(hwuValue.intValue());
			Double bbcValue = TrustGUI.this.helper.getTrustBroker().retrieveTrustValue(bbcQuery).get();
			sliderBBC.setValue(bbcValue.intValue());
//			Double itunesValue = TrustGUI.this.helper.getTrustBroker().retrieveTrustValue(itunesQuery).get();
//			sliderItunes.setValue(itunesValue.intValue());
			
		} catch (MalformedTrustedEntityIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TrustException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private TrustQuery getQuery(RequestorServiceBean requestor) throws MalformedTrustedEntityIdException{
		TrustedEntityId trusteeID = new TrustedEntityId(TrustedEntityType.SVC, requestor.getRequestorId());

		TrustedEntityId trustorID = new TrustedEntityId(TrustedEntityType.CSS, TrustGUI.this.helper.getCommsMgr().getIdManager().getThisNetworkNode().getBareJid());
		TrustQuery trustQuery = new TrustQuery(trustorID);
		trustQuery.setTrusteeId(trusteeID);
		return trustQuery;
	}
}
