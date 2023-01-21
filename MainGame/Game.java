package MainGame;

/*
Game.java
ICS4U-01
FSE
*/

import javax.swing.*;

/*
class that starts the Game

Feohtan is a two-player platform fighting game where users select their own character, the stage to fight on, and their controls. 
Using the keyboard, users control their characters and can use a variety of attacks in attempt to launch the opponent of the screen.
The more attacks a player lands on their opponent, the further the opponent gets launched
Each player starts with 3 lives and when a player runs out of lives, the game ends! 

Select your Character! Left click selects for player 1 and right click selects for player 2. Clicking the character icons will switch them from CPU to Human controller and vice versa.

Select your Stage! Different stages have different layouts and music!

Select your controls! Revert back to the default with the default button!

Now you’re all set to play! Move using the keys you selected (or the defaults). Attack using the fast attack key and charged attack key. Pause the game to find the help menu!
*/

public class Game extends JFrame{ 
    Gamepanel game = new Gamepanel();
    public static Game frame;

    public Game(){
        super("Feohtan"); //set title 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(game);
		pack();  // set the size of my Frame exactly big enough to hold the contents
		setVisible(true);	
    }

    public static void main(String [] args){
        frame = new Game();
    }
}