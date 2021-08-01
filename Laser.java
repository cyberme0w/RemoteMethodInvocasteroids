import java.awt.*;
import java.io.Serializable;

// CLASS FOR LASER SHOTS
public class Laser implements Serializable {
  String id;
  Color color;

  double posX;
  double posY;
  double velX;
  double velY;
  double angle;

  double speed = 20;

  Laser(String id, double pX, double pY, double angle) {
    this.id = id;
    color = Color.RED;
    posX = pX;
    posY = pY;
    velX = Math.cos(Math.toRadians(angle));
    velY = (-1) * Math.sin(Math.toRadians(angle));
  }
}
