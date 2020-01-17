package de.fzi.dream.ploc.utility.identicon;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;

public class IdenticonGenerator {
    public static int height = 16;
    public static int width = 16;

    public static Bitmap generate(String userName,
                                  HashInterface hashGenerator) {

        byte[] hash = hashGenerator.generateHash(userName);
        Bitmap identicon = Bitmap.createBitmap(width, height, Config.ARGB_8888);

        int r = hash[0] & 255;
        int g = hash[1] & 255;
        int b = hash[2] & 255;

        int background = Color.argb(255, r, g, b);
        int foreground = Color.parseColor("#ffffff");

        for (int x = 0; x < width; x++) {
            int i = x < 8 ? x : 15 - x;
            int pixelColor;
            for (int y = 0; y < height; y++) {
                if ((hash[i] >> y & 1) == 1)
                    pixelColor = foreground;
                else
                    pixelColor = background;

                identicon.setPixel(x, y, pixelColor);
            }
        }

        Bitmap bmpWithBorder = Bitmap.createBitmap(100, 100, identicon.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(background);
        identicon = Bitmap.createScaledBitmap(identicon, 80, 80, false);
        canvas.drawBitmap(identicon, 11, 11, null);

        return bmpWithBorder;
    }
}