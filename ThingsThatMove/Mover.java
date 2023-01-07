package ThingsThatMove;
public class Mover {
    private double  x, //x coordinate
                    y, //y coordinate
                    vx, //velocity in the x direction
                    vy, //velocity in the y direction
                    ax, //acceleration in the x direction
                    ay, //acceleration in the x direction
                    maxVX, //maximum velocity in the x direction,
                    maxVY; //maximum velocity in the y direction

    public Mover(double X, double Y, double VX, double VY, double AX, double AY){
        x = X; y = Y;
        vx = VX; vy = VY;
        ax = AX; ay = AY;
        maxVX = Double.MAX_VALUE; maxVY = Double.MAX_VALUE;
    }

    public Mover(double X, double Y, double VX, double VY){
        x = X; y = Y;
        vx = VX; vy = VY;
        ax = 0; ay = 0;
        maxVX = Double.MAX_VALUE; maxVY = Double.MAX_VALUE;
    }

    public Mover(double X, double Y){
        x = X; y = Y;
        vx = 0; vy = 0;
        ax = 0; ay = 0;
        maxVX = Double.MAX_VALUE; maxVY = Double.MAX_VALUE;
    }

    public Mover(double X, double Y, double VX, double VY, double AX, double AY, double MAXVX, double MAXVY){
        x = X; y = Y;
        vx = VX; vy = VY;
        ax = AX; ay = AY;
        maxVX = MAXVX; maxVY = MAXVY;
    }

    public void move(){
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

    //setter methods
    public void setX(double X){x = X;}
    public void setY(double Y){y = Y;}
    public void setVX(double VX){vx = VX;}
    public void setVY(double VY){vy = VY;}
    public void setAX(double AX){ax = AX;}
    public void setAY(double AY){ay = AY;}

}