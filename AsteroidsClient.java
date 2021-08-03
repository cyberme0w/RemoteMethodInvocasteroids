import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;

public class AsteroidsClient extends AnimatedJPanel {
  final Integer id;
  Asteroids game = null;
  final int SCREEN_WIDTH = 1200;
  final int SCREEN_HEIGHT = 900;
  final int MAX_ROCKS = 50;

  boolean boostPressed = false;
  boolean upPressed = false;
  boolean downPressed = false;
  boolean leftPressed = false;
  boolean rightPressed = false;
  boolean spacePressed = false;

  int mouseX;
  int mouseY;

  double[] r1 = new double[40];
  double[] r2 = new double[40];

  int rockCountdownMax = 90;
  int rockCountdown = rockCountdownMax;

  AsteroidsClient(String host) {
    id = (int) (Math.random() * 100000);
    setFocusable(true);

    // Create random array for stars at startup
    for(int i = 0; i < 40; i++) { r1[i] = Math.random(); r2[i] = Math.random(); }

    // Connect client to server
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
          if(upPressed) {
            game.addVelFromAngle(id);
            if(cur.speed < cur.regSpeed) game.setSpeed(id, game.getSpeed(id) + 0.1);
          }

          if(!upPressed && cur.speed > 0) {
            game.setSpeed(id, game.getSpeed(id) - 0.05);
          }

          if(downPressed && cur.speed > 0) {
            game.setSpeed(id, game.getSpeed(id) - 0.2);
          }

          // Angle
          // TODO: Change to momentum based movement (without setAngle, since we don't want to immediately change vel)
          if(leftPressed) game.setAngle(id, (game.getAngle(id) + 10) % 360);
          if(rightPressed) game.setAngle(id, (game.getAngle(id) - 10) % 360);

          // Boost
          if(boostPressed && cur.speed < cur.maxSpeed) {
            game.addVelFromAngle(id);
            game.setSpeed(id, game.getSpeed(id) + 0.2);
          }

          // Slow down from boost
          if(!boostPressed && cur.speed > cur.regSpeed) {
            game.setSpeed(id, game.getSpeed(id) - 0.1);
          }

          // Shooting
          if(spacePressed && cur.waitLaser == 0) {
            game.setLaserCountdown(id, cur.timerLaser);
            game.newLaser(id);
          }

          // Countdown Laser Timer
          if(cur.waitLaser > 0) game.setLaserCountdown(id, cur.waitLaser - 1);


          // Update positions
          game.moveShip(id);

          // Loop around the borders
          if(cur.posX > SCREEN_WIDTH + 20)  game.setPos(id,-20.0, cur.posY);
          if(cur.posX < -20) game.setPos(id, SCREEN_WIDTH + 20, cur.posY);
          if(cur.posY > SCREEN_HEIGHT + 20) game.setPos(id, cur.posX,   -20);
          if(cur.posY < -20) game.setPos(id, cur.posX, SCREEN_HEIGHT + 20);

          // Move lasers
          var ls = game.getLasers();
          for(Laser l : ls) {
            if(l != null && l.id == id) {
              game.moveLaser(ls.indexOf(l));
            }
          }

          // Check laser-rock collisions and remove destroyed rocks
          // only check the client's lasers, but check collisions with all rocks
          for(Laser l : ls) {
            if(l.id == id) {
              var rocks = game.getRocks();
              for(Rock r : rocks) {
                if(Math.abs(Math.sqrt(l.posX * l.posX - r.posX * r.posX + l.posY * l.posY + r.posY * r.posY)) < r.radius) {
                  game.damageRock(rocks.indexOf(r), id);
                  System.out.println("Laser touched rock!");
                }
              }
            }
          }

          // Check player-rock collisions
          for(Rock r : game.getRocks()) {
            if(Math.hypot((cur.posX - r.posX), (cur.posY - r.posY)) < r.radius) {
            //if(Math.sqrt(cur.posX * cur.posX - r.posX * r.posX + cur.posY * cur.posY + r.posY * r.posY) < r.radius) {
              //game.damageShip(cur);
              System.out.println("Player touched rock!");
            }
          }

          // Remove old, out-of-bounds lasers
          // TODO: this needs improvement, not really nice to always redo the list
          if(ls.size() > 100) {
            var newLS = new ArrayList<Laser>();
            for(Laser l : ls) {
              if(l.posX < 0 || l.posX > SCREEN_WIDTH || l.posY < 0 || l.posY > SCREEN_HEIGHT) continue;
              newLS.add(l);
            }
            game.setLasers(newLS);
          }

          // Update Rock countdown / create rock
          var newRocks = new ArrayList<Rock>();
          for(Rock r : game.getRocks()) if(r.size > 0) newRocks.add(r);
          if(rockCountdown == 0 && game.getRocks().size() < MAX_ROCKS) {
            newRocks.add(new Rock(this.id, game.getShips(), SCREEN_WIDTH, SCREEN_HEIGHT));
            rockCountdown = rockCountdownMax;
          }

          game.setRocks(newRocks);
          rockCountdown--;

          // Update Rock Positions and loop around the border
          var rs = game.getRocks();
          for(Rock r : rs) {
            // Move the rock and if needed loop around the screen
            if(r.id == id) {
              int index = rs.indexOf(r);
              // loop
              if(r.posX + r.radius > SCREEN_WIDTH + 40)       game.setRockPos(index,-40, r.posY);
              else if(r.posX + r.radius < -40)                game.setRockPos(index, SCREEN_WIDTH + 40, r.posY);
              else if(r.posY + r.radius > SCREEN_HEIGHT + 40) game.setRockPos(index, r.posX,   -40);
              else if(r.posY + r.radius < -40)                game.setRockPos(index, r.posX, SCREEN_HEIGHT + 40);
              // move
              else game.moveRock(rs.indexOf(r));
            }
          }
        }
      } catch (Exception eTimer) { eTimer.printStackTrace(); }

      // Paint to canvas
      repaint();
    });

    t.start();

    addMouseListener(new MouseAdapter() {
      public void mouseMoved(MouseEvent e) {
        System.out.println("EVENT x: " + e.getX() + " y: " + e.getY());
        System.out.println("VARS  x: " + mouseX + " y: " + mouseY);
        System.out.println();
        mouseX = e.getX();
        mouseY = e.getY();
      }
      public void mouseClicked(MouseEvent e) {
        try {
          game.createShip(id);
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




  public void paintComponent(Graphics g) {
    // Paint black backdrop
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    // Paint random stars in background
    for(int i = 0; i < 40; i++) {
      g.setColor(Color.WHITE);
      g.fillOval((int) (r1[i] * SCREEN_WIDTH), (int) (r2[i] * SCREEN_HEIGHT), 2, 2);
    }

    try {
      // Paint ships
      for (Ship cur : game.getShips().values()) {
        g.setColor(Color.GREEN);
        if(cur.id == this.id) g.setColor(Color.CYAN);

        double cos = Math.cos(Math.toRadians(cur.angle));
        double sin = Math.sin(Math.toRadians(cur.angle));

        // Center circle in Ship (center of ship)
        g.fillOval((int) cur.posX - 5, (int) cur.posY - 5, 10, 10);

        // Directional triangle around ship
        g.drawPolygon(new int[] {
                            (int) (cur.posX + 20 * cos), // Front
                            (int) (cur.posX - 20 * cos - 15 * sin), // Left
                            (int) (cur.posX - 20 * cos + 15 * sin)}, // Right
                      new int[] {
                            (int) (cur.posY - 20 * sin), // Front
                            (int) (cur.posY + 20 * sin - 15 * cos), // Left
                            (int) (cur.posY + 20 * sin + 15 * cos)}, // Right
                      3);

        if(cur.id == this.id) {
          // UI Overlay
          String uiPoints = "Points: " + cur.points;
          String uiAngle = "Angle: " + cur.angle;
          String uiVel = "Vel(x,y): " + cur.velX + ":" + cur.velY;
          String uiSpeed = "Speed: " + cur.speed;
          String uiLaser = "Laser(waitTime): " + cur.waitLaser;
          String uiLaserCount = "Laser(count): " + game.getLasers().size();
          String uiRockCount = "Rock(count): " + game.getRocks().size() + "/" + MAX_ROCKS;
          String uiRockCD = "Rock(cd): " + (rockCountdownMax - rockCountdown) + "/" + rockCountdownMax;
          String uiMousePos = "Mouse(x,y): " + mouseX + ":" + mouseY;

          g.setColor(Color.WHITE);
          g.drawChars(uiPoints.toCharArray(), 0, uiPoints.length(), 100, 100);
          g.drawChars(uiAngle.toCharArray(), 0, uiAngle.length(), 100, 120);
          g.drawChars(uiVel.toCharArray(), 0, uiVel.length(), 100, 140);
          g.drawChars(uiSpeed.toCharArray(), 0, uiSpeed.length(), 100, 160);
          g.drawChars(uiLaser.toCharArray(), 0, uiLaser.length(), 100, 180);
          g.drawChars(uiLaserCount.toCharArray(), 0, uiLaserCount.length(), 100, 200);
          g.drawChars(uiRockCount.toCharArray(), 0, uiRockCount.length(), 100, 220);
          g.drawChars(uiRockCD.toCharArray(), 0, uiRockCD.length(), 100, 240);
          g.drawChars(uiMousePos.toCharArray(), 0, uiMousePos.length(), 100, 260);
        }
      }

      // Paint Lasers
      for(Laser l : game.getLasers()) {
        ((Graphics2D) g).setStroke(new BasicStroke(3));

        g.setColor(Color.RED);
        g.fillOval((int) l.posX, (int) l.posY, 5, 5);
//        g.drawPolygon(new int[] {
//                        (int) (l.posX),
//                        (int) (l.posX + l.velX)},
//                      new int[] {
//                        (int) (l.posY),
//                        (int) (l.posY + l.velY)},
//                    3);
      }

      // Paint Rocks
      for(Rock r : game.getRocks()) {
        g.setColor(Color.WHITE);
        g.fillOval((int) (r.posX - r.radius),
                   (int) (r.posY - r.radius),
                   (int) r.radius,
                   (int) r.radius);
      }
    } catch (Exception ePaint) { ePaint.printStackTrace(); }
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.add(new AsteroidsClient(args[0]));
    f.pack();
    f.setVisible(true);
  }

  public void move() {
  }
}
