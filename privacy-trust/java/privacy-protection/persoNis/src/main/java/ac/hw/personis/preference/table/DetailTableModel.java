/**
 * 
 */
package ac.hw.personis.preference.table;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;


/**
 * @author PUMA
 *
 */
public class DetailTableModel extends AbstractTableModel{
	

	private List<String> list = new ArrayList<String>();

	public DetailTableModel(Enumeration<String> enumeration) {
		
		while (enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		
		
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return String.class;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		return this.list.get(row);
	}

	public String getRow(int row){
		return this.list.get(row);
	}
	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setValueAt(Object title, int row, int arg2) {
		this.list.set(row, (String) title);
		fireTableCellUpdated(row, arg2);
	}

	public void refreshData(Enumeration<String> enumeration){
		this.list  = new ArrayList<String>();
		
		while (enumeration.hasMoreElements()){
			list.add(enumeration.nextElement());
		}
		
		
		this.fireTableDataChanged();
	}
}
