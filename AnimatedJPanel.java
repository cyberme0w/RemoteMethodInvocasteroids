import javax.swing.*;

public abstract class AnimatedJPanel extends JPanel implements Animation {
  Timer t;

  public AnimatedJPanel() {
    super(true);
    t = new Timer(30, ev -> {
      move();
      repaint();
    });

    t.start();
  }
}
