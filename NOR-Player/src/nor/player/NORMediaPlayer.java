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
import java.util.HashMap;
import java.util.Random;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

import javafx.scene.image.Image;
import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.activation.UnsupportedDataTypeException;

/**
 *
 * @author Kacper Olszanski
 *
 */
public class NORMediaPlayer implements Serializable {

    public long serialVerUID = 2L;
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
    String[] supportedMedia = {".mp3"/*, ".mp2", ".mp1", ".aac", ".vlb", ".wav", ".flac", ".alac", ".mp4", ".avi", ".mkv"*/};
    String[] supportedPlaylists = {".npl", ".m3u8"/*, ".m3u", ".pls"*/};
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
        this.listener.playlistChanged();
    }

    public void addMedia(Media audio) {
        if (audio != null) {
            this.playlist.add(audio);
            this.listener.playlistChanged();

        }
    }

    public void addMedia(String filePath) {
        Media m = createMedia(filePath);
        if (m != null) {
            this.addMedia(m);
            this.listener.playlistChanged();

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
        this.listener.playlistChanged();

    }

    public void addMedia(File file) {

        Media m = createMedia(file);
        if (m != null) {
            this.addMedia(m);
            this.listener.playlistChanged();

        }
        
    }

    public Media createMedia(String filePath) {
        Media m;
        try {
            m = new Media(filePath);
        } catch (Exception e) {
            return null;
        }

        return m;

    }

    public Media createMedia(File file) {
        Media m = null;
        try {
            if (isSupported(file.getName(), supportedMedia)) {

                m = new Media(file.toURI().toString());
            }
        } catch (MediaException e) {
            System.err.println(e);

        }
        return m;

    }

    public Image createImage(File file) {
        Image img = null;
        try {

            img = new Image(file.toURI().toString());

        } catch (Exception e) {
            System.err.println(e);

        }
        return img;

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
            this.listener.playlistChanged();

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
        this.listener.playlistChanged();

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
        this.listener.playlistChanged();

    }

    public void clearPlaylist() {
        stop();
        norPlayer = null;
        this.playlist.clear();
        this.playIndex = 0;
        this.listener.playlistChanged();

    }

    public void sort() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameAsc);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }

    public void sortByNameAsc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameAsc);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }

    public void sortByNameDesc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cFileNameDesc);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }

    public void sortByPathAsc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cPathNameAsc);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }

    public void sortByPathDesc() {
        if (!this.playlist.isEmpty()) {
            stop();
            this.playlist.sort(this.cPathNameDesc);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }

    }

    public void shuffle() {

        if (!this.playlist.isEmpty()) {

            stop();
            Collections.shuffle(this.playlist);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();
        }
    }

    public void shuffle(Random randomSeed) {
        if (!this.playlist.isEmpty()) {
            stop();
            Collections.shuffle(this.playlist, randomSeed);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }

    public Media getCurrentMedia() {
        if (this.playlist.isEmpty() || this.playlist.size() <= this.playIndex) {
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
        if (!isEmpty() && getCurrentMedia() != null) {
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

    public void savePlaylist(String path) {

        OutputStream fos = null;
        this.playlistName = path;
        try {
            try {
                File f = new File(path);
                try {
                    this.playlistName = f.getName().split("\\.")[0];
                } catch (Exception e) {
                    this.playlistName = f.getName();
                }

            } catch (Exception e) {
                this.playlistName = path;
            }

            if (path.endsWith(".npl")) {

            } else {
                path = path + ".npl";
            }
            fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            HashMap output = new HashMap();

            ArrayList<String> tmpList = new ArrayList<String>();
            for (Media m : this.playlist) {
                String src;
                try {
                    src = m.getSource();
                } catch (Exception e) {
                    src = null;
                }
                if (src != null) {
                    tmpList.add(src);
                }
            }

            output.put("list", tmpList);
            output.put("index", this.playIndex);
            output.put("name", this.playlistName);
            output.put("repeatList", this.repeatList);
            output.put("repeatCurrent", this.repeatCurrent);
            output.put("path", path);
            output.put("ver", this.serialVerUID);

            oos.writeObject(output);

            oos.flush();

        } catch (IOException e) {
            System.out.println(e);

        } finally {
            try {
                fos.close();
                this.listener.playlistChanged();
            } catch (Exception ee) {
                System.out.println(ee);

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
//        if(this.playIndex > playlist.size()-1);
//            this.playIndex = 0;
        this.playlist = playlist;
        setCurrentToMediaPlayer();
        if (b) {
            play();
        }
        this.listener.playlistChanged();

    }

    public void loadPlaylist(File f, boolean changePl) throws FileNotFoundException, IOException {

        String path = f.getAbsolutePath();
        String name = f.getName();
        if (name.contains(Character.toString(dot))) {

            if (name.endsWith(".npl")) {
                if (changePl) {
                    //this.playIndex = 0;
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
                if (changePl) {
                    //this.playIndex = 0;
                    changePlaylist(loadM3u8(path));
                } else {
                    ArrayList<Media> tmpArM = loadM3u8(path);
                    ArrayList<Object> tmpArO = new ArrayList<Object>();
                    for (Media m : tmpArM) {
                        tmpArO.add((Object) m);
                    }

                    addMediaArray(tmpArO);
                }
            }

        } else {
            throw new UnsupportedDataTypeException("FileFormat not Supported");
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
        this.listener.playlistChanged();

    }

    private ArrayList<Media> loadM3u8(String path) throws FileNotFoundException, IOException {
        ArrayList<String> newPlaylist = new ArrayList<>();
            ArrayList<Media>  outputPlaylist = new ArrayList<>();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            
            
            Thread t1 = new Thread(new Runnable() {
                
                @Override
                public void run() {
                     
                     
                    try {
                        while (br.ready()) {
                            
                            String tmpStr = br.readLine();
                            System.out.println(tmpStr);
                            
                            if (tmpStr.charAt(0) == '#'); else if (tmpStr.charAt(0) == '\\') {
                                newPlaylist.add(String.format("%s:%s", path.charAt(0), tmpStr));
                            } else if (tmpStr.charAt(1) == ':') {
                                newPlaylist.add((tmpStr));
                            }
//            else if(tmpStr.charAt(0) == '.' && tmpStr.charAt(1) == '.' && tmpStr.charAt(2) == '\\' ){
//                newPlaylist.add(createMedia(String.format("%s:\\%s",.path.charAt(0), tmpStr.)));
//            }

                        }
                    } catch (IOException ex) {
                        Logger.getLogger(NORMediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            t1.start();
            Thread t2 = new Thread(new Runnable() {
                
                @Override
                public void run() {
                    
                    
                     if(newPlaylist != null && !newPlaylist.isEmpty())
                    for (String s : newPlaylist) {
                        outputPlaylist.add(createMedia(new File(s)));
                    }
                    
                }
            });
            t1.join();
            br.close();
            fr.close();
            t2.start();
            
            
            
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(NORMediaPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputPlaylist;
        
    }

    private ArrayList<Media> loadNpl(String path) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap input = (HashMap) ois.readObject();
            if ((long) input.get("ver") == this.serialVerUID) {
                ArrayList<String> tmpList = (ArrayList<String>) input.get("list");
                this.repeatCurrent = (boolean) input.get("repeatCurrent");
                this.repeatList = (boolean) input.get("repeatList");
                this.playIndex = (int) input.get("index");
                this.playlistName = (String) input.get("name");
                ArrayList<Media> newPlaylist = new ArrayList<Media>();
                for (String s : tmpList) {
                    Media m = createMedia(s);
                    if (m != null) {
                        newPlaylist.add(m);

                    }
                }
                return newPlaylist;
            }

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
