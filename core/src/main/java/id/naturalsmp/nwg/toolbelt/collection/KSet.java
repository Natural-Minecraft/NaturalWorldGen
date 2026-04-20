package id.naturalsmp.nwg.toolbelt.collection;

import java.util.Collection;
import java.util.LinkedHashSet;

public class KSet<T> extends LinkedHashSet<T> {
    private static final long serialVersionUID = 1L;

    public KSet() { super(); }

    public KSet(int cap) { super(cap); }

    public KSet(Collection<? extends T> c) { super(c); }

    public KList<T> toKList() {
        return new KList<>(this);
    }

    public KSet<T> merge(KSet<T> other) {
        addAll(other);
        return this;
    }
}
