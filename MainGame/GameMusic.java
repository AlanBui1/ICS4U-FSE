package MainGame;

import javax.swing.*;

import java.io.*;
import javax.sound.midi.*;

public class GameMusic extends JFrame{
    Timer myTimer;   

    private static Sequencer midiPlayer;
   
    public static void startMidi(String midFilename) {
        endMidi();
        try {
            File midiFile = new File(midFilename);
            Sequence song = MidiSystem.getSequence(midiFile);
            midiPlayer = MidiSystem.getSequencer();
            midiPlayer.open();
            midiPlayer.setSequence(song);
            midiPlayer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY); // repeat 0 times (play once)
            midiPlayer.start();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void endMidi(){
        if (midiPlayer != null){
            midiPlayer.stop();
        }
    }
}

