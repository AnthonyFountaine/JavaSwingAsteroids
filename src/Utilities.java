import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

public class Utilities {
    public static int randint(int a, int b) {
        return (int)(Math.random()*(b-a+1)+a);
    }

    public static Image loadImage(String img){
		return new ImageIcon(img).getImage();
	}

    public static Font loadFont(String name, int size){
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
		return new Color(randint(0, 255), randint(0, 255), randint(0, 255));
	}
}
