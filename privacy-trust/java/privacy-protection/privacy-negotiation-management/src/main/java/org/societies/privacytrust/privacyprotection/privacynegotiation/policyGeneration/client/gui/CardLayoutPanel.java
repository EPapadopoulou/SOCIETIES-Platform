package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class CardLayoutPanel extends JPanel implements ActionListener{
	
	private static final String PANEL = "panel";
	private List<RequestItemPanel> panels;
	private int pointer = 0;
	private JButton buttonNext;
	private JButton buttonBack;
	private JPanel cardsPanel;

	public CardLayoutPanel(List<RequestItemPanel> panels){
		super();
		
		this.panels = panels;
		cardsPanel = new JPanel(new CardLayout());
		for (int i=0; i<panels.size(); i++){
			cardsPanel.add(panels.get(i), PANEL+i);
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{404, 1, 0};
		gridBagLayout.rowHeights = new int[]{400, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		buttonBack = new JButton("< Back");
		buttonBack.addActionListener(this);
		
		
		GridBagConstraints gbc_cardsPanel = new GridBagConstraints();
		gbc_cardsPanel.fill = GridBagConstraints.BOTH;
		gbc_cardsPanel.gridwidth = 2;
		gbc_cardsPanel.insets = new Insets(0, 0, 5, 0);
		gbc_cardsPanel.gridx = 0;
		gbc_cardsPanel.gridy = 0;
		add(cardsPanel, gbc_cardsPanel);
		GridBagConstraints gbc_buttonBack = new GridBagConstraints();
		gbc_buttonBack.ipadx = 40;
		gbc_buttonBack.anchor = GridBagConstraints.NORTHWEST;
		gbc_buttonBack.insets = new Insets(5, 15, 5, 5);
		gbc_buttonBack.gridx = 0;
		gbc_buttonBack.gridy = 1;
		add(buttonBack, gbc_buttonBack);
		buttonNext = new JButton("Next >");
		buttonNext.addActionListener(this);
		GridBagConstraints gbc_buttonNext = new GridBagConstraints();
		gbc_buttonNext.ipadx = 40;
		gbc_buttonNext.insets = new Insets(5, 5, 5, 15);
		gbc_buttonNext.anchor = GridBagConstraints.NORTHEAST;
		gbc_buttonNext.gridx = 1;
		gbc_buttonNext.gridy = 1;
		add(buttonNext, gbc_buttonNext);
		
		buttonBack.setVisible(false);
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(buttonNext)){
			if (pointer<panels.size()-1){
				pointer ++;
			}
			CardLayout cl = (CardLayout) cardsPanel.getLayout();
			cl.show(cardsPanel, PANEL+pointer);
		}else if (event.getSource().equals(buttonBack)){
			if (pointer>0){
				pointer --;
				CardLayout cl = (CardLayout) cardsPanel.getLayout();
				cl.show(cardsPanel, PANEL+pointer);
			}
		}
		
		if (pointer==0){
			buttonBack.setVisible(false);
		}else{
			buttonBack.setVisible(true);
		}
		if (pointer==panels.size()-1){
			buttonNext.setVisible(false);
		}else{
			buttonNext.setVisible(true);
		}
	}

}
