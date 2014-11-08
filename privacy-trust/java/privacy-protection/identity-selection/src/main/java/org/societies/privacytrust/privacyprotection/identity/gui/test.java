package org.societies.privacytrust.privacyprotection.identity.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JScrollPane;

public class test extends JFrame {

	private JPanel contentPane;
	private JList list2;
	private JList list1;
	private int counter;
	private int mainCounter;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					test frame = new test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		DefaultListModel listModel1 = new DefaultListModel();
		listModel1.addElement("1");
		counter = 0;

		scrollPane = new JScrollPane();
		scrollPane.setBounds(37, 42, 95, 164);
		contentPane.add(scrollPane);
		list1 = new JList(listModel1);
		scrollPane.setViewportView(list1);
		list1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (list1.getSelectedIndex()>=0){
					if (!arg0.getValueIsAdjusting()){
						DefaultListModel listModel2 = (DefaultListModel) list2.getModel();
						String selectedString = (String) list1.getSelectedValue();
						listModel2.addElement(selectedString+" "+counter);
						list2.setModel(listModel2);
						counter++;

					}
				}

			}
		});

		DefaultListModel listModel2 = new DefaultListModel();

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(213, 42, 95, 164);
		contentPane.add(scrollPane_1);
		list2 = new JList(listModel2);
		scrollPane_1.setViewportView(list2);
		mainCounter = 2;
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultListModel listModel1 = (DefaultListModel) list1.getModel();
				listModel1.addElement(""+mainCounter);
				list1.setModel(listModel1);
				mainCounter++;
			}
		});
		btnNewButton.setBounds(133, 228, 89, 23);
		contentPane.add(btnNewButton);
	}
}
