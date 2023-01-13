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
    private boolean [] keysPressed;
	private int [] keysHeldTime, keysReleasedTime;
	// private ArrayList <Platform> platforms;
	private Stage curStage;
	public static  HashMap <String, Double> shooterStats = new HashMap<String, Double>(); //statName -> value
	public static  HashMap <String, Attack> shooterAtks = new HashMap<String, Attack>(); //attackName -> hitboxes

    Timer timer;
    // Player p2; 
	Player p1;
	ShooterAI p2;
    public static final int WIDTH = 800, HEIGHT = 600;

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

		// platforms = new ArrayList<Platform>();
		// platforms.add(new Platform(300, 700, 800, 10));
		// platforms.add(new Platform(250, 700, 100, 10, 250, 1100, 800, 800, 10, 0));
		curStage = Util.loadStage("stages/verticalPlat.txt");

		shooterStats= Util.loadStats("shooterStats.txt");
		shooterAtks = Util.loadAtks("shooterAtks.txt");;

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
	}

    public void move(){		
		try{
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

			for (Platform p : curStage.getPlats()){
				p.move();
			}
		}
		catch(Exception e){}
	}

	public void checkCollisions(Player curPlayer, Player oppoPlayer){ //curPlayer attacking oppoPlayer
		ArrayList<Hitbox> toDelH = new ArrayList<Hitbox>();
		for (Hitbox h : curPlayer.getHitBoxes()){
			if (oppoPlayer.getRect().intersects(h.getRect())){
				toDelH.add(h);
				oppoPlayer.addForce(new Force(Util.knockBack(h.getKnockBackX(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  Util.knockBack(h.getKnockBackY(), oppoPlayer.getWeight(), oppoPlayer.getDamage()), 
									  h.getStun()));
			}
		}

		for (Hitbox h : toDelH){
			oppoPlayer.setStun(Math.max(h.getStun(), oppoPlayer.getStun()));
			curPlayer.getHitBoxes().remove(h);
			oppoPlayer.addDamage(h.getDamage());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		for (int i=0; i<KeyEvent.KEY_LAST; i++){
			if (keysPressed[i]){
				keysHeldTime[i]++;
			}
		}
		
		// System.out.println(keysPressed[KeyEvent.VK_E] + " " + keysHeldTime[KeyEvent.VK_E] + " " + keysReleasedTime[KeyEvent.VK_E]);
		move(); 	// never draw in move
		repaint(); 	// only draw
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
	@Override
	public void	mouseClicked(MouseEvent e){}

	@Override
	public void	mouseEntered(MouseEvent e){}

	@Override
	public void	mouseExited(MouseEvent e){}

	@Override
	public void	mousePressed(MouseEvent e){}

	@Override
	public void	mouseReleased(MouseEvent e){}

	@Override
	public void paint(Graphics g){
		g.setColor(Color.BLACK);
		g.fillRect(0,0,WIDTH,HEIGHT);

		for (Platform p : curStage.getPlats()){
			p.draw(g, Color.GREEN);
		}

		// DRAWING LIVES
		Polygon test = new Polygon(new int []{10+300, 25+300, 40+300, 25+300}, new int []{25+250,40+250,25+250,10+250}, 4);
		if (test.intersects(p1.getRect()) || test.intersects(p2.getRect())){
			//System.out.println("AS");
			g.setColor(Color.WHITE);
		}
		else{
			g.setColor(Color.RED);
		}
		g.fillPolygon(test);
		g.setColor(Color.RED);
		// for (int L = 0; L < 3; L++){
		// 	if (p1.getLives()-L == 0){
		// 		g.setColor(Color.WHITE);
		// 	}
		// 	g.fillRect(20 + L*30, 570, 20, 20);
		// }

		// g.setColor(Color.RED);
		// for (int L = 0; L < 3; L++){
		// 	if (p2.getLives()-L == 0){
		// 		g.setColor(Color.WHITE);
		// 	}
		// 	g.fillRect(700 + L*30, 570, 20, 20);
		// }

        p1.draw(g);
		for (Hitbox h : p1.getHitBoxes()){
			h.draw(g);
		}

		g.drawString(""+Util.fDouble(p1.getDamage(), 1), 40, 40);

		p2.draw(g);
		for (Hitbox h : p2.getHitBoxes()){
			h.draw(g);
		}
		g.drawString(""+Util.fDouble(p2.getDamage(), 1), 740, 40);
    }
}