import java.util.ArrayList;
import java.util.HashMap;

// Asteroids Implementation
public class AsteroidsImpl implements Asteroids {
  private HashMap<String, Ship> ships = new HashMap<>();
  private ArrayList<Laser> lasers = new ArrayList<>();
  private ArrayList<Rock> rocks = new ArrayList<>();

  public void createShip(String name) {
    ships.put(name, new Ship(name));
  }

  public void newLaser(String id) {
    var ship = ships.get(id);
    lasers.add(new Laser(id, ship.posX, ship.posY, ship.angle));
  }

  public void moveLaser(int laserIndex) {
    Laser las = lasers.get(laserIndex);
    las.posX += 20 * las.velX;
    las.posY += 20 * las.velY;
  }

  public void setLasers(ArrayList<Laser> newLS) { lasers = newLS; }

  public void moveRock(Rock r) {
    r.posX += r.velX;
    r.posY += r.velY;
  }

  public ArrayList<Rock> getRocks() {
    return rocks;
  }

  public void setRocks(ArrayList<Rock> newRocks) {
    rocks = newRocks;
  }

  public void moveShip(String id) {
    Ship cur = ships.get(id);
    if (cur != null) {
      cur.posX += cur.velX * cur.speed;
      cur.posY += cur.velY * cur.speed;
    }
  }

  public HashMap<String, Ship> getShips() {
    return ships;
  }

  public void setVel(String name, double x, double y) {
    Ship cur = ships.get(name);
    if (cur != null) { cur.velX = x; cur.velY = y; }
  }

  public void setPos(String name, double x, double y) {
    Ship cur = ships.get(name);
    if (cur != null) {
      cur.posX = x;
      cur.posY = y;
    }
  }

  public double getSpeed(String id) {
    return ships.get(id).speed;
  }
  public void setSpeed(String id, double newSpeed) {
    ships.get(id).speed = newSpeed;
  }

  public void setVelX(String id, double x) { ships.get(id).velX = x; }
  public void setVelY(String id, double y) { ships.get(id).velY = y; }

  public double getVelX(String id) {return ships.get(id).velX;}
  public double getVelY(String id) {return ships.get(id).velY;}

  public double getVelXFromAngle(String id) {return Math.cos(Math.toRadians(ships.get(id).angle));}
  public double getVelYFromAngle(String id) {return Math.sin(Math.toRadians(ships.get(id).angle));}

  public ArrayList<Laser> getLasers() { return lasers; }

  public void setAngle(String id, double newAngle) {
    ships.get(id).angle = newAngle;
  }

  public void addVelFromAngle(String id) {
    var cur = ships.get(id);
    cur.velX = Math.cos(Math.toRadians(cur.angle));
    cur.velY = (-1) * Math.sin(Math.toRadians(cur.angle));
  }

  public double getAngle(String name) {
    return ships.get(name).angle;
  }
}
