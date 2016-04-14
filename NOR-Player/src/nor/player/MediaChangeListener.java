package nor.player;

/**
 * Dieser Listener horcht darauf, ob das MediaFile gewechselt wurde.
 * @author Kacper Olszanski
 */
public interface MediaChangeListener {
    public void mediaChanged();
    public void playlistChanged();
    public void changeText(String s);
    
}
