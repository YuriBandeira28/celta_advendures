package byuri.games.world;

import byuri.games.entities.*;
import byuri.games.graficos.Spritesheet;
import byuri.games.main.Game;
import jdk.nashorn.internal.runtime.regexp.JoniRegExp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class World {

    public static Tile[] tiles;
    public static int WIDTH, HEIGHT;
    public static final int TILE_SIZE = 16;
    public World(String path){
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            int[] pixels = new int[map.getWidth()* map.getHeight()];

            WIDTH = map.getWidth();
            HEIGHT = map.getHeight();

            tiles = new Tile[map.getWidth()* map.getHeight()];

            map.getRGB(0,0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

            for(int xx = 0; xx < map.getWidth(); xx++){
                for(int yy = 0; yy < map.getHeight(); yy++) {
                    int pixel_atual = pixels[xx + (yy * map.getWidth())];
                    tiles[xx + (yy* WIDTH)] = new Floor_tile(xx*16, yy*16, Tile.TILE_FLOOR);
                    if(pixel_atual == 0xFF000000){
                        //chão
                        tiles[xx + (yy* WIDTH)] = new Floor_tile(xx*16, yy*16, Tile.TILE_FLOOR);
                    }
                    else if(pixel_atual == 0xFFffffff){
                        //parede
                        tiles[xx + (yy* WIDTH)] = new Wall_tile(xx*16, yy*16, Tile.TILE_WALL);
                    }
                    else if(pixel_atual == 0xFF5b6ee1){
                        //player
                        Game.player.setX(xx*16);
                        Game.player.setY(yy*16);

                    }
                    else if(pixel_atual == 0xFFfbf236){
                        //BALA
                        Game.entities.add(new Bullet(xx*16, yy*16, 16,16, Entity.BULLET_ENT));
                    }
                    else if(pixel_atual == 0xFFd77bba){
                        //vida
                        Game.entities.add(new Life(xx*16, yy*16, 16,16, Entity.LIFE_ENT));
                    }
                    else if(pixel_atual == 0xFFdf7126){
                        //arma
                        Game.entities.add(new Weapon(xx*16, yy*16, 16,16, Entity.WEAPON_ENT));
                    }
                    else if(pixel_atual == 0xFFac3232){
                        //inimigo
                        Enemy en = new Enemy(xx*16, yy*16, 16,16, Entity.ENEMY_ENT);
                        Game.entities.add(en);
                        Game.enemies.add(en);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void render(Graphics g){

        int xStart = Camera.x >> 4;
        int yStart = Camera.y >> 4;


        int xEnd = xStart + (Game.WIDTH >>4 );
        int yEnd = yStart + (Game.HEIGHT >>4);

        for (int xx = xStart; xx <= xEnd; xx++){
            for(int yy = yStart; yy <= yEnd; yy++){

                if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT){
                    continue;
                }
                Tile tile = tiles[xx + (yy* WIDTH)];
                tile.render(g);
            }

        }
    }

    public static void restartGame(String level){
        Game.entities = new ArrayList<Entity>();
        Game.enemies = new ArrayList<Enemy>();

        Game.spritesheet = new Spritesheet("/spritesheet.png");
        Game.player = new Player(0,0, 16,16, Game.spritesheet.getSprite(32, 0, 16, 16));
        Game.entities.add(Game.player);
        Game.world = new World("/"+level);
        return;
    }
    public static boolean isFree(int xNext, int yNext, int zPlayer){
        int x1 = xNext / TILE_SIZE;
        int y1 = yNext / TILE_SIZE;

        int x2 = (xNext + TILE_SIZE - 1)/ TILE_SIZE;
        int y2 = yNext/ TILE_SIZE;

        int x3 = xNext/ TILE_SIZE;
        int y3 = (yNext + TILE_SIZE - 1)/ TILE_SIZE;

        int x4 = (xNext + TILE_SIZE - 1)/ TILE_SIZE;
        int y4 = (yNext + TILE_SIZE - 1)/ TILE_SIZE;


        if (!((tiles[x1 + (y2 * World.WIDTH)] instanceof Wall_tile ||
                tiles[x2 + (y2 * World.WIDTH)] instanceof Wall_tile ||
                tiles[x3 + (y3 * World.WIDTH)] instanceof Wall_tile ||
                tiles[x4 + (y4 * World.WIDTH)] instanceof Wall_tile))){
            return true;
        }if (zPlayer >0){
            return true;
        }

        return false;
    }
}
