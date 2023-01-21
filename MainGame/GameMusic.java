package MainGame;

import java.io.*;
import javax.sound.midi.*;


/*
class to play music using midiPlayer

Methods:
startMidi(fileName) plays a new song given the file name and ends any previously played songs
endMidi() ends any playing songs

*/

public class GameMusic{
    private static Sequencer midiPlayer;
   
    public static void startMidi(String midFilename) { //plays a new song, given its file name
        endMidi(); //ends any previously played songs
        try {
            File midiFile = new File(midFilename);
            Sequence song = MidiSystem.getSequence(midiFile);
            midiPlayer = MidiSystem.getSequencer();
            midiPlayer.open();
            midiPlayer.setSequence(song);
            midiPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); // repeat 0 times (play once)
            midiPlayer.start(); //stats the song
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void endMidi(){ //method to end any playing songs
        if (midiPlayer != null){
            midiPlayer.stop();
        }
    }
}

