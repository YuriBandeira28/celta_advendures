package byuri.games.entities;

import byuri.games.main.Game;
import byuri.games.world.Camera;
import byuri.games.world.Node;
import byuri.games.world.Vector2i;

import java.util.List;
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

    protected List<Node> path;

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

    public void followPath(List<Node> path){
        if (path != null){
            if(path.size() > 0){
                Vector2i target = path.get(path.size() - 1).tile;
                //xPrev = x;
                //yPrev = y;
                if( x < target.x * 16 && !isColidingEnt(this.getX() + 1, this.getY())){
                  x++;
                }else if(x > target.x*16 && !isColidingEnt(this.getX() - 1, this.getY())){
                    x--;
                }
                if(y < target.y * 16 && !isColidingEnt(this.getX(), this.getY() + 1)){
                    y++;
                }else if(y > target.y*16 && !isColidingEnt(this.getX(), this.getY() - 1)){
                    y--;
                }

                if (x == target.x * 16 && y == target.y * 16){
                    path.remove(path.size() -1);
                }
            }
        }
    }
    public double calculateDistance(int x1, int y1, int x2, int y2){

        return Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
    }
    public static boolean isColiding(Entity e1, Entity e2){
        Rectangle ent1 = new Rectangle(e1.getX() +e1.maskX, e1.getY()+e1.maskY, e1.maskW, e1.maskH);
        Rectangle ent2 = new Rectangle(e2.getX() +e2.maskX, e2.getY()+e2.maskY, e2.maskW, e2.maskH);
        if(ent1.intersects(ent2) && e1.z == e2.z){
            return true;
        }
        return false;
    }

    public boolean isColidingEnt(int xNext, int yNext){
        Rectangle enemyCurrent = new Rectangle(xNext +maskX, yNext+maskY, maskW, maskH);
        for (int i =0; i < Game.enemies.size(); i++){
            Enemy e = Game.enemies.get(i);
            if (e == this){
                continue;
            }
            Rectangle targetEnemy = new Rectangle(e.getX()+ maskX, e.getY()+ maskY, maskW, maskH);
            if (enemyCurrent.intersects(targetEnemy)){
                return true;
            }
        }
        return false;
    }
}
