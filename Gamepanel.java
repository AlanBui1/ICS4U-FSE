import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

class Gamepanel extends JPanel implements KeyListener, ActionListener, MouseListener{	
    private boolean [] keys;
	private ArrayList<Bullet>bullets;

    Timer timer;
    Player p1 = new Player(30, 30);
    public static final int WIDTH = 800, HEIGHT = 600;

	int shootCoolDown;

	public Gamepanel(){
		keys = new boolean[KeyEvent.KEY_LAST+1];
		bullets = new ArrayList<Bullet>(); 
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
        p1.addAccel("Gravity", 10, -1, "Y");
	}

    public void move(){
        p1.move(keys);

		double canShoot = p1.shoot(bullets, keys, shootCoolDown);
		if (canShoot != -1){
			bullets.add(new Bullet(10, p1.getX()+15, p1.getY()+15, 1));
			shootCoolDown = 25;
		}

		for (int b = 0; b < bullets.size(); b++){ // moves every bullet 
			bullets.get(b).move(); 
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
        g.setColor(Color.RED);
        g.fillRect(0,300, 800, 300);
        p1.draw(g);

		for (int b = 0; b < bullets.size(); b++){ // draws all bullets
			bullets.get(b).draw(g);
		}
    }
}