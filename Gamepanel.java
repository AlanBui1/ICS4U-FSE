import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class Gamepanel extends JPanel implements KeyListener, ActionListener, MouseListener{	
    private boolean [] keys;
	private ArrayList<Bullet>bullets;
	private ArrayList<Bullet>remBullets;
	private ArrayList <Platform> platforms;

    Timer timer;
    Player p1 = new Player(300, 30, Player.RIGHT, 3);
	Player p2 = new Player(280, 30, Player.RIGHT, 3);
    public static final int WIDTH = 800, HEIGHT = 600;

	int shootCoolDown;

	public Gamepanel(){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		bullets = new ArrayList<Bullet>(); 
		remBullets = new ArrayList<Bullet>();

		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		timer = new Timer(20, this);
		timer.start();
		shootCoolDown = 10;

        p1.setDKey(KeyEvent.VK_S);
        p1.setLKey(KeyEvent.VK_A);
        p1.setRKey(KeyEvent.VK_D);
        p1.setUKey1(KeyEvent.VK_W);
        p1.setUKey2(KeyEvent.VK_Q);
		p1.setShootKey(KeyEvent.VK_E);
        p1.addAccel("Gravity", 2.5, -1, "Y");

		p2.setDKey(KeyEvent.VK_K);
        p2.setLKey(KeyEvent.VK_J);
        p2.setRKey(KeyEvent.VK_L);
        p2.setUKey1(KeyEvent.VK_I);
        p2.setUKey2(KeyEvent.VK_U);
		p2.setShootKey(KeyEvent.VK_O);
        p2.addAccel("Gravity", 2.5, -1, "Y");

		platforms = new ArrayList<Platform>();
		platforms.add(new Platform(250, 240, 100, 1));
		platforms.add(new Platform(300, 300, WIDTH-300, 300));
	}

    public void move(){		
		// UHH i'll change this later so it's not just copy pasted code for each player
        p1.move(keys, platforms);
		p2.move(keys, platforms);

		double canShoot1 = p1.shoot(bullets, keys, shootCoolDown);
		double canShoot2 = p2.shoot(bullets, keys, shootCoolDown);
		if (canShoot1 != -1){
			bullets.add(new Bullet(10, p1.getX()+(p1.getW()/2), p1.getY()+5, 1, p1, p2));
			bullets.get(bullets.size()-1).addAccel("Shot", 10*p1.getDir(), -1, "X");
			shootCoolDown = 25;
		}
		if (canShoot2 != -1){
			bullets.add(new Bullet(10, p2.getX()+(p2.getW()/2), p2.getY()+5, 1, p2, p1));
			bullets.get(bullets.size()-1).addAccel("Shot", 10*p2.getDir(), -1, "X");
			shootCoolDown = 25;
		}

		for (int b = 0; b < bullets.size(); b++){ // moves every bullet 
			bullets.get(b).move(); 
			if (bullets.get(b).checkHitPlayer(bullets)){
				remBullets.add(bullets.get(b));
				bullets.get(b).getOppo().loseLife();
				System.out.println("PLAYER 1: " + p1.getLives());
				System.out.println("PLAYER 2: " + p2.getLives());
			}
		}

		if (remBullets != null){ // removes all elements that have been hit 
			bullets.removeAll(remBullets);
		}
    }
	
	@Override
	public void actionPerformed(ActionEvent e){
		shootCoolDown --;
		move(); 	// never draw in move
		repaint(); 	// only draw
	}
	
	@Override
	public void keyReleased(KeyEvent ke){
		int key = ke.getKeyCode();
		keys[key] = false;
	}	
	
	@Override
	public void keyPressed(KeyEvent ke){
		int key = ke.getKeyCode();
		keys[key] = true;
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
		g.fillRect(0,0,800,800);

		platforms.get(0).draw(g, Color.BLUE);
		platforms.get(1).draw(g, Color.RED);
		// for (Platform p : platforms){
		// 	p.draw(g);
		// }

        p1.draw(g);
		p2.draw(g);

		for (int b = 0; b < bullets.size(); b++){ // draws all bullets
			bullets.get(b).draw(g);
		}
		
    }
}