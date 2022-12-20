public class Mover {
    private double x, y, vx, vy, ax, ay;

    public Mover(double X, double Y, double VX, double VY, double AX, double AY){
        x = X;
        y = Y;
        vx = VX;
        vy = VY;
        ax = AX;
        ay = AY;
    }

    public void move(){
        vx += ax;
        x += vx;
        vy += ay;
        y += vy;
    }

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
    public void setAX(double AX){vx = AX;}
    public void setAY(double AY){vy = AY;}

}
