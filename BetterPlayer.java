import java.util.ArrayList;
import java.awt.*;

public class BetterPlayer extends Mover{
    public static final int LEFT = -1, RIGHT = 1, WIDTH = 20, HEIGHT = 30;

    private int LKey, RKey, UKey1, UKey2, DKey, fastKey; //keys used to move in respective directions

    private double weight, airaccel, airfriction, groundfriction, airspd, fallspd, gravity, runspd, jumpforce;

    /*
    weight () is a measure of how much a Player can resist knockback i.e. more weight => less knockback
    airaccel (pixels / frame^2) is the rate a Player can change their horizontal velocity midair
    airfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force midair
    groundfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force on the ground
    airspd (pixels / frame) is the maximum speed a Player can move horizontally midair
    fallspd (units / frame) is the rate a Player can move downward mid-air
    gravity (pixels / frame^2) is a measure of how fast a falling Player reaches fallspd
    runspd (pixels / frame) is the speed a Player moves on the ground horizontally (running)
    jumpforce (pixles / frame) is the force exerted on the Player when it jumps 
    */

    private int stunTime, dir, atkCooldown;

    private boolean jump1, jump2, onGround;
    /*
    jump1 is true if the Player can use their first jump
    jump2 is true if the Player can use their second jump
    onGround is true if the Player is on a platform
    */

    ArrayList <Hitbox> hitboxes; //hitboxes the Player sends out e.g. bullets
    private ArrayList<Force> forces;

    public BetterPlayer(double x, double y, double weight,double airaccel, double airfriction, double groundfriction, double airspd,double fallspd,double gravity, double runspd, double jumpforce){
        super(x, y);
        
        this.weight = weight;
        this.airaccel = airaccel;
        this.airfriction = airfriction;
        this.groundfriction = groundfriction;
        this.airspd = airspd;
        this.fallspd = fallspd;
        this.gravity = gravity;
        this.runspd = runspd;
        this.jumpforce = jumpforce;

        stunTime = 0;
        atkCooldown = 0;
        dir =1;
        onGround = false;
        hitboxes = new ArrayList<Hitbox>();
        forces = new ArrayList<Force>();
    }

    public void move(boolean [] keys, ArrayList <Platform> plats){ //moves the Player
        if (keys[32]){
            loseLife();
        }

        // this depends on if we want the players to die immediately after hitting the edge or not
        // rn it just resets player to starting pos... idk if that's what we want it to do
        // maybe add an invincibility period ?

        //player dies by going off-screen
        if (getX() <= 0 || getX()+WIDTH >= Gamepanel.WIDTH){
            loseLife();
            setX(300);
            setY(30);
            // System.out.println(lives);
        }
        if (getY() <= 0 || getY()+HEIGHT >= Gamepanel.HEIGHT){
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
                if (onGround){
                    setVX(-runspd);
                }
                else{
                    setVX(-airspd);
                    // setVX(-Math.min(airspd, airaccel + getVX()));
                }
                dir = LEFT;
            }
            if (keys[RKey]){ //moves right with constant velocity
                if (onGround){
                    setVX(runspd);
                }
                else{
                    setVX(airspd);
                    // setVX(Math.min(airspd, airaccel + getVX()));
                }
                dir = RIGHT;
                
            }

            if (keys[UKey1] || keys[UKey2]){
                if (keys[UKey1]){
                    if (jump1){
                        // // setVY(getVY() - 20);
                        // addForce(new Force(0, -800, 1, 0));
                        setVY(-jumpforce);
                        jump1 = false;
                        onGround = false;
                    }
                }
                else if (keys[UKey2]){
                    if (jump2){
                        // addForce(new Force(0, -1000, 1, 0));
                        // setVY(getVY() - 20);
                        setVY(-jumpforce*.85);
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
        //System.out.println(onGround + " " + getVX() + " " + getVY());
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
        // System.out.println(stunTime);
        
    }

    public void applyForces(){

        //GROUND FRICTION
        if (onGround){
            if (getVX() < 0){ //moving left
                addVX(groundfriction);
            }
            else if (getVX() > 0){ //moving right
                addVX(-groundfriction);
            }
        }

        else{

            //AIR FRICTION
            if (getVX() < 0){ //moving left
                addVX(airfriction);
            }
            else{ //moving right or standing still
                addVX(-airfriction);
            }

            //GRAVITY
            setVY(Math.min(getVY() + gravity, fallspd));
        }

        if (Math.abs(getVX()) < 1){
            setVX(0);
        }

        for (int i=forces.size()-1; i>=0; i--){
            Force f = forces.get(i);
            addVX(f.magnitudeX);
            addVY(f.magnitudeY);
            f.addTime(-1);
            if (f.getTime() <= 0){
                forces.remove(i);
            }
        }
    }
    
    public void checkPlats(ArrayList<Platform> plats){
        //method to check if the player is on a platform and adjusts the Player accordingly
        Rectangle guyr = getRect();
		Rectangle guyrNoVY = new Rectangle((int)getX(),(int)(getY()-getVY()),WIDTH, HEIGHT);		

        onGround = false;
			
		for(Platform p: plats){
			// currently overlaps
			if(guyr.intersects(p.getRect())){
				// moving down
                onGround = true;
				if(getVY() > 0){
					// caused by moving down
					if(!guyrNoVY.intersects(p.getRect())){
						setY(p.getY()-HEIGHT);
                        setVY(0);
						onGround = true;						
					}					
				}
			}		
		}	
    }
    
    public void loseLife(){
        // lives--;
        setX(300); setY(30);
        setVX(0);
        setVY(0);
        setAX(0);
        setAY(0);
    }

    public void addHitBox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double KBX, double KBY, int stun){
        hitboxes.add(new Hitbox(X, Y, W, H, VX, VY, AX, AY, T, KBX, KBY, 10, stun));
    }

    public void addHitBox(Mover m, Force f, double w, double h, double time){
        hitboxes.add(new Hitbox(m.getX(), m.getY(), w, h, m.getVX(), m.getVY(), m.getAX(), m.getAY(), time, f.getMX(), f.getMY(), f.getTime(), f.getStun()));
    }

    public void addForce(double magX, double magY, int time, int stun){
        forces.add(new Force(magX, magY, time, stun));
    }
    public void addForce(Force f){
        forces.add(f);
    }

    public void addStun(int time){
        stunTime += time;
    }
    
    public Rectangle getRect(){return new Rectangle((int)getX(), (int)getY(), WIDTH+1, HEIGHT+1);}
    public int getCoolDown(){return atkCooldown;}
    public ArrayList <Hitbox> getHitBoxes(){return hitboxes;} 

    public int getFastKey(){return fastKey;}
    public int getUKey1(){return UKey1;}
    public int getDKey(){return DKey;}
    public int getDir(){return dir;}
    public int getStun(){return stunTime;}
    
    public void setLKey(int k){LKey = k;}
    public void setRKey(int k){RKey = k;}
    public void setUKey1(int k){UKey1 = k;}
    public void setUKey2(int k){UKey2 = k;}
    public void setDKey(int k){DKey = k;}
    public void setFastKey(int k){fastKey = k;}

    public void setCoolDown(int time){atkCooldown = time;}
}
