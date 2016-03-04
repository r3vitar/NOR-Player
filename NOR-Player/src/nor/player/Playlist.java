/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nor.player;

import java.util.ArrayList;
import javafx.scene.media.AudioClip;

/**
 *
 * @author kacpe_000
 */
public class Playlist {
    private ArrayList<AudioClip> playlist;

    public ArrayList<AudioClip> getPlaylist() {
        return playlist;
    }

    public Playlist() {
        this.playlist = new ArrayList<AudioClip>();
        
    }
    
    public Playlist(ArrayList<AudioClip> playlist) {
        this.playlist = playlist;
        
    }
    
    public Playlist(AudioClip audio){
        this.playlist = new ArrayList<>();
        this.playlist.add(audio);
    }
    
    public void addAudioClip(AudioClip audio){
        this.playlist.add(audio);
    }
    
    
    
    
}
