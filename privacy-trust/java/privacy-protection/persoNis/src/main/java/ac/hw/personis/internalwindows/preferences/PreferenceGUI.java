package ac.hw.personis.internalwindows.preferences;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import javax.swing.JLabel;
import java.awt.Insets;

public class PreferenceGUI extends JDialog implements ActionListener{

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JTree tree;
	private JLabel lblConfidence;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Condition condition = new Condition();
			condition.setConditionConstant(ConditionConstants.RIGHT_TO_OPTOUT);
			condition.setValue("yes");
			IPrivacyPreferenceCondition conditionPreference = new PrivacyCondition(condition);
			PrivacyPreference conditionNode =new PrivacyPreference(conditionPreference);
			List<Action> actions = new ArrayList<Action>();
			Action action = new Action();
			action.setActionConstant(ActionConstants.READ);
			actions.add(action );
			IPrivacyOutcome outcome = new PPNPOutcome(condition);
			PrivacyPreference outcomeNode = new PrivacyPreference(outcome);
			conditionNode.add(outcomeNode);
			PreferenceGUI dialog = new PreferenceGUI(conditionNode, "type");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public PreferenceGUI(PrivacyPreference preference, String dataType) {
		setTitle(dataType);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			tree = new JTree(preference);
			tree.addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent arg0) {
					PrivacyPreference privPreference = (PrivacyPreference) tree.getLastSelectedPathComponent();
					if (privPreference!=null){
						if (privPreference.isLeaf())
							lblConfidence.setText("Confidence Level: "+privPreference.getOutcome().getConfidenceLevel());

					}

				}
			});
			GridBagConstraints gbc_tree = new GridBagConstraints();
			gbc_tree.fill = GridBagConstraints.BOTH;
			gbc_tree.gridx = 0;
			gbc_tree.gridy = 0;
			contentPanel.add(tree, gbc_tree);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				GridBagLayout gbl_buttonPane = new GridBagLayout();
				gbl_buttonPane.columnWidths = new int[]{331, 46, 47, 0};
				gbl_buttonPane.rowHeights = new int[]{23, 0};
				gbl_buttonPane.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				gbl_buttonPane.rowWeights = new double[]{0.0, Double.MIN_VALUE};
				buttonPane.setLayout(gbl_buttonPane);
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				{
					lblConfidence = new JLabel("New label");
					GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
					gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
					gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
					gbc_lblNewLabel.gridx = 0;
					gbc_lblNewLabel.gridy = 0;
					buttonPane.add(lblConfidence, gbc_lblNewLabel);
				}
				GridBagConstraints gbc_okButton = new GridBagConstraints();
				gbc_okButton.anchor = GridBagConstraints.NORTHWEST;
				gbc_okButton.gridx = 2;
				gbc_okButton.gridy = 0;
				buttonPane.add(okButton, gbc_okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(PreferenceGUI.this.okButton)){
			PreferenceGUI.this.dispose();
		}

	}

}
