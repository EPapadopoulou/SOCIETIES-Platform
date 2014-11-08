package ac.hw.personis.notification;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class NotificationsModel  extends AbstractTableModel  implements TableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<JLabel> labels;
	
	public NotificationsModel(){
		labels = new ArrayList<JLabel>();
		labels.add(new JLabel("lksjklfsdlflskdfklsdlfjsldfldsj"));
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 1;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return labels.size();
	}
	@Override
	public Object getValueAt(int row, int column) {
		// TODO Auto-generated method stub
		return labels.get(row);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex){
		return JLabel.class;
	}
}
