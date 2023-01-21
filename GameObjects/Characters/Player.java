package GameObjects.Characters;

import java.util.*;
import java.awt.*;

import GameObjects.Characters.Attacks.*;
import GameObjects.Stages.Platform;
import GameObjects.Stages.Stage;
import MainGame.*;
import Utility.Mover;
import Utility.Util;

//class for Player Objects
//Players move according to keyboard movement  
//Players have a variety of fields that are used for dealing with the physics of the game
//Main things Players can do are: move, draw its Image on the screen, and attack
//Players' movement and attacks can even be controlled by the program!

public class Player extends Mover{
    public static final int LEFT = -1, RIGHT = 1;
	public static final String [] keyNames = {"UKey", "DKey", "LKey", "RKey", "fastKey", "chargeKey", "shieldKey"}; //names of keys that Player used
    public static final double maxCharge = 50; //the maximum amount a move can be charged for

    private boolean isComputer; //true if the Player should be computer or not

    private int LKey, //key used to move left
                RKey, //key used to move right
                UKey, //key used to move up
                DKey, //key used to move down
                fastKey, //key used to use the fast attack
                chargeKey, //key used to use the charged attack
                shieldKey, //key used to activate shield
                stunTime, //how long the Player is stunned for
                dir, //direction the Player is facing (-1 is LEFT, 1 is RIGHT)
                atkCooldown, //how long until the Player can attack again
                lives, //number of lives the Player has left
                shieldTime; //how long the Player has shield active for

    private double  weight, //weight () is a measure of how much a Player can resist knockback i.e. more weight => less knockback
                    airfriction, //airfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force midair
                    groundfriction, //groundfriction (units / frame^2) is a measure of how long it takes a character to stop moving from a sideways force on the ground
                    airspd, //airspd (pixels / frame) is the maximum speed a Player can move horizontally midair
                    fallspd, //fallspd (units / frame) is the maximum rate a Player can move downward mid-air
                    gravity, //gravity (pixels / frame^2) is a measure of how fast a falling Player reaches fallspd
                    runspd, //runspd (pixels / frame) is the speed a Player moves on the ground horizontally (running)
                    jumpforce, //jumpforce (pixels / frame) is the force exerted on the Player when it jumps 
                    width, //width (pixels) is the number of pixels wide the Player is
                    height, //height (pixels) is the number of pixels high the Player is
                    frameNum, // frameNum is current frame that the player should be showing
                    offsetX, // offsetX is the offset in the x direction of the player's images
                    offsetY; // offsetY is the offset in the y direction of the player's images

    private double  chargedMoveSize, //how long the charged move was charged for
                    damage, //the damage taken by the Player
                    damageDealt, //how much damage the Player dealt
                    damageTaken; //how much damage the Player took

    private boolean jump1, //jump1 is true if the Player can use their first jump
                    jump2, //jump2 is true if the Player can use their second jump
                    jump3, //jump3 is true if the Player can use their ChargeUpAtk 
                    onGround; //onGround is true if the Player is on a platform

    private String type, state;

    ArrayList <Hitbox> hitboxes; //hitboxes the Player sends out e.g. bullets
    private ArrayList<Force> forces; //Forces that act on Player

    private HashMap <String, Attack> attacks; //attacks in the form {name, Attack}
    private HashMap <String, Image[]> frames; // hashmap that stores all frames {name of action, Image array of all frames}
    private HashMap <String, Integer> actions; // holds all possible


    public Player(double x, 
                  double y, 
                  HashMap<String, Double> stats, 
                  HashMap<String, Attack> atks, 
                  boolean cpu, 
                  String charType, 
                  String playerState, 
                  int playerFrameNum){

        super(x, y);

        height = stats.get("height");
        width = stats.get("width");
        weight = stats.get("weight");
        airfriction = stats.get("airfriction");
        groundfriction = stats.get("groundfriction");
        airspd = stats.get("airspd");
        fallspd = stats.get("fallspd");
        gravity = stats.get("gravity");
        runspd = stats.get("runspd");
        jumpforce = stats.get("jumpforce");
        offsetX = stats.get("offsetX");
        offsetY = stats.get("offsetY");
        isComputer = cpu;

        attacks = atks;
        stunTime = 0;
        shieldTime = 0;
        atkCooldown = 0;
        chargedMoveSize= 0;
        damage = 0;
        damageDealt = 0;
        damageTaken = 0;
        dir =1;
        onGround = false;
        hitboxes = new ArrayList<Hitbox>();
        forces = new ArrayList<Force>();
        lives = 4;
        type = charType;
        state = playerState;
        frameNum = playerFrameNum;
        frames = Util.loadFrames(this, atks);
    }

    public void loadKeyLayout(HashMap<String, Integer> keyVals){ //sets the key data from a HashMap
        UKey = keyVals.get("UKey");
        DKey = keyVals.get("DKey");
        LKey = keyVals.get("LKey");
        RKey = keyVals.get("RKey");
        fastKey = keyVals.get("fastKey");
        chargeKey = keyVals.get("chargeKey");
        shieldKey = keyVals.get("shieldKey");
    }

