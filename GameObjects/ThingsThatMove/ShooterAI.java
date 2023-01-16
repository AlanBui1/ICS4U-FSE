package GameObjects.ThingsThatMove;

import java.util.*;
import MainGame.Gamepanel;
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
        
        Platform curPlat = nearestPlat(stage);
        int midX = ((int)curPlat.getX() + (curPlat.getWidth()/2));
        
        if (Util.randint(1, 100) < 2) keysPressed[getUKey()] = Util.randBoolean(); //random jumps
        if (getY() > curPlat.getY()) keysPressed[getUKey()] = true; //jumps if below nearest platform

        if (!(midX - 50 <= getX() && getX() <= midX+1)){ //moves towards the middle of the nearest platform
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
        if (getCoolDown() > 0) return;
        if (Util.randint(0, 4) %5 != 0) return;

        int randAtk = attackKeys[Util.randint(0, 1)];

        if (randAtk == getChargeKey()){
            System.out.println(1);
            if (getCharge() < 50){
                keysReleasedTime[randAtk] = 0;
                keysPressed[randAtk] = false;
            }
            else{
                super.attack("ChargeSideAtk");
            }
        }

        else{
            super.attack("BonusAtk");
            super.attack("FastSideAtk");
        }
    }

    public Platform nearestPlat(Stage curStage){ //returns the nearest Platform to the Player in the stage 
        Platform nearestPlat = curStage.getMainPlat(); //by default is the main platform of the Stage
        ArrayList <Platform> plats = curStage.getPlats(); //platforms of the Stage
        double nearestDist = Util.taxicabDist(getCenterPoint(), nearestPlat.getCenterPoint()); //sets initial nearest distance

        for (Platform p : plats){ //loops through all Platforms of the Stage
            if (p.getInvis()) continue; //if the Platform is set to invisible, doesn't check it

            double distToPlat= Util.taxicabDist(getX(), getY(), p.getX(), p.getY()); //distance from Player to Platform
            if (distToPlat < nearestDist){
                //if the distance was less, sets new nearestDist and nearestPlat 
                nearestDist = distToPlat;
                nearestPlat = p;
            }
        }
        return nearestPlat; //returns the nearest Platform to the Player
    }
}
