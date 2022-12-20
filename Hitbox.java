import java.awt.*;
public class Hitbox extends Mover{
    private double w, h, time, kbx, kby;
    private Force knockBack;

    public Hitbox(double X, double Y, double W, double H, double VX, double VY, double AX, double AY, double T, double knockBackX, double knockBackY, int knockBackTime){
        super(X, Y, VX, VY, AX, AY);

        w = W; h = H;
        time = T;
        knockBack = new Force(knockBackX, knockBackY, knockBackTime);
        kbx = knockBackX;
        kby = knockBackY;
    }

    
    public double getTime(){return time;};
    public Rectangle getRect(){return new Rectangle((int)this.getX(), (int)this.getY(), (int)w, (int)h);}
    public Force getForce(){return knockBack;}

    public void addTime(double t){
        time += t;
    }

    @Override
    public void move(){
        time --;
        if (time < 0){
            return;
        }
        super.move();
    }

    public void draw(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect((int)this.getX(), (int)this.getY(), (int)w, (int)h);
    }
}
