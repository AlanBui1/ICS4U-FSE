package MainGame;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.*;

import javax.swing.*;

import GameObjects.ThingsThatMove.Player;
import GameObjects.ThingsThatMove.Platform;
import GameObjects.ThingsThatMove.AttackStuff.*;
import GameObjects.ThingsThatMove.AttackStuff.Hitbox;
import GameObjects.Stage;
import Utility.*;

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
	public static  HashMap <String, Double> bladeStats = new HashMap<String, Double>(); //statName -> value NEW
	public static  HashMap <String, Attack> bladeAtks = new HashMap<String, Attack>(); //attackName -> hitboxes

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

	private String [] characterNames = {"shooter", "swordsperson", "shooter"}; //all character names

	private ArrayList <SelectRect> stageSelectRects, //SelectRects in the stage select screen
								   charSelectRects, //SelectRects in the character select screen
								   keySelectRects; //SelectRects in the key select screen

	private ArrayList <Stage> allStages; //all stages
	// private String player1, player2; //String of the character name the Player 1 and Player 2 have selected

	private SelectRect controlScreenRect, //SelectRect to go to the control select screen
					   pauseRect; //SelectRect to pause/unpause the game

    Timer timer; //Timer to count frames in the game

	Player p1, p2; //Player 1 and Player 2 used to battle in the BATTLE screen

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
		// timer.setActionCommand("maingame");
		timer.start();

		shooterStats = Util.loadStats("shooterStats.txt");
		shooterAtks = Util.loadAtks("shooterAtks.txt");
		swordspersonStats = Util.loadStats("swordspersonStats.txt");
		swordspersonAtks = Util.loadAtks("swordspersonAtks.txt");
		bladeStats = Util.loadStats("bladeStats.txt"); //NEW
		bladeAtks = Util.loadAtks("bladeAtks.txt"); //NEW

		curStage = Util.loadStage("stages/verticalPlat.txt"); //sets the stage to a default stage

		controlScreenRect = new SelectRect(new Rectangle(0, 0, 100, 100), 0, "", "assets/stage1.png"); 
		pauseRect = new SelectRect(new Rectangle(WIDTH/2, HEIGHT-100, 100, 100), 0, "", "assets/stage1.png");

		//sets current screen to the start by default
		curScreen = START;

		//PRECOMPUTE STAGES
		allStages = new ArrayList<Stage>();
		String [] stageNames = {"noPlats", "verticalPlat", "triPlat", "twoMoving","ground", "mainMoving", "twoPillars"};
		for (int i=0; i<stageNames.length; i++){allStages.add(Util.loadStage("stages/"+stageNames[i]+".txt"));} 

		//INITIALIZE SelectRects
		stageSelectRects = Util.loadSelectRects("selectRects/stageSelect.txt");
		charSelectRects = Util.loadSelectRects("selectRects/charSelect.txt");
		keySelectRects = Util.loadSelectRects("selectRects/keySelect.txt");

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
		// player1 = "shooter";
		// player2 = "shooter";

		//loads font
		String fName = "NeonLight-Regular.ttf"; 
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
			h.lowerInvis(); //NEW
			if (h.getInvis() > 0){
				h.setY(curPlayer.getY());
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

			oppoPlayer.addForce(new Force(Force.knockBack(h.getKnockBackX(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  Force.knockBack(h.getKnockBackY(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  h.getStun())); //adds a Force acting on the opposing Player
			oppoPlayer.setStun(Math.max(h.getStun(), oppoPlayer.getStun())); //stuns the opposing Player
			oppoPlayer.addDamage(h.getDamage()); //adds damage to the opposing Player

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
		// System.out.println(timer.getActionCommand());
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

	public void updateMouse(MouseEvent e){
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
		//TO DO CHANGE TO A BACKGROUND IMAGE
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("CLICK TO START", WIDTH/2, HEIGHT/2);
	}
	public void paintControlSelect(Graphics g){
		//TO DO CHANGE TO A BACKGROUND IMAGE 
		
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		for (SelectRect curRect : keySelectRects){
			curRect.draw(g);
			if (!curRect.name.equals("START") && !curRect.name.equals("DEFAULT")) g.drawString(""+(char)(int)playerKeys.get(curRect.val).get(curRect.name), (int)curRect.rect.getX()+(int)curRect.rect.getWidth()/2, (int)curRect.rect.getY()+(int)curRect.rect.getHeight()/2);
		}
	}
	public void paintCharacterSelect(Graphics g){
		//TO DO show which characters are selected / if they are cpu

		//TO DO CHANGE TO A BACKGROUND IMAGE, the next couple of lines are temp
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		for (int i=0; i<charSelectRects.size(); i++){
			charSelectRects.get(i).draw(g);
		}
	}
	public void paintStageSelect(Graphics g){
		//TO DO CHANGE TO A BACKGROUND IMAGE
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		for (int i=0; i<stageSelectRects.size(); i++){
			stageSelectRects.get(i).draw(g);
		}
	}
	public void paintEnd(Graphics g){
		//TO DO CHANGE TO A BACKGROUND IMAGE
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);

		//shows stats of the battle such as how much damage was done, taken, lives lost, etc.
	}
	public void paintBattle(Graphics g){
		try{
			//draw Stage and Platforms
			curStage.draw(g);

			//draw Players and their Hitboxes
			p1.draw(g, 30, 30);
			p2.draw(g, 730, 30);
			pauseRect.draw(g);
		}
		catch(NullPointerException ex){
			System.out.println(p1 + " " + p2);
			// System.out.println(player1 + " " + player2);
		}
	}
	public void paintPause(Graphics g){
		//TO DO CHANGE TO A BACKGROUND IMAGE
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("PAUSE SCREEN", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		pauseRect.draw(g);
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
						p1.loadKeyLayout(playerKeys.get(0));
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
			for (SelectRect curRect : charSelectRects){ //loops through SelectRects 
				if (curRect.contains(mouseX, mouseY)){ // checks if the mouse is inside them
					if (curRect.name.equals("START")){
						curScreen = STAGESELECT; //moves to the next screen
						mousePressed = false;
						//initializes Players
						p1 = new Player(0,0, bladeStats, bladeAtks, false, "bladekeeper", "Idle", 0); // maybe change string to constant (or completely remove)
						// p1 = new Player(0,0, Util.loadStats(p1.getType()+"Stats.txt"), Util.loadAtks(player1+"Atks.txt"), false);
						p1.loadKeyLayout(playerKeys.get(0));
						// p2 = new Player(0,0, Util.loadStats(player2+"Stats.txt"), Util.loadAtks(player2+"Atks.txt"), false);
						p2 = new Player(0,0, swordspersonStats, swordspersonAtks, false, "swordsperson", "Idle", 0);
						p2.loadKeyLayout(playerKeys.get(1));

					}

					else{
						//LEFTCLICK affects player1
						//RIGHTCLICK affects player2
						if (curRect.name.equals("RANDOM")){ //changes the Player to a random one
							if (mouseButton == LEFTCLICK){
								p1.setType(characterNames[Util.randint(0, characterNames.length-1)]);
								// player1 = characterNames[Util.randint(0, characterNames.length-1)];
							}
							else if (mouseButton == RIGHTCLICK){ 
								p2.setType(characterNames[Util.randint(0, characterNames.length-1)]);
								// player2 = characterNames[Util.randint(0, characterNames.length-1)];
							}
						}
						else{
							//changes the Player to the one that was clicked
							if (mouseButton == LEFTCLICK){
								p1.setType(characterNames[curRect.val]);
								// player1 = characterNames[curRect.val];
							}
							else if (mouseButton == RIGHTCLICK){
								p2.setType(characterNames[curRect.val]);
								// player2 = characterNames[curRect.val];
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

		move(); 	// never draw in move
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