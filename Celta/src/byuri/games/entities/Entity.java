package byuri.games.entities;

import byuri.games.main.Game;
import byuri.games.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Entity {

    public static BufferedImage LIFE_ENT = Game.spritesheet.getSprite(96, 0, 16,16);
    public static BufferedImage WEAPON_ENT = Game.spritesheet.getSprite(112, 0, 16,16);
    public static BufferedImage BULLET_ENT = Game.spritesheet.getSprite(0, 16, 16,16);
    public static BufferedImage ENEMY_ENT = Game.spritesheet.getSprite(16, 16, 16,16);
    public static BufferedImage ENEMY_DMG_RIGHT = Game.spritesheet.getSprite(80, 96, 16,16);
    public static BufferedImage ENEMY_DMG_LEFT = Game.spritesheet.getSprite(80, 112, 16,16);

    public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(112, 0, 16,16);
    public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(128, 0, 16,16);


    protected double x, y, z;

    public int width, height;
    private int maskX, maskY, maskW, maskH;
    public BufferedImage sprite;

    public Entity(int x, int y, int width, int height, BufferedImage sprite){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.maskX = 0;
        this.maskY = 0;
        this.maskW = width;
        this.maskH = height;
    }

    // Define os getters (para acessar os valores sem precisar deixar eles static)
    public int getX(){
        return  (int)this.x;
    }
    public int getY(){
        return  (int)this.y;
    }
    public int getWidth(){
        return  this.width;

    }
    public int getHeight(){
        return  this.height;
    }
    // fim dos getters

    //define os setters (para definir valores as variáveis)
    public void setX(int newX) {
        this.x = newX;
    }
    public void setY(int newY) {
        this.y = newY;
    }

    public void setMask(int maskX, int maskY, int maskW, int maskH){
        this.maskX = maskX;
        this.maskY = maskY;
        this.maskW = maskW;
        this.maskH = maskH;
    }

    // fim dos setters

    public void render(Graphics g){
        g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y,null);
    }

    public void tick(){
    }

    public static boolean isColiding(Entity e1, Entity e2){
        Rectangle ent1 = new Rectangle(e1.getX() +e1.maskX, e1.getY()+e1.maskY, e1.maskW, e1.maskH);
        Rectangle ent2 = new Rectangle(e2.getX() +e2.maskX, e2.getY()+e2.maskY, e2.maskW, e2.maskH);
        if(ent1.intersects(ent2) && e1.z == e2.z){
            return true;
        }
        return false;
    }
}
