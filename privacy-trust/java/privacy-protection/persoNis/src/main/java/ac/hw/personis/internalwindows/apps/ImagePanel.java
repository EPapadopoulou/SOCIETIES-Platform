package ac.hw.personis.internalwindows.apps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ImagePanel extends JPanel {

    private Image image = null;

    public ImagePanel(){
    	super();
    	
    }
    public ImagePanel(String filename) {
    	//super();
    	
        this.image = new ImageIcon(ImagePanel.class.getResource("/images"+filename)).getImage();
        if (this.image==null){
        	System.out.println("image is null");
        }
    	
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
        g.drawImage(image, 0, 0, null);
        //System.out.println(image.getWidth(null));
        
        //System.out.println("paintComponent");
        
        revalidate();
        
    }
    


}
