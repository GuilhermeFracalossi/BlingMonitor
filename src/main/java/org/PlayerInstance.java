package org;


import javafx.scene.image.ImageView;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.util.ArrayList;
import java.util.List;


public class PlayerInstance extends MediaPlayerEventAdapter {

    private final EmbeddedMediaPlayer mediaPlayer;
    private final ImageView videoSurface;
    protected String cameraAddress;
    protected String cameraIp;
    protected int cameraPort;
    protected boolean cameraOpen;
    private static PlayerInstance instance;

    private int id;
    private float gamma = 1;
    private float brightness= 1;
    private float saturation = 1;
    private float contrast = 1;

    public float getGamma() {
        return gamma;
    }
    public float getBrightness() {
        return brightness;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getContrast() {
        return contrast;
    }

    public static List<PlayerInstance> players = new ArrayList<PlayerInstance>();

    public PlayerInstance(int id, EmbeddedMediaPlayer mediaPlayer, String cameraAddress,  float gamma, float brightness, float saturation, float contrast) {
        this.id = id;

        this.mediaPlayer = mediaPlayer;
        this.videoSurface = new ImageView();
        this.cameraAddress = cameraAddress;
        this.cameraPort = cameraPort;

        this.gamma = gamma;
        this.brightness = brightness;
        this.saturation = saturation;
        this.contrast = contrast;

        players.add(this);
        mediaPlayer.events().addMediaPlayerEventListener(this);
    }

    public EmbeddedMediaPlayer mediaPlayer() {
        return mediaPlayer;
    }

    public ImageView videoSurface() {
        return videoSurface;
    }

    public String cameraAddress(){
        return cameraAddress;
    }
    public String cameraIp(){
        return cameraIp;
    }
    public int cameraPort(){
        return cameraPort;
    }
    public boolean getCameraOpen(){
        return cameraOpen;
    }
    public void setCameraOpen(Boolean cameraOpen){
        this.cameraOpen = cameraOpen;
    }

    public static boolean releaseAll(){
        // Release all player instances
        if(players.isEmpty()){
            return false;
        }

        for (int i = 0; i < players.size(); i++){
            players.get(i).mediaPlayer().controls().stop();
            players.get(i).mediaPlayer().release();
        }
        players.clear();
        return true;
    }

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, MediaRef media) {
        //System.out.println("mediaChanged");
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        //System.out.println("playing");
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
       // System.out.println("paused");
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        //System.out.println("stopped");
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        //System.out.println("finished");
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        //System.out.println("error");
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {
        //System.out.println("opening");
    }

    public int getId() {
        return this.id;
    }
}
