/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nor.player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author kacpe_000
 */
public class Playlist implements Serializable {

    private ArrayList<Media> playlist;
    private boolean repeatList = false;
    private boolean repeatCurrent = false;
    private String playlistName = "NoName";
    private MediaPlayer norPlayer;
    private boolean playing = false;

    private Comparator<Media> cFileNameAsc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {
            return audioClipToName(o1).compareTo(audioClipToName(o2));
        }

        private String audioClipToName(Media audio) {
            String audioName = "";
            String[] tmp = audio.getSource().split("/");
            audioName = tmp[tmp.length - 1];

            return audioName;
        }
    };
    private Comparator<Media> cFileNameDesc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {

            return audioClipToName(o1).compareTo(audioClipToName(o2)) * (-1);
        }

        private String audioClipToName(Media audio) {
            String audioName = "";
            String[] tmp = audio.getSource().split("/");
            audioName = tmp[tmp.length - 1];

            return audioName;
        }
    };
    private Comparator<Media> cPathNameDesc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2)) * (-1);
        }

        private String audioClipToPath(Media audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };
    private Comparator<Media> cPathNameAsc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2));
        }

        private String audioClipToPath(Media audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };

    
    public boolean isRepeatList() {
        return repeatList;
    }

    public boolean isRepeatCurrent() {
        return repeatCurrent;
    }

    public MediaPlayer getNorPlayer() {
        return norPlayer;
    }
    private void playOrPause() {
        playing = !playing;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public ArrayList<Media> getPlaylist() {

        return playlist;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Playlist() {
        this.playlist = new ArrayList<Media>();

    }

    public Playlist(ArrayList<Media> playlist) {
        this.playlist = playlist;
        setCurrentToMediaPlayer();

    }

    public Playlist(Media audio) {
        this.playlist = new ArrayList<Media>();
        this.playlist.add(audio);
        setCurrentToMediaPlayer();
    }

    public Playlist(String filePath) {
        this.playlist = new ArrayList<Media>();
        this.playlist.add(createMedia(filePath));
        setCurrentToMediaPlayer();
    }

    public Playlist(File file) {
        this.playlist = new ArrayList<Media>();
        this.playlist.add(createMedia(file));
        setCurrentToMediaPlayer();
    }

    public void addMedia(Media audio) {

        this.playlist.add(audio);
    }

    public void addMedia(String filePath) {
        this.addMedia(createMedia(filePath));
    }

    public void addMedia(File file) {
        this.addMedia(createMedia(file));
    }

    private Media createMedia(String filePath) {
        return new Media(filePath);
    }

    private Media createMedia(File file) {

        return new Media(file.toURI().toString().replace('\\', '/'));

    }

    public void addMediaArray(ArrayList<Object> mediaArray) {
        if (mediaArray.isEmpty()) {
            throw new IllegalArgumentException("ArrayList is empty");
        } else if (mediaArray.get(0) instanceof String) {
            for (Object filePath : mediaArray) {
                this.playlist.add(createMedia(filePath.toString()));
            }
        } else if (mediaArray.get(0) instanceof Media) {
            this.playlist.addAll(playlist);

        } else {
            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
        }

    }

    public void deleteMedia(Media audio) {
        if (audio.equals(this.playlist.get(0))) {
            stopCurrent();
        }
        this.playlist.remove(audio);
        if (playlist.isEmpty()) {
            norPlayer = null;
        }
    }

    public void deleteMedia(int index) {
        if (index == 0) {
            stopCurrent();
        }
        this.playlist.remove(index);
        if (playlist.isEmpty()) {
            norPlayer = null;
        }
    }

    public void deleteMediaArray(ArrayList<Object> mediaArray) {
        stopCurrent();
        if (mediaArray.isEmpty()) {
            throw new IllegalArgumentException("ArrayList is empty");
        } else if (mediaArray.get(0) instanceof String) {
            for (Object filePath : mediaArray) {
                this.playlist.remove(createMedia(filePath.toString()));
            }
        } else if (mediaArray.get(0) instanceof Media) {
            this.playlist.removeAll(playlist);

        } else {
            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
        }
        if (playlist.isEmpty()) {
            norPlayer = null;
        }

    }

    public void deletePlaylist() {
        stopCurrent();
        this.playlist.removeAll(this.playlist);
        norPlayer = null;
    }

    public void clearPlaylist() {
        stopCurrent();
        this.playlist.clear();
        norPlayer = null;
    }

    public void sort() {
        stopCurrent();
        this.playlist.sort(this.cFileNameAsc);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void sortByNameAsc() {
        stopCurrent();
        this.playlist.sort(this.cFileNameAsc);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void sortByNameDesc() {
        stopCurrent();
        this.playlist.sort(this.cFileNameDesc);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void sortByPathAsc() {
        stopCurrent();
        this.playlist.sort(this.cPathNameAsc);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void sortByPathDesc() {
        stopCurrent();
        this.playlist.sort(this.cPathNameDesc);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void shuffle() {
        stopCurrent();
        Collections.shuffle(this.playlist);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public void shuffle(Random randomSeed) {
        stopCurrent();
        Collections.shuffle(this.playlist, randomSeed);
        setCurrentToMediaPlayer();
        playCurrent();
    }

    public Media getCurrentMedia() {
        if (this.playlist.isEmpty()) {
            return null;
        } else {
            return this.playlist.get(0);
        }

    }

    private void setCurrentToMediaPlayer() {
        norPlayer = new MediaPlayer(this.playlist.get(0));
        norPlayer.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {
                nextClip();
            }
        });
    }

    public void playCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            norPlayer.play();
            this.playing = true;
        }
    }

    public void playOrPauseCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            if (!isPlaying()) {
                norPlayer.play();
            } else {
                norPlayer.pause();

            }
            playOrPause();
        }
    }

    public void stopCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            norPlayer.stop();
            this.playing = false;
        }
    }

    public void pauseCurrent() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            norPlayer.pause();
            this.playing = false;
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

        Media tmpClip = this.playlist.get(0);
        playlist.remove(0);
        playlist.add(tmpClip);
        setCurrentToMediaPlayer();
        playCurrent();

    }

    public void prevClip() {
        stopCurrent();

        Media tmpClip = this.playlist.get(this.playlist.size() - 1);
        playlist.remove(this.playlist.size() - 1);
        playlist.add(0, tmpClip);
        setCurrentToMediaPlayer();
        playCurrent();

    }

    public void setRepeatCurrent() {
        if (this.repeatCurrent) {
            this.repeatCurrent = false;
            norPlayer.setCycleCount(0);

        } else {
            norPlayer.setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }

    public void setRepeatCurrent(boolean b) {
        if (!b) {
            this.repeatCurrent = false;
            norPlayer.setCycleCount(0);

        } else {
            norPlayer.setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }

    public void savePlaylist(String name) {
        OutputStream fos = null;
        try {
            if (name.contains("npl")) {

            } else {
                name = name + ".npl";
            }
            fos = new FileOutputStream(name);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            ArrayList<String> tmpList = new ArrayList<String>();
            for (Media m : this.playlist) {
                tmpList.add(m.getSource());
            }

            oos.writeObject(tmpList);

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

    private void changePlaylist(ArrayList<Media> playlist) {
        boolean b = false;
        if (this.isPlaying()) {
            b = true;
        }
        if (b) {
            stopCurrent();
        }
        this.playlist = playlist;
        setCurrentToMediaPlayer();
        if (b) {
            playCurrent();
        }
    }

    public void loadPlaylist(String path) throws FileNotFoundException, IOException {

        String[] tmpPath = path.split("\\.");

        String fileType = tmpPath[tmpPath.length - 1];

        if (fileType.equalsIgnoreCase("npl")) {
            changePlaylist(loadNpl(path));

        } else if (fileType.equalsIgnoreCase("m3u8")) {
            changePlaylist(loadM3u8(path));
        }
    }

    private ArrayList<Media> loadM3u8(String path) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        ArrayList<Media> newPlaylist = new ArrayList<Media>();
        while (br.ready()) {
            String tmpStr = br.readLine();
            if (tmpStr.charAt(0) == '#'); else if (tmpStr.charAt(0) == '\\') {
                newPlaylist.add(createMedia(String.format("%s:%s", path.charAt(0), tmpStr)));
            } else if (tmpStr.charAt(1) == ':') {
                newPlaylist.add(createMedia(String.format("%s", tmpStr)));
            }
//            else if(tmpStr.charAt(0) == '.' && tmpStr.charAt(1) == '.' && tmpStr.charAt(2) == '\\' ){
//                newPlaylist.add(createMedia(String.format("%s:\\%s",.path.charAt(0), tmpStr.)));
//            }

        }

        return newPlaylist;
    }

    private ArrayList<Media> loadNpl(String path) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<String> tmpList = (ArrayList<String>) ois.readObject();
            ArrayList<Media> newPlaylist = new ArrayList<Media>();
            for (String s : tmpList) {
                newPlaylist.add(createMedia(s));
            }
            return newPlaylist;

        } catch (IOException e) {
            System.out.println(e);

        } catch (ClassNotFoundException ex) {
            System.out.println(ex);

        }
        return null;
    }

    public boolean isEmpty() {
        if (playlist == null) {
            return true;
        } else if (playlist.isEmpty()) {
            return true;
        }
        return false;
    }

}
