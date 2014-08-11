/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.gui.privacy.PPN;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

public class PPNPreferenceDetailsTable extends AbstractTableModel{

	private List<PPNPreferenceDetailsBean> details = new ArrayList<PPNPreferenceDetailsBean>();
	private String[] columnNames = {"Context Type", "Provider Identity", "CIS ID or ServiceID"};
	public PPNPreferenceDetailsTable(List<PPNPreferenceDetailsBean> initDetails){
		super();
		
		for (PPNPreferenceDetailsBean d: initDetails){
			this.addRow(d);
		}
	}
	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return details.size();
	}

	@Override
	public Class getColumnClass(int c){
		return String.class;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PPNPreferenceDetailsBean d = this.details.get(rowIndex);
		
		if (columnIndex==0){
			return d.getResource().getDataType();
		}
		
		
		if (columnIndex==1){
			if (d.getRequestor().getRequestorId()!=null){
				return d.getRequestor().getRequestorId();
			}
			else{
				return "";
			}
		}
		
		if (columnIndex==2){
			RequestorBean requestor = d.getRequestor();
			if (requestor instanceof RequestorServiceBean){
				return ((RequestorServiceBean) requestor).getRequestorServiceId();
			}
			
			if (requestor instanceof RequestorCisBean){
				return ((RequestorCisBean) requestor).getCisRequestorId();
			}
				return "";
		}
		return new String("");
	}

	public void setValueAt(PPNPreferenceDetailsBean value, int row, int col){
		PPNPreferenceDetailsBean d = this.details.get(row);
		
		if (col==0){
			d.getResource().setDataType(value.getResource().getDataType());
			fireTableCellUpdated(row, col);
		}

		
		if (col==1){
			if (value.getRequestor()!=null){
				d.setRequestor(value.getRequestor());
				fireTableCellUpdated(row, col);
			}
		}
		
		if (col==2){
			if (value.getRequestor()!=null){
				d.setRequestor(value.getRequestor());
				fireTableCellUpdated(row, col);
			}
		}
	}
    public void addRow(PPNPreferenceDetailsBean d){
    	//System.out.println("addRow("+d.toString()+")");
    	this.details.add(d);
    	this.fireTableDataChanged();
    }
    public void removeRow(int row){
    	this.details.remove(row);
    	this.fireTableDataChanged();
    }
    
    public PPNPreferenceDetailsBean getRow(int row){
    	return this.details.get(row);
    }
}
