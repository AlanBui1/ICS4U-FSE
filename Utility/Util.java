package Utility;
import java.util.*;
import java.awt.*;
import javax.swing.*;

import MainGame.Game;
import MainGame.Gamepanel;
import GameObjects.*;
import GameObjects.Characters.Player;
import GameObjects.Characters.Attacks.*;
import GameObjects.Stages.Platform;
import GameObjects.Stages.Stage;

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
	public static double taxicabDist(Point p1, Point p2){ //returns the taxicab distance between two points p1 and p2
		return taxicabDist(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public static void drawFilledRect(Rectangle r, Graphics g){ //method to draw a filled rectangle given a Rectangle
		g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	}

	//returns a HashMap with all the fields of a Player given the filename of the file with its data
	public static HashMap <String, Double> loadStats(String fileName){ 
		HashMap <String, Double> stats = new HashMap<String, Double>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			int NUMSTATS = 13; //number of stats 
			for (int i=1; i<=NUMSTATS; i++){
				String name = inFile.next(); //name of the stat
				Double val = inFile.nextDouble(); //value of the stat
				inFile.nextLine();
				stats.put(name, val);
			}
			inFile.close();

			String [] statsX = {"width", "runspd", "airspd", "airaccel", "groundfriction", "airfriction", "offsetX"}, //stats that scale with the width of the screen
					  statsY = {"height", "gravity", "fallspd", "jumpforce", "offsetY"}; //stats that scale with the height of the screen

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

	//method to load Image frames 
	public static HashMap <String, Image[]> loadFrames(Player player, HashMap<String, Attack> atks){
		HashMap <String, Integer> actions = new HashMap <String, Integer>();
		HashMap <String, Image[]> frames = new HashMap <String, Image[]>();
		for (String atkName : atks.keySet()){ // iterates through attack keys
			Image[] atkFrames = new Image[atks.get(atkName).getnumFrames()];// array to store all frames of an attack
			actions.put(atkName, atks.get(atkName).getnumFrames());
			for (int k = 0; k < atks.get(atkName).getnumFrames(); k++){// iterates through every frame image of attack and updates frame array
				if (player.getType().equals("swordsperson")){ // swordsperson sprite is slightly smaller, scale to match size of other characters
					atkFrames[k] = loadScaledImg("assets/" + player.getType() + "/" + atkName + k + ".png", 337, 149);
					
				}
				else{
					atkFrames[k] = loadImg("assets/" + player.getType() + "/" + atkName + k + ".png");
				}
			}
			frames.put(atkName, atkFrames);
			
		}

		HashMap <String, Integer> moves = new HashMap <String, Integer>(); // new moves hashmap that stores non-attack moves and their corresponding number of frames
		moves.put("Run", 10);
		moves.put("Idle", 10);
		moves.put("Jump", 3);
		moves.put("Fall", 3);

		for (String moveName : moves.keySet()){
			Image[] moveFrames = new Image[moves.get(moveName)]; // creates an array for frames of move
			for (int k = 0; k < moves.get(moveName); k++){ // loads all frames for each move
				if (player.getType().equals("swordsperson")){ // specialized scaling for swordsperson
					moveFrames[k] = loadScaledImg("images/" + player.getType() + "/" + moveName + k + ".png", 337, 149);
				}
				else{
					moveFrames[k] = loadImg("assets/" + player.getType() + "/" + moveName + k + ".png");
				}
				frames.put(moveName, moveFrames); // adds all frames
				actions.put(moveName, moves.get(moveName)); // adds moves ot all actions
			}
		}
		if (player.getType().equals("shooter")){ // sepcial case, loads projectile frame
			Image[] FastSideAtkProjectile = {loadImg("assets/shooter/FastSideAtkProjectile.png")};
			frames.put("FastSideAtkProjectile", FastSideAtkProjectile);
		}

		player.setActions(actions); 
		return frames;
	}

	//method to load a HashMap <String, Attack> from the data in a txt file given the file name
	public static HashMap <String, Attack> loadAtks(String fileName){
		HashMap <String, Attack> atks = new HashMap <String, Attack>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			String curName; //name of the current attack that's being loaded in
			int numHitboxes; //number of Hitboxes in an Attack

			while (true){
				String name = inFile.next();
				if (name.equals("---END---")) break; //reached the end of the File, break

				curName = name.replaceAll("-", "");
				numHitboxes = inFile.nextInt();
				inFile.nextLine();

				atks.put(curName, new Attack());
				int NUMSTATS = 16; //number of stats each Hitbox has

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
				int cdown = inFile.nextInt(); //cooldown
				atks.get(curName).setCoolDown(cdown); //sets the attack cooldown
				
				inFile.next();
				int numFrames = inFile.nextInt(); //number of frames the attack is
				atks.get(curName).setNumFrames(numFrames); //sets the number of frames
					
				inFile.nextLine();
			}

			inFile.close();
		}
		catch (IOException e){}
		return atks;
	}

	//returns a Stage with the stats from the file with the given filename
	public static Stage loadStage(String fileName){ 
		ArrayList <Platform> plats = new ArrayList<Platform>();
		Stage newStage = new Stage();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName))); 
			int numPlats = inFile.nextInt();
			int PLATSTATS = 11; //number of stats each Platform has

			for (int i=0; i<numPlats; i++){
				HashMap <String, Integer> platStats = new HashMap<String, Integer>();

				for (int k=0; k<PLATSTATS; k++){
					platStats.put(inFile.next(), 
								 (int)(inFile.nextDouble() * (k%2 == 0 ? Gamepanel.WIDTH : Gamepanel.HEIGHT))); //scales with WIDTH or HEIGHT of the screen depending on if the stat affects X or Y direction
					inFile.nextLine();
				}

				plats.add(new Platform(platStats)); //adds Platform to the Platforms that are in the Stage
			}

			newStage = new Stage(plats);
			inFile.next();
			String name = inFile.next();
			inFile.nextLine();
			newStage.setElements(name);

			inFile.close();
		}
		catch (IOException e){}
		return newStage;
	}

	public static ArrayList<SelectRect> loadSelectRects(String fileName){
		ArrayList <SelectRect> ret = new ArrayList<SelectRect>();
		try{
			Scanner inFile = new Scanner(new BufferedReader(new FileReader(fileName)));

			int numRects = inFile.nextInt(); //number of SelectRects

			for (int i=0; i<numRects; i++){
				ret.add(new SelectRect(
					new Rectangle((int)(inFile.nextDouble() * Gamepanel.WIDTH), //x coordinate, scaled with WIDTH of screen
								  (int)(inFile.nextDouble() * Gamepanel.HEIGHT), //y coordinate, scaled with HEIGHT of screen
								  (int)(inFile.nextDouble() * Gamepanel.WIDTH), //width, scaled with WIDTH of screen
								  (int)(inFile.nextDouble() * Gamepanel.HEIGHT)), //height, scaled with HEIGHT of screen
					inFile.nextInt(), //val of the SelectRect
					inFile.next(), //name of the SelectRect
					"assets/"+inFile.next() //image file name of the SelectRect
				));
			}
		}
		catch(IOException e){}

		return ret;
	}
}