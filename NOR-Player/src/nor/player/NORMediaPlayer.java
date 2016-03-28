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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.input.KeyCode.T;
import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.activation.UnsupportedDataTypeException;

/**
 *
 * @author Kacper Olszanski
 *
 */
public class NORMediaPlayer implements Serializable {

    private ArrayList<Media> playlist;
    private boolean repeatList = true;
    private boolean repeatCurrent = false;
    private String playlistName = "NoName";
    private MediaPlayer norPlayer;
    private boolean playing = false;
    private MediaChangeListener listener;
    private final char dot = '.';
    private MediaView mv;
    String[] supportedAudio = {".mp3", ".mp2", ".mp1", ".aac", ".vlb", ".wav", ".flac", ".alac"};
    String[] supportedMedia = {".mp3", ".mp2", ".mp1", ".aac", ".vlb", ".wav", ".flac", ".alac", ".mp4", ".avi", ".mkv"};
    String[] supportedPlaylists = {".npl", ".m3u", ".m3u8", ".pls"};
    String[] supportedVideo = {".mp4", ".avi", ".mkv"};
    private int playIndex = 0;

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

    public MediaView getMv() {
        return mv;
    }

    public String[] getSupportedAudio() {
        return supportedAudio;
    }

    public String[] getSupportedMedia() {
        return supportedMedia;
    }

    public String[] getSupportedPlaylists() {
        return supportedPlaylists;
    }

    public String[] getSupportedVideo() {
        return supportedVideo;
    }

    public boolean isRepeatList() {
        return repeatList;
    }

    public boolean isRepeatCurrent() {
        return repeatCurrent;
    }

    public MediaPlayer getNorPlayer() {
        return norPlayer;
    }

