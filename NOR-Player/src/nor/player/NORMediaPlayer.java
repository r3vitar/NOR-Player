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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.media.AudioClip.INDEFINITE;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javax.activation.UnsupportedDataTypeException;

/**
 *
 * @author Kacper_Olszanski
 * Unser Media Player
 */
public class NORMediaPlayer{

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
    public static final String[] supportedAudio = {".mp3", ".aac", ".vlb", ".wav", ".flac", ".alac"};
    public static final String[] supportedMedia = {".mp3", ".aac", ".vlb", ".wav", ".flac", ".alac", ".mp4", ".avi", ".mkv"};
    public static final String[] supportedPlaylists = {".npl", ".m3u8"/*, ".m3u", ".pls"*/};
    public static final String[] supportedVideo = {".mp4", ".avi", ".mkv"};
    private int playIndex = 0;

    
    public static final Comparator<Media> cTitleAsc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2));
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[1];

            return name;
        }
    };
    public static final Comparator<Media> cTitleDesc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2)) * (-1);
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[1];

            return name;
        }
    };
    public static final Comparator<Media> cArtistAsc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2));
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[0];

            return name;
        }
    };
    public static final Comparator<Media> cArtistDesc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2)) * (-1);
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[0];

            return name;
        }
    };
    public static final Comparator<Media> cAlbumAsc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2));
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[2];

            return name;
        }
    };
    public static final Comparator<Media> cAlbumDesc = new Comparator<Media>() {

        String[] requiredData = {"artist=", "title=", "album="};

        @Override
        public int compare(Media o1, Media o2) {
            return getNameFromMedia(o1).compareTo(getNameFromMedia(o2)) * (-1);
        }

        private String getNameFromMedia(Media audio) {
            String name = "";
            name = readMetadata(requiredData, audio)[2];

            return name;
        }
    };
    public static final Comparator<Media> cFileNameAsc = new Comparator<Media>() {

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
    public static final Comparator<Media> cFileNameDesc = new Comparator<Media>() {

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
    public static final Comparator<Media> cPathNameDesc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2)) * (-1);
        }

        private String audioClipToPath(Media audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };
    public static final Comparator<Media> cPathNameAsc = new Comparator<Media>() {

        @Override
        public int compare(Media o1, Media o2) {
            return audioClipToPath(o1).compareTo(audioClipToPath(o2));
        }

        private String audioClipToPath(Media audio) {
            String audioName = audio.getSource();

            return audioName;
        }
    };
    
    /**
     * Gets the Index
     * @return Index of the current Media 
     */
    public int getPlayIndex() {
        return playIndex;
    }
    /**
     * Sets the Index
     * @param playIndex Index of the Media which should be played
     */
    public void setPlayIndex(int playIndex) {
        this.playIndex = playIndex;
    }
    /**
     * Sets the Index to the last Media
     */
    public void setPlayIndexToLast() {
        this.playIndex = playlist.size() - 1;
    }
    /**
     * NOT SUPPORTED YET!   
     * @return MediaView 
     */
    public MediaView getMv() {
        return mv;
    }

    /**
     * returns a StringArray of all playable Audio formats (example.: ".mp3")
     * @return String[] of supported Audio
     */
    public String[] getSupportedAudio() {
        return supportedAudio;
    }
    /**
     * returns a StringArray of all playable Media formats (example.: ".mp3")
     * @return String[] of supported Media
     */
    public String[] getSupportedMedia() {
        return supportedMedia;
    }
    /**
     * returns a StringArray of all playable Playlist formats (example.: ".npl")
     * @return String[] of supported Playlists
     */
    public String[] getSupportedPlaylists() {
        return supportedPlaylists;
    }
    /**
     * returns a StringArray of all playable Video formats (example.: ".mp4)
     * @return String[] of supported Video
     */
    public String[] getSupportedVideo() {
        return supportedVideo;
    }
    /**
     * 
     * @return is List set to repeat
     */
    public boolean isRepeatList() {
        return repeatList;
    }
    /**
     * 
     * @return CurrentMedia List set to repeat
     */
    public boolean isRepeatCurrent() {
        return repeatCurrent;
    }
    /**
     *  
     * @return returns MediaPlayer
     */
    public MediaPlayer getNorPlayer() {
        return norPlayer;
    }
    /**
     * changes the "playing" {@link boolean}
     */
    private void changePlayOrPause() {
        playing = !playing;
    }
    /**
     * 
     * @return Returns "true" if the Player is playing. Else false
     */
    public boolean isPlaying() {
        return this.playing;
    }
    /**
     * 
     * @return returns an {@link ArrayList} of {@link Media} Files (the Playlist)
     */
    public ArrayList<Media> getPlaylist() {

        return playlist;
    }
    /**
     * 
     * @return returns the name of the Playlist
     */
    public String getPlaylistName() {
        return playlistName;
    }
    /**
     * Sets the Name of the playlist
     * @param playlistName {@link String}
     */
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
    /**
     * Empty Constructor
     */
    public NORMediaPlayer() {

    }
    /**
     * sets playlist to new @link Arraylist and sets the listener
     * @param listener Object which implements {@link MediaChangeListener} 
     */
    public NORMediaPlayer(Object listener) {
        this.playlist = new ArrayList<Media>();

        this.listener = (MediaChangeListener) listener;

    }
    /**
     * Makes a new NORMediaPlayer with playlist
     * @param playlist @link ArrayList of {@link Media}
     */
    public NORMediaPlayer(ArrayList<Media> playlist) {
        if (playlist != null) {
            this.playlist = playlist;
            setCurrentToMediaPlayer();
        }

    }
    /**
     * Makse a new NORMediaPlayer with one Media
     * @param audio {@link Media}
     */
    public NORMediaPlayer(Media audio) {
        this.playlist = new ArrayList<Media>();
        Media m = audio;
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
    }
    /**
     * Makes new NORMediaPlayer with one Media generated of the filePath
     * @param filePath Path of the {@link Media}
     */
    public NORMediaPlayer(String filePath) {
        this.playlist = new ArrayList<Media>();
        Media m = createMedia(filePath);
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
    }
    /**
     * Makes new NORMediaPlayer with one @link Media generated of a @link File
     * @param file 
     */
    public NORMediaPlayer(File file) {

        this.playlist = new ArrayList<Media>();
        Media m = createMedia(file);
        if (m != null) {
            this.playlist.add(m);
            setCurrentToMediaPlayer();
        }
        this.listener.playlistChanged();
    }
    /**
     * Adds a {@link Media} to the Playlist
     * @param audio {@link Media}
     */
    public void addMedia(Media audio) {
        if (audio != null) {
            this.playlist.add(audio);
            //this.listener.playlistChanged();
            this.listener.playlistChanged();

        }
    }
    /**
     * Adds a {@link Media} to the Playlist
     * @param filePath {@link String}
     */
    public void addMedia(String filePath) {
        Media m = createMedia(filePath);
        if (m != null) {
            this.addMedia(m);
            //this.listener.playlistChanged();

        };
        this.listener.playlistChanged();

    }
    /**
     * Adds  {@link Media}s to the Playlist
     * @param data {@link ArrayList} of {@link File}s
     */
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
    /**
     * Adds a {@link Media} to the Playlist
     * @param file {@link File}
     */
    public void addMedia(File file) {

        Media m = createMedia(file);
        if (m != null) {
            this.addMedia(m);

        }
        this.listener.playlistChanged();

    }
    /**
     * creates Media from a FilePath
     * @param filePath the Path of the File
     * @return the created Media
     */
    public Media createMedia(String filePath) {
        Media m;
        try {
            m = new Media(filePath);
        } catch (Exception e) {
            return null;
        }

        return m;

    }
    /**
     * creates Media from a {@link File}
     * @param file the Path of the File
     * @return the created Media
     */
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
    /**
     * Adds a List of {@link Media} to the playlist
     * @param mediaArray {@link ArrayList} of {@link String}, {@link Media} or {@link File}
     */
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
    /**
     * deletes a Media
     * @param audio the {@link Media} you want to delete
     */
    public void deleteMedia(Media audio) {
        if (audio.equals(getCurrentMedia())) {
            stop();
        }
        this.playlist.remove(audio);

        if (this.playIndex >= this.playlist.size()) {
            this.playIndex = this.playlist.size() - 1;

        }
    }
    /**
     * deletes a Media with the index
     * @param index the index of the {@link Media} you want to delete
     */
    public void deleteMedia(int index) {
        if (index == this.playIndex) {
            stop();
            

        }
        this.playlist.remove(index);
        if (this.playIndex >= this.playlist.size()) {
            this.playIndex = this.playlist.size() - 1;

        }
        
        if (index == this.playIndex) {
            this.setCurrentToMediaPlayer();
            nextClip();

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
    /**
     * deletes all Media
     */
    public void deletePlaylist() {
        stop();
        this.playlist.removeAll(this.playlist);
        this.playIndex = 0;
        norPlayer = null;
        this.listener.playlistChanged();

    }
    /**
     * deletes all Media
     */
    public void clearPlaylist() {
        stop();
        norPlayer = null;
        this.playlist.clear();
        this.playIndex = 0;

        this.listener.playlistChanged();

    }
    /**
     * sort the Music by FileName
     */
    public void sort() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cFileNameAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by title asc
     */
    public void sortByTitleAsc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cTitleAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by title desc
     */
    public void sortByTitleDesc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cTitleDesc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by artist asc
     */
    public void sortByArtistAsc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cArtistAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by artist desc
     */
    public void sortByArtistDesc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cArtistDesc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by album asc
     */
    public void sortByAlbumAsc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cAlbumAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by album desc
     */
    public void sortByAlbumDesc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cAlbumDesc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by FileName asc
     */
    public void sortByNameAsc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cFileNameAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by FileName desc
     */
    public void sortByNameDesc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cFileNameDesc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by Path asc
     */
    public void sortByPathAsc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cPathNameAsc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * sort the Music by Path desc
     */
    public void sortByPathDesc() {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            this.playlist.sort(this.cPathNameDesc);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }

    }
    /**
     * shuffles the list
     */
    public void shuffle() {

        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();

            Collections.shuffle(this.playlist);
            this.playIndex = this.playlist.indexOf(tmpM);

            this.listener.playlistChanged();

        }
    }
    /**
     * shuffles the List using a seed
     * @param randomSeed the seed for shuffeling
     */
    public void shuffle(Random randomSeed) {
        if (!this.playlist.isEmpty()) {
            Media tmpM = getCurrentMedia();
            stop();
            Collections.shuffle(this.playlist, randomSeed);
            this.playIndex = this.playlist.indexOf(tmpM);
            setCurrentToMediaPlayer();
            play();
            this.listener.playlistChanged();

        }
    }
    /**
     * gets the current {@link Media}
     * @return returns the {@link Media} which is now loaded in the {@link MediaPlayer}
     */
    public Media getCurrentMedia() {
        if (this.playlist.isEmpty() || this.playlist.size() <= this.playIndex) {
            return null;
        } else {
            return this.playlist.get(this.playIndex);
        }

    }
    /**
     * 
     * @return true if the current {@link Media} is a Video
     */
    public boolean isVideo() {
        if (this.mv == null) {
            return false;
        }
        return true;
    }
    /**
     * sets the Media with the current index to the {@link MediaPlayer}
     */
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
    /**
     * plays the Current {@link Media}
     */
    public void play() {

        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            if (norPlayer != null) {
                if (this.playing) {
                    norPlayer.stop();
                    norPlayer.play();
                } else {
                    norPlayer.play();
                }
                this.playing = true;
            }

        }

        //this.listener.mediaChanged();
    }
    /**
     * Plays a {@link Media}
     * @param audio {@link Media} which will be played
     */
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
    /**
     * Plays the {@link Media} with the given index
     * @param index the index of the {@link Media} which will be played
     */
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
            norPlayer.stop();
            norPlayer.play();
            this.playing = true;

        }
        //this.listener.mediaChanged();
    }
    /**
     * if song is playing it will be paused and vice versa
     */
    public void playOrPause() {
        if (this.playlist.isEmpty()) {
            System.err.println("keine AudioClips vorhanden");

        } else {
            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            if (norPlayer.getStatus() == Status.PLAYING) {
                norPlayer.pause();
                this.playing = false;
            } else {

                norPlayer.play();
                this.playing = true;

            }

        }
    }
    /**
     * Stops the playing song
     */
    public void stop() {
        if (this.playlist.isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("keine AudioClips vorhanden");

        
        }else if (this.isPlaying()) {

            if (norPlayer == null) {
                setCurrentToMediaPlayer();
            }
            if(norPlayer.getStatus() != Status.PLAYING){
                norPlayer.play();
            }
            
            norPlayer.stop();
            this.playing = false;

        }
    }
    /**
     * pauses the {@link MediaPlayer}
     */
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
    /**
     * changes the repeatList to true or false
     */
    public void setRepeat() {
        this.repeatList = !this.repeatList;
    }
    /**
     * set the repeatList to true or false
     * @param r true or false
     */
    public void setRepeat(boolean r) {
        this.repeatList = r;
    }
    /**
     * index++; plays the next {@link Media}
     */
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
    /**
     * index--; plays the preview {@link Media} 
     */
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
    /**
     * set current repeat
     */
    public void setRepeatCurrent() {
        if (this.repeatCurrent) {
            this.repeatCurrent = false;
            norPlayer.setCycleCount(0);

        } else {
            norPlayer.setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }
    /**
     * set current repeat
     * @param b true or false
     */
    public void setRepeatCurrent(boolean b) {
        if (!b) {
            this.repeatCurrent = false;
            norPlayer.setCycleCount(0);

        } else {
            norPlayer.setCycleCount(INDEFINITE);
            this.repeatCurrent = true;

        }
    }
    /**
     * saves the playlist as an ".npl" File
     * @param path path of the location of the playlist
     */
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
    /**
     * clears the current playlsit and sets a new playlist
     * @param playlist {@link ArrayList} of {@link Media}
     */
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
    /**
     * loads an .npl or .m3u8 playlist
     * @param f is the Playlist{@link File}
     * @param changePl  {@link Boolean} says if the old playlist should be deleted or not
     * @throws FileNotFoundException
     * @throws IOException 
     */
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
    /**
     * loads an M3u8 playlist
     * @param path path of the playlit
     * @return {@link ArrayList} of {@link Media}
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private ArrayList<Media> loadM3u8(String path) throws FileNotFoundException, IOException {
        ArrayList<String> newPlaylist = new ArrayList<>();
        ArrayList<Media> outputPlaylist = new ArrayList<>();
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

                    if (newPlaylist != null && !newPlaylist.isEmpty()) {
                        for (String s : newPlaylist) {
                            outputPlaylist.add(createMedia(new File(s)));
                        }
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
    /**
     * loads an playlist of the npl file format
     * @param path path of the playlsit
     * @return {@link ArrayList} of {@link Media}
     */
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
    /**
     * 
     * @return true if the playlist is empty or the playlist == null
     */
    public boolean isEmpty() {
        if (playlist == null) {
            return true;
        } else if (playlist.isEmpty()) {
            return true;
        }
        return false;
    }
    /**
     * tells if file is supported
     * @param name filename
     * @param supportedList {@link String[]} of supported File endings
     * @return 
     */
    private boolean isSupported(String name, String[] supportedList) {

        ArrayList<String> tmpStr = new ArrayList<String>(Arrays.asList(supportedList));
        for (String s : tmpStr) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;

    }
    
    Media createMediaByLink(String text) {

        return new Media(text);

    }
    /**
     * read the Metadata(Artist, Title, Album) ofa Media
     * @param requiredData 
     * @param m {@link Media}
     * @return String of metadata
     */
    public static String[] readMetadata(String[] requiredData, Media m) {
        String meta = m.getMetadata().toString();
        String[] data = new String[requiredData.length];
        // 0 -> artist; 1 --> title; 2 --> album
        // searching for requiredData
        File f = new File(m.getSource());
        for (int x = 0; x < requiredData.length; x++) {
            for (int i = 0; i < meta.length() && (meta.length() - i) >= requiredData[x].length(); i++) {
                if (meta.substring(i, i + requiredData[x].length()).equalsIgnoreCase(requiredData[x])) {
                    i += requiredData[x].length();
                    String temp = "";
                    for (; meta.charAt(i) != ',' && meta.charAt(i) != '}'; i++) {
                        temp += meta.charAt(i);
                    }
                    data[x] = temp;
                    i--;
                }
            }
        }

        if (data[1] == null && data[0] == null) {
            if (f.getName().contains("-")) {
                if (f.getName().split("-").length > 3) {
                    data[0] = (f.getName().split("-")[0].replace("%20", " ") + f.getName().split("-")[1].replace("%20", " ")).replace(".mp3", "").replace(".wav", "");
                    data[1] = (f.getName().split("-")[2].replace("%20", " ") + f.getName().split("-")[3].replace("%20", " ")).replace(".mp3", "").replace(".wav", "");
                } else if (f.getName().split("-").length > 2) {
                    data[0] = (f.getName().split("-")[0].replace("%20", " ") + f.getName().split("-")[1].replace("%20", " ")).replace(".mp3", "").replace(".wav", "");
                    data[1] = f.getName().split("-")[2].replace("%20", " ").replace(".mp3", "").replace(".wav", "");
                } else {
                    data[0] = f.getName().split("-")[0].replace("%20", " ").replace(".mp3", "").replace(".wav", "");
                    data[1] = f.getName().split("-")[1].replace("%20", " ").replace(".mp3", "").replace(".wav", "");
                }
            } else {
                data[1] = f.getName().replace("%20", " ").replace(".mp3", "").replace(".wav", "");
                data[0] = "";
            }

        } else if (data[1] == null) {
            if (f.getName().contains("-")) {
                data[1] = f.getName().split("-")[1].replace("%20", " ").replace(".mp3", "").replace(".wav", "");
            } else {
                data[1] = f.getName().replace("%20", " ").replace(".mp3", "").replace(".wav", "");

            }

        } else if (data[0] == null) {
            if (f.getName().contains("-")) {
                data[0] = f.getName().split("-")[0].replace("%20", " ").replace(".mp3", "").replace(".wav", "");
            } else {
                data[0] = f.getName().replace("%20", " ").replace(".mp3", "").replace(".wav", "");

            }
        }

        data[1] = data[1].trim().replace("%5D", " ").replace("%6D", " ").replace("%5B", " ").replace("%6B", " ");
        data[0] = data[0].trim().replace("%5D", " ").replace("%6D", " ").replace("%5B", " ").replace("%6B", " ");

        return data;
    }

}
