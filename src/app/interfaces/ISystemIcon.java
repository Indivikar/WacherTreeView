package app.interfaces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sun.awt.shell.ShellFolder;

public interface ISystemIcon {
	
	public static Image getSystemImageView(File file)  {		
		return getSystemImage(file);		
	}
	
	public static Image getSystemImage(File file)  {

        ShellFolder sf = null;
		try {
			sf = ShellFolder.getShellFolder(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Get large icon
		ImageIcon ico = new ImageIcon(sf.getIcon(true), sf.getFolderType());
		java.awt.Image awtImage = ico.getImage();
		
		BufferedImage bImg ;
		if (awtImage instanceof BufferedImage) {
		    bImg = (BufferedImage) awtImage ;
		} else {
		    bImg = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		    Graphics2D graphics = bImg.createGraphics();
		    graphics.drawImage(awtImage, 0, 0, null);
		    graphics.dispose();
		}
		
		Image fxImage = SwingFXUtils.toFXImage(bImg, null);
   			
//		System.out.println("Icon-Breite: " + fxImage.getWidth() + "  -  Icon-Höhe: " + fxImage.getHeight());
		
		return fxImage;

	}
	
}
