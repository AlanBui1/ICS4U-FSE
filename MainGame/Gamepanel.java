package MainGame;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.io.*;

import javax.swing.*;

import GameObjects.ThingsThatMove.Player;
import GameObjects.ThingsThatMove.ShooterAI;
import GameObjects.ThingsThatMove.Platform;
import GameObjects.ThingsThatMove.AttackStuff.*;
import GameObjects.ThingsThatMove.AttackStuff.Hitbox;
import GameObjects.Stage;
import Utility.Util;

public class Gamepanel extends JPanel implements KeyListener, ActionListener, MouseListener{	
	public static final int WIDTH = 800, HEIGHT = 600;
	public static final int LEFTCLICK = 1, RIGHTCLICK = 3;
	public static final int START = 0, 
							CONTROLSELECT = 1, 
							CHARACTERSELECT = 2,
							STAGESELECT = 3,
							BATTLE = 4,
							ENDSCREEN = 5;
	
	public static final String [] keyNames = {"UKey", "DKey", "LKey", "RKey", "fastKey", "chargeKey"};
	public static final HashMap<String, Integer> defaultKeys1 = new HashMap<String, Integer>();
	public static final HashMap<String, Integer> defaultKeys2 = new HashMap<String, Integer>();

	private ArrayList<HashMap <String, Integer>> playerKeys;
	private HashMap<Rectangle, String> keyRects;

    private boolean [] keysPressed;
	private int [] keysHeldTime, keysReleasedTime;
	private boolean mousePressed;
	private int mouseX, mouseY, mouseButton;
	private String selectedKey;
	private int selectedPlayer;
	
	private int curScreen;
	private Stage curStage;

	private ArrayList <Rectangle> stageSelectRects;
	private ArrayList <Stage> allStages;

	private ArrayList <Rectangle> charSelectRects;
	private ArrayList <String> allCharacters;

	private String player1, player2;

	private Rectangle defaultRect;

    Timer timer;
    // Player p2; 
	Player p1;
	ShooterAI p2;

	private Font fontLocal; //Font used for text to be drawn on screen

	private Rectangle nextScreenRect;

