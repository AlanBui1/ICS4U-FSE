package GameObjects.ThingsThatMove;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import GameObjects.ThingsThatMove.AttackStuff.*;
import GameObjects.Stage;
import MainGame.*;
import Utility.Util;

public class Player extends Mover{
    public static final int LEFT = -1, RIGHT = 1;

    private boolean isComputer;

    private int LKey, //key used to move left
                RKey, //key used to move right
                UKey, //key used to move up
                DKey, //key used to move down
                fastKey, //key used to use the fast attack
                chargeKey, //key used to use the charged attack
                stunTime, //how long the Player is stunned for
                dir, //direction the Player is facing (-1 is LEFT, 1 is RIGHT)
                atkCooldown, //how long until the Player can attack again
                lives; //number of lives the Player has left

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

    private double  chargedMoveSize, 
                    damage;

    private boolean jump1, //jump1 is true if the Player can use their first jump
                    jump2, //jump2 is true if the Player can use their second jump
                    jump3, //jump3 is true if the Player can use their ChargeUpAtk 
                    onGround; //onGround is true if the Player is on a platform

    ArrayList <Hitbox> hitboxes; //hitboxes the Player sends out e.g. bullets
    private ArrayList<Force> forces; //Forces that act on Player

    private HashMap <String, Attack> attacks; //attacks in the form {name, Attack}

    public Player(double x, double y, HashMap<String, Double> stats, HashMap<String, Attack> atks, boolean cpu){
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
        isComputer = cpu;

        attacks = atks;
        stunTime = 0;
        atkCooldown = 0;
        chargedMoveSize= 0;
        damage = 0;
        dir =1;
        onGround = false;
        hitboxes = new ArrayList<Hitbox>();
        forces = new ArrayList<Force>();
        lives = 4;
    }

    public void loadKeyLayout(HashMap<String, Integer> keyVals){
        UKey = keyVals.get("UKey");
        DKey = keyVals.get("DKey");
        LKey = keyVals.get("LKey");
        RKey = keyVals.get("RKey");
        fastKey = keyVals.get("fastKey");
        chargeKey = keyVals.get("chargeKey");
    }

