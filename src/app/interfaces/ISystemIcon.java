package app.interfaces;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;

import app.StartWacherDemo;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import sun.awt.shell.ShellFolder;

public interface ISystemIcon {
	
	public static Image getSystemImageView(File file)  {
		return getSystemImage(file);		
	}
	
	public static Image getSystemImage(File file)  {
		Image fxImage = null;

		try {
			ShellFolder sf = ShellFolder.getShellFolder(file);

			
			// Get large icon
			ImageIcon ico = null;
			try {
				ico = new ImageIcon(sf.getIcon(true), sf.getFolderType());
			} catch (Exception e) {				
//				e.printStackTrace();
//				System.out.println(file);
				return new Image(StartWacherDemo.class.getResourceAsStream("view/images/document.png"));
			}
			
			
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
			
			fxImage = SwingFXUtils.toFXImage(bImg, null);
			
		} catch (FileNotFoundException e) {
			System.out.println(file);
			e.printStackTrace();
		}

   			
//		System.out.println("Icon-Breite: " + fxImage.getWidth() + "  -  Icon-Höhe: " + fxImage.getHeight());
		
		return fxImage;

	}
	
}