    private void changePlayOrPause() {
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

    public NORMediaPlayer() {

    }

    public NORMediaPlayer(Object o) {
        this.playlist = new ArrayList<Media>();

        this.listener = (MediaChangeListener) o;

    }

    public NORMediaPlayer(ArrayList<Media> playlist) {
        if (playlist != null) {
            this.playlist = playlist;
            setCurrentToMediaPlayer();
        }

    }

    public NORMediaPlayer(Media audio) {
        this.playlist = new ArrayList<Media>();
        Media m = audio;
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
    }

    public NORMediaPlayer(String filePath) {
        this.playlist = new ArrayList<Media>();
        Media m = createMedia(filePath);
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
    }

    public NORMediaPlayer(File file) {
        this.playlist = new ArrayList<Media>();
        Media m = createMedia(file);
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
    }

    public void addMedia(Media audio) {
        if (audio != null) {
            this.playlist.add(audio);
        }
    }

    public void addMedia(String filePath) {
        Media m = createMedia(filePath);
        if (m != null) {
            this.addMedia(m);
        };
    }

    public void addMedia(ArrayList<File> data) {

        for (File f : data) {
            String name = f.getName();
            if (isSupported(name, this.supportedPlaylists)) {

                try {
                    loadPlaylist(f, false);
                } catch (IOException ex) {
                    System.err.println(ex);
                }
            } else if (isSupported(name, supportedAudio)) {
                this.addMedia(f);

            } else if (isSupported(name, supportedVideo)) {
                this.addMedia(f);
            }
        }
        if (this.playlist == null) {
            setCurrentToMediaPlayer();
        }
    }

    public void addMedia(File file) {

        Media m = createMedia(file);
        if (m != null) {
            this.addMedia(m);
        }
    }

    public Media createMedia(String filePath) {

        return new Media(filePath);

    }

    public Media createMedia(File file) {
        if (isSupported(file.getName(), supportedMedia)) {
            return new Media(file.toURI().toString().replace('\\', '/'));
        }

        return null;

    }

    public void addMediaArray(ArrayList<Object> mediaArray) {
        if (mediaArray.isEmpty()) {
            throw new IllegalArgumentException("ArrayList is empty");
        } else if (mediaArray.get(0) instanceof String) {
            for (Object filePath : mediaArray) {
                this.playlist.add(createMedia(filePath.toString()));
            }
        } else if (mediaArray.get(0) instanceof Media) {
            for (Object m : mediaArray) {
                this.playlist.add((Media) m);
            }

        } else if (mediaArray.get(0) instanceof File) {
            for (Object f : mediaArray) {
                Media m = createMedia((File) f);
                if (m != null) {
                    this.addMedia(m);
                }

            }

        } else {
            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
        }

    }

    public void deleteMedia(Media audio) {
        if (audio.equals(getCurrentMedia())) {
            stop();
        }
        this.playlist.remove(audio);

        if (this.playIndex >= this.playlist.size()) {
            this.playIndex = this.playlist.size() - 1;

        }
    }

    public void deleteMedia(int index) {
        if (index == this.playIndex) {
            stop();
        }
        this.playlist.remove(index);
        if (this.playIndex >= this.playlist.size()) {
            this.playIndex = this.playlist.size() - 1;

        }

    }

    public void deleteMediaArray(ArrayList<Object> mediaArray) {
        throw new UnsupportedOperationException();
//        stopCurrent();
//        if (mediaArray.isEmpty()) {
//            throw new IllegalArgumentException("ArrayList is empty");
//        } else if (mediaArray.get(0) instanceof String) {
//            for (Object filePath : mediaArray) {
//                this.playlist.remove(createMedia(filePath.toString()));
//            }
//        } else if (mediaArray.get(0) instanceof Media) {
//            this.playlist.removeAll(playlist);
//
//        } else {
//            throw new IllegalArgumentException("Unsupported Objects in ArrayList");
//        }
//        if (playlist.isEmpty()) {
//            norPlayer = null;
//        }

    }

    public void deletePlaylist() {
        stop();
        this.playlist.removeAll(this.playlist);
        this.playIndex = 0;
        norPlayer = null;
    }

    public void clearPlaylist() {
        stop();
        norPlayer = null;
        this.playlist.clear();
        this.playIndex = 0;

    }

    public void sort() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameAsc);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void sortByNameAsc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameAsc);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void sortByNameDesc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameDesc);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void sortByPathAsc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cPathNameAsc);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void sortByPathDesc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cPathNameDesc);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void shuffle() {
        if (!this.playlist.isEmpty()) {
            stop();
            Collections.shuffle(this.playlist);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public void shuffle(Random randomSeed) {
        if (!this.playlist.isEmpty()) {
            stop();
            Collections.shuffle(this.playlist, randomSeed);
            setCurrentToMediaPlayer();
            play();
        }
    }

    public Media getCurrentMedia() {
        if (this.playlist.isEmpty()) {
            return null;
        } else {
            return this.playlist.get(this.playIndex);
        }

    }

    public boolean isVideo() {
        if (this.mv == null) {
            return false;
        }
        return true;
    }

    private void setCurrentToMediaPlayer() {

        if (getCurrentMedia() == null); else {
            norPlayer = new MediaPlayer(getCurrentMedia());
            if (isSupported(norPlayer.getMedia().getSource(), supportedVideo)) {
                mv = new MediaView(norPlayer);
            } else {
                mv = null;
            }

            this.listener.mediaChanged();
        }

        norPlayer.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {
                if (getCurrentMedia() == null) {
                    deleteMedia(getCurrentMedia());
                }

                if (!repeatCurrent) {
                    if (playIndex == playlist.size() - 1) {
                        if (repeatList) {
                            nextClip();
                        } else {
                            stop();
                            playIndex = 0;

                        }

                    } else {
                        nextClip();
                    }
                } else {
                    stop();
                    play();
                }

            }
        });

    }

    public void play() {

        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }

            norPlayer.play();
            this.playing = true;

        }
        //this.listener.mediaChanged();
    }

    public void play(Media audio) {

        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null && !playlist.contains(audio)) {

            } else if (norPlayer.getMedia().equals(audio)) {

            } else {
                playIndex = playlist.indexOf(audio);
                setCurrentToMediaPlayer();
            }

            norPlayer.play();
            this.playing = true;

        }
        //this.listener.mediaChanged();
    }

    public void play(int index) {

        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null && playIndex != index) {

            } else if (norPlayer.getMedia().equals(playlist.get(index))) {

            } else {
                playIndex = index;
                setCurrentToMediaPlayer();
            }

            norPlayer.play();
            this.playing = true;

        }
        //this.listener.mediaChanged();
    }

    public void playOrPause() {
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
            changePlayOrPause();
        }
    }

    public void stop() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else if (this.isPlaying()) {

            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }

            norPlayer.stop();
            this.playing = false;
        }
    }

    public void pause() {
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
        stop();
        if (this.playIndex == this.playlist.size() - 1) {
            this.playIndex = 0;
        } else {
            this.playIndex++;
        }

        setCurrentToMediaPlayer();
        play();

    }

    public void prevClip() {
        stop();
        if (this.playIndex == 0) {
            this.playIndex = this.playlist.size() - 1;
        } else {
            this.playIndex--;
        }
        setCurrentToMediaPlayer();
        play();

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
        this.playlistName = name;
        try {
            if (name.endsWith(".npl")) {

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
            try {
                File f = new File(name);
                try {
                    this.playlistName = f.getName().split("\\.")[0];
                } catch (Exception e) {
                    this.playlistName = f.getName();
                }

            } catch (Exception e) {
                this.playlistName = name;
            }
        }
    }

    public void changePlaylist(ArrayList<Media> playlist) {
        boolean b = false;
        if (this.isPlaying()) {
            b = true;
        }
        if (b) {
            stop();
        }
        this.playIndex = 0;
        this.playlist = playlist;
        setCurrentToMediaPlayer();
        if (b) {
            play();
        }
    }

    public void loadPlaylist(File f, boolean changePl) throws FileNotFoundException, IOException {

        String path = f.getAbsolutePath();
        String name = f.getName();
        if (name.contains(Character.toString(dot))) {

            if (name.endsWith(".npl")) {
                if (changePl) {
                    this.playIndex = 0;
                    changePlaylist(loadNpl(path));
                } else {
                    ArrayList<Media> tmpArM = loadNpl(path);
                    ArrayList<Object> tmpArO = new ArrayList<Object>();
                    for (Media m : tmpArM) {
                        tmpArO.add((Object) m);
                    }

                    addMediaArray(tmpArO);
                }

            } else if (name.endsWith("m3u8")) {
                changePlaylist(loadM3u8(path));
            }
            try {
                File ff = new File(name);
                try {
                    this.playlistName = ff.getName().split("\\.")[0];
                } catch (Exception e) {
                    this.playlistName = ff.getName();
                }

            } catch (Exception e) {
                this.playlistName = name;
            }
        }else{
            throw new UnsupportedDataTypeException("FileFormat not Supported");
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
                Media m = createMedia(s);
                if (m != null) {
                    newPlaylist.add(m);

                }
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

    private boolean isSupported(String name, String[] supportedList) {

        ArrayList<String> tmpStr = new ArrayList<String>(Arrays.asList(supportedList));
        for (String s : tmpStr) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;

    }

}