    public void move(boolean [] keysPressed, int [] keysReleasedTime, Stage stage){ //moves the Player
        if (isComputer){ //CPU movement
            keysPressed[RKey] = false; keysPressed[LKey] = false; //doesnt move left or right
        
            Platform nearPlat = nearestPlat(stage); //nearest Platform to the Player
            int midX = ((int)nearPlat.getX() + (nearPlat.getWidth()/2)); //x coordinate of the middle of the nearest Platform to the Player
            
            if (Util.randint(1, 100) < 2) keysPressed[UKey] = Util.randBoolean(); //random jumps
            if (getY() > nearPlat.getY()) keysPressed[UKey] = true; //jumps if below nearest platform

            if (!(midX - 50 <= getX() && getX() <= midX+1)){ //presses keys to get the Player closer to the middle of the nearest platform
                if (getX() < midX){
                    keysPressed[RKey] = true;
                }
                else if (getX() > midX){
                    keysPressed[LKey] = true;
                }
            }
        }

        // player dies by going off-screen
        if (getX() <= -20 || getX()+width >= Gamepanel.WIDTH || getY() <= 0 || getY()+height >= Gamepanel.HEIGHT){
            loseLife();
        }

        keyBoardMovement(keysPressed, keysReleasedTime); //applies the keyboard movement
        applyForces(); //applies forces acting on the Player
        move(); //moves according to acceleration and velocity

        checkPlats(stage.getPlats()); //checks if on a platform and adjusts position 

        ArrayList <Hitbox> toDel = new ArrayList<Hitbox>(); //Hitboxes to remove if they're time active is done

        for (Hitbox h : hitboxes){ //loops through Player's Hitboxes
            if (h.getInvis() <= 0) h.move();  //moves Hitbox
            if (h.getTime() <= 0){ //if time active is done
                toDel.add(h); //adds to toDel to be deleted
            }
        }
        for (Hitbox h : toDel){ //removes all Hitboxes that should be deleted
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
        setX(Gamepanel.WIDTH/2); setY(30); //goes back to starting position
        
        //reset values
        setVX(0); setVY(0);
        setAX(0); setAY(0);
        damage = 0;

        System.out.println(lives);
    }

    public void attack(boolean [] keysPressed, int [] keysReleasedTime){
        if (stunTime > 0 || atkCooldown > 0) return; //doesn't attack if still on cooldown or is stunned

        if (isComputer){
            if (Util.randint(0, 4) %5 != 0) return; //randomly chooses not to attack

            if (Util.randBoolean()){ //randomly decides which move to use
                if (getCharge() < maxCharge){ //charges move if it's not fully charged
                    chargeMove();
                }
                else{
                    attack("ChargeSideAtk"); //attacks if the move is fully charged
                    state = "ChargeSideAtk";
                }
            }
    
            else{
                //attacks with fast attacks
                attack("FastSideAtk");
                state = "FastSideAtk";
            }
            return;
        }

        if (keysPressed[shieldKey]){ //activates shield
            Attack shieldMove = attacks.get("Shield");
            state = "Shield";
            stunTime = shieldMove.getnumFrames() * 2;
            atkCooldown = shieldMove.getnumFrames() * 2;
            shieldTime = shieldMove.getnumFrames() * 2;
        }
            
        else if (1 <= keysReleasedTime[fastKey] && keysReleasedTime[fastKey] <= 10){ //attacks if the fast key was released fast enough
            if (keysPressed[UKey]){
                attack("FastUpAtk", attacks.get("FastUpAtk"), 1);
                state = "FastUpAtk";
            }
            else if (keysPressed[DKey]){
                attack("FastDownAtk", attacks.get("FastDownAtk"), 1);
            }
            else{

                attack("FastSideAtk", attacks.get("FastSideAtk"), 1);
                state = "FastSideAtk";
                frameNum = 0;
             
            }

            keysReleasedTime[fastKey] = 0;
        }

        else if (1 <= keysReleasedTime[chargeKey] && keysReleasedTime[chargeKey] <= 10){//attacks if the charge key was released fast enough
            if (!onGround){
                return;
            } 
            if (keysPressed[DKey]){
                attack("ChargeDownAtk");
                state = "ChargeDownAtk";
            }
            else{
                attack("ChargeSideAtk");
                state = "ChargeSideAtk";
            }
            keysReleasedTime[chargeKey] = 0;
        }

        else if (keysPressed[chargeKey]){ //charges if the charge key was held
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

    public void attack(String atkName){ //attacks given an attack name
        double scale = 1; //how much values should be scaled by, depending on if it's a charged attack and if so, how long it was charged for
        if (atkName.contains("Charge")){
            scale = chargedMoveSize/maxCharge;
            chargedMoveSize = 0;
        }
        attack(atkName, attacks.get(atkName), scale); //attacks using other method
    }

    public void attack(String name, Attack atk, double scale){
        stunTime += atk.getnumFrames()*2; //stuns the attacker to prevent the Player from moving while attacking
        for (Hitbox h : atk.getHitboxes()){ //Loops through the Attack's hitboxes
            Hitbox toAdd = h.cloneHitbox(); //makes a clone of the Hitbox
            toAdd.setName(name);
            toAdd.setPlayer(this);

            //puts the Hitbox where it belongs
            if (type.equals("bladekeeper") && (name.contains("Down"))){
                toAdd.setX(getX() + (toAdd.getOffsetX()*(dir > 0 ? dir : 0)) - (toAdd.getWidth()/2)*(dir > 0 ? 0 : 1));
            }
            else if (type.equals("shooter") && name.equals("FastUpAtk")){
                toAdd.setX(getX());
            }
            else if (type.equals("shooter") && name.equals("ChargeDownAtk")){ //this is a unique attack to the shooter
                chargedMoveSize = maxCharge; //fully charges the attack bar
                damage += 5; //takes damage
            }
            else toAdd.setX(getX() + (toAdd.getOffsetX()*(dir > 0 ? dir : 0)) - (toAdd.getWidth())*(dir > 0 ? 0 : 1));
            toAdd.setY(getY() + toAdd.getOffsetY());

            //sets acceleration, velocity, and knockback depending on which way the character is facing/attacking
            toAdd.setVX(toAdd.getVX()*dir);
            toAdd.setAX(toAdd.getAX()*dir);
            //scales the knockback and damage by how charged the move was
            toAdd.setKnockBackX(toAdd.getKnockBackX()*dir*scale);
            toAdd.setKnockBackY(toAdd.getKnockBackY()*dir*scale);
            toAdd.setDamage(toAdd.getDamage() * scale);
            
            addHitBox(toAdd);
        }
        setCoolDown(atk.getCoolDown()); //sets the attack cooldown
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

    public void draw(Graphics g, int xx, int yy){ //draws the Player according to what action it's doing
         
        if (state.equals("Run") || state.equals("Jump") || state.equals("Fall") || state.equals("Idle")){
            if (getVX() != 0 && getVY() == 0 && state.equals("Idle")){
                state = "Run";
                frameNum = 0;
            }
            else if (getVY() < 0){
                state = "Jump";
                frameNum = 0;
            }
            else if (getVY() > 0){
                state = "Fall";
                frameNum = 0;
            }
            else if (getVX() == 0 && getVY() == 0){
                state = "Idle";
                frameNum = 0;
            }
        }

        if (dir == RIGHT){
            g.drawImage(frames.get(state)[(int)frameNum], (int)(getX()+offsetX), (int)(getY()+offsetY), null);
        }
        else{
            // divide by 2 is just to "slow down" frame rate of character
            int imgWidth = (frames.get(state)[(int)frameNum/2]).getWidth(null);
            int imgHeight = (frames.get(state)[(int)frameNum/2]).getHeight(null);
            g.drawImage(frames.get(state)[(int)frameNum], (int)(getX()+imgWidth+offsetX), (int)(getY()+offsetY), -(int)(imgWidth), (int)(imgHeight), null);
        } 

        for (Hitbox h : hitboxes){ //draws Hitboxes
			h.draw(g);
		}
        g.setColor(Color.BLUE);
		g.drawString(""+Util.fDouble(damage, 1), xx, yy); //draws Player percent
    }

    public void frameIncrease(){
        if (frameNum >= (actions.get(state)-1)){
            if (state.equals("Run") || state.equals("Idle")){
                frameNum = 0;
            }
            else{
                state = "Idle";
                frameNum = 0;
            }
        }
        else{
            frameNum += 0.5;
        }
    }

    //adder methods????
    public void addHitBox(Hitbox h){hitboxes.add(h);} //adds a Hitbox to the Player's ArrayList of Hitboxes
    public void addForce(Force f){forces.add(f);} //adds a Force to the Player's ArrayList of Forces
    public void addStun(int time){stunTime += time;}
    public void addDamage(double d){damage += d;}
    public void addTakenDamage(double d){damageTaken += d;}
    public void addDealtDamage(double d){damageDealt += d;}
    public void chargeMove(){chargedMoveSize = Math.min(chargedMoveSize+1, maxCharge);}
    
    //getter methods
    public double getCharge(){return chargedMoveSize;}
    public Rectangle getRect(){return new Rectangle((int)getX(), (int)getY(), (int)width+1, (int)height+1);}
    public int getCoolDown(){return atkCooldown;}
    public int getStun(){return stunTime;}
    public int getShieldTime(){return shieldTime;}
    public int getLives(){return lives;}
    public double getWeight(){return weight;}
    public double getDamage(){return damage;}
    public boolean getOnGround(){return onGround;}
    public boolean getCPU(){return isComputer;}
    public Point getCenterPoint(){return new Point((int)(getX() + width/2), (int)(getY() + height/2));}
    public String getType(){return type;}
    public double getWidth(){return width;}
    public int getDir(){return dir;}
    public ArrayList <Hitbox> getHitBoxes(){return hitboxes;} 
    public HashMap <String, Image[]> getFrames(){return frames;} 
    
    
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
    public void setShieldTime(int time){shieldTime = time;}
    public void setCPU(boolean b){isComputer = b;}
    public void setActions(HashMap <String, Integer> allActions){actions = allActions;}
    public void setType(String t){type = t;}
}
