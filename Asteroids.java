import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Asteroids extends Remote {
  void createShip(String name) throws RemoteException;
  void moveShip(String name) throws RemoteException;

  HashMap<String, Ship> getShips() throws RemoteException;
  ArrayList<Laser> getLasers() throws RemoteException;

  double getAngle(String name) throws RemoteException;
  void setAngle(String name, double newAngle) throws RemoteException;

  void setPos(String name, double x, double y) throws RemoteException;

  double getSpeed(String name) throws RemoteException;
  void setSpeed(String name, double newSpeed) throws RemoteException;

  void setVelX(String id, double x) throws RemoteException;
  void setVelY(String id, double y) throws RemoteException;

  double getVelX(String id) throws RemoteException;
  double getVelY(String id) throws RemoteException;

  double getVelXFromAngle(String id) throws RemoteException;
  double getVelYFromAngle(String id) throws RemoteException;

  void addVelFromAngle(String id) throws RemoteException;

  void newLaser(String id) throws RemoteException;

  void moveLaser(int laserIndex) throws RemoteException;

  void setLasers(ArrayList<Laser> newLS) throws RemoteException;

  void moveRock(Rock r) throws RemoteException;

  ArrayList<Rock> getRocks() throws RemoteException;

  void setRocks(ArrayList<Rock> newRocks) throws RemoteException;

  // TODO: make meteors
  //void createMeteor(String name) throws RemoteException;
  //Map<String, Meteor>(String name) throws RemoteException;

}
