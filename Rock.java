import java.io.Serializable;
import java.util.HashMap;

public class Rock implements Serializable {
  int id;
  double posX;
  double posY;
  short size;
  double radius;

  double velX;
  double velY;

  Rock(int id, HashMap<Integer, Ship> ships, int width, int height) {
    this.id = id;

    size = (short) (3 * Math.random() + 1); // [1 , 4]
    radius = size * 20 + size * 10 * Math.random(); // [30 , 120]
    var angle = 360 * Math.random();
    velX = Math.cos(Math.toRadians(angle));
    velY = Math.sin(Math.toRadians(angle));

    boolean badPos = true;

    while (badPos) {
      double rand = Math.random();
      if(rand < 0.25)      { posX = 0; posY = Math.random() * height; }
      else if(rand < 0.5)  { posX = width; posY = Math.random() * height; }
      else if(rand < 0.75) { posX = Math.random() * width; posY = 0; }
      else                 { posX = Math.random() * width; posY = height; }

      badPos = false;
      for(Ship s : ships.values()) {
        if(s != null) {
          if(Math.sqrt(s.posX * s.posX - posX * posX + s.posY * s.posY - posY * posY) < 50) {
            badPos = true;
            break;
          }
        }
      }
    }
  }
}
