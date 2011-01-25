package wandledi.core;

import java.util.*;

import org.xml.sax.Attributes;

/**
 *
 * @author Markus
 */
public abstract class AbstractSpell implements Spell {

    protected Spell parent;
    protected LinkedList<SpellLine> lines = new LinkedList<SpellLine>();
    private boolean ignoreBounds = false;

    @Override
    public abstract Spell clone();

    public boolean hierarchyContains(Spell spell) {

        return this == spell || parent.hierarchyContains(spell);
    }

    protected void reset() {

        clearLines();
    }

    protected void pushLine(SpellLine line) {

        lines.add(line);
    }

    protected SpellLine popLine() {

        return lines.removeLast();
    }

    protected SpellLine pullLine() {

        return lines.removeFirst();
    }

    protected void clearLines() {

        lines.clear();
    }

    public void setParent(Spell parent) {
        this.parent = parent;
    }

    public void startTransformedElement(String name, Attributes attributes) {

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            parent.startTransformedElement(name, attributes);
        }
    }

    public void endTransformedElement(String name) {

        if (ignoreBounds()) {
            endElement(name);
        } else {
            parent.endTransformedElement(name);
        }
    }

    public void startElement(String name, Attributes attributes) {
        parent.startElement(name, attributes);
    }

    public void endElement(String name) {
        parent.endElement(name);
    }

    public void writeCharacters(char[] characters, int offset, int length) {
        parent.writeCharacters(characters, offset, length);
    }

    public void writeString(String string) {

        char[] characters = string.toCharArray();
        writeCharacters(characters, 0, characters.length);
    }

    public void ignoreBounds(boolean ignoreBounds) {

        this.ignoreBounds = ignoreBounds;
    }

    public boolean ignoreBounds() {

        return ignoreBounds;
    }
}