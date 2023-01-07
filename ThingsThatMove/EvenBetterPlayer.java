package ThingsThatMove;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import MainGame.*;
import AttackStuff.*;

public class EvenBetterPlayer extends Mover{
    public static final int LEFT = -1, RIGHT = 1;

    private int LKey, RKey, UKey1, UKey2, DKey, fastKey; //keys used to move in respective directions

    private double  weight, //weight () is a measure of how much a Player can resist knockback i.e. more weight => less knockback
                    airaccel, //airaccel (pixels / frame^2) is the rate a Player can change their horizontal velocity midair
                    airfriction, //airfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force midair
                    groundfriction, //groundfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force on the ground
                    airspd, //airspd (pixels / frame) is the maximum speed a Player can move horizontally midair
                    fallspd, //fallspd (units / frame) is the maximum rate a Player can move downward mid-air
                    gravity, //gravity (pixels / frame^2) is a measure of how fast a falling Player reaches fallspd
                    runspd, //runspd (pixels / frame) is the speed a Player moves on the ground horizontally (running)
                    jumpforce, //jumpforce (pixels / frame) is the force exerted on the Player when it jumps 
                    width, //width (pixels) is the number of pixels wide the Player is
                    height; //height (pixels) is the number of pixels high the Player is

    private int stunTime, dir, atkCooldown;

    private boolean jump1, //jump1 is true if the Player can use their first jump
                    jump2, //jump2 is true if the Player can use their second jump
                    onGround; //onGround is true if the Player is on a platform

    ArrayList <Hitbox> hitboxes; //hitboxes the Player sends out e.g. bullets
    private ArrayList<Force> forces; //Forces that act on Player

    private HashMap <String, Attack> attacks; //attacks in the form {name, Attack}

    public EvenBetterPlayer(double x, double y, HashMap<String, Double> stats, HashMap<String, Attack> atks){
        super(x, y);

        this.height = stats.get("height");
        this.width = stats.get("width");
        this.weight = stats.get("weight");
        this.airaccel = stats.get("airaccel");
        this.airfriction = stats.get("airfriction");
        this.groundfriction = stats.get("groundfriction");
        this.airspd = stats.get("airspd");
        this.fallspd = stats.get("fallspd");
        this.gravity = stats.get("gravity");
        this.runspd = stats.get("runspd");
        this.jumpforce = stats.get("jumpforce");

        attacks = atks;
        stunTime = 0;
        atkCooldown = 0;
        dir =1;
        onGround = false;
        hitboxes = new ArrayList<Hitbox>();
        forces = new ArrayList<Force>();
    }

    public void move(boolean [] keysPressed, int [] keysReleasedTime, ArrayList <Platform> plats){ //moves the Player
        if (keysPressed[32]){
            loseLife();
        }

        // this depends on if we want the players to die immediately after hitting the edge or not
        // rn it just resets player to starting pos... idk if that's what we want it to do
        // maybe add an invincibility period ?

        //player dies by going off-screen
        if (getX() <= 0 || getX()+width >= Gamepanel.WIDTH){
            loseLife();
            setX(300);
            setY(30);
            // System.out.println(lives);
        }
        if (getY() <= 0 || getY()+height >= Gamepanel.HEIGHT){
            loseLife();
            setX(300);
            setY(30);
            // System.out.println(lives);
        }

        keyBoardMovement(keysPressed, keysReleasedTime);

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

    public void keyBoardMovement(boolean [] keysPressed, int [] keysReleasedTime){
        if (stunTime <= 0){
            if (onGround){ //on temporary ground
                jump1 = true;
                jump2 = true;
                setVY(0);
            }

            if (1 <= keysReleasedTime[LKey] && keysReleasedTime[LKey] <= 10){
                setVX(0);
                dir = LEFT;
                keysReleasedTime[LKey] = 0;
            }
            else if (keysPressed[LKey]){ //moves left with constant velocity
                if (onGround){
                    setVX(Math.max(-runspd, getVX() - runspd));
                }
                else{
                    setVX(Math.max(-airspd, getVX() - airspd));
                    // setVX(-airspd);
                    // setVX(-Math.min(airspd, airaccel + getVX()));
                }
                dir = LEFT;
            }

            if (1 <= keysReleasedTime[RKey] && keysReleasedTime[RKey] <= 10){
                setVX(0);
                dir = RIGHT;
                keysReleasedTime[RKey] = 0;
            }
            else if (keysPressed[RKey]){ //moves right with constant velocity
                if (onGround){
                    setVX(Math.min(runspd, getVX() + runspd));
                }
                else{
                    setVX(Math.min(airspd, getVX() + airspd));
                    // setVX(airspd);
                    // setVX(Math.min(airspd, airaccel + getVX()));
                }
                dir = RIGHT;
                
            }

            if (keysPressed[UKey1] || keysPressed[UKey2]){
                if (keysPressed[UKey1]){
                    if (jump1){
                        // // setVY(getVY() - 20);
                        // addForce(new Force(0, -800, 1, 0));
                        setVY(-jumpforce);
                        jump1 = false;
                        onGround = false;
                    }
                }
                else if (keysPressed[UKey2]){
                    if (jump2){
                        // addForce(new Force(0, -1000, 1, 0));
                        // setVY(getVY() - 20);
                        setVY(-jumpforce*.85);
                        jump2 = false;
                        onGround = false;
                    }
                }
            }

            if (keysPressed[DKey]){
                // y+=7;
                // vy += 5;
            }
        }
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
            addVX(f.getMX());
            addVY(f.getMY());

            forces.remove(i);
            
        }
    }
    
