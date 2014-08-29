/**
 * 
 */
package org.societies.privacytrust.privacyprotection.privacynegotiation.identityCreation.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.privacytrust.privacyprotection.privacynegotiation.Logger;

/**
 * @author PUMA
 *
 */
public class IdentityInformationTableModel extends AbstractTableModel  implements TableModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<CtxAttribute> ctxAttributes;
	private Logger logging = new Logger(this.getClass().getName());
	public IdentityInformationTableModel(){
		this.ctxAttributes = new ArrayList<CtxAttribute>();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return ctxAttributes.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		this.logging.debug("getValueAt("+rowIndex+", "+columnIndex+") with size of attributes: "+ctxAttributes.size());
		if (this.ctxAttributes.size()>0){
			if (columnIndex==0){
				return ctxAttributes.get(rowIndex).getType();
			}else if (columnIndex==1){
				return ctxAttributes.get(rowIndex).getStringValue();
			}else if (columnIndex==2){
				return ctxAttributes.get(rowIndex).getId().getUri();
			}
		}

		return "";
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex==0){
			return "Data Type";
		}else if (columnIndex==1){
			return "Current value";
		}else if (columnIndex==2){
			return "ID";

		}

		return "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addAttribute(CtxAttribute ctxAttribute){
		this.logging.debug("Adding new ctxAttribute: "+ctxAttribute.getType()+" - "+ctxAttribute.getStringValue());
		boolean typeAlreadyExists = false;
		for (CtxAttribute attr : this.ctxAttributes){
			if (attr.getType().equalsIgnoreCase(ctxAttribute.getType())){
				this.ctxAttributes.remove(attr);
				this.ctxAttributes.add(ctxAttribute);
				typeAlreadyExists = true;
				break;
			}
		}

		if (!typeAlreadyExists){
			this.ctxAttributes.add(ctxAttribute);
		}
		this.fireTableDataChanged();
	}

	public List<CtxIdentifier> getValues() {
		
		List<CtxIdentifier> list = new ArrayList<CtxIdentifier>();
		for (CtxAttribute ctxAttr : this.ctxAttributes){
			list.add(ctxAttr.getId());
		}
		return list;
	}

}
