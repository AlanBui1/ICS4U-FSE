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

			String [] statsX = {"width", "runspd", "airspd", "airaccel", "groundfriction", "airfriction"}, //stats that scale with the width of the screen
					  statsY = {"height", "gravity", "fallspd", "jumpforce"}; //stats that scale with the height of the screen

			for (int i=0; i<statsX.length; i++){
				//scales stats with the width
				stats.put(statsX[i], stats.get(statsX[i]) * Gamepanel.WIDTH);	
			}
			for (int i=0; i<statsY.length; i++){
				//scales stats with the height
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
				int NUMSTATS = 15; //number of stats each Hitbox has

				for (int i=0; i<numHitboxes; i++){
					HashMap <String, Double> hitStats = new HashMap<String, Double>();

					for (int k=0; k<NUMSTATS; k++){ 
						String key = inFile.next();
						Double val = inFile.nextDouble();
						inFile.nextLine();
						hitStats.put(key, val);
					}

					String [] stats = {"basekb", "v", "a", "maxv", "startoffset"};
					for (int j=0; j<stats.length; j++){
						hitStats.put(stats[j] + "x", hitStats.get(stats[j] + "x") * Gamepanel.WIDTH); //scales stats with width of the screen
						hitStats.put(stats[j] + "y", hitStats.get(stats[j] + "y") * Gamepanel.HEIGHT); //scales stats with height of the screen
					}
					hitStats.put("width", hitStats.get("width") * Gamepanel.WIDTH);//scales with width of the screen
					hitStats.put("height", hitStats.get("height") * Gamepanel.HEIGHT);//scales with height of the screen
					
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

	public static Stage loadStage(String fileName){ //returns a Stage with the stats from the given filename
		ArrayList <Platform> plats = new ArrayList<Platform>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			int numPlats = inFile.nextInt();
			int PLATSTATS = 11; //number of stats each Platform has

			for (int i=0; i<numPlats; i++){
				HashMap <String, Integer> platStats = new HashMap<String, Integer>();

				for (int k=0; k<PLATSTATS; k++){
					platStats.put(inFile.next(), (int)(inFile.nextDouble() * (k%2 == 0 ? Gamepanel.WIDTH : Gamepanel.HEIGHT))); //scales with WIDTH or HEIGHT of the screen depending on if the stat affects X or Y direction
					inFile.nextLine();
				}

				plats.add(new Platform(platStats));
			}

			inFile.close();
		}
		catch (IOException e){}
		return new Stage(plats);
	}

	public static ArrayList<SelectRect> loadSelectRects(String fileName){
		ArrayList <SelectRect> ret = new ArrayList<SelectRect>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName)));

			int numRects = inFile.nextInt(); //number of SelectRects

			for (int i=0; i<numRects; i++){
				ret.add(new SelectRect(
					new Rectangle((int)(inFile.nextDouble() * Gamepanel.WIDTH), 
								  (int)(inFile.nextDouble() * Gamepanel.HEIGHT), 
								  (int)(inFile.nextDouble() * Gamepanel.WIDTH), 
								  (int)(inFile.nextDouble() * Gamepanel.HEIGHT)),
					inFile.nextInt(),
					inFile.next(),
					"assets/"+inFile.next()
				));
			}
		}
		catch(IOException e){}

		return ret;
	}
}