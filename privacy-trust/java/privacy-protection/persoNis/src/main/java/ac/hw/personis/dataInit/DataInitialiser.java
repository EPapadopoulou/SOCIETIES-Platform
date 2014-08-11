package ac.hw.personis.dataInit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;

public class DataInitialiser extends JDialog implements ActionListener {

	private static final String FULLNAME = "name";
	private static final String LOCATION_COORDINATES = "locationCoordinates";
	private static final String EMAIL = "email";
	private static final String OCCUPATION = "occupation";
	private static final String AGE = "age";
	private static final String LANGUAGES = "languages";
	private static final String INTERESTS = "interests";
	private static final String LOCATION_SYMBOLIC = "locationSymbolic";
	private static final String SEX = "sex";

	private JPanel contentPane;
    private Logger logging = LoggerFactory.getLogger(this.getClass());


	public static String[] dataTypes = new String[]{LOCATION_SYMBOLIC, INTERESTS, LANGUAGES, AGE, OCCUPATION,  EMAIL, LOCATION_COORDINATES, FULLNAME};
	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtJob;
	private JTextField txtInterests;
	private JTextField txtLanguages;

	private ICtxBroker ctxBroker;

	private IIdentity userId;

	private JComboBox cmbAge;

	private JComboBox cmbSex;

	private IndividualCtxEntity person;
	
	private boolean initialised = false;
	private List<CtxAttribute> attributes;


