import java.awt.*;
import java.util.ArrayList;

public class Player{
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    private double x, y, vx, vy, gravity;

    /*
    (x, y) are the Player's coordinates on the screen 
    vx and vy are the Player's velocity in the x and y directions, respectively
    gravity is the force of gravity acting on Player when they aren't onGround
    */

    private int w, h, lives;

    /*
    w is the width of the Player
    h is the height of the Player
    lives is the number of lives the player has
    */

    private ArrayList<Vector> accelX, accelY;
    private int LKey, RKey, UKey1, UKey2, DKey, shootKey;
    private boolean jump1, jump2, onGround;

    /*
    jump1 is true if the Player can use their first jump
    jump2 is true if the Player can use their second jump
    onGround is true if the Player is on a platform
    */

    private String type;
    private int dir;

    public Player(int xx, int yy, int direct, int numLives, double g){
        x = xx;
        y = yy;
        w = 10;
        h = 20;
        jump1 = false;
        jump2 = false;
        onGround = false;
        lives = numLives;
        gravity = g;
        // type = t;
        
        accelX = new ArrayList<Vector>();
        accelY = new ArrayList<Vector>();
        dir = direct;
    }

    public void move(boolean [] keys, ArrayList <Platform> plats){ //moves the Player
        // this depends on if we want the players to die immediately after hitting the edge or not
        // rn it just resets player to starting pos... idk if that's what we want it to do
        // maybe add an invincibility period ?

        //player dies by going off-screen
        if (x <= 0 || x+w >= Gamepanel.WIDTH){
            loseLife();
            x = 300;
            y = 30;
            System.out.println(lives);
        }
        if (y <= 0 || y+h >= Gamepanel.HEIGHT){
            loseLife();
            x = 300;
            y = 30;
            System.out.println(lives);
        }

        if (onGround){ //on temporary ground
            jump1 = true;
            jump2 = true;
            vy = 0;
        }

        if (keys[LKey]){ //moves left with constant velocity
            x-=7;
            dir = LEFT;
        }
        if (keys[RKey]){ //moves right with constant velocity
            x+=7;
            dir = RIGHT;
        }

        if (keys[UKey1]){
            if (jump1){
                vy = -20;
                jump1 = false;
                onGround = false;
            }
        }
        if (keys[UKey2]){
            if (jump2){
                vy = -20;
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
        for (int i=accelX.size()-1; i>=0; i--){ 
            Vector ax = accelX.get(i);
            vx += ax.getMagnitude();
            ax.changeTime(-1);
            if (ax.getTime() < 0){
                accelX.remove(i);
            }
        }
        for (int i=accelY.size()-1; i>=0; i--){
            Vector ay = accelY.get(i);
            vy += ay.getMagnitude();
            ay.changeTime(-1);
            if (ay.getTime() < 0){
                accelY.remove(i);
            }
        }

        if (!onGround){
            vy += gravity;
        }

        if (vy > 10){
            vy = 10;
        }
        
        x += vx;
        y += vy;

        checkPlats(plats); //checks if on a platform and adjusts position 
    }

    public void checkPlats(ArrayList<Platform> plats){
        //method to check if the player is on a platform and adjusts the Player accordingly
        Rectangle guyr = new Rectangle((int)x,(int)y,w,h);
		Rectangle guyrNoVY = new Rectangle((int)x,(int)(y-vy),w,h);		

        onGround = false;
			
		for(Platform p: plats){
			// currently overlaps
			if(guyr.intersects(p.getRect())){
				// moving down
                onGround = true;
				if(vy > 0){
					// caused by moving down
					if(!guyrNoVY.intersects(p.getRect())){
						y = p.getY()-h;
                        
						vy = 0;
						onGround = true;						
					}					
				}
			}		
		}	
    }

    public double shoot(ArrayList<Bullet>bullets, boolean [] keys, int shootCoolDown){
        if(keys[shootKey] && shootCoolDown <= 0){ 
            // if a shot can be taken, space pressed and cool down is complete x position is returned
            return x;
        }
        else{
            return -1;
        }
    }

    public void loseLife(){
        lives--;
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect((int)x, (int)y, w, h);
    }

    public Rectangle getRect(){ // returns Rectangle
        return new Rectangle((int)x, (int)y, w, h);
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
    public void setShootKey(int k){
        shootKey = k;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    public int getDir(){
        return dir;
    }

    public int getLives(){
        return lives;
    }

    // TEMP get rid of this if width for players is remaining the same for all characters
    public int getW(){
        return w;
    }
    
    public void addAccel(String name, double magnitude, int time, String dir){
        if (dir == "X") accelX.add(new Vector(name, magnitude, time));
        if (dir == "Y") accelY.add(new Vector(name, magnitude, time));
    }

}

