package Utility;
import java.util.*;
import java.awt.*;
import javax.swing.*;

import GameObjects.ThingsThatMove.AttackStuff.*;
import MainGame.Game;
import MainGame.Gamepanel;
import GameObjects.*;
import GameObjects.ThingsThatMove.*;

import java.io.*;

//Utility methods that are used throughout the entire program
public class Util {
	public static final double GRAVITY = 100;

    public static int randint(int low, int high){ //returns a random integer in the range [low, high]
		return (int)(Math.random()*(high-low+1)+low);
	}
	public static double randDouble(double low, double high){ //returns a random double in the range [low, high]
		return Math.random()*(high-low+1)+low;
	}
	public static boolean randBoolean(){ //returns a random boolean
		return randint(0, 1) == 0 ? false : true;
	}

	public static String fDouble(double n, int decimals){ //returns the String of a double formatted to a certain number of decimal places
		return String.format("%."+decimals+"f", n);
	}

	public static Image loadImg(String fileName){ //returns Image with file name fileName
		return new ImageIcon(fileName).getImage();
	}

	public static Image loadScaledImg(String fileName, int width, int height){ //returns scaled Image with file name fileName, width, and height
		Image img = loadImg(fileName);
		return img.getScaledInstance(width, height, Image.SCALE_SMOOTH); //returns scaled image
	}

	public static double taxicabDist(double x1, double y1, double x2, double y2){ //returns the taxicab distance between (x1, y1) and (x2, y2)
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}
	public static double taxicabDist(Point p1, Point p2){
		return taxicabDist(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public static void drawFilledRect(Rectangle r, Graphics g){ //method to draw a filled rectangle given a Rectangle
		g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	}

	public static HashMap <String, Double> loadStats(String fileName){ //returns a HashMap with all the fields of a Player given the filename of the file with its data
		HashMap <String, Double> stats = new HashMap<String, Double>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			int NUMSTATS = 11;
			for (int i=1; i<=NUMSTATS; i++){
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

	public static Stage loadStage(String fileName){
		ArrayList <Platform> plats = new ArrayList<Platform>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			int numPlats = inFile.nextInt();

			for (int i=0; i<numPlats; i++){
				HashMap <String, Integer> platStats = new HashMap<String, Integer>();

				for (int k=0; k<11; k++){
					platStats.put(inFile.next(), (int)(inFile.nextDouble() * (k%2 == 0 ? Gamepanel.WIDTH : Gamepanel.HEIGHT)));
					inFile.nextLine();
				}

				plats.add(new Platform(platStats));
			}

			inFile.close();
		}
		catch (IOException e){}
		return new Stage(plats);
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