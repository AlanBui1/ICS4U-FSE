package MainGame;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.*;

import javax.swing.*;

import GameObjects.Characters.Player;
import GameObjects.Characters.Attacks.*;
import GameObjects.Stages.Platform;
import GameObjects.Stages.Stage;
import Utility.*;

//main game class that connects all object interactions together
//has methods to move objects, display images on screen, check mouse location and if it's pressed, start levels, and check collisions between Hitboxes and Players
//can also track which keys are being pressed and for how long

public class Gamepanel extends JPanel implements KeyListener, ActionListener, MouseListener{	
	public static final int WIDTH = 800, //width of the screen
							HEIGHT = 600, // height of the screen
							LEFTCLICK = 1, //the mouse button that corresponds to left click
							RIGHTCLICK = 3; //the mouse button that corresponds to right click
							
	//numbers assigned to each screen
	public static final int	START = 0, 
							CONTROLSELECT = 1, 
							CHARACTERSELECT = 2,
							STAGESELECT = 3,
							BATTLE = 4,
							PAUSESCREEN = 5,
							ENDSCREEN = 6;
	
	private ArrayList<HashMap <String, Integer>> playerKeys; //keys that the Players use
	//playerKeys.get(0) are the keys that p1 uses
	//playerKeys.get(1) are the keys that p2 uses

	public static  HashMap <String, Double> shooterStats = new HashMap<String, Double>(); //statName -> value
	public static  HashMap <String, Attack> shooterAtks = new HashMap<String, Attack>(); //attackName -> hitboxes
	public static  HashMap <String, Double> swordspersonStats = new HashMap<String, Double>(); //statName -> value
	public static  HashMap <String, Attack> swordspersonAtks = new HashMap<String, Attack>(); //attackName -> hitboxes
	public static  HashMap <String, Double> bladeStats = new HashMap<String, Double>(); //statName -> value
	public static  HashMap <String, Attack> bladeAtks = new HashMap<String, Attack>(); //attackName -> hitboxes
	public static  HashMap <String, HashMap <String, Double>> allStats = new HashMap <String, HashMap <String, Double>>(); //all stats
	public static  HashMap <String, HashMap <String, Attack>> allAtks = new HashMap <String, HashMap <String, Attack>>(); //all attacks

    private boolean [] keysPressed; //keysPressed[i] is true if the ith key is pressed
	private int []  keysHeldTime, //how long the keys are held for
					keysReleasedTime; //how quickly the keys are released
	
	//DEFAULT KEYS
	int [][] defaultKeys ={
			{KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_Q, KeyEvent.VK_Z},
			{KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_U, KeyEvent.VK_O, KeyEvent.VK_SLASH}}; //stores default keyss

	private boolean mousePressed; //true if the mouse is pressed, false otherwise
	private int mouseX, //x coordinate of the mouse
				mouseY, //y coordinate of the mouse
				mouseButton; //which mouse button is pressed

	private String selectedKey; //which key is selected to change in the key select screen
	private int selectedPlayer; //which character is selected to change in the key select screen
	
	private int curScreen; //which screen the game is on
	private Stage curStage; //which stage the battle is on

	private String [] characterNames = {"shooter", "swordsperson", "bladekeeper"}; //all character names
	private String [] stageNames = {"noPlats", "verticalPlat", "triPlat", "twoMoving","ground", "mainMoving", "twoPillars"}; //all stage names

	private ArrayList <SelectRect> stageSelectRects, //SelectRects in the stage select screen
								   charSelectRects, //SelectRects in the character select screen
								   keySelectRects; //SelectRects in the key select screen

	private ArrayList <Stage> allStages; //all stages

	private SelectRect controlScreenRect, //SelectRect to go to the control select screen
					   pauseRect,
					   playRect; //SelectRect to pause/unpause the game

	private Image pauseImage; //image for the pause screen
	private Image endImage; //image for the end screen
	private Image startImage; //image for the start screen 
	private Image controlImage; //image for the control screen
	private Image charSelectImage; //image for the character select screen

    Timer timer; //Timer to count frames in the game

	Player p1, p2; //Player 1 and Player 2 used to battle in the BATTLE screen
	SelectRect player1Rect, player2Rect;

	private Font fontLocal; //Font used for text to be drawn on screen

