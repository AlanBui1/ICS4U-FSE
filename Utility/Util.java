package Utility;
import java.util.*;
import java.awt.*;
import javax.swing.*;

import GameObjects.ThingsThatMove.AttackStuff.*;
import MainGame.Game;
import MainGame.Gamepanel;

import java.io.*;

public class Util {
	public static final double GRAVITY = 100;

    public static int randint(int low, int high){ //returns a random integer in the range [low, high]
		return (int)(Math.random()*(high-low+1)+low);
	}
	public static double randDouble(double low, double high){ //returns a random double in the range [low, high]
		return Math.random()*(high-low+1)+low;
	}
	public static boolean randBoolean(){
		return randint(0, 1) == 0 ? false : true;
	}

	public static Image loadImg(String fileName){ //returns Image with file name fileName
		return new ImageIcon(fileName).getImage();
	}

	public static Image loadScaledImg(String fileName, int width, int height){ //returns scaled Image with file name fileName, width, and height
		Image img = loadImg(fileName);
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH); //returns scaled image
	}

	public static double taxicabDist(double x1, double y1, double x2, double y2){
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

	public static double knockBack(double base, double weight, double damage){
		return base * (100 / weight) * ((damage+10) / 100);
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

			String [] statsY = {"height", "gravity", "fallspd", "jumpforce"},
					  statsX = {"width", "runspd", "airspd", "airaccel", "groundfriction", "airfriction"};
			for (int i=0; i<statsX.length; i++){
				stats.put(statsX[i], stats.get(statsX[i]) * Gamepanel.WIDTH);		
			}
			for (int i=0; i<statsY.length; i++){
				stats.put(statsY[i], stats.get(statsY[i]) * Gamepanel.HEIGHT);
			}
			
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

					for (int k=0; k<15; k++){ //CHANGE THE 15 to however many fields there are
						String KEY = inFile.next();
						Double val = inFile.nextDouble();
						inFile.nextLine();
						hitStats.put(KEY, val);
					}

					String [] stuff = {"basekb", "v", "a", "maxv", "startoffset"};
					for (int j=0; j<stuff.length; j++){
						hitStats.put(stuff[j] + "x", hitStats.get(stuff[j] + "x") * Gamepanel.WIDTH);
						hitStats.put(stuff[j] + "y", hitStats.get(stuff[j] + "y") * Gamepanel.HEIGHT);
					}
					hitStats.put("width", hitStats.get("width") * Gamepanel.WIDTH);
					hitStats.put("height", hitStats.get("height") * Gamepanel.HEIGHT);
					
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
  change Hitbox class to have Polygons hitboxes -> going to use bunch of rects instead

  */