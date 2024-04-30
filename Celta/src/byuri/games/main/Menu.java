package byuri.games.main;

import byuri.games.world.World;
import com.sun.javafx.image.IntPixelGetter;
import com.sun.org.apache.xalan.internal.xsltc.dom.SimpleResultTreeImpl;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.Struct;

public class Menu {

    public String[] options = {"Novo Jogo", "Carregar jogo", "Sair", "Continuar"};

    public int currOpt = 0, maxOpt = options.length -2;
    public boolean up, down, enter;

    public static boolean pause = false;

    public static boolean saveExists = false, saveGame = false;
    public static void saveGame(String[] val1, int[] val2, int encode){
        BufferedWriter write = null;

        try{
            write = new BufferedWriter(new FileWriter("save.txt"));
        }catch (IOException e){
            System.out.println("Erro ao cirar no arquivo de save - " + e);

        }
        for (int i=0; i<val1.length; i++){
            String curr =  val1[i];
            curr += ":";
            char[] value = Integer.toString(val2[i]).toCharArray();
            for (int n = 0; n< value.length; n++){
                value[n] += encode;
                curr += value[n];
            }
            try{
                write.write(curr);
                if (i < val1.length-1){
                    write.newLine();
                }
            }catch (IOException e){
                System.out.println("Erro ao gravar no arquivo de save - " + e);
            }
        }
        try {
            write.flush();
            write.close();
        }catch (IOException e){
        }
    }

    public static String loadGame(int encode){
        String line = "";
        File file = new File("save.txt");
        if(file.exists()){
            try{
                String singleLine = null;
                BufferedReader reader = new BufferedReader(new FileReader("save.txt"));

                try{
                    while ((singleLine = reader.readLine()) != null){
                        String[] trans = singleLine.split(":");
                        char[] val = trans[1].toCharArray();
                        trans[1] = "";
                        for (int i =0; i < val.length; i ++){
                            val[i] -= encode;
                            trans[1] += val[i];
                        }
                        line += trans[0];
                        line += ":";
                        line += trans[1];
                        line += "/";
                    }
                }catch (IOException e){
                    System.out.println("Erro ao decodificar o arquivo de save - " + e);
                }


            }catch (FileNotFoundException e){
                System.out.println("Erro ao ler arquivo de save - " + e);
            }

        }else{
            System.out.println("Nenhum arquivo de save encontrado");
        }
        return line;
    }

    public static void applySave(String str){
        String[] spl = str.split("/");
        for (int i=0; i< spl.length; i++){
            String[] spl2 = spl[i].split(":");
            switch (spl2[0]){
                case "level":
                    World.restartGame("level"+ spl2[1] + ".png");
                    Game.gameState = "NORMAL";
                    break;
                case "vida":
                    Game.player.vida = Integer.parseInt(spl2[1]);
                    break;

            }
        }
    }
    public void tick(){
        File file = new File("save.txt");
        if (file.exists()){
            saveExists = true;
        }else {
            saveExists = false;
        }


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
                file = new File("save.txt");
                file.delete();
            }else if (options[currOpt] == "Carregar jogo"){
                file = new File("save.txt");
                if (file.exists()){
                    String saver = loadGame(10);
                    applySave(saver);
                }
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
