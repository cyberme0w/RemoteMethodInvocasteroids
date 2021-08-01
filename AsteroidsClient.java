import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.registry.LocateRegistry;

import static java.awt.event.KeyEvent.*;

public class AsteroidsClient extends AnimatedJPanel {
  final String id;
  Color color = Color.BLUE;
  Asteroids game = null;
  final int width = 1200;
  final int height = 900;

  boolean boostPressed = false;
  boolean upPressed = false;
  boolean downPressed = false;
  boolean leftPressed = false;
  boolean rightPressed = false;
  boolean spacePressed = false;

  double[] r1 = new double[40];
  double[] r2 = new double[40];

  AsteroidsClient(String host, String n) {
    this.id = n;
    setFocusable(true);

    // Create random array for stars at startup
    for(int i = 0; i < 40; i++) { r1[i] = Math.random(); r2[i] = Math.random(); }

    try {
      var registry = LocateRegistry.getRegistry(host);
      game = (Asteroids) registry.lookup("asteroids");
    } catch (Exception eRegister) { eRegister.printStackTrace(); }

    // Timed events (incl. controls) are written in this timer
    super.t = new javax.swing.Timer(30, ev -> {
      try {
        Ship cur = null;
        if (game != null) cur = game.getShips().get(id);
        if (cur != null) {
          // Acceleration
          if(upPressed && cur.speed < cur.regSpeed) {
            cur.speed += 0.05;
          }

          if(!upPressed && cur.speed > 0) {
            cur.speed -= 0.05;
          }

          // Angle
          // TODO: Change to momentum based movement (without setAngle, since we don't want to immediately change vel)
          if(leftPressed) game.setAngle(id, (game.getAngle(id) + 5) % 360);
          if(rightPressed) game.setAngle(id, (game.getAngle(id) - 5) % 360);

          // Boost
          if(boostPressed && cur.speed < cur.maxSpeed) {
            game.setSpeed(id, game.getSpeed(id) + 0.05);
          }

          // Breaking
          if(!boostPressed && cur.speed > cur.regSpeed) {
            game.setSpeed(id, game.getSpeed(id) - 0.1);
          }

          // Shooting
          if(spacePressed && cur.waitLaser == 0) {
            cur.waitLaser = cur.timerLaser;
            game.newLaser(id);
          }

          // Countdown Laser Timer
          if(cur.waitLaser > 0) cur.waitLaser--;

          // Update positions
          game.moveShip(id);

          // Loop around the borders
          if(cur.posX > width + 20)  game.setPos(id,-20.0, cur.posY);
          if(cur.posX < -20)         game.setPos(id, width + 20, cur.posY);
          if(cur.posY > height + 20) game.setPos(id, cur.posX,   -20);
          if(cur.posY < -20)         game.setPos(id, cur.posX,   height + 20);

          // TODO: Update Laser Positions
          for(Laser l : game.getLasers()) {
            if(l.id.equals(id)) game.moveLaser(l);
          }

          // TODO: Update Meteor Positions
          // TODO: Loop Meteors around the borders
        }
      } catch (Exception eTimer) { eTimer.printStackTrace(); }

      // Paint to canvas
      repaint();
    });

    t.start();

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        try {
          game.createShip(id, color);
        } catch (Exception eMouse) { eMouse.printStackTrace(); }
      }
    });

    addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        try {
          switch (e.getKeyCode()) {
            case VK_S -> downPressed = false;
            case VK_W -> upPressed = false;
            case VK_A -> leftPressed = false;
            case VK_D -> rightPressed = false;
            case VK_SPACE -> spacePressed = false;
            case VK_J -> boostPressed = false;
          }
        } catch (Exception eKeyReleased) { eKeyReleased.printStackTrace(); }
      }

      public void keyPressed(KeyEvent e) {
        try {
          switch (e.getKeyCode()) {
            case VK_S -> downPressed = true;
            case VK_W -> upPressed = true;
            case VK_A -> leftPressed = true;
            case VK_D -> rightPressed = true;
            case VK_J -> boostPressed = true;

            // TODO: SHOOT
            case VK_SPACE -> spacePressed = true;
          }
        } catch (Exception eKeyPressed) { eKeyPressed.printStackTrace(); }
      }
    });
  }

  private void moveLaser(Laser l) {
    l.posX += l.velX;
    l.posY += l.velY;
  }

  public void paintComponent(Graphics g) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);

    for(int i = 0; i < 40; i++) {
      g.setColor(Color.WHITE);
      g.fillOval((int) (r1[i] * width), (int) (r2[i] * height), 2, 2);
    }

    // TODO make some stars n shit

    try {
      // Paint ships
      for (Ship cur : game.getShips().values()) {
        g.setColor(Color.GREEN);
        if(cur.id.equals(id)) g.setColor(Color.CYAN);

        g.fillOval((int) cur.posX - 5, (int) cur.posY - 5, 10, 10); // cast to int to avoid problems with double
        g.drawPolygon(new int[] {
                            (int) (cur.posX + 20 * cur.velX), // Front
                            (int) (cur.posX - 20 * cur.velX + 15 * Math.sin(Math.toRadians(cur.angle))), // Left
                            (int) (cur.posX - 20 * cur.velX - 15 * Math.sin(Math.toRadians(cur.angle)))}, // Right
                      new int[] {
                            (int) (cur.posY + 20 * cur.velY), // Front
                            (int) (cur.posY - 20 * cur.velY + 15 * Math.cos(Math.toRadians(cur.angle))), // Left
                            (int) (cur.posY - 20 * cur.velY - 15 * Math.cos(Math.toRadians(cur.angle)))}, // Right
                      3);

        // UI Overlay
        String uiPoints = "Points: " + cur.points;
        String uiAngle = "Angle: " + cur.angle;
        String uiVel = "Vel(x,y): " + cur.velX + ":" + cur.velY;

        g.setColor(Color.WHITE);
        g.drawChars(uiPoints.toCharArray(), 0, uiPoints.length(), 100, 100);
        g.drawChars(uiAngle.toCharArray(), 0, uiAngle.length(), 100, 120);
        g.drawChars(uiVel.toCharArray(), 0, uiVel.length(), 100, 140);
      }

      for(Laser l : game.getLasers()) {
        g.setColor(l.color);
        g.fillOval((int) l.posX, (int) l.posY, 10, 10);
      }
    } catch (Exception ePaint) { ePaint.printStackTrace(); }
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.add(new AsteroidsClient(args[0], args[1]));
    f.pack();
    f.setVisible(true);
  }

  public void move() {
  }
}
