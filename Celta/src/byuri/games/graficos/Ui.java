package byuri.games.graficos;

import byuri.games.entities.Player;
import byuri.games.main.Game;

import java.awt.*;

public class Ui {


    public void render(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(8,4,100, 8);
        g.setColor(Color.GREEN);
        g.fillRect(8,4,(int)((Game.player.vida / Game.player.maxLife)*100), 8);
    }
}