	public Gamepanel(){
		keysPressed = new boolean[KeyEvent.KEY_LAST+1];
		keysHeldTime = new int[KeyEvent.KEY_LAST+1];
		keysReleasedTime = new int[KeyEvent.KEY_LAST+1];

		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		timer = new Timer(20, this);
		timer.start();

		curStage = Util.loadStage("stages/verticalPlat.txt");

		nextScreenRect = new Rectangle(500, 400, 50, 50);

		curScreen = START;

		//PRECOMPUTE STAGES
		allStages = new ArrayList<Stage>();
		String [] stageNames = {"noPlats", "verticalPlat", "triPlat", "twoMoving"};
		for (int i=0; i<stageNames.length; i++){
			allStages.add(Util.loadStage("stages/"+stageNames[i]+".txt"));
		} 
		stageSelectRects = new ArrayList<Rectangle>();
		stageSelectRects.add(new Rectangle(100, 100, 100, 100));
		stageSelectRects.add(new Rectangle(300, 300, 100, 100));
		stageSelectRects.add(new Rectangle(100, 300, 100, 100));
		stageSelectRects.add(new Rectangle(300, 100, 100, 100));

		//PRECOMPUTE PLAYERS
		allCharacters = new ArrayList<String>();
		String [] characterNames = {"shooter"};
		for (int i=0; i<characterNames.length; i++){
			allCharacters.add(characterNames[i]);
		}
		charSelectRects = new ArrayList<Rectangle>();
		charSelectRects.add(new Rectangle(50, 50, 200, 200));

		//INITIALIZE KEY SELECT
		playerKeys = new ArrayList<HashMap<String, Integer>>();
		playerKeys.add(new HashMap<String, Integer>());
		playerKeys.add(new HashMap<String, Integer>());

		keyRects = new HashMap<Rectangle, String>();
		for (int i=100; i<700; i+=100){
			keyRects.put(new Rectangle(i, 10, 50, 50), keyNames[i/100-1]);
			keyRects.put(new Rectangle(i, 400, 50, 50), keyNames[i/100-1]);
		}

		//PRECOMPUTE DEFAULT KEYS
		int [] default1 = {KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_E, KeyEvent.VK_Q},
			   default2 = {KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_U, KeyEvent.VK_O};			

		for (int i=0; i<6; i++){
			defaultKeys1.put(keyNames[i], default1[i]);
			defaultKeys2.put(keyNames[i], default2[i]);
			playerKeys.get(0).put(keyNames[i], default1[i]);
			playerKeys.get(1).put(keyNames[i], default2[i]);
		}
		
		player1 = "shooter";
		player2 = "shooter";

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
    public void move(){		
		try{
			for (Platform p : curStage.getPlats()){
				p.move();
			}
			if (p1.getStun() > 0) p1.addStun(-1);
			if (p2.getStun() > 0) p2.addStun(-1);
			
			p1.setCoolDown(p1.getCoolDown()-1);
			p1.attack(keysPressed, keysReleasedTime);
			p1.move(keysPressed, keysReleasedTime, curStage);
			p2.setCoolDown(p2.getCoolDown()-1);
			p2.move(keysPressed, keysReleasedTime, curStage);
			p2.attack(keysPressed, keysReleasedTime);

			checkCollisions(p1, p2);
			checkCollisions(p2, p1);
		}
		catch(Exception e){}
	}

	public void checkCollisions(Player curPlayer, Player oppoPlayer){ //curPlayer attacking oppoPlayer
		ArrayList<Hitbox> toDelH = new ArrayList<Hitbox>();
		for (Hitbox h : curPlayer.getHitBoxes()){
			if (oppoPlayer.getRect().intersects(h.getRect())){
				toDelH.add(h);
				oppoPlayer.addForce(new Force(Force.knockBack(h.getKnockBackX(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  Force.knockBack(h.getKnockBackY(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  h.getStun()));
			}
		}

		for (Hitbox h : toDelH){
			oppoPlayer.setStun(Math.max(h.getStun(), oppoPlayer.getStun()));
			curPlayer.getHitBoxes().remove(h);
			oppoPlayer.addDamage(h.getDamage());
		}
	}
//###########################################################################################################################


	@Override
	public void actionPerformed(ActionEvent e){
		for (int i=0; i<KeyEvent.KEY_LAST; i++){
			if (keysPressed[i]){
				keysHeldTime[i]++;
			}
		}

		try{
			if (curScreen == START){
				if (mousePressed){
					curScreen = CHARACTERSELECT;
				}
			}
			else if (curScreen == CHARACTERSELECT){
				if (mousePressed){
					for (int i=0; i<charSelectRects.size(); i++){
						if (charSelectRects.get(i).contains(mouseX, mouseY)){
							if (mouseButton == LEFTCLICK){
								player1 = allCharacters.get(i);
							}
							else if (mouseButton == RIGHTCLICK){
								player2 = allCharacters.get(i);
							}
						}
					}
					if (nextScreenRect.contains(mouseX, mouseY)){
						curScreen = STAGESELECT;
						mousePressed = false;

						p1 = new Player(0,0, Util.loadStats(player1+"Stats.txt"), Util.loadAtks(player1+"Atks.txt"));
						p1.loadKeyLayout(playerKeys.get(0));
						p2 = new ShooterAI(0,0, Util.loadStats(player2+"Stats.txt"), Util.loadAtks(player2+"Atks.txt"));
						p2.loadKeyLayout(playerKeys.get(1));
					}
				}
			}
			else if (curScreen == STAGESELECT){
				if (mousePressed){
					for (int i=0; i<stageSelectRects.size(); i++){
						if (stageSelectRects.get(i).contains(mouseX, mouseY)){
							curStage = allStages.get(i);
						}
					}
					if (nextScreenRect.contains(mouseX, mouseY)){
						curScreen = CONTROLSELECT;
						mousePressed = false;
					}
				}
			}
			else if (curScreen == CONTROLSELECT){
				if (mousePressed){
					if (nextScreenRect.contains(mouseX, mouseY)){
						curScreen = BATTLE;
						mousePressed = false;
					}

					for (Rectangle r : keyRects.keySet()){
						if (r.contains(mouseX, mouseY)){
							selectedKey = keyRects.get(r);
							selectedPlayer = mouseButton == 1 ? 0 : 1;
							break;
						}
					}
				}
			}
			
			else if (curScreen == BATTLE){
				move(); 	// never draw in move
				if (p1.getLives() <= 0 || p2.getLives() <= 0){
					curScreen = ENDSCREEN;
				}
			}
			else if (curScreen == ENDSCREEN){
				if (mousePressed){
					mousePressed = false;
					curScreen = START;
				}
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
		else if (curScreen == ENDSCREEN){
			paintEnd(g);
		}
    }

	public void paintStart(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("CLICK TO START", WIDTH/2, HEIGHT/2);
	}
	public void paintControlSelect(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("control select", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		Util.drawFilledRect(nextScreenRect, g);

		g.setColor(Color.BLUE);
		for (Rectangle r : keyRects.keySet()){
			Util.drawFilledRect(r, g);
			g.drawString(keyRects.get(r), (int)r.getX(), (int)(r.getY()+2*r.getHeight()));

			g.drawString(""+(char)(int)(playerKeys.get(0).get(keyRects.get(r))), (int)r.getX(), 180);
			g.drawString(""+(char)(int)(playerKeys.get(1).get(keyRects.get(r))), (int)r.getX(), 550);
		}
	}
	public void paintCharacterSelect(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("char select SCREEN", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		Util.drawFilledRect(nextScreenRect, g);

		for (int i=0; i<charSelectRects.size(); i++){
			Util.drawFilledRect(charSelectRects.get(i), g);
		}
	}
	public void paintStageSelect(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("Stage Select SCREEN", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		Util.drawFilledRect(nextScreenRect, g);

		for (int i=0; i<stageSelectRects.size(); i++){
			Util.drawFilledRect(stageSelectRects.get(i), g);
		}
	}
	public void paintEnd(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("END SCREEN", WIDTH/2, HEIGHT/2);
	}
	public void paintBattle(Graphics g){
		try{
			//draw Stage and Platforms
			curStage.draw(g);

			//draw Players and their Hitboxes
			p1.draw(g, 30, 30);
			p2.draw(g, 730, 30);
		}
		catch(NullPointerException ex){
			System.out.println(player1 + " " + player2);
		}
	}
}