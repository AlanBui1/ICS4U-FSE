package GameObjects.Characters.Attacks;

//class for Forces that will act on Players
//Forces have magnitude, broken into x and y components as well as a stunTime, which is how long the Force will stun the Player affected

public class Force {
    double magnitudeX, //magnitude of the Force in the X direction
           magnitudeY; //magnitude of the Force in the Y direction

    int stunTime; //a measure of how long this Force will stun a Player

    public Force(double mx, double my, int stun){ 
        magnitudeX = mx;
        magnitudeY = my; 
        stunTime = stun;
    }

    public double getMX(){ //returns magnitude of the Force in the X direction
        return magnitudeX;
    }
    public double getMY(){ //returns magnitude of the Force in the Y direction
        return magnitudeY;
    }
    public int getStun(){ //returns how long this Force will stun a Player
        return stunTime;
    }

    public void setMX(double mx){ //sets the magnitude of the Force in the X direction
        magnitudeX = mx;
    }
    public void setMY(double my){ //sets the magnitude of the Force in the Y direction
        magnitudeY = my;
    }

    public static double knockBack(double base, double weight, double damage){ //returns the knockback with a certain base knockback, weight of Player receiving the knockback, and damage they are at
		return base * (100 / weight) * ((damage+10) / 100);
	}
}
