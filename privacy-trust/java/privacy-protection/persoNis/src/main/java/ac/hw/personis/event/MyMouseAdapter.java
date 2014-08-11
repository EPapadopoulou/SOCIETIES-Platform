package ac.hw.personis.event;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.PrivacyPreferenceTypeConstants;

import ac.hw.personis.PersonisHelper;
import ac.hw.personis.preference.AllPreferencesGUI;
import ac.hw.personis.preference.table.DetailTableModel;


public class MyMouseAdapter extends MouseAdapter {

	private JTable table;
	private AllPreferencesGUI gui;
	private PrivacyPreferenceTypeConstants type;

	public MyMouseAdapter(JTable table, AllPreferencesGUI gui, PrivacyPreferenceTypeConstants type ){
		this.table = table;
		this.gui = gui;
		this.type = type;
	}
	public void mousePressed( MouseEvent e )
	{
		// Left mouse click
		if ( SwingUtilities.isLeftMouseButton( e ) )
		{
			// Do something
		}
		// Right mouse click
		else if ( SwingUtilities.isRightMouseButton( e ))
		{
			// get the coordinates of the mouse click
			Point p = e.getPoint();
 
			// get the row index that contains that coordinate
			int rowNumber = table.rowAtPoint( p );
 
			// Get the ListSelectionModel of the JTable 
			// set the selected interval of rows. Using the "rowNumber"
			// variable for the beginning and end selects only that one row.
			table.getSelectionModel().setSelectionInterval( rowNumber, rowNumber );
			//JOptionPane.showMessageDialog(table, "rowNumber:"+rowNumber);
			
			 DetailTableModel model = (DetailTableModel) table.getModel();
			 
			 String preferenceString = model.getRow(rowNumber);
			 gui.showPreference(preferenceString, type);
		}
	}
}
