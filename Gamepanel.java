import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class Gamepanel extends JPanel implements KeyListener, ActionListener, MouseListener{	
    private boolean [] keys;
	private ArrayList <Platform> platforms;

    Timer timer;
    Shooter p1 = new Shooter(400, 30, Player.RIGHT, 3, 2.5);
	Shooter p2 = new Shooter(280, 30, Player.RIGHT, 3, 2.5);
    public static final int WIDTH = 800, HEIGHT = 600;

	int shootCoolDown;

	public Gamepanel(){
		keys = new boolean[KeyEvent.KEY_LAST+1];

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
		p1.setFastKey(KeyEvent.VK_E);

		p2.setDKey(KeyEvent.VK_K);
        p2.setLKey(KeyEvent.VK_J);
        p2.setRKey(KeyEvent.VK_L);
        p2.setUKey1(KeyEvent.VK_I);
        p2.setUKey2(KeyEvent.VK_U);
		p2.setFastKey(KeyEvent.VK_O);

		platforms = new ArrayList<Platform>();
		platforms.add(new Platform(250, 240, 100, 1));
		platforms.add(new Platform(300, 300, WIDTH-300, 300));
	}

    public void move(){		
		// UHH i'll change this later so it's not just copy pasted code for each player
		p1.setCoolDown(p1.getCoolDown()-1);
        p1.move(keys, platforms);
		p1.attack(keys);
		p2.setCoolDown(p2.getCoolDown()-1);
        p2.move(keys, platforms);
		p2.attack(keys);

		checkCollisions();
    }

	public void checkCollisions(){
		ArrayList<Hitbox> toDelH = new ArrayList<Hitbox>();
		for (Hitbox h : p1.getHitBoxes()){
			if (p2.getRect().intersects(h.getRect())){
				toDelH.add(h);
				System.out.println("ASDJASILL");
				p2.addForce(h.getForce());
			}
		}

		for (Hitbox h : toDelH){
			p1.getHitBoxes().remove(h);
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
		g.fillRect(0,0,WIDTH,HEIGHT);

		platforms.get(0).draw(g, Color.BLUE);
		platforms.get(1).draw(g, Color.RED);
		// for (Platform p : platforms){
		// 	p.draw(g);
		// }

        p1.draw(g);
		for (Hitbox h : p1.getHitBoxes()){
			h.draw(g);
		}
		p2.draw(g);
		for (Hitbox h : p2.getHitBoxes()){
			h.draw(g);
		}
    }
}