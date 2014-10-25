package org.societies.privacytrust.privacyprotection.privacynegotiation.identityCreation.gui;

import java.awt.EventQueue;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.model.privacypolicy.Agreement;
import org.societies.privacytrust.privacyprotection.privacynegotiation.PrivacyPolicyNegotiationManager;

public class IdentitySelectionWindow {

	private JFrame frame;
	private IdentitySelectionGUIDialog idsDialog;
	private IdentityCreationGUIDialog idcDialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
/*					ArrayList<IIdentity> identities = new ArrayList<IIdentity>();
					IIdentity ideliza  = new IdentityImpl("eliza.societies.local2");
					IIdentity idAnonymous = new IdentityImpl("anonymous234234.societies.local2");
					identities.add(ideliza);
					identities.add(idAnonymous);
					IdentitySelectionWindow window = new IdentitySelectionWindow(identities);
					
					//window.frame.setVisible(true);
					
					System.out.println("Got Identity: "+window.getSelectedIdentity());*/
					
/*					Agreement agreement = MockRequestPolicy.getAgreement();
					ICtxBroker ctxBroker = Mockito.mock(ICtxBroker.class);
					
					IdentitySelectionWindow widow = new IdentitySelectionWindow(agreement , ctxBroker , userId);*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * this class is now deprecated. Use IIdentitySelection component
	 */
	@Deprecated
	public IdentitySelectionWindow(List<IIdentity> identities, IIdentity recommendedIdentity) {
		initialize();
		idsDialog = new IdentitySelectionGUIDialog(frame, identities, recommendedIdentity);
	}

	public IIdentity getSelectedIdentity(){
		this.frame.dispose();
		return this.idsDialog.getSelectedIdentity();
		
	}
	
	
	public IdentitySelectionWindow(Agreement agreement, PrivacyPolicyNegotiationManager negManager, IIdentity userId, List<IIdentity> list){
		initialize();
		idcDialog = new IdentityCreationGUIDialog(frame, agreement, negManager, userId, list);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Hashtable<String, List<CtxIdentifier>> getIdentityInformation() {
		this.frame.dispose();
		return this.idcDialog.getIdentityInformation();
	}

}
