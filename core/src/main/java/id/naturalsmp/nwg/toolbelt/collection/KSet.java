package id.naturalsmp.nwg.toolbelt.collection;

import java.util.LinkedHashSet;

public class KSet<T> extends LinkedHashSet<T> {
    private static final long serialVersionUID = 1L;

    public KSet() { super(); }

    public KSet(int cap) { super(cap); }

    public KList<T> toKList() {
        return new KList<>(this);
    }
}
