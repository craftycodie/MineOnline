package gg.codie.mineonline.gui.sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class ClickSound {

    static URL url = ClickSound.class.getClassLoader().getResource("sounds/click.wav");

    public static void play() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open sounds clip and load samples from the sounds input stream.
            clip.open(audioIn);
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
