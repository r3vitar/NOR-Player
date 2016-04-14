package nor.player;

/**
 * Diese Klasse wird für die einzelnen Musikstücke und deren Informationen verwendet.
 * @author Philipp Radler
 */
public class LineItem {
    private String name;
    private String interpret;
    private String album;
    private int index;
    
    /**
     * 
     * @param index Index, den das LineItem haben soll.
     * @param name Name, den das LineItem haben soll.
     * @param interpret Name, den der Interpret des LineItems haben soll.
     * @param album Name, den das Album des LineItems haben soll.
     */
    public LineItem(int index, String name, String interpret, String album) {
        this.name = name;
        this.interpret = interpret;
        this.index = index;
        this.album = album;
    }
    
    /**
     * 
     * @return Gibt den Wert des Names des LineItems zurück.
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @param name Name, den das LineItem haben soll.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 
     * @return Gibt den Namen des Interpreten zurück.
     */
    public String getInterpret() {
        return interpret;
    }
    
    /**
     * 
     * @param interpret Name, den der Interpret des LineItems haben soll.
     */
    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }    
    
    /**
     * 
     * @return Gibt den Album-Namen des LineItems zurück.
     */
    public String getAlbum() {
        return album;
    }
    
    /**
     * 
     * @param album Name, den das Album des LineItems haben soll.
     */
    public void setAlbum(String album) {
        this.album = album;
    }
    
    /**
     * 
     * @return Gibt den Index des LineItems in der Playlist zurück.
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * 
     * @param index Index, den das LineItem haben soll.
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
}