import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface Asteroids extends Remote {
  void createShip(String name, Color c) throws RemoteException;
  void moveShip(String name) throws RemoteException;

  Map<String, Ship> getShips() throws RemoteException;
  ArrayList<Laser> getLasers() throws RemoteException;

  double getAngle(String name) throws RemoteException;
  void setAngle(String name, double newAngle) throws RemoteException;

  void setPos(String name, double x, double y) throws RemoteException;

  double getSpeed(String name) throws RemoteException;
  void setSpeed(String name, double newSpeed) throws RemoteException;


  void newLaser(String id) throws RemoteException;

  void moveLaser(Laser l) throws RemoteException;

  // TODO: make meteors
  //void createMeteor(String name) throws RemoteException;
  //Map<String, Meteor>(String name) throws RemoteException;

}
