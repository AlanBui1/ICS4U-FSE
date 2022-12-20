public class Force {
    String name;
    double magnitudeX, magnitudeY;
    int time, origTime;

    public Force(double mx, double my, int t){
        magnitudeX = mx;
        magnitudeY = my; 
        time = t; //how long the vector will be in effect for
        origTime= t;
    }

    public void addTime(int t){
        time += t;
    }
    public String getName(){
        return name;
    }
    public int getTime(){
        return time;
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
}
