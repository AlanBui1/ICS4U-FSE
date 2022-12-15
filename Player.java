import java.awt.*;
import java.util.ArrayList;

public class Player{
    private double x, y, vx, vy;
    private int w, h;
    private ArrayList<Vector> accelX, accelY;
    private int LKey, RKey, UKey1, UKey2, DKey, shootKey;
    private boolean jump1, jump2, onGround;

    public Player(int xx, int yy){
        x = xx;
        y = yy;
        w = 10;
        h = 20;
        jump1 = false;
        jump2 = false;
        onGround = false;
        
        accelX = new ArrayList<Vector>();
        accelY = new ArrayList<Vector>();
    }

    public void move(boolean [] keys, ArrayList <Platform> plats){
        // checkPlats(plats);

        if (onGround){ //on temporary ground
            jump1 = true;
            jump2 = true;
            vy = 0;
        }
        if (keys[LKey]){
            x-=7;
        }
        if (keys[RKey]){
            x+=7;
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

            if (ay.getName() == "Gravity"){
                if (!onGround){
                    vy += ay.getMagnitude();
                }
                continue;
            }

            vy += ay.getMagnitude();
            ay.changeTime(-1);
            if (ay.getTime() < 0){
                accelY.remove(i);
            }
        }

        if (vy > 10){
            vy = 10;
        }
        
        x += vx;
        y += vy;

        checkPlats(plats);
        // System.out.println(onGround);
    }

    public void checkPlats(ArrayList<Platform> plats){
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

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect((int)x, (int)y, w, h);
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
    
    public void addAccel(String name, double magnitude, int time, String dir){
        if (dir == "X") accelX.add(new Vector(name, magnitude, time));
        if (dir == "Y") accelY.add(new Vector(name, magnitude, time));
    }

}

