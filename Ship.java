import java.awt.*;
import java.io.Serializable;

public class Ship implements Serializable {
  public int points;
  boolean alive = false;
  int id;

  double posX;
  double posY;
  double velX = 0;
  double velY = 0;
  double angle = 0;

  int waitLaser = 0;
  int timerLaser = 30;

  double speed = 0;
  double regSpeed = 8;
  double maxSpeed = 16;

  // Constructor
  Ship(int id) {
    this.id = id;
    this.points = 0;

    // Spawn the ball in a random position within the middle of the game field
    posX = 120 + (int) (Math.random() * 960);
    posY = 90 + (int) (Math.random() * 720);
  }
}
