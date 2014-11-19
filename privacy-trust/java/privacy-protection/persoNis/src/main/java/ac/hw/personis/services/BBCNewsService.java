/**
 * 
 */
package ac.hw.personis.services;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.model.CtxIDChanger;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.internalwindows.apps.ImagePanel;
import javax.swing.SwingConstants;




/**
 * @author PUMA
 * 
 */
public class BBCNewsService extends JFrame implements ActionListener, CtxChangeEventListener, WindowListener{

	/**
	 * 
	 */
	private final static Logger logging = LoggerFactory.getLogger(BBCNewsService.class);
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
	private boolean loggedIn = false;

	public BBCNewsService(PersonisHelper personisHelper) {
		super();
		getContentPane().setBackground(Color.BLUE);
		this.personisHelper = personisHelper;
		this.ctxBroker = personisHelper.getCtxBroker();
		this.myServiceName = PersonisHelper.BBC_NEWS_APP;
		this.myIDBean = personisHelper.getBBCNewsRequestor();
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
		this.registerForContext();
		getContentPane().setLayout(null);
		setSize(467, 637);

		//TODO: needs to be updated to show bbc background
		ImagePanel imagePanel = new ImagePanel("/bbcnappback.png");
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
		lblName.setBounds(172, 193, 242, 30);
		imagePanel.add(lblName);
		
		lblName1 = new JLabel("Name:");
		lblName1.setBounds(46, 193, 116, 30);
		imagePanel.add(lblName1);
		
		lblDob1 = new JLabel("Date of Birth:");
		lblDob1.setBounds(46, 229, 116, 30);
		imagePanel.add(lblDob1);
		
		lblDoB = new JLabel("DoB");
		lblDoB.setBounds(172, 229, 242, 30);
		imagePanel.add(lblDoB);
		
		lblEmailAddress = new JLabel("Email address:");
		lblEmailAddress.setBounds(46, 270, 116, 30);
		imagePanel.add(lblEmailAddress);
		
		lblEmail = new JLabel("Email");
		lblEmail.setBounds(172, 270, 242, 30);
		imagePanel.add(lblEmail);
		
		lblLoggedIn = new JLabel("New label");
		lblLoggedIn.setBounds(131, 132, 283, 30);
		imagePanel.add(lblLoggedIn);
		
		lblCurrentLocation = new JLabel("Current location:");
		lblCurrentLocation.setBounds(46, 311, 116, 30);
		imagePanel.add(lblCurrentLocation);
		
		lblLoc = new JLabel("loc");
		lblLoc.setBounds(172, 311, 242, 48);
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
				CtxAttributeIdentifier ctxAttrId = CtxIDChanger.changeIDOwner(userIdReal.getBareJid(), (CtxAttributeIdentifier) ctxId);
				this.ctxBroker.registerForChanges(myID, this, ctxAttrId);
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if (!loggedIn){
			String selectedId = (String) JOptionPane.showInputDialog(BBCNewsService.this, "Login with identity:", "Login", JOptionPane.PLAIN_MESSAGE, null, new String[]{userID.getBareJid()}, userID.getBareJid());
			loggedIn = true;
		}
		if (event.getSource() == btnRetrieve) {
			System.out.println("btnRetrieve clicked, retrieving: "+this.dataIDs.size()+" dataTypes from ID: "+userID.getJid());
			new Thread() {

				public void run() {
					for (CtxIdentifier ctxIdentifier : BBCNewsService.this.dataIDs){
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
					
					lblLoggedIn.setText("You are logged in as "+userID.getBareJid());
					setLabelsVisible();
					BBCNewsService.this.logging.debug("Exiting thread");
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
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				CtxIdentifier id = event.getId();
				CtxAttributeIdentifier ctxIdentifier = CtxIDChanger.changeIDOwner(userID.getBareJid(), (CtxAttributeIdentifier) id);
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
		IIdentity userIdReal = personisHelper.getCommsMgr().getIdManager().getThisNetworkNode();
		for (int i = 0; i<this.dataIDs.size(); i++){
			try {
				CtxIdentifier ctxId = this.dataIDs.get(i);
				CtxAttributeIdentifier ctxAttrId = CtxIDChanger.changeIDOwner(userIdReal.getBareJid(), (CtxAttributeIdentifier) ctxId);
				this.ctxBroker.unregisterFromChanges(myID, this, ctxAttrId);
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
