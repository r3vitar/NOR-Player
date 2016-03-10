/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nor.player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import javafx.scene.media.AudioClip;
import static javafx.scene.media.AudioClip.INDEFINITE;
 

/**
 *
 * @author kacpe_000
 */
public class Playlist_AudioCLip_old implements Serializable {

    private ArrayList<AudioClip> playlist;
    private boolean repeatList = false;
    private boolean repeatCurrent = false;
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

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Playlist_AudioCLip_old() {
        this.playlist = new ArrayList<AudioClip>();

    }

    public Playlist_AudioCLip_old(ArrayList<AudioClip> playlist) {
        this.playlist = playlist;

    }

    public Playlist_AudioCLip_old(AudioClip audio) {
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
            throw new IllegalArgumentException("ArrayList is empty");
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
        if (audio.equals(this.playlist.get(0))) {
            stopCurrent();
        }
        this.playlist.remove(audio);
    }

    public void deleteAudioClip(int index) {
        if (index == 0) {
            stopCurrent();
        }
        this.playlist.remove(index);
    }

    public void deleteAudioClipArray(ArrayList<Object> audioArray) {
        stopCurrent();
        if (audioArray.isEmpty()) {
            throw new IllegalArgumentException("ArrayList is empty");
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
        stopCurrent();
        this.playlist.removeAll(this.playlist);
    }

    public void clearPlaylist() {
        stopCurrent();
        this.playlist.clear();
    }

    public void sort() {
        stopCurrent();
        this.playlist.sort(this.cFileNameAsc);
        playCurrent();
    }

    public void sortByNameAsc() {
        stopCurrent();
        this.playlist.sort(this.cFileNameAsc);
        playCurrent();
    }

    public void sortByNameDesc() {
        stopCurrent();
        this.playlist.sort(this.cFileNameDesc);
        playCurrent();
    }

    public void sortByPathAsc() {
        stopCurrent();
        this.playlist.sort(this.cPathNameAsc);
        playCurrent();
    }

    public void sortByPathDesc() {
        stopCurrent();
        this.playlist.sort(this.cPathNameDesc);
        playCurrent();
    }

    public void shuffle() {
        stopCurrent();
        Collections.shuffle(this.playlist);
        playCurrent();
    }

    public void shuffle(Random randomSeed) {
        stopCurrent();
        Collections.shuffle(this.playlist, randomSeed);
        playCurrent();
    }

    public AudioClip getCurrentAudioClip() {
        if (this.playlist.isEmpty()) {
            return null;
        } else {
            return this.playlist.get(0);
        }

    }

    public void playCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");
        } else {
            this.playlist.get(0).play();
        }
    }

    public void playOrStopCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");
        } else {
            if (this.playlist.get(0).isPlaying()) {
                this.playlist.get(0).play();
            } else {
                this.playlist.get(0).stop();
            }
        }
    }

    public void stopCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");
        } else {
            this.playlist.get(0).stop();
        }
    }

    public void setRepeat() {
        this.repeatList = !this.repeatList;
    }

    public void setRepeat(boolean r) {
        this.repeatList = r;
    }

    public void nextClip() {
        stopCurrent();

        AudioClip tmpClip = this.playlist.get(0);
        playlist.remove(0);
        playlist.add(tmpClip);

        playCurrent();

    }

    public void prevClip() {
        stopCurrent();

        AudioClip tmpClip = this.playlist.get(this.playlist.size() - 1);
        playlist.remove(this.playlist.size() - 1);
        playlist.add(0, tmpClip);

        playCurrent();

    }

    public void setRepeatCurrent() {
        if (this.repeatCurrent) {
            this.repeatCurrent = false;
            this.playlist.get(0).setCycleCount(0);

        } else {
            this.playlist.get(0).setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }

    public void setRepeatCurrent(boolean b) {
        if (!b) {
            this.repeatCurrent = false;
            this.playlist.get(0).setCycleCount(0);

        } else {
            this.playlist.get(0).setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }

    public void safePlaylist(String name) {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(name + ".npl");
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(this.playlist);

            oos.flush();

        } catch (IOException e) {
            System.out.println(e);

        } finally {
            try {
                fos.close();
            } catch (Exception ee) {
                System.out.println(ee);

            }
        }
    }

    public void loadPlaylist(String name) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.playlist = (ArrayList<AudioClip>) ois.readObject();
        } catch (IOException e) {
            System.out.println(e);

        } catch (ClassNotFoundException ex) {
            System.out.println(ex);

        }
    }

}
