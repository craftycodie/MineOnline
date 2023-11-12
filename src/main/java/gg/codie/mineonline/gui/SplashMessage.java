package gg.codie.mineonline.gui;

import gg.codie.mineonline.gui.screens.GuiMainMenu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SplashMessage {
    private static String splashMessage;

    public static String getSplashMessage() {
        if (splashMessage == null) {
            loadSplash();
        }

        return splashMessage;
    }

    private static void loadSplash() {
        splashMessage = "missingno";
        Random random = new Random();

        try
        {
            ArrayList arraylist = new ArrayList();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((GuiMainMenu.class).getResourceAsStream("/gui/splashes.txt"), Charset.forName("UTF-8")));
            do
            {
                String splash;
                if((splash = bufferedreader.readLine()) == null)
                {
                    break;
                }
                splash = splash.trim();
                if(splash.length() > 0)
                {
                    arraylist.add(splash);
                }
            } while(true);
            do
            {
                splashMessage = (String)arraylist.get(random.nextInt(arraylist.size()));
            } while(splashMessage.hashCode() == 0x77f432f);
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(Calendar.MONTH) + 1 == 12 && (calendar.get(Calendar.DATE) == 24 || calendar.get(Calendar.DATE) == 25))
        {
            splashMessage = "Merry X-mas!";
        }
        else if (calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1)
        {
            splashMessage = "Happy new year!";
        }
        else if (calendar.get(Calendar.MONTH) + 1 == 3 && calendar.get(Calendar.DATE) == 26)
        {
            splashMessage = "Happy Birthday Codie c:";
        }
    }
}
