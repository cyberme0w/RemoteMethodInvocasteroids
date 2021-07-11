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

    void setVel(String name, int x, int y) throws RemoteException;
    void setPos(String name, int x, int y) throws RemoteException;

    int getPosX(String name) throws RemoteException;
    int getPosY(String name) throws RemoteException;
    int getVelX(String name) throws RemoteException;
    int getVelY(String name) throws RemoteException;

    int getSpeed(String name) throws RemoteException;
    void setSpeed(String name, int newSpeed) throws RemoteException;
  }

  // EXTRA CLASS FOR BALL
  public class Ball implements Serializable {
    String name;
    Color c = Color.RED;
    int posX;
    int posY;
    int velX;
    int velY;
 
    int speed = 2;

    Ball(String name, Color c) { posX = 0; posY = 0; velX = 0; velY = 0; this.c = c; }
  }


  // BALL GAME IMPLEMENTATION
  static class BallGameImpl implements BallGame {
    private Map<String, Ball> bs = new HashMap<>();

    public void createBall(String name, Color c) { bs.put(name, new Ball(name, c)); }

    public void move(String name) {
      Ball cur = bs.get(name);
      if(cur != null) {cur.posX += cur.velX; cur.posY += cur.velY;}
    }

    public Map<String, Ball> getBalls() { return bs; }

    public void setVel(String name, int x, int y) {
      Ball cur = bs.get(name);
      if(cur != null) {
        cur.velX = x;
        cur.velY = y;
      }
    }

    public void setPos(String name, int x, int y) {
      Ball cur = bs.get(name);
      if(cur != null) {
        cur.posX = x;
        cur.posY = y;
      }
    }

    public int getPosX(String name) { return bs.get(name).posX; }
    public int getPosY(String name) { return bs.get(name).posY; }
    public int getVelX(String name) { return bs.get(name).velX; }
    public int getVelY(String name) { return bs.get(name).velY; }

    public int getSpeed(String name) { return bs.get(name).speed; }
    public void setSpeed(String name, int newSpeed) { bs.get(name).speed = newSpeed; }
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
    int ballSpeedVER = 1;
    int ballSpeedHOR = 1;

    int ballPosVER = 25;
    int ballPosHOR = 25;

    final String name;
    Color color = Color.RED;
    BallGame game = null;
    final int width = 800;
    final int height = 600;

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
              case VK_DOWN:  game.setVel(name, game.getVelX(name),0);  downPressed = false;  break;
              case VK_UP:    game.setVel(name, game.getVelX(name),0);  upPressed = false;    break;
              case VK_LEFT:  game.setVel(name, 0, game.getVelY(name)); leftPressed = false;  break;
              case VK_RIGHT: game.setVel(name, 0, game.getVelY(name)); rightPressed = false; break;

              case VK_X:     game.setSpeed(name, game.getSpeed(name) / 2); boost = false;    break;
            }
          } catch (Exception eKeyReleased) {System.out.println(eKeyReleased);}
        }
        
        public void keyPressed(KeyEvent e) {
          try {
            switch (e.getKeyCode()) {
              case VK_DOWN:  if(!downPressed) {game.setVel(name, game.getVelX(name), game.getSpeed(name) *  2); 
                                                downPressed = true; } break;

              case VK_UP:    if(!upPressed)   {game.setVel(name, game.getVelX(name), game.getSpeed(name) * -2); 
                                                upPressed = true; } break;

              case VK_LEFT:  if(!leftPressed) {game.setVel(name, game.getSpeed(name) * -2, game.getVelY(name)); 
                                                leftPressed = true; } break;

              case VK_RIGHT: if(!rightPressed){game.setVel(name, game.getSpeed(name) *  2, game.getVelY(name));
                                                rightPressed = true; } break; 

              case VK_X:     if(!boost)       {game.setSpeed(name, game.getSpeed(name) * 2); 
                                                boost = true; } break;
            }

            // DEBUG
            System.err.println("\nBall moving down.\nNew values:\n" +
                               "Ball.posX = " + game.getPosX(name) + "\n" +
                               "Ball.posY = " + game.getBalls().get(name).posY + "\n" +
                               "Ball.velX = " + game.getBalls().get(name).velX + "\n" +
                               "Ball.velY = " + game.getBalls().get(name).velY + "\n\n");
          } catch (Exception eKeyPressed) {System.out.println(eKeyPressed);}
        }
      });
    }

    public void paintComponent(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, width, height);
      try {
        for(Ball d : game.getBalls().values()) {
          g.setColor(d.c);
          g.fillOval(d.posX, d.posY, 10, 10);
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
