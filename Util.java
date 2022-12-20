import java.util.*;
import java.awt.*;
import javax.swing.*;

public class Util {
    public static int randint(int low, int high){ //returns a random integer in the range [low, high]
		return (int)(Math.random()*(high-low+1)+low);
	}
	public static double randDouble(double low, double high){ //returns a random double in the range [low, high]
		return Math.random()*(high-low+1)+low;
	}

	public static Image loadImg(String fileName){ //returns Image with file name fileName
		return new ImageIcon(fileName).getImage();
	}

	public static Image loadScaledImg(String fileName, int width, int height){ //returns scaled Image with file name fileName, width, and height
		Image img = loadImg(fileName);
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH); //returns scaled image
	}
}

/*
cd C:\Users\alanb\OneDrive - Greater Essex County District School Board\Documents\ICS4U\ICS4U-FSE
javac Platform.java Gamepanel.java Game.java Player.java Util.java Vector.java Bullet.java Hitbox.java Shooter.java
java Game
 */

 /*
  MAKE shooter class have a Player inside? -> it's using inheritance

  later have an arraylist of players probably
  change the draw() in Hitbox
  maybe have a super class with x, y, vx, vy, ax, ay for "cleaner code"
  asadhdsajdlskadsada

  */