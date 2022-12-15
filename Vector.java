public class Vector {
    String name;
    double magnitude;
    int time;

    public Vector(String s, double v, int t){
        name = s; //name of vector
        magnitude = v; 
        t = time; //how long the vector will be in effect for
    }

    public void changeTime(int t){
        time -= t;
    }
    public String getName(){
        return name;
    }
    public int getTime(){
        return time;
    }
    public double getMagnitude(){
        return magnitude;
    }
}
