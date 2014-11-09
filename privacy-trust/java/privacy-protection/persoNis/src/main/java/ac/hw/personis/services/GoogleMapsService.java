/**
 * 
 */
package ac.hw.personis.services;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.internalwindows.apps.ImagePanel;

import javax.swing.JScrollPane;




/**
 * @author PUMA
 * 
 */
public class GoogleMapsService extends JFrame implements ActionListener, CtxChangeEventListener{

	/**
	 * 
	 */
	private final static Logger logging = LoggerFactory.getLogger(GoogleMapsService.class);
	private static final long serialVersionUID = 1L;
	private String authKey = "AIzaSyA9BkjBXcXYv5ES0kvf5IyUpZXEuaBlc1M";
	private JButton btnRetrieve;
	//private JTextArea textArea;
	private ICtxBroker ctxBroker;
	private PersonisHelper personisHelper;
	private List<CtxIdentifier> dataIDs;
	private String myServiceName;
	private RequestorServiceBean myIDBean;
	private RequestorService myID;
	private IIdentity userID;

	private Agreement agreement;
	private JLabel lblYourProfile;
	//private JScrollPane scrollPane;
	private JLabel lblName;
	private JLabel lblName1;
	private JLabel lblDob1;
	private JLabel lblDoB;
	private JLabel lblEmailAddress;
	private JLabel lblEmail;
	private JLabel lblLoggedIn;
	private JLabel lblCurrentLocation;
	private JLabel lblLoc;

