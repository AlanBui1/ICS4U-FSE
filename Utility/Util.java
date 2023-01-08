package Utility;
import java.util.*;
import java.awt.*;
import javax.swing.*;

import ThingsThatMove.AttackStuff.*;

import java.io.*;

public class Util {
	public static final double GRAVITY = 100;

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

	public static HashMap <String, Double> loadStats(String fileName){
		HashMap <String, Double> stats = new HashMap<String, Double>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			for (int i=1; i<=11; i++){ //CHANGE THE 11 to however many fields there are
				String name = inFile.next();
				Double val = inFile.nextDouble();
				inFile.nextLine();
				stats.put(name, val);
			}
			inFile.close();
		}
		catch(IOException e){}

		return stats;
	}

	public static HashMap <String, Attack> loadAtks(String fileName){
		HashMap <String, Attack> atks = new HashMap <String, Attack>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			String curName; //name of the current attack that's being loaded in
			int numHitboxes; //number of Hitboxes in an Attack

			while (true){
				String name = inFile.next();
				if (name.equals("---END---")) break;

				curName = name.replaceAll("-", "");
				numHitboxes = inFile.nextInt();
				inFile.nextLine();

				atks.put(curName, new Attack());

				for (int i=0; i<numHitboxes; i++){
					HashMap <String, Double> hitStats = new HashMap<String, Double>();

					for (int k=0; k<14; k++){ //CHANGE THE 14 to however many fields there are
						String KEY = inFile.next();
						Double val = inFile.nextDouble();
						inFile.nextLine();
						//System.out.println(KEY + " " + val);
						hitStats.put(KEY, val);
					}
					atks.get(curName).addHitbox(new Hitbox(hitStats));
					
				}

				inFile.next();
				int cdown = inFile.nextInt();
				inFile.nextLine();
				atks.get(curName).setCoolDown(cdown);
				
				
			}

			inFile.close();
		}
		catch (IOException e){}
		return atks;
	}
}

/*
cd C:\Users\alanb\OneDrive - Greater Essex County District School Board\Documents\ICS4U\ICS4U-FSE
javac Platform.java Gamepanel.java Game.java Player.java Util.java Hitbox.java Shooter.java Mover.java BetterPlayer.java BetterShooter.java
java Game
 */

 /*
  MAKE shooter class have a Player inside? -> it's using inheritance
  maybe have a super class with x, y, vx, vy, ax, ay for "cleaner code" -> Mover class 

  later have an arraylist of players probably
  change the draw() in Hitbox for images
  change Hitbox class to have Polygons hitboxes
  make a stage class that has Gravity, platforms, etc.

  */