package gg.codie.mineonline.gui.sound;

import gg.codie.mineonline.Settings;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;

public class ClickSound {

    static URL url = ClickSound.class.getClassLoader().getResource("sounds/click.wav");

    public static void play() {
        try {
            if (Settings.singleton.getSoundVolume() == 0)
                return;

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open sounds clip and load samples from the sounds input stream.
            clip.open(audioIn);
            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // This isn't perfect but will do for now.
            gainControl.setValue(-37f + (25 * Settings.singleton.getSoundVolume()));
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
