package byuri.games.main;

import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {

    private AudioClip clip;

    public static final Sound musicBackground = new Sound("/background.wav");

    public static final Sound hit = new Sound("/hit.wav");

    private Sound(String name){
        try{
            clip = Applet.newAudioClip(Sound.class.getResource(name));
        }catch (Throwable e){
            System.out.println("Erro a carregar som - " + e);
        }
    }

    public void play(){
        try{
            new Thread(){
                public void run(){
                    clip.play();
                }
            }.start();
        }catch (Throwable e){
            System.out.println("Erro ao rodar som - " + e);
        }
    }
    public void loop(){
        try{
            new Thread(){
                public void run(){
                    clip.loop();
                }
            }.start();
        }catch (Throwable e){
            System.out.println("Erro ao rodar som - " + e);
        }
    }

}
