/*
Game.java
ICS4U-01
FSE
*/

import javax.swing.*;

public class Game extends JFrame{ 
    public static final int WIDTH = 800, HEIGHT = 800; //width and height of the window
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