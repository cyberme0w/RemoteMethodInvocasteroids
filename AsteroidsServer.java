import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

// BALL GAME SERVER
public class AsteroidsServer {
  public static void main(String[] args) throws Exception {
    var gameObj = new AsteroidsImpl();
    var stub = (Asteroids) UnicastRemoteObject.exportObject(gameObj, 0);
    LocateRegistry.getRegistry().bind("asteroids", stub);
    System.err.println("Asteroids Game Server Ready!");
  }
}
