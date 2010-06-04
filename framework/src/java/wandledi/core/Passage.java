package wandledi.core;

import java.util.*;

class Passage extends Selector {

    private List<Spell> spells = new LinkedList<Spell>();
    private Queue<ChargedEntry> transientSpells = new LinkedList<ChargedEntry>();
    private List<LateEntry> lateSpells = new ArrayList<LateEntry>();

    public Passage(Selector selector) {

        super(selector.getLabel(), selector.getElementClass(), selector.getId());
    }

    /**Adds the given spell with the specified amount of charges
     * as a transient spell of this passage.
     *
     * Only one transient spell can be active at a time.
     * Therefore there is a transient spell queue whose first
     * spell will be cast until its charges have been used up.
     *
     * Only then will the following spell be cast.
     *
     * @param spell
     * @param charges
     */
    public void addTransientSpell(Spell spell, int charges) {

        transientSpells.add(new ChargedEntry(spell, charges));
    }

    public void addTransientSpell(Spell spell) {

        addTransientSpell(spell, 1);
    }

    /**Adds a persistent spell to this passage.
     *
     * @param spell
     */
    public void addSpell(Spell spell) {

        spells.add(spell);
    }

    /**Adds this spell to this passages late transient spells.
     * A late transient spells needs a number of impulses until
     * it goes off.
     *
     * After that it's used up.
     *
     * @param spell
     * @param offset number of impulses required for this spell to go off
     */
    public void addSpell(Spell spell, int offset) {

        System.out.println("adding late spell: " + spell);
        lateSpells.add(new LateEntry(spell, offset));
    }

    /**With each read the charges of the containing transient spells decrease.
     * If the charges of a transient spell fall to 0 it can't be used anymore.
     * Persistent spells are there to stay.
     *
     * @return
     */
    public List<Spell> readSpells() {

        List spells = new LinkedList();
        transferSpellsInto(spells);
        return spells;
    }

    /**Reads this scroll's spells and transfers them into the given collection.
     *
     * With each read the charges of the containing transient spells decrease.
     * If the charges of a transient spell fall to 0 it can't be used anymore.
     * Persistent spells are there to stay.
     *
     * @param spells
     */
    public void transferSpellsInto(Collection<Spell> spells) {

        spells.addAll(this.spells);
        transferTransientSpellsInto(spells);
        transferLateSpellsInto(spells);
    }

    private void transferTransientSpellsInto(Collection<Spell> spells) {

        if (!transientSpells.isEmpty()) {
            ChargedEntry entry = transientSpells.peek();
            if (--entry.charges <= 0) {
                transientSpells.remove();
            }
            spells.add(entry.spell);
        }
    }

    private void transferLateSpellsInto(Collection<Spell> spells) {

        Iterator<LateEntry> entries = lateSpells.iterator();
        while (entries.hasNext()) {
            LateEntry entry = entries.next();
            if (entry.offset-- <= 0) {
                System.out.println("Late Spell: " + entry.spell);
                spells.add(entry.spell);
                entries.remove();
            }
        }
    }

    private static class ChargedEntry {

        Spell spell;
        int charges; // how often a Spell is applied until its power is used up

        ChargedEntry(Spell spell, int charges) {

            this.spell = spell;
            this.charges = charges;
        }
    }

    private static class LateEntry {

        Spell spell;
        int offset;

        LateEntry(Spell spell, int offset) {

            this.spell = spell;
            this.offset = offset;
        }
    }
}