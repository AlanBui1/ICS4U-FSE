import java.awt.*;
import java.util.ArrayList;

public class Player extends Mover{
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    private double gravity;

    /*
    (x, y) are the Player's coordinates on the screen 
    vx and vy are the Player's velocity in the x and y directions, respectively
    gravity is the force of gravity acting on Player when they aren't onGround
    */

    private int w, h, lives, atkCooldown;

    /*
    w is the width of the Player
    h is the height of the Player
    lives is the number of lives the player has
    atkCooldown is a cooldown for attacks to prevent excessive spamming
    */

    private ArrayList<Force> forces;
    private int LKey, RKey, UKey1, UKey2, DKey, fastKey;
    private boolean jump1, jump2, onGround;

    /*
    jump1 is true if the Player can use their first jump
    jump2 is true if the Player can use their second jump
    onGround is true if the Player is on a platform
    */
    private int dir;

    ArrayList <Hitbox> hitboxes; //hitboxes the Player sends out e.g. bullets

    public Player(int xx, int yy, int direct, int numLives, double g){
        super(xx, yy, 0, 0, 0, 0);
        w = 10;
        h = 20;
        jump1 = false;
        jump2 = false;
        onGround = false;
        lives = numLives;
        gravity = g;

        hitboxes = new ArrayList<Hitbox>();
        forces = new ArrayList<Force>();
        dir = direct;
        atkCooldown = 0;
    }

    public void move(boolean [] keys, ArrayList <Platform> plats){ //moves the Player
        // this depends on if we want the players to die immediately after hitting the edge or not
        // rn it just resets player to starting pos... idk if that's what we want it to do
        // maybe add an invincibility period ?

        //player dies by going off-screen
        if (this.getX() <= 0 || this.getX()+w >= Gamepanel.WIDTH){
            loseLife();
            this.setX(300);
            this.setY(30);
            System.out.println(lives);
        }
        if (this.getY() <= 0 || this.getY()+h >= Gamepanel.HEIGHT){
            loseLife();
            this.setX(300);
            this.setY(30);
            System.out.println(lives);
        }

        if (onGround){ //on temporary ground
            jump1 = true;
            jump2 = true;
            this.setVY(0);
        }

        if (keys[LKey]){ //moves left with constant velocity
            this.setX(this.getX() - 7);
            dir = LEFT;
        }
        if (keys[RKey]){ //moves right with constant velocity
            this.setX(this.getX() + 7);
            dir = RIGHT;
        }

        if (keys[UKey1]){
            if (jump1){
                this.setVY(this.getVY() - 20);
                jump1 = false;
                onGround = false;
            }
        }
        if (keys[UKey2]){
            if (jump2){
                this.setVY(this.getVY() - 20);
                jump2 = false;
                onGround = false;
            }
        }
        if (keys[DKey]){
            // y+=7;
            // vy += 5;
        }

        //applies accelerations acting on the Player
        //idk if getting hit by attacks will be an acceleration force or velocity

        for (int i=forces.size()-1; i>=0; i--){
            Force f = forces.get(i);
            this.setVY(this.getVY() + f.getMY());
            this.setVX(this.getVX() + f.getMX());
            f.addTime(-1);
            System.out.println("TIME " + f.getTime());
            if (f.getTime() <= 0){
                this.setVX(this.getVX() - f.getMX()*f.getOrigTime());
                this.setVX(this.getVY() - f.getMY()*f.getOrigTime()); // is this supposed to be setVX ? 
                forces.remove(i);
            }
        }

        if (!onGround){
            this.setVY(this.getVY() + gravity);
            // vy += gravity;
        }

        if (this.getVY() > 10){
            this.setVY(10);
        }
        
        move();

        checkPlats(plats); //checks if on a platform and adjusts position 

        ArrayList <Hitbox> toDel = new ArrayList<Hitbox>();
        for (Hitbox h : hitboxes){
            h.move();
            if (h.getTime() <= 0){
                toDel.add(h);
            }
        }
        for (Hitbox h : toDel){
            hitboxes.remove(h);
        }
    }

    public void checkPlats(ArrayList<Platform> plats){
        //method to check if the player is on a platform and adjusts the Player accordingly
        Rectangle guyr = this.getRect();
		Rectangle guyrNoVY = new Rectangle((int)this.getX(),(int)(this.getY()-this.getVY()),w,h);		

        onGround = false;
			
		for(Platform p: plats){
			// currently overlaps
			if(guyr.intersects(p.getRect())){
				// moving down
                onGround = true;
				if(this.getVY() > 0){
					// caused by moving down
					if(!guyrNoVY.intersects(p.getRect())){
						this.setY(p.getY()-h);
                        this.setVY(0);
						onGround = true;						
					}					
				}
			}		
		}	
    }

    public void loseLife(){
        lives--;
        this.setVX(0);
        this.setVY(0);
        this.setAX(0);
        this.setAY(0);
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect((int)this.getX(), (int)this.getY(), w, h);
    }

    public Rectangle getRect(){ // returns Rectangle
        return new Rectangle((int)this.getX(), (int)this.getY(), w, h);
    }

    public void setLKey(int k){
        LKey = k;
    }
    public void setRKey(int k){
        RKey = k;
    }
    public void setUKey1(int k){
        UKey1 = k;
    }
    public void setUKey2(int k){
        UKey2 = k;
    }
    public void setDKey(int k){
        DKey = k;
    }
    public void setFastKey(int k){
        fastKey = k;
    }
    public void setLives(int l){
        lives = l;
    }
    public void setGravity(double g){
        gravity = g;
    }
    public void setDirect(int d){
        dir = d;
    }
    public int getDir(){
        return dir;
    }
    public int getLives(){
        return lives;
    }
    public int getCoolDown(){
        return atkCooldown;
    }
    public int getFastKey(){
        return fastKey;
    }
    public int getUKey1(){
        return UKey1;
    }
    public int getDKey(){
        return DKey;
    }

    // TEMP get rid of this if width for players is remaining the same for all characters
    public int getW(){
        return w;
    }
    
    public void addForce(double magX, double magY, int time){
        forces.add(new Force(magX, magY, time));
    }
    public void addForce(Force f){
        forces.add(f);
    }

    public void addHitBox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double KBX, double KBY){
        hitboxes.add(new Hitbox(X, Y, W, H, VX, VY, AX, AY, T, KBX, KBY, 2));
    }

    public ArrayList <Hitbox> getHitBoxes(){
        return hitboxes;
    } 

    public void setCoolDown(int time){
        atkCooldown = time;
    }

}

