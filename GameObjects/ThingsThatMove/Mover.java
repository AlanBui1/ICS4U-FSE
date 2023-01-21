package GameObjects.ThingsThatMove;

import java.util.HashMap;

//class for all Objects that move in the game
//acceleration and velocity are broken down into x and y components
public class Mover {
    private double  x, //x coordinate
                    y, //y coordinate
                    vx, //velocity in the x direction
                    vy, //velocity in the y direction
                    ax, //acceleration in the x direction
                    ay, //acceleration in the y direction
                    maxVX, //maximum velocity in the x direction,
                    maxVY; //maximum velocity in the y direction

    public Mover(double X, double Y){
        x = X; y = Y;
        vx = 0; vy = 0;
        ax = 0; ay = 0;
        maxVX = Double.MAX_VALUE; maxVY = Double.MAX_VALUE;
    }

    public Mover(HashMap <String, Double> stats){
        vx = stats.get("vx");
        vy = stats.get("vy"); 
        ax = stats.get("ax"); 
        ay = stats.get("ay"); 
        maxVX = stats.get("maxvx"); 
        maxVY = stats.get("maxvy");
    }

    public void move(){ //method to change the location of the Mover according to its velocity and acceleration
        if (Math.abs(vx + ax) < Math.abs(maxVX)) vx += ax;
        if (Math.abs(vy + ay) < Math.abs(maxVY)) vy += ay;
        x += vx;
        y += vy;
    }

    //methods to add to vals
    public void addX(double X){x += X;}
    public void addY(double Y){y += Y;}
    public void addAX(double x){ax += x;}
    public void addVX(double x){vx += x;}
    public void addAY(double Y){ay += Y;}
    public void addVY(double Y){vy += Y;}
    
    //getter methods
    public double getX(){return x;}
    public double getY(){return y;}
    public double getVX(){return vx;}
    public double getVY(){return vy;}
    public double getAX(){return ax;}
    public double getAY(){return ay;}
    public double getMAXVX(){return maxVX;}
    public double getMAXVY(){return maxVY;}

    //setter methods
    public void setX(double X){x = X;}
    public void setY(double Y){y = Y;}
    public void setVX(double VX){vx = VX;}
    public void setVY(double VY){vy = VY;}
    public void setAX(double AX){ax = AX;}
    public void setAY(double AY){ay = AY;}

}
