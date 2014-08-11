/**
 * 
 */
package ac.hw.personis.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextArea;



/**
 * @author PUMA
 * 
 */
public class GoogleMapsService extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String authKey = "AIzaSyA9BkjBXcXYv5ES0kvf5IyUpZXEuaBlc1M";
	private JButton btnRetrieve;
	private JTextArea textArea;
	
	
	public GoogleMapsService() {
		super();
		setResizable(false);
		getContentPane().setLayout(null);
		setSize(800, 700);
		btnRetrieve = new JButton("Retrieve data ");
		btnRetrieve.setBounds(10, 11, 190, 48);
		getContentPane().add(btnRetrieve);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setBounds(61, 162, 691, 470);
		getContentPane().add(textArea);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
	}



	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == btnRetrieve) {
			textArea.append("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,");
		}

	}
	
	
	public static void main(String[] args) {
		GoogleMapsService service = new GoogleMapsService();
		
		service.setVisible(true);
	}
}
