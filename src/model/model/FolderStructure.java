/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;
import java.util.LinkedList;
import java.util.Iterator;
/**
 *
 * @author cfernandez
 */
public class FolderStructure
{

    // Attributes
    // -------------------------------------------------------------------------
    private String name;
    private LinkedList<FolderStructure> children;

    // Constructor
    // -------------------------------------------------------------------------
    public FolderStructure(String name, LinkedList<FolderStructure> children) {
        this.name = name;
        this.children = children;
    }

    /**
     * Returns the children from this FolderStructure. It may be null or empty
     * @return List of FolderSTructure children
     */
    public LinkedList<FolderStructure> getChildren() {
        return children;
    }

    /**
     * Returns the name of the FOlder STructure
     * @return Name of the structure
     */
    public String getName() {
        return name;
    }

    public String GetHTML ()
    {
        String html = "<li>" + name;
        if(children != null)
        {
            Iterator<FolderStructure> folderIte = children.iterator();
            boolean atLeastOne = false;
            if(folderIte.hasNext())
            {
                atLeastOne = true;
                html += "<ul>";
            }
            while(folderIte.hasNext())
            {
                html += folderIte.next().GetHTML();
            }
            if(atLeastOne)
                html += "</ul>";
        }
        html += "</li>";
        return html;
    }

}