	/**
	 * Create the frame.
	 */
	public DataInitialiser(ICtxBroker ctxBroker, IIdentity userId, JFrame frame) {
		super(frame, "Store your details", true);
		this.ctxBroker = ctxBroker;
		this.userId = userId;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 338);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{424, 0};
		gbl_contentPane.rowHeights = new int[]{1, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

		JPanel mainPanel = new JPanel();
		GridBagConstraints gbc_mainPanel = new GridBagConstraints();
		gbc_mainPanel.insets = new Insets(20, 20, 20, 20);
		gbc_mainPanel.anchor = GridBagConstraints.NORTH;
		gbc_mainPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_mainPanel.gridx = 0;
		gbc_mainPanel.gridy = 0;
		contentPane.add(mainPanel, gbc_mainPanel);
		mainPanel.setLayout(new GridLayout(7, 2, 10, 10));

		JLabel lblNewLabel = new JLabel("Your name:");
		mainPanel.add(lblNewLabel);

		txtName = new JTextField();
		mainPanel.add(txtName);
		txtName.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Your email:");
		mainPanel.add(lblNewLabel_1);

		txtEmail = new JTextField();
		mainPanel.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblYourAge = new JLabel("Your age:");
		mainPanel.add(lblYourAge);

		Vector<String> ages = new Vector<String>();
		for (int i = 1; i<=100; i++){
			ages.add(""+i);
		}
		cmbAge = new JComboBox(ages);
		mainPanel.add(cmbAge);

		JLabel lblNewLabel_5 = new JLabel("Gender:");
		mainPanel.add(lblNewLabel_5);

		String[] genderOptions = new String[]{"male", "female"};
		cmbSex = new JComboBox(genderOptions);
		mainPanel.add(cmbSex);

		JLabel lblNewLabel_2 = new JLabel("Your occupation:");
		mainPanel.add(lblNewLabel_2);

		txtJob = new JTextField();
		mainPanel.add(txtJob);
		txtJob.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Your interests:");
		mainPanel.add(lblNewLabel_3);

		txtInterests = new JTextField();
		mainPanel.add(txtInterests);
		txtInterests.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Languages you speak:");
		mainPanel.add(lblNewLabel_4);

		txtLanguages = new JTextField();
		mainPanel.add(txtLanguages);
		txtLanguages.setColumns(10);

		JPanel btnPanel = new JPanel();
		GridBagConstraints gbc_btnPanel = new GridBagConstraints();
		gbc_btnPanel.fill = GridBagConstraints.BOTH;
		gbc_btnPanel.gridx = 0;
		gbc_btnPanel.gridy = 1;
		contentPane.add(btnPanel, gbc_btnPanel);
		btnPanel.setLayout(new GridLayout(1, 1, 20, 20));

		JButton btnSave = new JButton("Save details");
		btnSave.addActionListener(this);
		btnPanel.add(btnSave);
		

	}

	
	public boolean dataExists(){
		this.attributes = new ArrayList<CtxAttribute>();
		boolean allDataTypesExist = true;

		try {
			person = this.ctxBroker.retrieveIndividualEntity(this.userId).get();
			for (String dataType : dataTypes){				
				Set<CtxAttribute> attributes = person.getAttributes(dataType);
				if (!attributes.isEmpty()){
					CtxAttribute attribute = attributes.iterator().next();
					this.attributes.add(attribute);
					if ((attribute.getStringValue()==null) || (attribute.getStringValue()=="")){
						allDataTypesExist = false;
					}
				}else{
					allDataTypesExist = false;
					this.logging.debug(dataType+" doesn't exist");
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return allDataTypesExist;
	}
	
	private void initialiseData(){
		for (CtxAttribute attribute : attributes) {
			String dataType = attribute.getType();
			String value = "null";
			
			if ((attribute.getStringValue() != null)
					&& (attribute.getStringValue() != "")) {
				value = attribute.getStringValue();
			}
			this.setValueToTextField(dataType, value);
		}
	}
	
	private void storeData(){
		try {
			if (this.person==null){

				person = this.ctxBroker.retrieveIndividualEntity(userId).get();
			}
			this.createOrUpdateAttribute(FULLNAME, this.txtName.getText());
			this.createOrUpdateAttribute(EMAIL, this.txtEmail.getText());
			this.createOrUpdateAttribute(AGE, this.cmbAge.getSelectedItem().toString());
			this.createOrUpdateAttribute(SEX, this.cmbSex.getSelectedItem().toString());
			this.createOrUpdateAttribute(INTERESTS, this.txtInterests.getText());
			this.createOrUpdateAttribute(LANGUAGES, this.txtLanguages.getText());
			this.createOrUpdateAttribute(OCCUPATION, this.txtJob.getText());
			this.createOrUpdateAttribute(LOCATION_COORDINATES, this.getHWUCoordinates());
			this.createOrUpdateAttribute(LOCATION_SYMBOLIC, "PumaLab");
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private CtxAttribute createOrUpdateAttribute(String datatype, String value){
		try {
			Set<CtxAttribute> attributes = person.getAttributes(datatype);
			if (attributes.size()==0){
				CtxAttribute ctxAttribute = this.ctxBroker.createAttribute(this.person.getId(), datatype).get();
				ctxAttribute.setStringValue(value);
				return (CtxAttribute) this.ctxBroker.update(ctxAttribute).get();
				
			}else if (attributes.size()==1){
				CtxAttribute ctxAttribute = attributes.iterator().next();
				ctxAttribute.setStringValue(value);
				return (CtxAttribute) this.ctxBroker.update(ctxAttribute).get();
			}else{
				Iterator<CtxAttribute> iterator = attributes.iterator();
				while(iterator.hasNext()){
					CtxAttribute ctxAttribute = iterator.next();
					ctxAttribute.setStringValue(value);
					return (CtxAttribute) this.ctxBroker.update(ctxAttribute).get();
				}
				this.logging.debug("Context has more than one attribute of type: "+datatype);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String getHWUCoordinates(){
		return "55.912378, -3.323077";
	}
	private void setValueToTextField(String dataType, String value){
		if (dataType.equals(DataInitialiser.FULLNAME)){
			this.txtName.setText(value);
			return;
		}
		if (dataType.equals(EMAIL)){
			this.txtEmail.setText(value);
			return;
		}
		if (dataType.equals(OCCUPATION)){
			this.txtJob.setText(value);
		}
		if (dataType.equals(AGE)){
			this.cmbAge.setSelectedItem(value);
		}
		if (dataType.equals(INTERESTS)){
			this.txtInterests.setText(value);
		}
		if (dataType.equals(LANGUAGES)){
			this.txtLanguages.setText(value);
		}
		if (dataType.equals(SEX)){
			this.cmbSex.setSelectedItem(value);
		}



	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.storeData();
		this.initialised = true;
		this.dispose();
	}


	public boolean isInitialised() {
		this.initialiseData();
		this.setVisible(true);
		return initialised;
	}


	public void setInitialised(boolean initialised) {
		this.initialised = initialised;
	}

}
