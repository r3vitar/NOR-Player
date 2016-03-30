/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nor.player;

/**
 *
 * @author philipp
 */
public class LineItem {
    private String name;
    private String interpret;

    public LineItem(String name, String interpret) {
        this.name = name;
        this.interpret = interpret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }    
}