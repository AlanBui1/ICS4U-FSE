import java.awt.*;
import java.util.ArrayList;

public class Player extends Mover{
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    private double mass;

    /*
    gravity is the force of gravity acting on Player when they aren't onGround
    */

    private int w, h, lives, atkCooldown, stunTime;

    /*
    w is the width of the Player
    h is the height of the Player
    lives is the number of lives the player has
    atkCooldown is a cooldown for attacks to prevent excessive spamming
    stunTime is a cooldown for all movements usually after getting hit by a Hitbox
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

    public Player(int xx, int yy, int direct, int numLives, double m){
        super(xx, yy, 0, 0, 0, 0);
        w = 10;
        h = 20;
        jump1 = false;
        jump2 = false;
        onGround = false;
        lives = numLives;
        mass = m;

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
        if (getX() <= 0 || getX()+w >= Gamepanel.WIDTH){
            loseLife();
            setX(300);
            setY(30);
            // System.out.println(lives);
        }
        if (getY() <= 0 || getY()+h >= Gamepanel.HEIGHT){
            loseLife();
            setX(300);
            setY(30);
            // System.out.println(lives);
        }

        if (stunTime <= 0){
            if (onGround){ //on temporary ground
                jump1 = true;
                jump2 = true;
                setVY(0);
            }

            if (keys[LKey]){ //moves left with constant velocity
                setX(getX() - 7);
                dir = LEFT;
            }
            if (keys[RKey]){ //moves right with constant velocity
                setX(getX() + 7);
                dir = RIGHT;
            }

            if (keys[UKey1] || keys[UKey2]){
                if (keys[UKey1]){
                    if (jump1){
                        // setVY(getVY() - 20);
                        addForce(new Force(0, -800, 1, 0));
                        jump1 = false;
                        onGround = false;
                    }
                }
                else if (keys[UKey2]){
                    if (jump2 && !jump1){
                        addForce(new Force(0, -1000, 1, 0));
                        // setVY(getVY() - 20);
                        jump2 = false;
                        onGround = false;
                    }
                }
            }

            if (keys[DKey]){
                // y+=7;
                // vy += 5;
            }
        }

        applyForces(); 
        this.move();

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
        // System.out.println(stunTime);
    }

    @Override
    public void move(){
        addVX(getAX());
        addVY(getAY());
        addX(getVX());
        addY(getVY());
    }

    public void applyForces(){
        setAX(0);
        setAY(0);

        if (!onGround){
            if (getVY() < 10){
                addVY(Util.GForce(mass));
            }
        }

        //applies accelerations acting on the Player
        //idk if getting hit by attacks will be an acceleration force or velocity (it's a Force = mass*accel)
        //F = ma
        //F/m = a

        for (int i=forces.size()-1; i>=0; i--){
            Force f = forces.get(i);
            addAX(f.magnitudeX/mass);
            addAY(f.magnitudeY/mass);
            f.addTime(-1);
            //System.out.println("TIME " + f.getTime());
            if (f.getTime() <= 0){
                addVX(-f.magnitudeX/mass*f.getOrigTime());
                // addVY(-f.magnitudeY/mass*f.getOrigTime());
                forces.remove(i);
            }
        }
    }

    public void checkPlats(ArrayList<Platform> plats){
        //method to check if the player is on a platform and adjusts the Player accordingly
        Rectangle guyr = getRect();
		Rectangle guyrNoVY = new Rectangle((int)getX(),(int)(getY()-getVY()),w,h);		

        onGround = false;
			
		for(Platform p: plats){
			// currently overlaps
			if(guyr.intersects(p.getRect())){
				// moving down
                onGround = true;
				if(getVY() > 0){
					// caused by moving down
					if(!guyrNoVY.intersects(p.getRect())){
						setY(p.getY()-h);
                        setVY(0);
						onGround = true;						
					}					
				}
			}		
		}	
    }

    public void loseLife(){
        lives--;
        setVX(0);
        setVY(0);
        setAX(0);
        setAY(0);
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect((int)getX(), (int)getY(), w, h);
    }

    public Rectangle getRect(){ // returns Rectangle
        return new Rectangle((int)getX(), (int)getY(), w, h);
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
    
    public void addForce(double magX, double magY, int time, int stun){
        forces.add(new Force(magX, magY, time, stun));
    }
    public void addForce(Force f){
        forces.add(f);
    }

    public void addHitBox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double KBX, double KBY, int stun){
        hitboxes.add(new Hitbox(X, Y, W, H, VX, VY, AX, AY, T, KBX, KBY, 10, stun));
    }

    public void addHitBox(Mover m, Force f, double w, double h, double time){
        hitboxes.add(new Hitbox(m.getX(), m.getY(), w, h, m.getVX(), m.getVY(), m.getAX(), m.getAY(), time, f.getMX(), f.getMY(), f.getTime(), f.getStun()));
    }

    public ArrayList <Hitbox> getHitBoxes(){
        return hitboxes;
    } 

    public void setCoolDown(int time){
        atkCooldown = time;
    }

    public void addStun(int time){
        stunTime += time;
    }

    public int getStun(){
        return stunTime;
    }
}

