import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

/*
 * Utilities.java
 * Anthony Fountaine
 * This class handles all basic utility functions that are utilized throughout the program.
 */

public class Utilities {
    public static int randint(int a, int b) {
		/*
		 * This method returns a random integer between a and b
		 */
        return (int)(Math.random()*(b-a+1)+a);
    }

    public static Image loadImage(String img){
		/*
		 * This method loads an image from a file
		 */
		return new ImageIcon(img).getImage();
	}

    public static Font loadFont(String name, int size){
		/*
		 * This method loads a font from a file
		 */
		Font font=null;
    	try{
			File fntFile = new File(name);
    		font = Font.createFont(Font.TRUETYPE_FONT, fntFile).deriveFont((float)size);
    	}
    	catch(IOException ex){
    		System.out.println(ex);	
    	}
    	catch(FontFormatException ex){
    		System.out.println(ex);	
    	}
		return font;
	}

	public static Color randomColor() {
		/*
		 * This method returns a random color
		 */
		return new Color(randint(0, 255), randint(0, 255), randint(0, 255));
	}

	public static Vector2D randomdir() {
		/*
		 * This method returns a random direction in Vector2D format
		 */
		return new Vector2D(1, Math.toRadians(Utilities.randint(0, 360)));
	}
}
