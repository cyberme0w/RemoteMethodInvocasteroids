import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface Asteroids extends Remote {
  // Create elements
  void createShip(int id, String name) throws RemoteException;
  void newLaser(int id) throws RemoteException;

  // Move stuff around
  void moveShip(int id) throws RemoteException;
  void moveLaser(int laserIndex) throws RemoteException;
  void moveRock(int rockIndex) throws RemoteException;

  // Setter
  void setLasers(ArrayList<Laser> newLS) throws RemoteException;
  void setRocks(ArrayList<Rock> newRocks) throws RemoteException;
  void setPos(int id, double x, double y) throws RemoteException;
  void setSpeed(int id, double newSpeed) throws RemoteException;
  void setAngle(int id, double newAngle) throws RemoteException;
  void setVelX(int id, double x) throws RemoteException;
  void setVelY(int id, double y) throws RemoteException;
  void addVelFromAngle(int id) throws RemoteException;
  void setRockPos(int index, double x, double y) throws RemoteException;

  // Getter
  HashMap<Integer, Ship> getShips() throws RemoteException;
  ArrayList<Laser> getLasers() throws RemoteException;
  ArrayList<Rock> getRocks() throws RemoteException;
  double getSpeed(int id) throws RemoteException;
  double getAngle(int id) throws RemoteException;
  double getVelX(int id) throws RemoteException;
  double getVelY(int id) throws RemoteException;
  double getVelXFromAngle(int id) throws RemoteException;
  double getVelYFromAngle(int id) throws RemoteException;

  void damageRock(int index, int shipID) throws RemoteException;
  void setLaserCountdown(int id, int i) throws RemoteException;
  void killShip(int id) throws RemoteException;
  void resetShip(int id, String name) throws RemoteException;

  void removeLaser(int index) throws RemoteException;

  void reset() throws RemoteException;

  void setReady(int id, boolean b) throws RemoteException;

  // TODO: make meteors
  //void createMeteor(String name) throws RemoteException;
  //Map<String, Meteor>(String name) throws RemoteException;

}
