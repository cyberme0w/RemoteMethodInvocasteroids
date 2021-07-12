import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

import java.io.Serializable;

import java.util.*;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
import static java.awt.event.KeyEvent.*;

public interface RMI {
 
  // BALL GAME REMOTE METHODS
  static interface BallGame extends Remote {
    void createBall(String name, Color c) throws RemoteException;
    void move(String name) throws RemoteException;
    Map<String, Ball> getBalls() throws RemoteException;

    void setVel(String name, double x, double y) throws RemoteException;
    void setPos(String name, double x, double y) throws RemoteException;

    double getPosX(String name) throws RemoteException;
    double getPosY(String name) throws RemoteException;
    double getVelX(String name) throws RemoteException;
    double getVelY(String name) throws RemoteException;

    double getSpeed(String name) throws RemoteException;
    void setSpeed(String name, double newSpeed) throws RemoteException;
  }

  // EXTRA CLASS FOR BALL
  public class Ball implements Serializable {
    String name;
    Color c = Color.RED;
    double posX;
    double posY;
    double velX;
    double velY;
 
    double speed = 2;

    Ball(String name, Color c) { posX = 0; posY = 0; velX = 0; velY = 0; this.c = c; }
  }


  // BALL GAME IMPLEMENTATION
  static class BallGameImpl implements BallGame {
    private Map<String, Ball> bs = new HashMap<>();

    public void createBall(String name, Color c) { bs.put(name, new Ball(name, c)); }

    public void move(String name) {
      Ball cur = bs.get(name);
      if(cur != null) {cur.posX += cur.velX * cur.speed; cur.posY += cur.velY * cur.speed;}
    }

    public Map<String, Ball> getBalls() { return bs; }

    public void setVel(String name, double x, double y) {
      Ball cur = bs.get(name);
      if(cur != null) {
        cur.velX = x;
        cur.velY = y;
      }
    }

    public void setPos(String name, double x, double y) {
      Ball cur = bs.get(name);
      if(cur != null) {
        cur.posX = x;
        cur.posY = y;
      }
    }

    public double getPosX(String name) { return bs.get(name).posX; }
    public double getPosY(String name) { return bs.get(name).posY; }
    public double getVelX(String name) { return bs.get(name).velX; }
    public double getVelY(String name) { return bs.get(name).velY; }

    public double getSpeed(String name) { return bs.get(name).speed; }
    public void setSpeed(String name, double newSpeed) { bs.get(name).speed = newSpeed; }
  }


  // BALL GAME SERVER
  static class BallGameServer {
    public static void main(String[] args) throws Exception {
      var gameObj = new BallGameImpl();
      var stub = (BallGame) UnicastRemoteObject.exportObject(gameObj, 0);
      LocateRegistry.getRegistry().bind("balls", stub);
      System.err.println("Ball Game Server Ready!");
    }
  }

  // BALL GAME CLIENT
  static interface Animation {
    public void move();
  }

  static abstract class AnimatedJPanel extends JPanel implements Animation {
    javax.swing.Timer t;

    public AnimatedJPanel() {
      super(true);
      t = new javax.swing.Timer(30, ev -> {
        move();
        repaint();
      });

      t.start();
    }
  }

  static class BallGameClient extends AnimatedJPanel {
    final String name;
    Color color = Color.RED;
    BallGame game = null;
    final int width = 1200;
    final int height = 900;

    boolean boost = false;
    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;

    BallGameClient(String host, String n, String c) {
      this.name = n;

      switch(c) {
        case "0": color = Color.GREEN; break;
        case "1": color = Color.BLUE; break;
        case "2": color = Color.CYAN; break;
        case "3": color = Color.YELLOW; break;
        default: color = Color.WHITE; break;
      }

      setFocusable(true);
      try {
        var registry = LocateRegistry.getRegistry(host); 
        game = (BallGame) registry.lookup("balls");
      } catch(Exception eRegister) {System.out.println(eRegister);}

      super.t = new javax.swing.Timer(30, ev -> {
        try {
          Ball cur = null;
          if(game != null) cur = game.getBalls().get(name);
          if(cur != null) game.move(name);
        }
        catch (Exception eTimer) { System.out.println(eTimer); }

        repaint();
      });

      t.start();

      addMouseListener(new MouseAdapter(){
        public void mouseClicked(MouseEvent e) {
          try{game.createBall(name, color);} 
          catch(Exception eMouse) {System.out.println(eMouse);}
        }
      });

      addKeyListener(new KeyAdapter(){
        public void keyReleased(KeyEvent e) {
          try{
            switch (e.getKeyCode()) {
              case VK_DOWN, VK_S: if(downPressed) {game.setVel(name, game.getVelX(name),0);  downPressed = false;  break;}
              case VK_UP,   VK_W: if(upPressed)   {game.setVel(name, game.getVelX(name),0);  upPressed = false;    break;}
              case VK_LEFT, VK_A: if(leftPressed) {game.setVel(name, 0, game.getVelY(name)); leftPressed = false;  break;}
              case VK_RIGHT,VK_D: if(rightPressed){game.setVel(name, 0, game.getVelY(name)); rightPressed = false; break;}

              // Releasing X will halve the speed and current velocity
              case VK_X, VK_J: if(boost) {
                game.setSpeed(name, game.getSpeed(name) / 1.5);
                game.setVel(name, game.getVelX(name) / 1.5, game.getVelY(name) / 1.5);
                boost = false;
                break;
              }
            }
          } catch (Exception eKeyReleased) {System.out.println(eKeyReleased);}
        }
        
        public void keyPressed(KeyEvent e) {
          try {
            switch (e.getKeyCode()) {
              // MOVEMENT: DOWN
              case VK_DOWN, VK_S: 
                if(!downPressed) {game.setVel(name, game.getVelX(name), game.getSpeed(name));downPressed = true;} break;
              
              // MOVEMENT: UP
              case VK_UP, VK_W: 
                if(!upPressed)   {game.setVel(name, game.getVelX(name), -game.getSpeed(name));upPressed = true; } break;
              
              // MOVEMENT: LEFT
              case VK_LEFT, VK_A:
                if(!leftPressed) {game.setVel(name, -game.getSpeed(name), game.getVelY(name));leftPressed = true; } break;

              // MOVEMENT: RIGHT
              case VK_RIGHT, VK_D:
                if(!rightPressed) {game.setVel(name, game.getSpeed(name), game.getVelY(name));rightPressed = true; } break; 


              // MOVEMENT: BOOST
              case VK_X, VK_J:
                if(!boost) {
                  game.setSpeed(name, game.getSpeed(name) * 1.5);
                  game.setVel(name, game.getVelX(name) * 1.5, game.getVelY(name) * 1.5);
                  boost = true;
                } 
                break;

              // TODO OTHER: SHOOT

            }
          } catch (Exception eKeyPressed) {System.out.println(eKeyPressed);}
        }
      });
    }

    public void paintComponent(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, width, height);
      // TODO make some stars n shit
      
      try {
        for(Ball d : game.getBalls().values()) {
          g.setColor(d.c);
          g.fillOval((int) d.posX, (int) d.posY, 20, 20); // cast to int to avoid problems with double
        }
      } catch (Exception ePaint) {System.out.println(ePaint);}
    }

    public Dimension getPreferredSize() {
      return new Dimension(width, height);
    }

    public static void main(String[] args) {
      JFrame f = new JFrame();
      f.add(new BallGameClient(args[0], args[1], args[2]));
      f.pack();
      f.setVisible(true);
    }

    public void move() {}
  }
}
