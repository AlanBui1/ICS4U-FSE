package MainGame;

/*
Game.java
ICS4U-01
FSE
*/

import javax.swing.*;

/*
class that starts the Game

"NAME" is a two-player platform fighting game where users select their own character, the stage to fight on, and their controls. 
Using the keyboard, users control their characters and can use a variety of attacks in attempt to launch the opponent of the screen.
The more attacks a player lands on their opponent, the further the opponent gets launched
Each player starts with 3 lives and when a player runs out of lives, the game ends! 
*/

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
        frame = new Game();
    }
}