import java.awt.*;
public class Hitbox {
    private double x, y, vx, vy, ax, ay, w, h, time;

    public Hitbox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T){
        x = X; y = Y;
        vx = VX; vy = VY;
        ax = AX; ay = AY;
        w = W; h = H;
        time = T;
    }

    public double getX(){return x;}
    public double getY(){return y;}
    public double getVX(){return vx;}
    public double getVY(){return vy;}
    public double getAX(){return ax;}
    public double getAY(){return ay;}
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)x, (int)y, (int)w, (int)h);}

    public void addTime(double t){
        time += t;
    }

    public void move(){
        time --;
        if (time < 0){
            return;
        }
        vx += ax;
        x += vx;
        vy += ay;
        x += vy;
    }

    public void draw(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect((int)x, (int)y, (int)w, (int)h);
    }
}
