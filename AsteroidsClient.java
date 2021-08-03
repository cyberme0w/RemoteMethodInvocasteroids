import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
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
  boolean readyPressed = false;

  int mouseX = 0;
  int mouseY = 0;

  double[] r1 = new double[40];
  double[] r2 = new double[40];

  int rockCountdownMax = 90;
  int rockCountdown = rockCountdownMax;

  int deathBlinkTimer = 0;
  int deathBlinkTimerMax = 30;
  boolean allDead = false;
  boolean allReady = false;

  boolean showDebugUI = false;


  AsteroidsClient(String host, String name) {
    String shipName = name.toLowerCase();
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
          if(upPressed && cur.alive) {
            game.addVelFromAngle(id);
            if(cur.speed < cur.regSpeed) game.setSpeed(id, game.getSpeed(id) + 0.1);
          }

          if((cur.alive && !upPressed && cur.speed > 0) || (!cur.alive && cur.speed > 0)) {
            game.setSpeed(id, game.getSpeed(id) - 0.1);
          }

          if(downPressed && cur.alive && cur.speed > 0) {
            game.setSpeed(id, game.getSpeed(id) - 0.2);
          }

          // Angle
          if(leftPressed && cur.alive) game.setAngle(id, (game.getAngle(id) + 10) % 360);
          if(rightPressed && cur.alive) game.setAngle(id, (game.getAngle(id) - 10) % 360);

          // Boost
          if(boostPressed && cur.alive && cur.speed < cur.maxSpeed) {
            game.addVelFromAngle(id);
            game.setSpeed(id, game.getSpeed(id) + 0.2);
          }

          // Slow down from boost
          if(!boostPressed && cur.speed > cur.regSpeed) {
            game.setSpeed(id, game.getSpeed(id) - 0.1);
          }

          // Shooting
          if(spacePressed && cur.alive && cur.waitLaser == 0) {
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
          var lasers = game.getLasers();
          for(Laser l : lasers) {
            if(l.id == id) {
              var rocks = game.getRocks();
              for(Rock r : rocks) {
                double yDiff = Math.abs(l.posY - r.posY);
                double xDiff = Math.abs(l.posX - r.posX);
                double dist  = Math.sqrt(yDiff * yDiff + xDiff * xDiff);
                if(dist <= r.radius) {
                  game.damageRock(rocks.indexOf(r), id);
                  game.removeLaser(lasers.indexOf(l));
                  System.out.println("Laser touched rock!");
                }
              }
            }
          }

          // Check player-rock collisions
          for(Rock r : game.getRocks()) {
            double yDiff = Math.abs(cur.posY - r.posY);
            double xDiff = Math.abs(cur.posX - r.posX);
            double dist  = Math.sqrt(yDiff * yDiff + xDiff * xDiff);
            if(dist <= r.radius) {
              game.killShip(id);
              System.out.println("Player touched rock!");
            }
          }

          // Check if everyone is dead and/or ready to restart the match
          allDead = true;
          allReady = true;
          for(Ship s : game.getShips().values()) {
            if(s == null) break;
            if(s.id == id && readyPressed) game.setReady(id, true);
            if(s.alive) { allDead = false; }
            if(!s.ready) { allReady = false; }
          }

          // DEBUG
          if(showDebugUI && allDead) System.out.println("ALL DEAD");
          if(showDebugUI && allReady) System.out.println("ALL READY");

          // Remove old, out-of-bounds lasers
          // 100 should be enough, but can be changed for more/less players -> my machine started struggling with 300
          /* TODO: might be improvable by using a hashmap with randomized ids
                   -> no more need to create an array every 30 miliseconds :D */
          if(ls.size() > 100) {
            var newLS = new ArrayList<Laser>();
            for(Laser l : ls) {
              if(l.posX < 0 || l.posX > SCREEN_WIDTH || l.posY < 0 || l.posY > SCREEN_HEIGHT) continue;
              newLS.add(l);
            }
            game.setLasers(newLS);
          }


          if(allReady) {
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
                if(r.posX > SCREEN_WIDTH + r.radius)       game.setRockPos(index, -r.radius, r.posY);
                else if(r.posX < -r.radius)                game.setRockPos(index, SCREEN_WIDTH + r.radius, r.posY);
                else if(r.posY > SCREEN_HEIGHT + r.radius) game.setRockPos(index, r.posX,   -40);
                else if(r.posY < -r.radius)                game.setRockPos(index, r.posX, SCREEN_HEIGHT + r.radius);
                  // move
                else game.moveRock(rs.indexOf(r));
              }
            }
          }
        }
      } catch (Exception eTimer) { eTimer.printStackTrace(); }

      // Paint to canvas
      repaint();
    });

    t.start();

    addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
          case VK_S, VK_DOWN -> downPressed = false;
          case VK_W, VK_UP -> upPressed = false;
          case VK_A, VK_LEFT -> leftPressed = false;
          case VK_D, VK_RIGHT -> rightPressed = false;
          case VK_SPACE -> spacePressed = false;
          case VK_J -> boostPressed = false;
          case VK_R -> readyPressed = false;
        }
      }

      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case VK_ENTER -> {
            try{
              if(allDead) game.reset();
              else if(!allReady) {game.createShip(id, shipName);}
            } catch (RemoteException ex) { ex.printStackTrace();}}
          case VK_S -> downPressed = true;
          case VK_W -> upPressed = true;
          case VK_A -> leftPressed = true;
          case VK_D -> rightPressed = true;
          case VK_J -> boostPressed = true;
          case VK_SPACE -> spacePressed = true;
          case VK_R -> readyPressed = true;
        }
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

        // change colors when dead
        if(!cur.alive) {
          if(deathBlinkTimer > deathBlinkTimerMax/2) {
            g.setColor(Color.RED);
          }
          deathBlinkTimer = ((deathBlinkTimer+1) % deathBlinkTimerMax);
        }

        // some math for the ships shape
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
          g.setColor(Color.WHITE);
          if(showDebugUI) {
            g.drawString("Points: " + cur.points, 100, 100);
            g.drawString("Angle: " + cur.angle, 100, 120);
            g.drawString("Vel(x,y): " + cur.velX + ":" + cur.velY, 100, 140);
            g.drawString("Speed: " + cur.speed, 100, 160);
            g.drawString("Laser(waitTime): " + cur.waitLaser, 100, 180);
            g.drawString("Laser(count): " + game.getLasers().size(), 100, 200);
            g.drawString("Rock(count): " + game.getRocks().size() + "/" + MAX_ROCKS, 100, 220);
            g.drawString("Rock(cd): " + (rockCountdownMax - rockCountdown) + "/" + rockCountdownMax, 100, 240);
            g.drawString("Mouse(x,y): " + mouseX + ":" + mouseY, 100, 260);
          }
          Ship bestShip = cur;
          for(Ship s : game.getShips().values()) {
            if(s.points > cur.points) bestShip = s;
          }
          g.drawString("Current top ship: " + bestShip.name + " with " + bestShip.points + " points", 100, 200);
          g.drawString("     Your points: " + cur.points, 100, 220);
        }
      }

      // Paint Lasers
      for(Laser l : game.getLasers()) {
        ((Graphics2D) g).setStroke(new BasicStroke(3));

        g.setColor(Color.RED);
        g.fillOval((int) l.posX, (int) l.posY, 5, 5);
      }

      // Paint Rocks
      for(Rock r : game.getRocks()) {
        g.setColor(Color.WHITE);
        g.fillOval((int) (r.posX - r.radius),
                   (int) (r.posY - r.radius),
                   (int) r.radius,
                   (int) r.radius);
      }

      // Paint Death Screen
      Ship ship = game.getShips().get(id);
      if(ship != null && !ship.alive) {
        Ship bestShip = ship;
        for(Ship s : game.getShips().values()) bestShip = s.points > bestShip.points ? s : bestShip;
        g.setColor(Color.WHITE);
        g.drawString("You died! Your score was " + ship.points, 500, 400);
        g.drawString("The best score was " + bestShip.points + " by user:" + bestShip.name, 500, 420);

        if(!allDead) g.drawString("Waiting for all players to die...", 500, 440);
        else g.drawString("(press enter to restart)", 500, 460);
      }

      // Paint Waiting for Players to be ready screen
      if(!allReady) {
        g.setColor(Color.WHITE);
        if(!game.getShips().get(id).ready) g.drawString("Press R when you are ready!", 500, 400);
        else g.drawString("Waiting for all players to be ready...", 500, 400);
      }

    } catch (Exception ePaint) { ePaint.printStackTrace(); }
  }

  public static void main(String[] args) {
    var gameClient = new AsteroidsClient(args[0], args[1]);
    JFrame f = new JFrame();
    f.add(gameClient);
    f.setMinimumSize(new Dimension(gameClient.SCREEN_WIDTH, gameClient.SCREEN_HEIGHT));
    f.setMaximumSize(new Dimension(gameClient.SCREEN_WIDTH, gameClient.SCREEN_HEIGHT));
    f.pack();
    f.setVisible(true);
  }

  public void move() {
  }
}
