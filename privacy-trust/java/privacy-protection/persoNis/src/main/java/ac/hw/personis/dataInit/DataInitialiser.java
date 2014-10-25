package ac.hw.personis.dataInit;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

/*	private static final String FULLNAME = "name";
	private static final String LOCATION_COORDINATES = "locationCoordinates";
	private static final String EMAIL = "email";
	private static final String OCCUPATION = "occupation";
	private static final String AGE = "age";
	private static final String LANGUAGES = "languages";
	private static final String INTERESTS = "interests";
	private static final String LOCATION_SYMBOLIC = "locationSymbolic";
	private static final String SEX = "sex";*/

	private JPanel contentPane;
    private Logger logging = LoggerFactory.getLogger(this.getClass());


	public static String[] dataTypes = new String[]{
		org.societies.api.context.model.CtxAttributeTypes.LOCATION_SYMBOLIC, 
		org.societies.api.context.model.CtxAttributeTypes.INTERESTS, 
		org.societies.api.context.model.CtxAttributeTypes.LANGUAGES,
		org.societies.api.context.model.CtxAttributeTypes.AGE, 
		org.societies.api.context.model.CtxAttributeTypes.OCCUPATION,  
		org.societies.api.context.model.CtxAttributeTypes.EMAIL, 
		org.societies.api.context.model.CtxAttributeTypes.LOCATION_COORDINATES, 
		org.societies.api.context.model.CtxAttributeTypes.NAME,
		org.societies.api.context.model.CtxAttributeTypes.SEX,
		org.societies.api.context.model.CtxAttributeTypes.SKILLS,
		org.societies.api.context.model.CtxAttributeTypes.BIRTHDAY};
	private JTextField txtName;
	private JTextField txtEmail;
	private JTextField txtJob;
	private JTextField txtInterests;
	private JTextField txtLanguages;
	private JTextField txtBirthday;
	
	private ICtxBroker ctxBroker;

	private IIdentity userId;

	

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
		setBounds(100, 100, 450, 363);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel mainPanel = new JPanel();
		mainPanel.setBounds(5, 11, 424, 231);
		contentPane.add(mainPanel);
		mainPanel.setLayout(null);

		JLabel lblNewLabel = new JLabel("Your name:");
		lblNewLabel.setBounds(0, 0, 187, 20);
		mainPanel.add(lblNewLabel);

		txtName = new JTextField();
		lblNewLabel.setLabelFor(txtName);
		txtName.setBounds(210, 0, 204, 20);
		mainPanel.add(txtName);
		txtName.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Your email:");
		lblNewLabel_1.setBounds(0, 30, 187, 20);
		mainPanel.add(lblNewLabel_1);

		txtEmail = new JTextField();
		lblNewLabel_1.setLabelFor(txtEmail);
		txtEmail.setBounds(210, 30, 204, 20);
		mainPanel.add(txtEmail);
		txtEmail.setColumns(10);

		JLabel lblYourAge = new JLabel("Your birthday:");
		lblYourAge.setBounds(0, 60, 187, 20);
		mainPanel.add(lblYourAge);

		
		txtBirthday = new JTextField();
		lblYourAge.setLabelFor(txtBirthday);
		txtBirthday.setBounds(210, 60, 204, 20);
		txtBirthday.addMouseListener(new MouseAdapter() {
			 	public void mouseClicked(MouseEvent e){
					txtBirthday.setText(new DatePicker(DataInitialiser.this).setPickedDate());
			 	}
		});
		mainPanel.add(txtBirthday);

		JLabel lblNewLabel_5 = new JLabel("Gender:");
		lblNewLabel_5.setBounds(0, 90, 187, 20);
		mainPanel.add(lblNewLabel_5);

		String[] genderOptions = new String[]{"male", "female"};
		cmbSex = new JComboBox(genderOptions);
		lblNewLabel_5.setLabelFor(cmbSex);
		cmbSex.setBounds(210, 90, 204, 20);
		mainPanel.add(cmbSex);

		JLabel lblNewLabel_2 = new JLabel("Your occupation:");
		lblNewLabel_2.setBounds(0, 120, 187, 20);
		mainPanel.add(lblNewLabel_2);

		txtJob = new JTextField();
		lblNewLabel_2.setLabelFor(txtJob);
		txtJob.setBounds(210, 120, 204, 20);
		mainPanel.add(txtJob);
		txtJob.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Your interests:");
		lblNewLabel_3.setBounds(0, 150, 187, 20);
		mainPanel.add(lblNewLabel_3);

		txtInterests = new JTextField();
		lblNewLabel_3.setLabelFor(txtInterests);
		txtInterests.setBounds(210, 150, 204, 20);
		mainPanel.add(txtInterests);
		txtInterests.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Languages you speak:");
		lblNewLabel_4.setBounds(0, 180, 187, 20);
		mainPanel.add(lblNewLabel_4);

		txtLanguages = new JTextField();
		lblNewLabel_4.setLabelFor(txtLanguages);
		txtLanguages.setBounds(210, 180, 204, 20);
		mainPanel.add(txtLanguages);
		txtLanguages.setColumns(10);

		JPanel btnPanel = new JPanel();
		btnPanel.setBounds(5, 253, 424, 67);
		contentPane.add(btnPanel);

		JButton btnSave = new JButton("Save details");
		btnSave.setBounds(0, 11, 424, 49);
		btnSave.addActionListener(this);
		btnPanel.setLayout(null);
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
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.NAME, this.txtName.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.EMAIL, this.txtEmail.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.BIRTHDAY, this.txtBirthday.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.SEX, this.cmbSex.getSelectedItem().toString());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.INTERESTS, this.txtInterests.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.LANGUAGES, this.txtLanguages.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.OCCUPATION, this.txtJob.getText());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.LOCATION_COORDINATES, this.getHWUCoordinates());
			this.createOrUpdateAttribute(org.societies.api.context.model.CtxAttributeTypes.LOCATION_SYMBOLIC, "PumaLab");
			
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
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.NAME)){
			this.txtName.setText(value);
			return;
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.EMAIL)){
			this.txtEmail.setText(value);
			return;
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.OCCUPATION)){
			this.txtJob.setText(value);
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.BIRTHDAY)){
			this.txtBirthday.setText(value);
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.INTERESTS)){
			this.txtInterests.setText(value);
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.LANGUAGES)){
			this.txtLanguages.setText(value);
		}
		if (dataType.equals(org.societies.api.context.model.CtxAttributeTypes.SEX)){
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


	public String getUserName() {
		// TODO Auto-generated method stub
		return this.txtName.getText();
	}

}
