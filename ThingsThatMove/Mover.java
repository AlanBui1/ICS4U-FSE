package ThingsThatMove;
public class Mover {
    private double x, y, vx, vy, ax, ay;
    /*
    (x, y) are the Player's coordinates on the screen 
    vx and vy are the Player's velocity in the x and y directions, respectively
    */

    public Mover(double X, double Y, double VX, double VY, double AX, double AY){
        x = X;
        y = Y;
        vx = VX;
        vy = VY;
        ax = AX;
        ay = AY;
    }

    public Mover(double X, double Y, double VX, double VY){
        x = X;
        y = Y;
        vx = VX;
        vy = VY;
        ax = 0;
        ay = 0;
    }

    public Mover(double X, double Y){
        x = X;
        y = Y;
        vx = 0;
        vy = 0;
        ax = 0;
        ay = 0;
    }

    public void move(){
        vx += ax;
        x += vx;
        vy += ay;
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

    //setter methods
    public void setX(double X){x = X;}
    public void setY(double Y){y = Y;}
    public void setVX(double VX){vx = VX;}
    public void setVY(double VY){vy = VY;}
    public void setAX(double AX){ax = AX;}
    public void setAY(double AY){ay = AY;}

}