	public Gamepanel(){
		GameMusic.startMidi("music/FireEmblem.mid");
		keysPressed = new boolean[KeyEvent.KEY_LAST+1];
		keysHeldTime = new int[KeyEvent.KEY_LAST+1];
		keysReleasedTime = new int[KeyEvent.KEY_LAST+1];

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		timer = new Timer(30, this);
		timer.start();

		shooterStats = Util.loadStats("CharacterInfo/shooterStats.txt"); //stats of the shooter
		shooterAtks = Util.loadAtks("CharacterInfo/shooterAtks.txt"); //attacks of the shoter
		swordspersonStats = Util.loadStats("CharacterInfo/wordspersonStats.txt"); //stats of the swordsperson
		swordspersonAtks = Util.loadAtks("CharacterInfo/swordspersonAtks.txt"); //attacks of the swordsperson
		bladeStats = Util.loadStats("CharacterInfo/bladeStats.txt"); //stats of the bladekeeper
		bladeAtks = Util.loadAtks("CharacterInfo/bladeAtks.txt"); //attacks of the bladekeeper

		allStats.put("shooter", shooterStats);
		allAtks.put("shooter", shooterAtks);
		allStats.put("swordsperson", swordspersonStats);
		allAtks.put("swordsperson", swordspersonAtks);
		allStats.put("bladekeeper", bladeStats);
		allAtks.put("bladekeeper", bladeAtks);

		curStage = Util.loadStage("stageConfigs/verticalPlat.txt"); //sets the stage to a default stage

		controlScreenRect = new SelectRect(new Rectangle(100, 20, 70, 70), 0, "", "assets/changecontrol.png"); 
		
		playRect = new SelectRect(new Rectangle(20, 20, 70, 70), 0, "", "assets/play.png");
		pauseRect = new SelectRect(new Rectangle(20, 20, 70, 70), 0, "", "assets/pause.png");
		pauseImage = Util.loadImg("assets/pause.gif");
		endImage = Util.loadImg("assets/end.gif");
		startImage = Util.loadImg("assets/start.gif");
		charSelectImage = Util.loadImg("assets/charSelect.gif");
		controlImage = Util.loadScaledImg("assets/control.png", 800, 600);

		//sets current screen to the start by default
		curScreen = START;

		//PRECOMPUTE STAGES
		allStages = new ArrayList<Stage>();
		for (int i=0; i<stageNames.length; i++){allStages.add(Util.loadStage("stageConfigs/"+stageNames[i]+".txt"));} 

		//INITIALIZE SelectRects
		stageSelectRects = Util.loadSelectRects("selectRects/stageSelect.txt");
		charSelectRects = Util.loadSelectRects("selectRects/charSelect.txt");
		keySelectRects = Util.loadSelectRects("selectRects/keySelect.txt");
		player1Rect = new SelectRect(new Rectangle(150, 400, 100, 100), 0, "", "assets/shooter/shooter.png");
		player2Rect = new SelectRect(new Rectangle(430, 400, 100, 100), 1, "", "assets/shooter/shooter.png");


		//INITIALIZE KEY SELECT
		playerKeys = new ArrayList<HashMap<String, Integer>>();
		playerKeys.add(new HashMap<String, Integer>());
		playerKeys.add(new HashMap<String, Integer>());

		//sets default keys for Players
		for (int i=0; i<defaultKeys[0].length; i++){
			playerKeys.get(0).put(Player.keyNames[i], defaultKeys[0][i]);
			playerKeys.get(1).put(Player.keyNames[i], defaultKeys[1][i]);
		}
		
		//sets default player type for Players
		p1 = new Player(0, 
						0, 
						allStats.get("shooter"), 
						allAtks.get("shooter"), 
						false, "shooter", 
						"Idle", 
						0);
						
		p2 = new Player(0, 
						0, 
						allStats.get("shooter"), 
						allAtks.get("shooter"), 
						false, "shooter", 
						"Idle", 
						0);

		//loads font
		String fName = "Starborn.ttf"; 
    	InputStream is = Gamepanel.class.getResourceAsStream(fName);
    	try{
    		fontLocal = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(32f);
    	}
    	catch(IOException ex){
    		System.out.println(ex);	
    	}
    	catch(FontFormatException ex){
    		System.out.println(ex);	
    	}
	}

//###########################################################################################################################
//STUFF FOR THE BATTLE
    public void move(){ //moves Objects on the screen during a Battle
		try{
			//counts down the stunTime, shieldTime, and attack cooldown of Players
			if (p1.getStun() > 0) p1.addStun(-1); p1.setCoolDown(p1.getCoolDown()-1); p1.setShieldTime(p1.getShieldTime()-1);
			if (p2.getStun() > 0) p2.addStun(-1); p2.setCoolDown(p2.getCoolDown()-1); p2.setShieldTime(p2.getShieldTime()-1);

			//moves Platforms
			for (Platform p : curStage.getPlats()){
				p.move();
			}
			
			//Players attack and move
			p1.attack(keysPressed, keysReleasedTime);
			p1.move(keysPressed, keysReleasedTime, curStage);
			p2.move(keysPressed, keysReleasedTime, curStage);
			p2.attack(keysPressed, keysReleasedTime);

			//check if Players collide with Hitboxes
			checkCollisions(p1, p2);
			checkCollisions(p2, p1);
		}
		catch(Exception e){}
	}

