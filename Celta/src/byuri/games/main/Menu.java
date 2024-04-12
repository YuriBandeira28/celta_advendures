package byuri.games.main;

import java.awt.*;

public class Menu {

    public String[] options = {"Novo Jogo", "Carregar jogo", "Sair", "Continuar"};

    public int currOpt = 0, maxOpt = options.length -1;
    public boolean up, down, enter;

    public boolean pause = false;

    public void tick(){
        if (up) {
            up = false;
            currOpt --;
            if (currOpt < 0){
                currOpt = maxOpt;
            }
        }
        if (down){
            down = false;
            currOpt ++;
            if (currOpt > maxOpt){
                currOpt = 0;
            }
        }
        if (enter){
            enter = false;
            if (options[currOpt] == "Novo Jogo" || options[currOpt] == "Continuar"){
                Game.gameState = "NORMAL";
            }else if (options[currOpt] == "Carregar jogo"){

            }else if (options[currOpt] == "Sair"){
                System.exit(1);
            }
        }
    }

    public void render(Graphics g){
        g.setColor(new Color(0,0,0,150));
        g.fillRect(0,0, Game.WIDTH*Game.SCLAE, Game.HEIGHT*Game.SCLAE);
        g.setFont(new Font("Serif", Font.BOLD, 60));
        g.setColor(Color.WHITE);
        g.drawString("Celta Adventures", (Game.WIDTH*Game.SCLAE)/2 - 220, 60);

        g.setFont(new Font("arial", Font.BOLD, 40));
        if(pause){
            g.drawString("Continuar", (Game.WIDTH*Game.SCLAE)/2 - 90, (Game.HEIGHT*Game.SCLAE)/2 - 60);

        }else{
            g.drawString("Novo jogo", (Game.WIDTH*Game.SCLAE)/2 - 100, (Game.HEIGHT*Game.SCLAE)/2 - 60);

        }
        g.drawString("Carregar jogo", (Game.WIDTH*Game.SCLAE)/2 - 130, (Game.HEIGHT*Game.SCLAE)/2);
        g.drawString("Sair", (Game.WIDTH*Game.SCLAE)/2 - 40, (Game.HEIGHT*Game.SCLAE)/2 + 60);

        if (options[currOpt] == "Novo Jogo"){
            g.drawString(">", (Game.WIDTH*Game.SCLAE)/2 - 130, (Game.HEIGHT*Game.SCLAE)/2 - 60);
        }else if (options[currOpt] == "Carregar jogo"){
            g.drawString(">", (Game.WIDTH*Game.SCLAE)/2 - 160, (Game.HEIGHT*Game.SCLAE)/2);
        }else if (options[currOpt] == "Sair"){
            g.drawString(">", (Game.WIDTH*Game.SCLAE)/2 - 70, (Game.HEIGHT*Game.SCLAE)/2 + 60);
        }
    }

}
