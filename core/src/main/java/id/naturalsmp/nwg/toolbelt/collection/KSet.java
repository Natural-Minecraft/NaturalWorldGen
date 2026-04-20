package id.naturalsmp.nwg.toolbelt.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class KSet<T> extends LinkedHashSet<T> {
    private static final long serialVersionUID = 1L;

    public KSet() { super(); }

    public KSet(int cap) { super(cap); }

    public KSet(Collection<? extends T> c) { super(c); }

    @SafeVarargs
    public KSet(T... elements) {
        super(elements.length);
        addAll(Arrays.asList(elements));
    }

    public KList<T> toKList() {
        return new KList<>(this);
    }

    public KSet<T> merge(Set<T> other) {
        addAll(other);
        return this;
    }

    public static <T> KSet<T> merge(Set<T> a, Set<T> b) {
        KSet<T> result = new KSet<>(a);
        result.addAll(b);
        return result;
    }
}
