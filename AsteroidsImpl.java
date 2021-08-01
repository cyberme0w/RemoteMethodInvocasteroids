import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Asteroids Implementation
public class AsteroidsImpl implements Asteroids {
  private Map<String, Ship> bs = new HashMap<>();
  private ArrayList<Laser> ls = new ArrayList<>();
  //private Map<String, Meteo> ms = new HashMap<>(); // TODO implement meteors

  public void createShip(String name, Color c) {
    bs.put(name, new Ship(name, c));
  }

  public void newLaser(String id) throws RemoteException {
    ls.add(new Laser(id, bs.get(id).posX, bs.get(id).posY, bs.get(id).angle));
  }

  public void moveLaser(Laser l) throws RemoteException {
    l.posX += l.velX;
    l.posY += l.velY;
  }

  public void moveShip(String id) {
    Ship cur = bs.get(id);
    if (cur != null) {
      cur.posX += cur.velX * cur.speed;
      cur.posY += cur.velY * cur.speed;
    }
  }

  public Map<String, Ship> getShips() {
    return bs;
  }

  public void setVel(String name, double x, double y) {
    Ship cur = bs.get(name);
    if (cur != null) { cur.velX = x; cur.velY = y; }
  }

  public void setPos(String name, double x, double y) {
    Ship cur = bs.get(name);
    if (cur != null) {
      cur.posX = x;
      cur.posY = y;
    }
  }

  public double getSpeed(String name) {
    return bs.get(name).speed;
  }
  public void setSpeed(String name, double newSpeed) {
    bs.get(name).speed = newSpeed;
  }

  public ArrayList<Laser> getLasers() throws RemoteException { return ls; }

  public void setAngle(String name, double newAngle) {
    bs.get(name).angle = newAngle;
    bs.get(name).velX = Math.cos(Math.toRadians(newAngle));
    bs.get(name).velY = (-1) * Math.sin(Math.toRadians(newAngle));
  }

  public double getAngle(String name) {
    return bs.get(name).angle;
  }
}
