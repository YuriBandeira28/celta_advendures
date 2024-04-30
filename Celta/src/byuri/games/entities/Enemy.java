package byuri.games.entities;

import byuri.games.main.Game;
import byuri.games.main.Sound;
import byuri.games.world.AStar;
import byuri.games.world.Camera;
import byuri.games.world.Vector2i;
import byuri.games.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Entity{

    private double speed = 0.5;
    private int maskX = 5, maskY = 5, maskW= 10, maskH = 10;

    private boolean moved = false;
    public int right_dir = 0, left_dir = 1;
    public int dir = right_dir;
    private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;

    private boolean isDamaged = false;
    public int dmgFrames = 0;

    private int life = 3;

    private BufferedImage[] rightEnemy;
    private BufferedImage[] leftEnemy;
    public Enemy(int x, int y, int width, int height, BufferedImage sprite) {

        super(x, y, width, height, sprite);

        rightEnemy = new BufferedImage[4];
        leftEnemy = new BufferedImage[4];

        for(int i = 0; i < rightEnemy.length; i++){
            rightEnemy[i] = Game.spritesheet.getSprite(0 + (i*16), 96, 16, 16);
        }
        for(int i = 0; i < leftEnemy.length; i++){
            leftEnemy[i] = Game.spritesheet.getSprite(0 + (i*16), 112, 16, 16);
        }
    }

    public void tick() {

        if (!isColidingWithPlayer()) {
            if (path == null || path.size() == 0) {
                Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
                Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));

                path = AStar.findPath(Game.world, start, end);
            }
        } else {
            if (new Random().nextInt(100) < 50) {
                Sound.hit.play();
                Game.player.vida -= Game.rand.nextInt(3);
                Game.player.isDamaged = true;
            }
        }
        if (new Random().nextInt(100)< 70){
            followPath(path);
        }
        if (new Random().nextInt(100)< 10){
            Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
            Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));

            path = AStar.findPath(Game.world, start, end);
        }

        frames++;
        if (frames == maxFrames) {
            frames = 0;
            index++;

            if (index > maxIndex) {
                index = 0;
            }
        }
        colidingBullet();

        if (life <= 0) {
            destroySelf();
            return;
        }

        if (isDamaged) {
            this.dmgFrames++;
            if (this.dmgFrames == 8) {
                this.dmgFrames = 0;
                this.isDamaged = false;
            }
        }
    }


    public void destroySelf(){
        Game.enemies.remove(this);

        Game.entities.remove(this);
    }


    public void colidingBullet(){
        for(int i = 0; i < Game.bullets.size(); i++){
            Shoot shoot = Game.bullets.get(i);
            if(Entity.isColiding(this, shoot)){
                Sound.hit.play();
                life --;
                isDamaged = true;
                Game.bullets.remove(i);
                return;
            }
        }
    }

    public boolean isColidingWithPlayer(){
        Rectangle enemyCurrent = new Rectangle(this.getX() +maskX, this.getY()+maskY, maskW, maskH);
        Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), Game.player.getWidth(), Game.player.getHeight());

        return enemyCurrent.intersects(player);
    }


    public void render(Graphics g){
//        super.render(g);
//        g.setColor(Color.BLUE);
//        g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskW,maskH);
        if(dir == right_dir){
            if(isDamaged){
                g.drawImage(ENEMY_DMG_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y, null);
            }else{
                g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
            }
        }
        else if(dir == left_dir){
            if(isDamaged){
                g.drawImage(ENEMY_DMG_LEFT, this.getX() - Camera.x, this.getY() - Camera.y, null);
            }
            else{
                g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
            }
        }

    }
}

