package mainFolder;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;

public class ImageCatcher extends Applet {

     private BufferedImage img;

     public void init() {
         try {
             URL url = new URL(getCodeBase(), "examples/strawberry.jpg");
             img = ImageIO.read(url);
         } catch (IOException e) {
         }
     }

     public void paint(Graphics g) {
       g.drawImage(img, 50, 50, null);
     }
}