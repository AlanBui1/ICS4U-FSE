import java.util.*;

//class for the shooter character inherits from Player class

public class Shooter extends Player{
    public Shooter(int xx, int yy, int direct, int numLives, double g){
        super(xx, yy, direct, numLives, g); //gives these values to the Player constructor
    }

    public void shoot(boolean [] keys){
        if (this.getCoolDown() < 0 && keys[this.getShootKey()]){
            this.addHitBox(this.getX(), this.getY(), 7.0, 7.0, 10.0*this.getDir(), 0, 0, 0, 30);
            this.setCoolDown(30);
            System.out.println("ASLDJASLKDAL");
        }
    }

}
