import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

// Asteroids Implementation
public class AsteroidsImpl implements Asteroids {
  private HashMap<Integer, Ship> ships = new HashMap<>();
  private ArrayList<Laser> lasers = new ArrayList<>();
  private ArrayList<Rock> rocks = new ArrayList<>();

  public void createShip(int id) {
    ships.put(id, new Ship(id));
  }

  public void newLaser(int id) {
    var ship = ships.get(id);
    lasers.add(new Laser(id, ship.posX, ship.posY, ship.angle));
  }

  public void moveLaser(int laserIndex) {
    Laser las = lasers.get(laserIndex);
    las.posX += 20 * las.velX;
    las.posY += 20 * las.velY;
  }

  public void setLasers(ArrayList<Laser> newLS) { lasers = newLS; }

  public void moveRock(int rockIndex) {
    Rock rock = rocks.get(rockIndex);
    rock.posX += (25 - 5 * rock.size) * rock.velX;
    rock.posY += (25 - 5 * rock.size) * rock.velY;
  }

  public ArrayList<Rock> getRocks() {
    return rocks;
  }

  public void setRocks(ArrayList<Rock> newRocks) {
    rocks = newRocks;
  }

  public void moveShip(int id) {
    Ship cur = ships.get(id);
    if (cur != null) {
      cur.posX += cur.velX * cur.speed;
      cur.posY += cur.velY * cur.speed;
    }
  }

  public HashMap<Integer, Ship> getShips() {
    return ships;
  }

  public void setVel(int id, double x, double y) {
    Ship cur = ships.get(id);
    if (cur != null) { cur.velX = x; cur.velY = y; }
  }

  public void setPos(int id, double x, double y) {
    Ship cur = ships.get(id);
    if (cur != null) {
      cur.posX = x;
      cur.posY = y;
    }
  }

  public double getSpeed(int id) {
    return ships.get(id).speed;
  }
  public void setSpeed(int id, double newSpeed) {
    ships.get(id).speed = newSpeed;
  }

  public void setVelX(int id, double x) { ships.get(id).velX = x; }
  public void setVelY(int id, double y) { ships.get(id).velY = y; }

  public double getVelX(int id) {return ships.get(id).velX;}
  public double getVelY(int id) {return ships.get(id).velY;}

  public double getVelXFromAngle(int id) {return Math.cos(Math.toRadians(ships.get(id).angle));}
  public double getVelYFromAngle(int id) {return Math.sin(Math.toRadians(ships.get(id).angle));}

  public ArrayList<Laser> getLasers() { return lasers; }

  public void setAngle(int id, double newAngle) {
    ships.get(id).angle = newAngle;
  }

  public void addVelFromAngle(int id) {
    var cur = ships.get(id);
    cur.velX = Math.cos(Math.toRadians(cur.angle));
    cur.velY = (-1) * Math.sin(Math.toRadians(cur.angle));
  }

  public void setRockPos(int index, double x, double y) {
    var rock = rocks.get(index);
    rock.posX = x;
    rock.posY = y;
  }

  public double getAngle(int id) {
    return ships.get(id).angle;
  }
}