    public void move(boolean [] keysPressed, int [] keysReleasedTime, Stage stage){ //moves the Player
        if (isComputer){
            keysPressed[getRKey()] = false; keysPressed[getLKey()] = false;
        
            Platform curPlat = nearestPlat(stage);
            int midX = ((int)curPlat.getX() + (curPlat.getWidth()/2));
            
            if (Util.randint(1, 100) < 2) keysPressed[getUKey()] = Util.randBoolean(); //random jumps
            if (getY() > curPlat.getY()) keysPressed[getUKey()] = true; //jumps if below nearest platform

            if (!(midX - 50 <= getX() && getX() <= midX+1)){ //moves towards the middle of the nearest platform
                if (getX() < midX){
                    keysPressed[getRKey()] = true;
                }
                else if (getX() > midX){
                    keysPressed[getLKey()] = true;
                }
            }
        }
        if (keysPressed[32]){
            loseLife();
        }

        // this depends on if we want the players to die immediately after hitting the edge or not
        // rn it just resets player to starting pos... idk if that's what we want it to do
        // maybe add an invincibility period ?

        //player dies by going off-screen
        if (getX() <= 0 || getX()+width >= Gamepanel.WIDTH){
            loseLife();
            // System.out.println(lives);
        }
        if (getY() <= 0 || getY()+height >= Gamepanel.HEIGHT){
            loseLife();
            // System.out.println(lives);
        }

        keyBoardMovement(keysPressed, keysReleasedTime); //applies the keyboard movement

        applyForces(); 
        move();

        checkPlats(stage.getPlats()); //checks if on a platform and adjusts position 

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

    public void keyBoardMovement(boolean [] keysPressed, int [] keysReleasedTime){ //method to move the Player according to the keyboard movement by the user
        if (stunTime <= 0){
            if (onGround){ //if on a Platform
                jump1 = true; jump2 = true; jump3 = true; //enables all 3 jumps
                setVY(0); //stops falling
            }

            if ((1 <= keysReleasedTime[LKey] && keysReleasedTime[LKey] <= 10) || (1 <= keysReleasedTime[RKey] && keysReleasedTime[RKey] <= 10)){
                setVX(0); //stops moving in the X direction, brakes
                keysReleasedTime[LKey] = 0;
                keysReleasedTime[RKey] = 0;
            }

            if (keysPressed[LKey]){ //moves left with constant velocity
                if (onGround){
                    setVX(Math.max(-runspd, getVX() - runspd)); //velocity x goes at most the runspd
                }
                else{
                    setVX(Math.max(-airspd, getVX() - airspd)); //velocity x goes at most the airspd
                }
                dir = LEFT; //sets direction
            }
            if (keysPressed[RKey]){ //moves right with constant velocity
                if (onGround){
                    setVX(Math.min(runspd, getVX() + runspd)); //velocity x goes at most the runspd
                }
                else{
                    setVX(Math.min(airspd, getVX() + airspd)); //velocity x goes at most the airspd
                }
                dir = RIGHT; //sets direction
            }

            if (keysPressed[UKey]){
                if (jump1){
                    setVY(-jumpforce); //activates the first jump
                    if (keysPressed[DKey]) setVY(-jumpforce*.85); //shorter jump if the down key is pressed
                    jump1 = false; 
                    onGround = false;
                }
                else if (jump2 && getVY() > 0){ //if second jump is available and the Player is falling
                    setVY(-jumpforce*.95); //activates second jump
                    jump2 = false;
                    onGround = false;
                }
            }
        }
    }

    public void applyForces(){ //applies forces onto the Player
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

        if (Math.abs(getVX()) < 2){ //stops moving in the x direction if velocity is too small
            setVX(0);
        }

        for (int i=forces.size()-1; i>=0; i--){
            Force f = forces.get(i);
            addVX(f.getMX()); //applies the external Forces such as Hitboxes
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
				if(getVY() >= 0){
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
        lives--; //reduces number of lives of this Player
        setX(Gamepanel.WIDTH/2); setY(30); //goes back to starting position TO DO
        
        //reset values
        setVX(0); setVY(0);
        setAX(0); setAY(0);
        damage = 0;

        System.out.println(lives);
    }

    public void attack(boolean [] keysPressed, int [] keysReleasedTime){
        if (getCoolDown() > 0) return; //doesn't attack if still on cooldown

        if (isComputer){
            if (Util.randint(0, 4) %5 != 0) return; //randomly chooses not to attack

            if (Util.randBoolean()){ //randomly decides which move to use
                if (getCharge() < 50){ //charges move if it's not fully charged
                    chargeMove();
                }
                else{
                    attack("ChargeSideAtk"); //attacks if the move is fully charged
                }
            }
    
            else{
                attack("BonusAtk"); //attacks with fast attacks
                attack("FastSideAtk");
            }
            return;
        }
            
        if (1 <= keysReleasedTime[fastKey] && keysReleasedTime[fastKey] <= 10){
            if (keysReleasedTime[fastKey] <= 5) attack("BonusAtk", 1);
            
            if (keysPressed[UKey]){
                attack("FastUpAtk", 1);
            }
            else if (keysPressed[getDKey()]){
                attack("FastDownAtk", 1);
            }
            else{
                attack("FastSideAtk", 1);
            }

            keysReleasedTime[fastKey] = 0;
        }

        else if (1 <= keysReleasedTime[chargeKey] && keysReleasedTime[chargeKey] <= 10){
            if (keysPressed[DKey]){
                attack("ChargeDownAtk");
            }
            else attack("ChargeSideAtk");
            keysReleasedTime[chargeKey] = 0;
        }

        else if (keysPressed[chargeKey]){
            if (keysPressed[UKey]){
                if (jump3){
                    jump3 = false;
                    setVY(-jumpforce);
                    attack("ChargeUpAtk");
                }   
            }
            else chargeMove();
        }
    }

    public void attack(String atkName){
        double scale = 1;
        if (atkName.contains("Charge")){
            scale = chargedMoveSize/50;
            chargedMoveSize = 0;
        }
        attack(atkName, scale);
    }

    public void attack(String name, double factor){
        Attack a = attacks.get(name);
        for (Hitbox h : a.getHitboxes()){
            Hitbox toAdd = h.cloneHitbox();
            if (name.contains("Side")) toAdd.setX(getX() + (toAdd.getOffsetX()*(dir > 0 ? dir : 0)) - (toAdd.getWidth()/2)*factor*(dir > 0 ? 0 : 1));
            else toAdd.setX(getX() + toAdd.getOffsetX() - (toAdd.getWidth()/2)*factor);
            
            toAdd.setY(getY() + toAdd.getOffsetY() - (toAdd.getHeight()/2)*factor);
            toAdd.setVX(toAdd.getVX()*dir);
            toAdd.setAX(toAdd.getAX()*dir);
            toAdd.setKnockBackX(toAdd.getKnockBackX()*dir*factor);
            //for charged attacks
            toAdd.setWidth(toAdd.getWidth() * factor);
            toAdd.setHeight(toAdd.getHeight() * factor);
            toAdd.setDamage(toAdd.getDamage() * factor);
            
            addHitBox(toAdd);
        }
        setCoolDown(a.getCoolDown());
    }

    public Platform nearestPlat(Stage curStage){ //returns the nearest Platform to the Player in the stage 
        Platform nearestPlat = curStage.getMainPlat(); //by default is the main platform of the Stage
        ArrayList <Platform> plats = curStage.getPlats(); //platforms of the Stage
        double nearestDist = Util.taxicabDist(getCenterPoint(), nearestPlat.getCenterPoint()); //sets initial nearest distance

        for (Platform p : plats){ //loops through all Platforms of the Stage
            if (p.getInvis()) continue; //if the Platform is set to invisible, doesn't check it

            double distToPlat= Util.taxicabDist(getX(), getY(), p.getX(), p.getY()); //distance from Player to Platform
            if (distToPlat < nearestDist){
                //if the distance was less, sets new nearestDist and nearestPlat 
                nearestDist = distToPlat;
                nearestPlat = p;
            }
        }
        return nearestPlat; //returns the nearest Platform to the Player
    }

    public void draw(Graphics g, int xx, int yy){ //draws the Player
        g.setColor(Color.BLUE);
        if (chargedMoveSize == 50) g.setColor(Color.CYAN);
        if (stunTime > 0) g.setColor(Color.RED);
        g.fillRect((int)getX(), (int)getY(), (int)width, (int)height);

        for (Hitbox h : hitboxes){ //draws Hitboxes
			h.draw(g);
		}

		g.drawString(""+Util.fDouble(damage, 1), xx, yy); //draws Player percent
    }


    //adder methods????
    public void addHitBox(Hitbox h){hitboxes.add(h);}
    public void addForce(double magX, double magY, int stun){forces.add(new Force(magX, magY, stun));}
    public void addForce(Force f){forces.add(f);}
    public void addStun(int time){stunTime += time;}
    public void addDamage(double d){damage += d;}

    public void chargeMove(){chargedMoveSize = Math.min(chargedMoveSize+1, 50);}
    
    //getter methods
    public double getCharge(){return chargedMoveSize;}
    public Rectangle getRect(){return new Rectangle((int)getX(), (int)getY(), (int)width+1, (int)height+1);}
    public int getFastKey(){return fastKey;}
    public int getChargeKey(){return chargeKey;}
    public int getUKey(){return UKey;}
    public int getDKey(){return DKey;}
    public int getLKey(){return LKey;}
    public int getRKey(){return RKey;}
    public int getDir(){return dir;}
    public int getCoolDown(){return atkCooldown;}
    public int getStun(){return stunTime;}
    public int getLives(){return lives;}
    public double getWeight(){return weight;}
    public double getDamage(){return damage;}
    public boolean getOnGround(){return onGround;}
    public Point getCenterPoint(){return new Point((int)(getX() + width/2), (int)(getY() + height/2));}
    public ArrayList <Hitbox> getHitBoxes(){return hitboxes;} 
    
    //setter methods
    public void setLKey(int k){LKey = k;}
    public void setRKey(int k){RKey = k;}
    public void setUKey(int k){UKey = k;}
    public void setDKey(int k){DKey = k;}
    public void setFastKey(int k){fastKey = k;}
    public void setChargeKey(int k){chargeKey = k;}
    public void setCoolDown(int time){atkCooldown = time;}
    public void setKeyPressed(int k, boolean [] keysPressed, boolean b){keysPressed[k] = b;}
    public void setStun(int time){stunTime = time;}
}
