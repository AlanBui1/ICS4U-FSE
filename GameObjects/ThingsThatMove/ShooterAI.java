package GameObjects.ThingsThatMove;

import java.util.HashMap;
import Utility.Util;
import GameObjects.ThingsThatMove.AttackStuff.*;
import GameObjects.Stage;

public class ShooterAI extends Player{
    private int []  movementKeys = new int[4],
                    attackKeys = new int[2];

    public ShooterAI(double xx, double yy, HashMap <String, Double> stats, HashMap <String, Attack> atks){
        super(xx, yy, stats, atks);
    }

    public void setKeys(){
        movementKeys[0] = getUKey();
        movementKeys[1] = getRKey();
        movementKeys[2] = getLKey();
        movementKeys[3] = getDKey();
        attackKeys[0] = getFastKey();
        attackKeys[1] = getChargeKey();
    }

    @Override
    public void move(boolean [] keysPressed, int [] keysReleasedTime, Stage stage){
        keysPressed[getRKey()] = false; keysPressed[getLKey()] = false;
        // keysPressed[getFastKey()] = true;
        
        Platform curPlat = nearestPlat(stage.getPlats());
        int midX = ((int)curPlat.getX() + (curPlat.getWidth()/2));
        
        if (Util.randint(1, 100) < 2) keysPressed[getUKey()] = Util.randBoolean(); //random jumps
        if (getY() > curPlat.getY()) keysPressed[getUKey()] = true; //jumps if below nearest platform

        if (!(midX - 50 <= getX() && getX() <= midX+1)){ //moves to the middle of the nearest platform
            if (getX() < midX){
                keysPressed[getRKey()] = true;
            }
            else if (getX() > midX){
                keysPressed[getLKey()] = true;
            }
        }
        super.move(keysPressed, keysReleasedTime, stage);
    }

    @Override
    public void attack(boolean [] keysPressed, int [] keysReleasedTime){
        if (Util.randint(0, 4) %5 != 0) return;

        chargeMove();

        int randAtk = attackKeys[Util.randint(0, 1)];
        keysReleasedTime[randAtk] = 1;
        keysPressed[randAtk] = true;

        if (randAtk == getChargeKey()){
            if (getCharge() < 50){
                keysReleasedTime[randAtk] = 0;
                keysPressed[randAtk] = false;
            }
        }
        
        super.attack(keysPressed, keysReleasedTime);
        keysReleasedTime[randAtk] = 0;
        keysPressed[randAtk] = false;
    }
}
