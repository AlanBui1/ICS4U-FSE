//class for the shooter character inherits from Player class

public class Shooter extends Player{
    public static final double MASS = 40;
    public Shooter(int xx, int yy, int direct, int numLives, double m){
        super(xx, yy, direct, numLives, m); //gives these values to the Player constructor
    }

    public void attack(boolean [] keys){
        if (getCoolDown() < 0){
            if (keys[getFastKey()]){
                if (keys[getUKey1()]){
                    fastUpAtk();
                }
                else if (keys[getDKey()]){
                    fastDownAtk();
                }
                else{
                    fastSideAtk();
                }
            }
        }
    }

    public void fastUpAtk(){
        addHitBox(getX(), getY(), 20.0, 20.0, 0, -10, 0, 0, 30, 0, -20, 100);
        setCoolDown(30);
    }

    public void fastDownAtk(){
        addHitBox(getX(), getY(), 7.0, 7.0, 0, 10, 0, 0, 30, 0, 20, 100);
        setCoolDown(30);
    }

    public void fastSideAtk(){
        Force knockForce = new Force(20*getDir(), 0, 10, 10);
        Mover movt = new Mover(getX(), getY(), 10*getDir(), 0);
        addHitBox(movt, knockForce, 7, 7, 30);
        setCoolDown(30);
    }

}
