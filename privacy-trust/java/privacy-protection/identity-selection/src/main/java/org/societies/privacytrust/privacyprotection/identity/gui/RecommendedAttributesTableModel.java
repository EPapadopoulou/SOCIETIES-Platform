/**
 * 
 */
package org.societies.privacytrust.privacyprotection.identity.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;

/**
 * @author PUMA
 *
 */
public class RecommendedAttributesTableModel extends AbstractTableModel  implements TableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//List<CtxAttribute> ctxAttributes;
	List<Pair> pairs;
    private Logger logging = LoggerFactory.getLogger(this.getClass());
	public RecommendedAttributesTableModel(){
		this.pairs = new ArrayList<Pair>();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return pairs.size();
	}
	
	

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		this.logging.debug("getValueAt("+rowIndex+", "+columnIndex+") with size of attributes: "+pairs.size());
		if (this.pairs.size()>0){
			if (columnIndex==0){
				return pairs.get(rowIndex).getChecked();
			}else if (columnIndex==1){
				return pairs.get(rowIndex).getCtxAttribute().getType();
			}else if (columnIndex==2){
				return pairs.get(rowIndex).getCtxAttribute().getStringValue();
			}else if (columnIndex==3){
				return pairs.get(rowIndex).getCtxAttribute().getId().getUri();
				
			}
		}

		return "";
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex==0){
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex==0){
			return "Accept";
			
		}else if (columnIndex==1){
			return "Data Type";
		}else if (columnIndex==2){
			return "Current value";
		}else if (columnIndex==3){
			return "ID";
		}

		return "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return columnIndex==0;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col==0){
			Boolean bool = (Boolean) value;
			this.pairs.get(row).setChecked(bool);
		}
	}

	public void addAttribute(CtxAttribute ctxAttribute){
		this.logging.debug("Adding new ctxAttribute: "+ctxAttribute.getType()+" - "+ctxAttribute.getStringValue());
		boolean typeAlreadyExists = false;
		for (Pair pair : this.pairs){
			
			CtxAttribute attr = pair.getCtxAttribute();
			if (attr.getType().equalsIgnoreCase(ctxAttribute.getType())){
				this.pairs.remove(pair);
				this.pairs.add(new Pair(ctxAttribute));
				typeAlreadyExists = true;
				break;
			}
		}

		if (!typeAlreadyExists){
			this.pairs.add(new Pair(ctxAttribute));
		}
		this.fireTableDataChanged();
	}

	public void remove(int index){
		if ((index>-1) && (index<pairs.size())){
			this.pairs.remove(index);
			fireTableDataChanged();
		}
		
	}
	public List<CtxAttribute> getValues() {
		
		List<CtxAttribute> list = new ArrayList<CtxAttribute>();
		
		for (Pair pair : pairs){
			if (pair.checked){
				list.add(pair.getCtxAttribute());
			}
		}
		return list;
	}
	
	
	private class Pair{
		private final CtxAttribute ctxAttribute;
		private Boolean checked;
		
		Pair(CtxAttribute ctxAttribute){
			this.ctxAttribute = ctxAttribute;
			checked = true;
		}

		public CtxAttribute getCtxAttribute() {
			return ctxAttribute;
		}

		public Boolean getChecked() {
			return checked;
		}

		public void setChecked(Boolean checked) {
			this.checked = checked;
		}
	}

}
