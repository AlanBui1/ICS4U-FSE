//class for the shooter character inherits from Player class

public class Shooter extends Player{
    public Shooter(int xx, int yy, int direct, int numLives, double g){
        super(xx, yy, direct, numLives, g); //gives these values to the Player constructor
    }

    public void attack(boolean [] keys){
        if (this.getCoolDown() < 0){
            if (keys[this.getShootKey()]){
                shoot();
            }
        }
    }

    public void shoot(){
        
            //if (keys[this.getShootKey()]){ //rename shoot key to be smth like "fast attack key" or "charged attack key" depending on 
        this.addHitBox(this.getX(), this.getY(), 7.0, 7.0, 10.0*this.getDir(), 0, 0, 0, 30);
        this.setCoolDown(30);
            // }
        
    }

}
