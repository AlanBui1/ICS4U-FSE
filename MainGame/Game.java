package MainGame;
import java.io.*;

/*
Game.java
ICS4U-01
FSE
*/

import javax.swing.*;

public class Game extends JFrame{ 
    Gamepanel game = new Gamepanel();
    public static Game frame;

    public Game(){
        super("FSE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(game);
		pack();  // set the size of my Frame exactly big enough to hold the contents
		setVisible(true);	
    }

    public static void main(String [] args){
        frame = new Game(); //creates a new Arkanoid object
    }
}