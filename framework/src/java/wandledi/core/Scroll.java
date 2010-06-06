package wandledi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import wandledi.spells.*;

/**A Scroll contains a number of spells which can be apply using the scroll.
 *
 * @author Markus Kahl
 */
public class Scroll {

    private String name;
    private String view;
    private List<Passage> passages = new ArrayList<Passage>();

    public Scroll() {

    }

    public Scroll(String name) {

        this.name = name;
    }

    public void addSpell(String selector, Spell spell) {

        addSpell(selector, spell, -1);
    }

    public void addSpell(String selector, Spell spell, int charges) {

        addSpell(CssSelector.valueOf(selector), spell, charges);
    }

    public void addSpell(Selector selector, Spell spell) {

        addSpell(selector, spell, -1);
    }

    public void addSpell(Selector selector, Spell spell, int charges) {

        Passage passage = getPassage(selector);
        if (charges > 0) {
            passage.addTransientSpell(spell, charges);
        } else {
            passage.addSpell(spell);
        }
    }

    public void addLateSpell(String selector, Spell spell, int offset) {

        addLateSpell(CssSelector.valueOf(selector), spell, offset);
    }

    public void addLateSpell(Selector selector, Spell spell, int offset) {

        Passage passage = getPassage(selector);
        passage.addSpell(spell, offset);
    }

    /**Gets this Scroll's passage for the given selector.
     *
     * @param selector
     * @return An existing Passage or a new one if there wasn't already a Passage for that selector.
     */
    protected Passage getPassage(Selector selector) {

        for (Passage passage: passages) {
            if (selector.equals(passage)) {
                return passage;
            }
        }
        Passage passage = new Passage(selector);
        passages.add(passage);
        Collections.sort(passages);
        
        return passage;
    }

    /**Finds the spells for the element with the given label
     * and the class and/or id read from the attributes.
     *
     * Found spells lose charges upon a call of this method!
     *
     * @param label
     * @param attributes
     * @return The list of found spells, which is empty, if none were found.
     */
    public List<Spell> readSpellsFor(String label, Attributes attributes) {

        List<Spell> spells = new LinkedList<Spell>();
        for (Passage passage: passages) {
            if (passage.matches(label, attributes)) {
                passage.transferSpellsInto(spells);
            }
        }
        return spells;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the view
     */
    public String getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(String view) {
        this.view = view;
    }
}