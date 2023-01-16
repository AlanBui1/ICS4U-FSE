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
	public static final int START = 0, 
							CONTROLSELECT = 1, 
							CHARACTERSELECT = 2,
							STAGESELECT = 3,
							BATTLE = 4,
							ENDSCREEN = 5;

    private boolean [] keysPressed;
	private int [] keysHeldTime, keysReleasedTime;
	private boolean mousePressed;
	private int mouseX, mouseY, mouseButton;
	
	private int curScreen;
	private Stage curStage;

	//moves these into another class with just stats and attacks
	public static  HashMap <String, Double> shooterStats = new HashMap<String, Double>(); //statName -> value 
	public static  HashMap <String, Attack> shooterAtks = new HashMap<String, Attack>(); //attackName -> hitboxes

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

		shooterStats= Util.loadStats("shooterStats.txt");
		shooterAtks = Util.loadAtks("shooterAtks.txt");
		nextScreenRect = new Rectangle(500, 400, 50, 50);

		curScreen = START;

		p1 = new Player(400, 30, shooterStats, shooterAtks);
        p1.setDKey(KeyEvent.VK_S);
        p1.setLKey(KeyEvent.VK_A);
        p1.setRKey(KeyEvent.VK_D);
        p1.setUKey(KeyEvent.VK_W);
        p1.setChargeKey(KeyEvent.VK_Q);
		p1.setFastKey(KeyEvent.VK_E);

		p2 = new ShooterAI(400, 30, shooterStats, shooterAtks);
		p2.setDKey(KeyEvent.VK_K);
        p2.setLKey(KeyEvent.VK_J);
        p2.setRKey(KeyEvent.VK_L);
        p2.setUKey(KeyEvent.VK_I);
		p2.setChargeKey(KeyEvent.VK_U);
		p2.setFastKey(KeyEvent.VK_O);
		p2.setKeys();

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
			else if (curScreen == CONTROLSELECT){

			}
			else if (curScreen == CHARACTERSELECT){
				if (mousePressed){
					if (nextScreenRect.contains(mouseX, mouseY)){
						curScreen = STAGESELECT;
					}
				}
			}
			else if (curScreen == STAGESELECT){
				if (mousePressed){
					if (nextScreenRect.contains(mouseX, mouseY)){
						curScreen = BATTLE;
					}
				}
			}
			else if (curScreen == BATTLE){
				move(); 	// never draw in move
			}
			else if (curScreen == ENDSCREEN){

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
	}
	public void paintCharacterSelect(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("char select SCREEN", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		g.fillRect((int)nextScreenRect.getX(), (int)nextScreenRect.getY(), (int)nextScreenRect.getWidth(), (int)nextScreenRect.getHeight());
	}
	public void paintStageSelect(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("Stage Select SCREEN", WIDTH/2, HEIGHT/2);
		g.setColor(Color.RED);
		g.fillRect((int)nextScreenRect.getX(), (int)nextScreenRect.getY(), (int)nextScreenRect.getWidth(), (int)nextScreenRect.getHeight());
	}
	public void paintEnd(Graphics g){
		g.setColor(Color.GRAY);
		g.fillRect(0,0, WIDTH, HEIGHT);
		g.setColor(Color.BLACK);
		g.drawString("END SCREEN", WIDTH/2, HEIGHT/2);
	}
	public void paintBattle(Graphics g){
		//draw Stage and Platforms
		curStage.draw(g);

		//draw Players and their Hitboxes
        p1.draw(g, 30, 30);
		p2.draw(g, 730, 30);
	}
}