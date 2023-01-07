package AttackStuff;
public class Force {
    String name;
    double magnitudeX, magnitudeY;
    int origTime, stunTime;

    public Force(double mx, double my, int stun){
        magnitudeX = mx;
        magnitudeY = my; 
        stunTime = stun;
    }

    public String getName(){
        return name;
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
