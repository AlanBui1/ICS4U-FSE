package GameObjects.ThingsThatMove.AttackStuff;
public class Force {
    double magnitudeX, magnitudeY;
    int origTime, stunTime;

    public Force(double mx, double my, int stun){
        magnitudeX = mx;
        magnitudeY = my; 
        stunTime = stun;
    }

    public double getMX(){
        return magnitudeX;
    }
    public double getMY(){
        return magnitudeY;
    }
    public int getOrigTime(){
        return origTime;
    }
    public int getStun(){
        return stunTime;
    }

    public void setMX(double mx){
        magnitudeX = mx;
    }
    public void setMY(double my){
        magnitudeY = my;
    }
}
