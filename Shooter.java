//class for the shooter character inherits from Player class

public class Shooter extends Player{
    public static final double MASS = 40;
    public Shooter(int xx, int yy, int direct, int numLives, double m){
        super(xx, yy, direct, numLives, m); //gives these values to the Player constructor
    }

    public void attack(boolean [] keys){
        if (this.getCoolDown() < 0){
            if (keys[this.getFastKey()]){
                if (keys[this.getUKey1()]){
                    fastUpAtk();
                }
                else if (keys[this.getDKey()]){
                    fastDownAtk();
                }
                else{
                    fastSideAtk();
                }
            }
        }
    }

    public void fastUpAtk(){
        this.addHitBox(this.getX(), this.getY(), 20.0, 20.0, 0, -10, 0, 0, 30, 0, -20);
        this.setCoolDown(30);
    }

    public void fastDownAtk(){
        this.addHitBox(this.getX(), this.getY(), 7.0, 7.0, 0, 10, 0, 0, 30, 0, 20);
        this.setCoolDown(30);
    }

    public void fastSideAtk(){
            //if (keys[this.getShootKey()]){ //rename shoot key to be smth like "fast attack key" or "charged attack key" depending on 
        this.addHitBox(this.getX(), this.getY(), 7.0, 7.0, 10.0*this.getDir(), 0, 0, 0, 30, 20*this.getDir(), 0);
        this.setCoolDown(30);
    }

}
