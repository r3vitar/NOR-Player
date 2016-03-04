/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nor.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import javafx.scene.media.AudioClip;

/**
 *
 * @author kacpe_000
 */
public class Playlist implements Serializable {

    private ArrayList<AudioClip> playlist;
    private String playlistName = "NoName";
    private Comparator<AudioClip> cFileNameAsc = new Comparator<AudioClip>() {

        @Override
        public int compare(AudioClip o1, AudioClip o2) {
            return audioClipToName(o1).compareTo(audioClipToName(o2));
        }

        private String audioClipToName(AudioClip audio) {
            String audioName = "";
            String[] tmp = audio.getSource().split("/");
            audioName = tmp[tmp.length - 1];

            return audioName;
        }
    };
    private Comparator<AudioClip> cFileNameDesc = new Comparator<AudioClip>() {

        @Override
        public int compare(AudioClip o1, AudioClip o2) {
            return audioClipToName(o1).compareTo(audioClipToName(o2)) * (-1);
        }

        private String audioClipToName(AudioClip audio) {
            String audioName = "";
            String[] tmp = audio.getSource().split("/");
            audioName = tmp[tmp.length - 1];

            return audioName;
        }
    };
    private Comparator<AudioClip> cPathNameDesc = new Comparator<AudioClip>() {

        @Override
        public int compare(AudioClip o1, AudioClip o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2)) * (-1);
        }

        private String audioClipToPath(AudioClip audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };
    private Comparator<AudioClip> cPathNameAsc = new Comparator<AudioClip>() {

        @Override
        public int compare(AudioClip o1, AudioClip o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2));
        }

        private String audioClipToPath(AudioClip audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };

    public ArrayList<AudioClip> getPlaylist() {
        return playlist;
    }

    public Playlist() {
        this.playlist = new ArrayList<AudioClip>();

    }

    public Playlist(ArrayList<AudioClip> playlist) {
        this.playlist = playlist;

    }

    public Playlist(AudioClip audio) {
        this.playlist = new ArrayList<>();
        this.playlist.add(audio);
    }

    public void addAudioClip(AudioClip audio) {
        this.playlist.add(audio);
    }

    public void addAudioClip(String filePath) {
        this.addAudioClip(createAudio(filePath));
    }

    private AudioClip createAudio(String filePath) {
        return new AudioClip(filePath);
    }

    public void addAudioClipArray(ArrayList<Object> audioArray) {
        if (audioArray.isEmpty()) {
            throw new IllegalArgumentException("ArraList is empty");
        } else if (audioArray.get(0) instanceof String) {
            for (Object filePath : audioArray) {
                this.playlist.add(createAudio(filePath.toString()));
            }
        } else if (audioArray.get(0) instanceof AudioClip) {
            this.playlist.addAll(playlist);

        } else {
            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
        }

    }

    public void deleteAudioClip(AudioClip audio) {
        this.playlist.remove(audio);
    }

    public void deleteAudioClip(int index) {
        this.playlist.remove(index);
    }

    public void deleteAudioClipArray(ArrayList<Object> audioArray) {
        if (audioArray.isEmpty()) {
            throw new IllegalArgumentException("ArraList is empty");
        } else if (audioArray.get(0) instanceof String) {
            for (Object filePath : audioArray) {
                this.playlist.remove(createAudio(filePath.toString()));
            }
        } else if (audioArray.get(0) instanceof AudioClip) {
            this.playlist.removeAll(playlist);

        } else {
            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
        }
    }

    public void deletePlaylist() {
        this.playlist.removeAll(this.playlist);
    }

    public void clearPlaylist() {
        this.playlist.clear();
    }

    public void sort() {
        this.playlist.sort(this.cFileNameAsc);
    }

    public void sortByNameAsc() {
        this.playlist.sort(this.cFileNameAsc);
    }
    public void sortByNameDesc() {
        this.playlist.sort(this.cFileNameDesc);
    }
    public void sortByPathAsc() {
        this.playlist.sort(this.cPathNameAsc);
    }
    public void sortByPathDesc() {
        this.playlist.sort(this.cPathNameDesc);
    }

}
