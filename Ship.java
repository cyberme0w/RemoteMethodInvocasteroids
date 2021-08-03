import java.awt.*;
import java.io.Serializable;

public class Ship implements Serializable {
  public int points;
  boolean alive = true;
  boolean ready = false;
  int id;
  String name;

  double posX;
  double posY;
  double velX = 0;
  double velY = 0;
  double angle = 0;

  int waitLaser = 0;
  int timerLaser = 5;

  double speed = 0;
  double regSpeed = 8;
  double maxSpeed = 16;

  // Constructor
  Ship(int id, String name) {
    this.name = name;
    this.id = id;
    this.points = 0;

    // Spawn the ball in a random position within the middle of the game field
    posX = 120 + (int) (Math.random() * 960);
    posY = 90 + (int) (Math.random() * 720);
  }
}