    public void checkPlats(ArrayList<Platform> plats){
        //method to check if the player is on a platform and adjusts the Player accordingly
        Rectangle guyr = getRect();
		Rectangle guyrNoVY = new Rectangle((int)getX(),(int)(getY()-getVY()),(int)width, (int)height);		

        onGround = false;
			
		for(Platform p: plats){
			// currently overlaps
			if(guyr.intersects(p.getRect())){
				// moving down
                onGround = true;
				if(getVY() > 0){
					// caused by moving down
					if(!guyrNoVY.intersects(p.getRect())){
						setY(p.getY()-height);
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

    public void attack(boolean [] keysPressed){
        if (getCoolDown() <= 0){
            if (keysPressed[getFastKey()]){
                if (keysPressed[getUKey1()]){
                    attack(attacks.get("FastUpAtk"));
                }
                else if (keysPressed[getDKey()]){
                    attack(attacks.get("FastDownAtk"));
                }
                else{
                    attack(attacks.get("FastSideAtk"));
                }
            }
        }
    }

    public void attack(Attack a){
        
        for (Hitbox h : a.getHitboxes()){
            Hitbox toAdd = h.cloneHitbox();
            toAdd.setX(getX() - toAdd.getOffsetX());
            toAdd.setY(getY() - toAdd.getOffsetY());
            toAdd.setVX(toAdd.getVX()*dir);
            toAdd.setAX(toAdd.getAX()*dir);
            toAdd.setKnockBackX(toAdd.getKnockBackX()*dir);
            addHitBox(toAdd);
        }
        setCoolDown(a.getCoolDown());
    }

    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect((int)getX(), (int)getY(), (int)width, (int)height);
    }

    public void addHitBox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double KBX, double KBY, int stun){hitboxes.add(new Hitbox(X, Y, W, H, VX, VY, AX, AY, KBX, KBY, 10, stun));}
    public void addHitBox(Mover m, Force f, double w, double h, double time){hitboxes.add(new Hitbox(m.getX(), m.getY(), w, h, m.getVX(), m.getVY(), m.getAX(), m.getAY(), time, f.getMX(), f.getMY(), f.getStun()));}
    public void addHitBox(Hitbox h){hitboxes.add(h);}

    public void addForce(double magX, double magY, int stun){forces.add(new Force(magX, magY, stun));}
    public void addForce(Force f){forces.add(f);}

    public void addStun(int time){stunTime += time;}
    
    public Rectangle getRect(){return new Rectangle((int)getX(), (int)getY(), (int)width+1, (int)height+1);}

    public int getCoolDown(){return atkCooldown;}
    public int getFastKey(){return fastKey;}
    public int getUKey1(){return UKey1;}
    public int getDKey(){return DKey;}
    public int getDir(){return dir;}
    public int getStun(){return stunTime;}
    public ArrayList <Hitbox> getHitBoxes(){return hitboxes;} 
    
    public void setLKey(int k){LKey = k;}
    public void setRKey(int k){RKey = k;}
    public void setUKey1(int k){UKey1 = k;}
    public void setUKey2(int k){UKey2 = k;}
    public void setDKey(int k){DKey = k;}
    public void setFastKey(int k){fastKey = k;}
    public void setCoolDown(int time){atkCooldown = time;}
}