	public void checkCollisions(Player curPlayer, Player oppoPlayer){ //method to check collisions with curPlayer's Hitboxes and opposing Player
		ArrayList<Hitbox> toDelH = new ArrayList<Hitbox>(); //Hitboxes to delete

		for (Hitbox h : curPlayer.getHitBoxes()){ 
			h.lowerInvis(); //lowers the time the Hitbox is inactive for
			if (h.getInvis() > 0){
				h.setY(curPlayer.getY() + h.getOffsetY()); //moves the hitbox where the Player is
				continue;
			} 

			if (oppoPlayer.getRect().intersects(h.getRect())){ //checks if Hitbox and Player collide
				toDelH.add(h);
			}
		}

		for (Hitbox h : toDelH){
			curPlayer.getHitBoxes().remove(h); //removes Hitbox from curPlayer's ArrayList
			if (oppoPlayer.getShieldTime() > 0){ //if the Player's shield is active
				oppoPlayer.setShieldTime(0); //deactivates the shield
				continue; //continues so the effects of the Hitbox are negated
			}

			oppoPlayer.addForce(new Force(Force.knockBack(h.getKnockBackX(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), //knockback in the x direction
									  Force.knockBack(h.getKnockBackY(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), //knockback in the y direction
									  h.getStun())); //adds a Force acting on the opposing Player
			oppoPlayer.setStun(Math.max(h.getStun(), oppoPlayer.getStun())); //stuns the opposing Player
			oppoPlayer.addDamage(h.getDamage()); //adds damage to the opposing Player

			//adds to statistics of each Player
			curPlayer.addDealtDamage(h.getDamage());
			oppoPlayer.addTakenDamage(h.getDamage());
		}
	}

//###########################################################################################################################


	@Override
	public void actionPerformed(ActionEvent e){
		if (p1 != null){
			p1.frameIncrease();
		}
		if (p2 != null){
			p2.frameIncrease();
		}

		for (int i=0; i<KeyEvent.KEY_LAST; i++){
			if (keysPressed[i]){
				keysHeldTime[i]++; //if key i is pressed, increases the time it is held for
			}
		}

		try{
			//does actions based on which screen is active
			if (curScreen == START){
				if (mousePressed){
					curScreen = CHARACTERSELECT;
				}
			}
			else if (curScreen == CHARACTERSELECT){
				charAction();
			}
			else if (curScreen == STAGESELECT){
				stageAction();
			}
			else if (curScreen == CONTROLSELECT){
				controlAction();
			}
			else if (curScreen == BATTLE){
				battleAction();
			}
			else if (curScreen == PAUSESCREEN){
				pauseAction();
			}
			else if (curScreen == ENDSCREEN){
				endAction();
			}
			
			repaint(); 	// only draw
		}

		catch(NullPointerException ex){}
	}
	
	@Override
	public void keyReleased(KeyEvent ke){
		int key = ke.getKeyCode();
		keysPressed[key] = false;
		keysReleasedTime[key] = keysHeldTime[key];
		keysHeldTime[key] = 0;
	}	
	
	@Override
	public void keyPressed(KeyEvent ke){
		int key = ke.getKeyCode();
		keysPressed[key] = true;
		keysReleasedTime[key] = 0;

		if (curScreen == CONTROLSELECT){
			playerKeys.get(selectedPlayer).put(selectedKey, ke.getKeyCode());
		}
	}
	
	@Override
	public void keyTyped(KeyEvent ke){}

	public void updateMouse(MouseEvent e){ //updates the mouse position and which button is pressed
		mouseX = e.getX();
		mouseY = e.getY();
		mouseButton = e.getButton();
	}
	@Override
	public void	mouseClicked(MouseEvent e){
		updateMouse(e);
	}

	@Override
	public void	mouseEntered(MouseEvent e){
		updateMouse(e);
	}
	@Override
	public void	mouseExited(MouseEvent e){
		updateMouse(e);
	}

	@Override
	public void	mousePressed(MouseEvent e){
		updateMouse(e);
		mousePressed = true;
	}

	@Override
	public void	mouseReleased(MouseEvent e){
		updateMouse(e);
		mousePressed = false;
	}

	@Override
	public void paint(Graphics g){
		g.setFont(fontLocal);
		//paint appropriate screen depending on screen name
		if (curScreen == START){
			paintStart(g);
		}
		else if (curScreen == CONTROLSELECT){
			paintControlSelect(g);
		}
		else if (curScreen == CHARACTERSELECT){
			paintCharacterSelect(g);
		}
		else if (curScreen == STAGESELECT){
			paintStageSelect(g);
		}
		else if (curScreen == BATTLE){
			paintBattle(g);
		}
		else if (curScreen == PAUSESCREEN){
			paintPause(g);
		}
		else if (curScreen == ENDSCREEN){
			paintEnd(g);
		}
    }

//######################################################################################################################
//Drawing different screens
	public void paintStart(Graphics g){
		g.drawImage(startImage, 0, 0, WIDTH, HEIGHT, null);
	}
	public void paintControlSelect(Graphics g){
		g.drawImage(controlImage, 0, 0, WIDTH, HEIGHT, null);
		for (SelectRect curRect : keySelectRects){
			curRect.draw(g);
			if (!curRect.name.equals("START") && !curRect.name.equals("DEFAULT")) g.drawString(""+(char)(int)playerKeys.get(curRect.val).get(curRect.name), (int)curRect.rect.getX()+(int)curRect.rect.getWidth()/2, (int)curRect.rect.getY()+(int)curRect.rect.getHeight()/2);
		}
	}
	public void paintCharacterSelect(Graphics g){
		g.drawImage(charSelectImage, 0, 0, WIDTH, HEIGHT, null);
		
		for (int i=0; i<charSelectRects.size(); i++){
			charSelectRects.get(i).draw(g);
		}

		player1Rect.draw(g); 
		player2Rect.draw(g);

		
	}
	public void paintStageSelect(Graphics g){
		g.drawImage(controlImage, 0, 0, WIDTH, HEIGHT, null);
		for (int i=0; i<stageSelectRects.size(); i++){
			stageSelectRects.get(i).draw(g);
		}
	}
	public void paintEnd(Graphics g){
		g.drawImage(endImage, 0,0,WIDTH,HEIGHT,null);

		//show which Player won
		if (p1.getLives() > 0){
			g.drawString("Player 1 Won!", 250, 380);
		}
		else{
			g.drawString("Player 2 Won!", 250, 380);
		}
	}
	public void paintBattle(Graphics g){
		try{
			//draw Stage and Platforms
			curStage.draw(g);

			//draw Players and their Hitboxes
			p1.draw(g, 0, 570);
			p2.draw(g, 700, 570);
			pauseRect.draw(g);

			g.fillRect(0, 585, (int)(100*(p1.getCharge()/Player.maxCharge)), 10);
			g.fillRect(700, 585, (int)(100*(p2.getCharge()/Player.maxCharge)), 10);
		}
		catch(NullPointerException ex){}
	}
	public void paintPause(Graphics g){
		g.drawImage(pauseImage, 0,0, WIDTH, HEIGHT, null);
		g.setColor(Color.BLACK);
		g.drawString("PAUSED", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		playRect.draw(g);
		controlScreenRect.draw(g);
	}

//######################################################################################################################
//Actions depending on screen

	public void controlAction(){ //actions when on the control screen
		if (mousePressed){
			for (SelectRect curRect : keySelectRects){ //loops through SelectRects
				if (curRect.contains(mouseX, mouseY)){ //checks if the mouse is inside the SelectRect
					if (curRect.name.equals("START")){
						curScreen = BATTLE; //moves to the next screen
						p1.loadKeyLayout(playerKeys.get(0)); //loads the controls
						p2.loadKeyLayout(playerKeys.get(1));
						mousePressed = false;
					}
				
					else {
						selectedKey = curRect.name; //get the Key that is to be changed
						selectedPlayer = curRect.val; //gets with Player is being selected

						if (curRect.name.equals("DEFAULT")){
							for (int i=0; i<defaultKeys[0].length; i++){ //returns the keys to the default
								playerKeys.get(selectedPlayer).put(Player.keyNames[i], defaultKeys[selectedPlayer][i]);
							}
						}
					}
					
				}
			}
		}
	}

	public void charAction(){ //actions on the character select screen
		if (mousePressed){
			if (player1Rect.contains(mouseX, mouseY)){
				p1.setCPU(true^p1.getCPU());
			}
			if (player2Rect.contains(mouseX, mouseY)){
				p2.setCPU(true^p2.getCPU());
			}
			for (SelectRect curRect : charSelectRects){ //loops through SelectRects 
				if (curRect.contains(mouseX, mouseY)){ // checks if the mouse is inside them
					if (curRect.name.equals("START")){
						curScreen = STAGESELECT; //moves to the next screen
						mousePressed = false;

						//initializes Players
						p1 = new Player(0, 
										0, 
										allStats.get(p1.getType()), 
										allAtks.get(p1.getType()), 
										p1.getCPU(), 
										p1.getType(), 
										"Idle", 
										0);

						p2 = new Player(0, 
										0, 
										allStats.get(p2.getType()), 
										allAtks.get(p2.getType()), 
										p2.getCPU(), 
										p2.getType(), 
										"Idle", 
										0);
						
						p1.loadKeyLayout(playerKeys.get(0));
						p2.loadKeyLayout(playerKeys.get(1));

					}

					else{
						//LEFTCLICK affects player1
						//RIGHTCLICK affects player2
						if (curRect.name.equals("RANDOM")){ //changes the Player to a random one
							if (mouseButton == LEFTCLICK){
								p1.setType(characterNames[Util.randint(0, characterNames.length-1)]);
								player1Rect.changeImg("assets/"+p1.getType()+"/"+p1.getType()+".png");
							}
							else if (mouseButton == RIGHTCLICK){ 
								p2.setType(characterNames[Util.randint(0, characterNames.length-1)]);
								player2Rect.changeImg("assets/"+p2.getType()+"/"+p2.getType()+".png");
							}
						}
						else{
							//changes the Player to the one that was clicked
							if (mouseButton == LEFTCLICK){
								p1.setType(characterNames[curRect.val]);
								player1Rect.changeImg("assets/"+p1.getType()+"/"+p1.getType()+".png");
							}
							else if (mouseButton == RIGHTCLICK){
								p2.setType(characterNames[curRect.val]);
								player2Rect.changeImg("assets/"+p2.getType()+"/"+p2.getType()+".png");
							}
						}
					}
				}
			}
		}
	}

	public void stageAction(){ //actions on the stage select screen
		if (mousePressed){
			for (SelectRect curRect : stageSelectRects){ //loops through SelectRects
				if (curRect.contains(mouseX, mouseY)){ //checks if mouse is inside them
					if (curRect.name.equals("START")){
						curScreen = CONTROLSELECT; //moves to the next screen
						mousePressed = false;
					}
					else{
						if (curRect.name.equals("RANDOM")){
							curStage = allStages.get(Util.randint(0, allStages.size()-1)); //sets curStage to a random Stage
							GameMusic.startMidi("music/BreakTheTargets.mid");
						}
						else{
							curStage = allStages.get(curRect.val); //sets curStage to the selected Stage
							GameMusic.startMidi("music/"+curRect.name+".mid");
						}
					}
				}
			}
		}
	}

	public void battleAction(){ //actions on the battle screen
		if (mousePressed && pauseRect.contains(mouseX, mouseY)){ //checks if mouse is pressed and is in the pause rect
			curScreen = PAUSESCREEN; //moves screen
			mousePressed = false;
			return;
		}

		move(); // never draw in move
		if (p1.getLives() <= 0 || p2.getLives() <= 0){
			curScreen = ENDSCREEN;
			GameMusic.startMidi("music/Gallery.mid");
		}
	}

	public void pauseAction(){ //actions on the pause screen
		if (mousePressed){
			if (pauseRect.contains(mouseX, mouseY)){
				curScreen = BATTLE; //moves to the battle screen
				mousePressed = false;
			}
			if (controlScreenRect.contains(mouseX, mouseY)){
				curScreen = CONTROLSELECT; //moves to the control select screen
				mousePressed = false;
			}
		}
	}

	public void endAction(){ //actions on the end screen
		if (mousePressed){
			curScreen = START; //goes back to the start screen
			mousePressed = false;
		}
	}
}