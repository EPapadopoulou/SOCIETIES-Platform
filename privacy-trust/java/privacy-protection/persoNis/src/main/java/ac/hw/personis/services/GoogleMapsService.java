/**
 * 
 */
package ac.hw.personis.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import ac.hw.personis.PersonisHelper;



/**
 * @author PUMA
 * 
 */
public class GoogleMapsService extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;
	private String authKey = "AIzaSyA9BkjBXcXYv5ES0kvf5IyUpZXEuaBlc1M";
	private JButton btnRetrieve;
	private JTextArea textArea;
	private ICtxBroker ctxBroker;
	private PersonisHelper personisHelper;
	private List<CtxIdentifier> dataIDs;
	private String myServiceName;
	private RequestorServiceBean myIDBean;
	private RequestorService myID;
	private IIdentity userID;

	private Agreement agreement;

	public GoogleMapsService(PersonisHelper personisHelper) {
		super();
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

		setResizable(false);
		getContentPane().setLayout(null);
		setSize(800, 700);
		btnRetrieve = new JButton("Retrieve data ");
		btnRetrieve.setBounds(10, 11, 190, 48);
		btnRetrieve.addActionListener(this);
		getContentPane().add(btnRetrieve);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setBounds(61, 162, 691, 470);
		getContentPane().add(textArea);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}



	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == btnRetrieve) {
			logging.debug("btnRetrieve clicked, retrieving: "+this.dataIDs.size()+" dataTypes from ID: "+userID.getJid());
			for (CtxIdentifier ctxIdentifier : this.dataIDs){
				logging.debug("btnRetrieve: "+ctxIdentifier.getUri());
				try {
					CtxAttribute ctxAttribute = (CtxAttribute) ctxBroker.retrieve(myID, ctxIdentifier).get();
					if (null==ctxAttribute){
						textArea.append("Could not access "+ctxIdentifier+ctxIdentifier+System.getProperty( "line.separator" ));
					}else{
						textArea.append("Retrieved "+ctxIdentifier.getType()+" = "+ctxAttribute.getStringValue()+" ("+ctxIdentifier+System.getProperty( "line.separator" ));
						logging.debug("Retrieved "+ctxIdentifier.getType()+" = "+ctxAttribute.getStringValue());
					}
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					textArea.append("CtxException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					textArea.append("InterruptedException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					textArea.append("ExecutionException while retrieving: "+ctxIdentifier+System.getProperty( "line.separator" ));
				}
			}

		}

	}


}
