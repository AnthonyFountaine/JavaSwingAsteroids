import java.awt.*;
import javax.swing.*;

public class Util {
    public static int randint(int a, int b) {
        return (int)(Math.random()*(b-a+1)+a);
    }

    public static Image loadImage(String img){
		return new ImageIcon(img).getImage();
	}
}
