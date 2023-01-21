package GameObjects.Characters.Attacks;
import java.awt.*;
import java.util.HashMap;

import GameObjects.Characters.Player;
import Utility.Mover;

//class for Hitboxes
//Hitboxes are able to move using methods inherited from the Mover class
//Hiboxes have their own move() which takes into consideration the time that the Hitbox has been active for before calling Mover's move()

public class Hitbox extends Mover{
    private double  width, //how wide the Hitbox is
                    height, //how tall the Hitbox is
                    time, //how long the Hitbox is active for
                    startOffsetX, //how far away in the X-direction from the top-left corner of the Player the Hitbox will be launched 
                    startOffsetY, //how far away in the Y-direction from the top-left corner of the Player the Hitbox will be launched 
                    baseKnockbackX, //base knockback in the x direction
                    baseKnockbackY, //base knockback in the y direction
                    damage, //how much damage this Hitbox does if the opponent gets hit by it
                    invisTime; //how long the Hitbox is inactive for

    private HashMap<String, Double> data; //HashMap of all the information needed to clone the Hitbox

    private int stunTime; //how long the Hitbox will stun the opponent for
    private String name; //name of Hitbox
    private Player player; //Player that uses the Hitbox

    public Hitbox(HashMap <String, Double> stats){ //constructor that initializes fields using a HashMap with all the necessary information
        super(stats);
        
        width = stats.get("width");
        height = stats.get("height");
        time = stats.get("timeactive");
        startOffsetX = stats.get("startoffsetx");
        startOffsetY = stats.get("startoffsety");
        baseKnockbackX = (double)stats.get("basekbx");
        baseKnockbackY = (double)stats.get("basekby");
        stunTime = (int)(double)stats.get("stuntime");
        damage = stats.get("damage");
        invisTime = stats.get("invisTime") == null ? -1 : stats.get("invisTime");

        data = stats;
    }

    //returns a new Hitbox with the same data
    public Hitbox cloneHitbox(){
        return new Hitbox(data);
    }
    
    //getter methods
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)width, (int)height);}
    public double getOffsetX(){return startOffsetX;}
    public double getOffsetY(){return startOffsetY;}
    public double getWidth(){return width;}
    public double getHeight(){return height;}
    public double getKnockBackX(){return baseKnockbackX;}
    public double getKnockBackY(){return baseKnockbackY;}
    public double getDamage(){return damage;}
    public int getStun(){return stunTime;}    
    public double getInvis(){return invisTime;} 

    //setter methods
    public void setWidth(double W){width= W;}
    public void setHeight(double H){height = H;}
    public void setKnockBackX(double kb){baseKnockbackX = kb;}
    public void setKnockBackY(double kb){baseKnockbackY = kb;}
    public void setDamage(double d){damage = d;}
    public void setName(String n){name = n;}
    public void setPlayer(Player p){player = p;}
    public void addTime(double t){time += t;}
    public void lowerInvis(){invisTime--;}

    @Override
    public void move(){
        time --; //reduces the time the Hitbox is active for
        if (time < 0){  //does not move if the Hitbox's time is up
            return;
        }
        super.move(); //moves with Mover's move method
    }

    public void draw(Graphics g){ //draws the Hitbox
        g.setColor(Color.GREEN);

        int dir = player.getDir();
        // drawing for projectiles of shooter
        if (player.getType().equals("shooter")){
            if (name.equals("FastSideAtk")){
                if (invisTime < 0){
                    Image projectileImg = player.getFrames().get(name + "Projectile")[0]; // gets correct frame for projectile (projectile is constant, only one frame)
                    if (dir == Player.LEFT){  // adjusts based on direction facing so arrow's starting position is correct offset
                        // subtracts width and height from x and y coordinates respectively to "shift" starting position 
                        g.drawImage(projectileImg, (int)this.getX()-projectileImg.getWidth(null), (int)(this.getY()), 64, 6, null);
                    }
                    else{
                        g.drawImage(projectileImg, (int)this.getX(), (int)(this.getY()), 64, 6, null);
                    }
                }
            }
        }
    }
}
