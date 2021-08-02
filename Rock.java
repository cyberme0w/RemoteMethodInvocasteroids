import java.io.Serializable;
import java.util.HashMap;

public class Rock implements Serializable {
  double posX;
  double posY;
  short size;
  double radius;

  double velX;
  double velY;

  Rock(HashMap<String, Ship> ships, int width, int height) {
    double rand = Math.random();

    size = (short) (3 * Math.random() + 1);
    radius = size * 20 + size * 10 * Math.random();

    var r = 360 * Math.random();
    velX = Math.cos(r);
    velY = Math.sin(r);

    boolean badPos = true;

    while (badPos) {
      if(rand < 0.25) { posX = 0; posY = Math.random() * height; }
      else if(rand < 0.5) { posX = width; posY = Math.random() * height; }
      else if(rand < 0.75) { posX = Math.random() * width; posY = 0; }
      else { posX = Math.random() * width; posY = height; }

      badPos = false;
      for(Ship s : ships.values()) {
        if(s != null) {
          if(Math.sqrt(s.posX * s.posX - posX * posX + s.posY * s.posY - posY * posY) < 200) {
            badPos = true;
            break;
          }
        }
      }
    }
  }
}
