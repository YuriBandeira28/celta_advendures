package byuri.games.entities;

import byuri.games.graficos.Spritesheet;
import byuri.games.main.Game;
import byuri.games.world.Camera;
import byuri.games.world.World;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;

import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity{

    public boolean right, up, left, down;
    public int right_dir = 0, left_dir = 1;

    public int dir = right_dir;
    public double speed = 1.5;

    private int frames = 0, maxFrames = 10, index = 0, maxIndex = 3;
    private boolean moved = false;
    private BufferedImage[] rightPlayer;
    private BufferedImage[] leftPlayer;
    private BufferedImage playerDmg;
    public int dmgFrames = 0;

    public double vida = 100, maxLife = 100;

    public int municao = 0;

    public boolean isDamaged = false;
    private boolean hasGun = false;
    public boolean shooting = false, mouseShooting = false;
    public int mx, my;

    public boolean jump = false, jumping = false, jumpUp = false, jumpDown = false;
    public int z = 0, jumpFrames = 25, jumpCur = 0, jumpSpeed = 2;
    public Player(int x, int y, int width, int height, BufferedImage sprite){
        super(x, y, width, height, sprite);

        rightPlayer = new BufferedImage[4];
        leftPlayer = new BufferedImage[4];


        playerDmg = Game.spritesheet.getSprite(0,32, 16,16);
        for(int i = 0; i < rightPlayer.length; i++){

            rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);

        }
        for(int i = 0; i < leftPlayer.length; i++){
            leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
        }


    }
    public void render(Graphics g){
        if (!isDamaged) {
            if (dir == right_dir) {
                g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
                if (hasGun){
                    g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 6, this.getY() - Camera.y - z, null);
                }
            } else if (dir == left_dir) {
                g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
                if (hasGun){
                    g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 6, this.getY() - Camera.y - z, null);
                }
            }
        }else {
            g.drawImage(playerDmg, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
            if (hasGun) {
                if (dir == right_dir) {
                    g.drawImage(Entity.GUN_RIGHT, this.getX() - Camera.x + 6, this.getY() - Camera.y - z, null);
                } else if (dir == left_dir) {
                    g.drawImage(Entity.GUN_LEFT, this.getX() - Camera.x - 6, this.getY() - Camera.y - z, null);
                }
            }
        }
        if (jumping){
            g.setColor(Color.BLACK);
            g.fillOval(this.getX() - Camera.x +6, this.getY() - Camera.y+16, 8,8 );
        }
    }
    public void tick(){
        moved = false;
        if(right && World.isFree((int)(x+speed), this.getY(), z)){
            moved = true;
            dir = right_dir;
            x+=speed;
        }
        else if(left &&  World.isFree((int)(x-speed), this.getY(), z)){
            moved = true;
            dir = left_dir;
            x-=speed;
        }
        if(up &&  World.isFree(this.getX(), (int)(y - speed), z)){
            moved = true;
            y-=speed;
        }
        else if(down &&  World.isFree(this.getX(), (int)(y + speed), z)){
            moved = true;
            y+=speed;
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

        if(this.hasGun && this.shooting && municao > 0){
            municao --;
            shooting = false;
            int dx = 0;
            int px = 0;
            int py = + 6;

            if(dir == right_dir){
                dx = 1;
                px = + 18;
            }else{
                dx = - 1;
                px = -8 ;
            }

            Shoot bullet = new Shoot(this.getX() + px, this.getY() + py, 3,3,null, dx, 0);
            Game.bullets.add(bullet);
        }

        if(this.hasGun && this.mouseShooting && municao > 0){
            municao --;
            mouseShooting = false;
            double angle = 0;

            int px = 0;
            int py = 6;

            if(dir == right_dir){
                angle = Math.atan2(my - (this.getY()+8 - Camera.y), mx - (this.getX()+px - Camera.x));
                px = + 18;
            }else{
                angle = Math.atan2(my - (this.getY()+8 - Camera.y), mx - (this.getX()+px - Camera.x));
                px = -8 ;
            }
            double dx = Math.cos(angle);
            double dy= Math.sin(angle);

            Shoot bullet = new Shoot(this.getX() + px, this.getY() + py, 3,3,null, dx, dy);
            Game.bullets.add(bullet);
        }
        Camera.x = Camera.clamp(this.getX() - (Game.WIDTH /2), 0, (World.WIDTH*16) - Game.WIDTH);
        Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT /2), 0, (World.HEIGHT*16) - Game.HEIGHT);

        if (jump) {
            if (jumping == false) {
                jump = false;
                jumping = true;
                jumpUp = true;
            }
        }
        if(jumping){
            if (jumpUp){
                jumpCur +=jumpSpeed;
            }else if (jumpDown){
                jumpCur -=jumpSpeed;
                if (jumpCur <= 0){
                    jumping = false;
                    jumpUp = false;
                    jumpDown = false;
                }
            }
            z = jumpCur;
            if (jumpCur >= jumpFrames){
                jumpUp = false;
                jumpDown = true;
            }
        }
        this.checkColissionLife();
        this.checkColissionAmmo();
        this.checkColissionGun();


        if (isDamaged){
            this.dmgFrames ++;
            if (this.dmgFrames == 8){
                this.dmgFrames = 0;
                this.isDamaged = false;
            }
        }

        if (this.vida <= 0){
            this.vida = 0;
            World.restartGame("level"+Game.CURR_LEVEL+".png");
            Game.gameState = "GAME_OVER";
            Game.showMessageGameOver = true;
        }
    }

    public  void checkColissionLife(){
        for(int i =0; i < Game.entities.size(); i++){
            Entity entAtual = Game.entities.get(i);
            if (entAtual instanceof Life){
                if (entAtual.isColiding(this, entAtual)){
                    vida +=30;
                    Game.entities.remove(entAtual);
                    if (vida >= maxLife){
                        vida  = maxLife;
                    }
                }
            }
        }
    }
    public  void checkColissionAmmo(){
        for(int i =0; i < Game.entities.size(); i++){
            Entity entAtual = Game.entities.get(i);
            if (entAtual instanceof Bullet){
                if (entAtual.isColiding(this, entAtual)){
                    municao +=100;
                    Game.entities.remove(entAtual);
                }
            }
        }
    }
    public  void checkColissionGun(){
        for(int i =0; i < Game.entities.size(); i++){
            Entity entAtual = Game.entities.get(i);
            if (entAtual instanceof Weapon){
                if (entAtual.isColiding(this, entAtual)){
                    hasGun = true;
                    Game.entities.remove(entAtual);
                }
            }
        }
    }



}
