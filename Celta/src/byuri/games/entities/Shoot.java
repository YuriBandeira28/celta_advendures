package byuri.games.entities;

import byuri.games.main.Game;
import byuri.games.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shoot extends Entity{


    private double dx, dy, spd = 3;

    private int life = 50, currLife = 0;
    public Shoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
        super(x, y, width, height, sprite);
        this.dx = dx;
        this.dy = dy;
    }
    public void tick(){
        x +=dx*spd;
        y +=dy*spd;
        currLife ++;
        if (currLife == life){
            Game.bullets.remove(this);
            return;
        }
    }


    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
    }
}
