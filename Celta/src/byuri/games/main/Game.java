package byuri.games.main;

import byuri.games.entities.Enemy;
import byuri.games.entities.Entity;
import byuri.games.entities.Player;
import byuri.games.entities.Shoot;
import byuri.games.graficos.Spritesheet;
import byuri.games.graficos.Ui;
import byuri.games.world.Camera;
import byuri.games.world.World;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.internal.ws.message.stream.StreamHeader;
import netscape.javascript.JSUtil;
import sun.security.mscapi.CPublicKey;

import javax.print.DocFlavor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.security.Key;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {


    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    public static final int WIDTH = 320;
    public static final int HEIGHT = 240;
    public static final int SCLAE = 3;
    private BufferedImage image;

    public static List<Entity> entities;
    public static Spritesheet spritesheet;

    public static World world;
    public static Player player;

    public static Random rand;
    public static List<Enemy> enemies;
    public static List<Shoot> bullets;
    public  Ui ui;

    public static int  CURR_LEVEL = 1, MAX_LEVEL = 2;

    public static String gameState =  "MENU";
    public static boolean showMessageGameOver = false;
    private int framesGameOver = 0;
    private boolean restartGame = false;
    public Menu menu;

    public Game() {
        Sound.musicBackground.loop();
        rand = new Random();
        ui = new Ui();
        addKeyListener(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(WIDTH * SCLAE, HEIGHT * SCLAE));
        initFrame();

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        entities = new ArrayList<Entity>();
        enemies = new ArrayList<Enemy>();
        bullets = new ArrayList<Shoot>();

        spritesheet = new Spritesheet("/spritesheet.png");
        player = new Player(0,0, 16,16, spritesheet.getSprite(32, 0, 16, 16));
        entities.add(player);
        world = new World("/level1.png");

        menu = new Menu();


    }

    public void initFrame() {
        frame = new JFrame("Celta");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        isRunning = true;
        requestFocus();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) {
        Game game = new Game();
        game.start();
    }


    public void tick() {
        if (gameState == "NORMAL") {
            this.restartGame = false;
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                e.tick();
            }

            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).tick();
            }

            if (enemies.size() == 0) {
                CURR_LEVEL++;
                if (CURR_LEVEL > MAX_LEVEL) {
                    CURR_LEVEL = 1;
                }

                String newLevel = "level" + CURR_LEVEL + ".png";

                World.restartGame(newLevel);
            }
        }else if (gameState == "GAME_OVER"){
            this.framesGameOver ++;
            if (this.framesGameOver ==30){
                this.framesGameOver = 0;
                if (showMessageGameOver){
                    showMessageGameOver = false;
                }else{
                    showMessageGameOver = true;
                }
            }

            if (restartGame){
                this.restartGame = false;
                gameState = "NORMAL";
                CURR_LEVEL = 1;
                String newLevel = "level" + CURR_LEVEL + ".png";

                World.restartGame(newLevel);
            }
        }else if(gameState == "MENU"){
            menu.tick();
        }

    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        world.render(g);

        for(int i =0; i < entities.size(); i++){
            Entity e = entities.get(i);
            e.render(g);
        }

        for(int i=0; i < bullets.size(); i++){
            Shoot bullet = bullets.get(i);
            bullet.render(g);
        }


        ui.render(g);
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCLAE, HEIGHT * SCLAE, null);
        g.setFont(new Font("arial", Font.BOLD, 17));
        g.setColor(Color.WHITE);
        g.drawString("Munição: "+ player.municao, 800, 20);

        if (gameState == "GAME_OVER"){
            Graphics g2 = (Graphics2D) g;
            g2.setColor(new Color(0,0,0,100));
            g2.fillRect(0,0, WIDTH*SCLAE, HEIGHT*SCLAE);
            g.setFont(new Font("arial", Font.BOLD, 50));
            g.setColor(Color.WHITE);
            g.drawString("GAME OVER", (WIDTH*SCLAE)/2-150, (HEIGHT*SCLAE)/ 2-100);
            g.setFont(new Font("arial", Font.BOLD, 40));
            if (showMessageGameOver) {
                g.drawString(">PRESSIONE ENTER PARA REINICIAR<", (WIDTH * SCLAE) / 2 - 350, (HEIGHT * SCLAE) / 2 + 50);
            }
        }else if(gameState == "MENU"){
            menu.render(g);
        }
        bs.show();


    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        while (isRunning) {
            long now = System.nanoTime();

            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                //sempre atualiza o jogo antes de renderizar
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D ){
            player.right = true;

        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A ) {
            player.left = true;

        }
        if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W ){
            player.up = true;

        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S ){
            player.down = true;

        }
        if(e.getKeyCode() == KeyEvent.VK_Z){
            player.shooting = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            if (gameState == "GAME_OVER"){
                this.restartGame = true;
            }else if (gameState == "MENU"){
                menu.enter = true;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            player.jump = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D ){
            player.right = false;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A ) {
            player.left = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W ){
            player.up = false;

            if (gameState == "MENU"){
                menu.up = true;
            }

        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S ){
            player.down = false;

            if (gameState == "MENU"){
                menu.down = true;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_Z){
            player.shooting = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
           gameState = "MENU";
           menu.pause = true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        player.mouseShooting = true;
        player.mx = (e.getX() / 3);
        player.my = (e.getY() / 3);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
