import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

import static java.lang.Thread.sleep;

public class SimpleVectorGraphics extends JFrame implements Runnable {

    private static final int SCREEN_W = 640;
    private static final int SCREEN_H = 480;

    private FrameRate frameRate;
    private BufferStrategy bs;
    private volatile boolean running;
    private Thread gameThread;
    private RelativeMouseInput mouse;
    private KeyboardInput keyboard;
    private Vector2f[] polygon;
    private Vector2f[] world;
    private float tx, ty;
    private float vx, vy;
    private float rot, rotStep;
    private float scale, scaleStep;
    private float sx, sxStep;
    private float sy, syStep;
    private boolean doTranslate;
    private boolean doScale;
    private boolean doRotate;
    private boolean doXShear;
    private boolean doYShear;

    public SimpleVectorGraphics() { }

    protected void createAndShowGUI() {

        Canvas canvas = new Canvas();
        canvas.setSize(640, 480);
        canvas.setBackground(Color.BLACK);
        canvas.setIgnoreRepaint(true)
        getContentPane().add(canvas);
        setTitle("Vector Graphics Example");
        setIgnoreRepaint(true);
        pack();

        keyboard = new KeyboardInput();
        canvas.addKeyListener(keyboard);

        mouse = new RelativeMouseInput(canvas);
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);

        setVisible(true);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        canvas.requestFocus();

        gameThread = new Thread(this);
        gameThread.start();
    }

    public void run() {
        running = true;
        initialize();
        while (running) {
            gameLoop();
        }
    }

    private void gameLoop() {
        processInput();
        processObjects();
        renderFrame();
        sleep(10L);
    }

    private void renderFrame() {
        do {
            do {
                Graphics g = null;
                try {
                    g = bs.getDrawGraphics();
                    g.clearRect(0, 0, getWidth(), getHeight());
                    render(g);
                } finally {
                    if (g != null) {
                        g.dispose();
                    }
                }
            } while(bs.contentsRestored());
            bs.show();
        } while(bs.contentsLost());
    }

    private void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) { }
    }

    private void initialize() {
        frameRate = new FrameRate();
        frameRate.initialize();
        polygon = new Vector2f[] {
            new Vector2f(10, 0),
                new Vector2f(-10, 8),
                new Vector2f(0, 0),
                new Vector2f(-10, -8),
        };

        world = new Vector2f[polygon.length];
        reset();
    }

    private void reset() {
        tx = SCREEN_W / 2;
        ty = SCREEN_H / 2;
        vx = vy= 2;
        rot = 0.0f;
        rotStep = (float)Math.toRadians(1.0);
        scale = 1.0f;
        scaleStep = 0.1f;
        sx = sy = 0.0f;
        sxStep = syStep = 0.01f;
        doRotate = doScale = doTranslate = false;
        doXShear = doYShear = false;
    }


}
