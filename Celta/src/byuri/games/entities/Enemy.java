package byuri.games.entities;

import byuri.games.main.Game;
import byuri.games.main.Sound;
import byuri.games.world.Camera;
import byuri.games.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

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

    public void tick(){
        if(isColidingWithPlayer() == false){
            if(this.x < Game.player.getX() && World.isFree((int)(x+speed), this.getY(), 0) && !isColiding((int)(x+speed), this.getY())){
                this.x +=speed;
                moved = true;
                dir = right_dir;


            }else if(this.x > Game.player.getX() && World.isFree((int)(x-speed), this.getY(), 0) && !isColiding((int)(x-speed), this.getY())){
                x-=speed;
                moved = true;
                dir = left_dir;

            }
            if(this.y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed), 0) && !isColiding(this.getX(), (int)(y+speed))){
                this.y +=speed;
                moved = true;

            }else if(this.y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed), 0) && !isColiding(this.getX(), (int)(y-speed))){
                y -=speed;
                moved = true;

            }
        }else{

            if (Game.rand.nextInt(100) < 10) {
                Game.player.isDamaged = true;
                Game.player.vida--;
                Sound.hit.play();

            }

        }

        if (moved){
            frames ++;
            if (frames == maxFrames){
                frames = 0;
                index ++;

                if (index > maxIndex){
                    index = 0;
                }
            }
        }

        colidingBullet();

        if (life <=0){
            destroySelf();
            return;
        }

        if (isDamaged){
            this.dmgFrames ++;
            if (this.dmgFrames == 8){
                this.dmgFrames = 0;
                this.isDamaged = false;
            }
        }
    }

    public void destroySelf(){
        Game.enemies.remove(this);

        Game.entities.remove(this);
    }

    public boolean isColiding(int xNext, int yNext){
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
