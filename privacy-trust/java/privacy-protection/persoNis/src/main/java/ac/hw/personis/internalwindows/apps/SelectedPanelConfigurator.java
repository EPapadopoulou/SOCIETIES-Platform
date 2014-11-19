package ac.hw.personis.internalwindows.apps;

import java.util.ArrayList;
import java.util.List;

public class SelectedPanelConfigurator {

	private List<AppButtonPanel> panels;

	public SelectedPanelConfigurator(){
		this.panels = new ArrayList<AppButtonPanel>();
	}
	
	public void addPanel(AppButtonPanel panel){
		this.panels.add(panel);
	}
	
	
	
	public void mouseClickedOn(AppButtonPanel panel){
		
		
		if (!panel.isInstalled()){
			panel.select();
			for (AppButtonPanel aPanel : panels){
				if (!panel.getStaticName().equals(aPanel.getStaticName())){
					aPanel.dehighlight();
				}
			}
		}
	}
}