	public GoogleMapsService(PersonisHelper personisHelper) {
		super();
		getContentPane().setBackground(Color.BLUE);
		this.personisHelper = personisHelper;
		this.ctxBroker = personisHelper.getCtxBroker();
		this.myServiceName = PersonisHelper.GOOGLE_VENUE_FINDER;
		this.myIDBean = personisHelper.getGoogleRequestor();
		try {
			this.myID = (RequestorService) RequestorUtils.toRequestor(myIDBean, personisHelper.getCommsMgr().getIdManager());
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		agreement = personisHelper.getAgreement(myServiceName);
		if (agreement!=null){
			this.dataIDs = new ArrayList<CtxIdentifier>();
			for (ResponseItem item: this.agreement.getRequestedItems()){
				try {
					CtxAttributeIdentifier ctxID = new CtxAttributeIdentifier(item.getRequestItem().getResource().getDataIdUri());
					this.dataIDs.add(ctxID);

				} catch (MalformedCtxIdentifierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			JOptionPane.showMessageDialog(this, "No agreement found");
		}

		this.userID = personisHelper.getUserID(myServiceName);
		getContentPane().setLayout(null);
		setSize(467, 637);

		ImagePanel imagePanel = new ImagePanel("/gvfappback1.png");
		imagePanel.setBounds(0, 0, 450, 600);
		getContentPane().add(imagePanel);
		imagePanel.setLayout(null);

		//scrollPane = new JScrollPane();
		//scrollPane.setBounds(60, 401, 354, 81);
		//imagePanel.add(scrollPane);

		//textArea = new JTextArea();
		//scrollPane.setViewportView(textArea);
		//textArea.setLineWrap(true);
		//textArea.setEditable(false);
		btnRetrieve = new JButton("Log in");
		btnRetrieve.setBounds(131, 493, 190, 48);
		imagePanel.add(btnRetrieve);

		lblYourProfile = new JLabel("Your Profile:");
		lblYourProfile.setBounds(46, 168, 78, 14);
		imagePanel.add(lblYourProfile);
		
		lblName = new JLabel("name");
		lblName.setBounds(160, 193, 254, 30);
		imagePanel.add(lblName);
		
		lblName1 = new JLabel("Name:");
		lblName1.setBounds(46, 193, 103, 30);
		imagePanel.add(lblName1);
		
		lblDob1 = new JLabel("Date of Birth:");
		lblDob1.setBounds(46, 229, 103, 30);
		imagePanel.add(lblDob1);
		
		lblDoB = new JLabel("DoB");
		lblDoB.setBounds(160, 229, 254, 30);
		imagePanel.add(lblDoB);
		
		lblEmailAddress = new JLabel("Email address:");
		lblEmailAddress.setBounds(46, 270, 103, 30);
		imagePanel.add(lblEmailAddress);
		
		lblEmail = new JLabel("Email");
		lblEmail.setBounds(160, 270, 254, 30);
		imagePanel.add(lblEmail);
		
		lblLoggedIn = new JLabel("New label");
		lblLoggedIn.setBounds(131, 132, 283, 30);
		imagePanel.add(lblLoggedIn);
		
		lblCurrentLocation = new JLabel("Current location:");
		lblCurrentLocation.setBounds(46, 311, 103, 30);
		imagePanel.add(lblCurrentLocation);
		
		lblLoc = new JLabel("loc");
		lblLoc.setBounds(160, 311, 254, 30);
		imagePanel.add(lblLoc);
		btnRetrieve.addActionListener(this);

		setLabelsHidden();
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}

	public void setLabelsVisible(){
		lblYourProfile.setVisible(true);
		lblName.setVisible(true);
		lblName1.setVisible(true);
		lblDoB.setVisible(true);
		lblDob1.setVisible(true);
		lblEmail.setVisible(true);
		lblEmailAddress.setVisible(true);
		lblLoggedIn.setVisible(true);
		lblLoc.setVisible(true);
		lblCurrentLocation.setVisible(true);
		btnRetrieve.setText("Update profile");
		
	}
	
	public void setLabelsHidden(){
		lblYourProfile.setVisible(false);
		lblName.setVisible(false);
		lblName1.setVisible(false);
		lblDoB.setVisible(false);
		lblDob1.setVisible(false);
		lblEmail.setVisible(false);
		lblEmailAddress.setVisible(false);
		lblLoggedIn.setVisible(false);
		lblLoc.setVisible(false);
		lblCurrentLocation.setVisible(false);
		btnRetrieve.setText("Login");
	}

	public void registerForContext(){
		IIdentity userIdReal = personisHelper.getCommsMgr().getIdManager().getThisNetworkNode();
		for (int i = 0; i<this.dataIDs.size(); i++){
			try {
				CtxIdentifier ctxId = this.dataIDs.get(i);
				CtxAttributeIdentifier ctxAttrId = CtxIDChanger.changeOwner(userIdReal.getBareJid(), (CtxAttributeIdentifier) ctxId);
				this.ctxBroker.registerForChanges(myID, this, ctxAttrId);
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent event) {

		String selectedId = (String) JOptionPane.showInputDialog(GoogleMapsService.this, "Login with identity:", "Login", JOptionPane.PLAIN_MESSAGE, null, new String[]{userID.getBareJid()}, userID.getBareJid());
		if (event.getSource() == btnRetrieve) {
			System.out.println("btnRetrieve clicked, retrieving: "+this.dataIDs.size()+" dataTypes from ID: "+userID.getJid());
			new Thread() {

				public void run() {
					for (CtxIdentifier ctxIdentifier : GoogleMapsService.this.dataIDs){
						System.out.println("btnRetrieve: "+ctxIdentifier.getUri());
						try {
							CtxAttribute ctxAttribute = (CtxAttribute) ctxBroker.retrieve(myID, ctxIdentifier).get();
							if (null==ctxAttribute){
								//textArea.append("Could not access "+ctxIdentifier+ctxIdentifier+System.getProperty( "line.separator" ));
								if (ctxIdentifier.getType().equals(CtxAttributeTypes.NAME)){
									lblName.setText(" Permission denied ");
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.BIRTHDAY)){
									lblDoB.setText(" Permission denied ");
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.EMAIL)){
									lblEmail.setText(" Permission denied ");
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
									lblLoc.setText(" Permission denied ");
								}
							}else{
								if (ctxIdentifier.getType().equals(CtxAttributeTypes.NAME)){
									lblName.setText(ctxAttribute.getStringValue());
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.BIRTHDAY)){
									lblDoB.setText(ctxAttribute.getStringValue());
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.EMAIL)){
									lblEmail.setText(ctxAttribute.getStringValue());
								}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
									lblLoc.setText(ctxAttribute.getStringValue());
								}
								//textArea.append("Retrieved "+ctxIdentifier.getType()+" = "+ctxAttribute.getStringValue()+" ("+ctxIdentifier+System.getProperty( "line.separator" ));
								System.out.println("Retrieved "+ctxIdentifier.getType()+" = "+ctxAttribute.getStringValue());
							}
						} catch (CtxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if (ctxIdentifier.getType().equals(CtxAttributeTypes.NAME)){
								lblName.setText(" Permission denied ");
							}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.BIRTHDAY)){
								lblDoB.setText(" Permission denied ");
							}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.EMAIL)){
								lblEmail.setText(" Permission denied ");
							}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
								lblLoc.setText(" Permission denied ");
							}
							//textArea.append("CtxException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//textArea.append("InterruptedException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//textArea.append("ExecutionException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
						}
					}
					registerForContext();
					lblLoggedIn.setText("You are logged in as "+userID.getBareJid());
					setLabelsVisible();
					GoogleMapsService.this.logging.debug("Exiting thread");
				}
			}.start();
		}

	}

	@Override
	public void onCreation(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModification(final CtxChangeEvent event) {
		new Thread(){
			public void run(){
				CtxIdentifier id = event.getId();
				CtxAttributeIdentifier ctxIdentifier = CtxIDChanger.changeOwner(userID.getBareJid(), (CtxAttributeIdentifier) id);
				try {
					CtxAttribute ctxAttribute = (CtxAttribute) ctxBroker.retrieve(myID, ctxIdentifier).get();
					if (ctxAttribute==null){
						if (ctxIdentifier.getType().equals(CtxAttributeTypes.NAME)){
							lblName.setText(" Permission denied ");
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.BIRTHDAY)){
							lblDoB.setText(" Permission denied ");
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.EMAIL)){
							lblEmail.setText(" Permission denied ");
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
							lblLoc.setText(" Permission denied ");
						}
						logging.debug("Retrieved ctxAttribute of type {} is null", ctxIdentifier.getType());
					}else{
						if (ctxIdentifier.getType().equals(CtxAttributeTypes.NAME)){
							lblName.setText(ctxAttribute.getStringValue());
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.BIRTHDAY)){
							lblDoB.setText(ctxAttribute.getStringValue());
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.EMAIL)){
							lblEmail.setText(ctxAttribute.getStringValue());
						}else if (ctxIdentifier.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
							lblLoc.setText(ctxAttribute.getStringValue());
						}
					}
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}