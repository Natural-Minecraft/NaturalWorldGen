package id.naturalsmp.nwg.toolbelt.collection;

import com.google.common.util.concurrent.AtomicDoubleArray;
import id.naturalsmp.nwg.toolbelt.function.NastyFunction;
import id.naturalsmp.nwg.toolbelt.json.JSONArray;
import id.naturalsmp.nwg.toolbelt.math.M;
import id.naturalsmp.nwg.toolbelt.math.RNG;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class KList<T> extends ArrayList<T> {
    private static final long serialVersionUID = -2892550695744823337L;

    public KList() { super(); }
    public KList(int cap) { super(cap); }

    @SafeVarargs
    public KList(T... ts) {
        super();
        add(ts);
    }

    public KList(Collection<T> values) {
        super();
        addAll(values);
    }

    public KList(Enumeration<T> e) {
        super();
        add(e);
    }

    public int indexOfAddIfNeeded(T v) {
        addIfMissing(v);
        return indexOf(v);
    }

    public void addMultiple(T t, int c) {
        for (int i = 0; i < c; i++) add(t);
    }

    private KList<T> add(Enumeration<T> e) {
        while (e.hasMoreElements()) add(e.nextElement());
        return this;
    }

    public KList<T> add(Collection<T> values) {
        addAll(values);
        return this;
    }

    public <K> KMap<K, T> asValues(Function<T, K> f) {
        KMap<K, T> m = new KMap<>();
        forEach(i -> m.putNonNull(f.apply(i), i));
        return m;
    }

    public <V> KMap<T, V> asKeys(Function<T, V> f) {
        KMap<T, V> m = new KMap<>();
        forEach(i -> m.putNonNull(i, f.apply(i)));
        return m;
    }

    public KList<KList<T>> divide(int targetCount) {
        return split(size() / targetCount);
    }

    public KList<KList<T>> split(int targetSize) {
        int sizeActual = targetSize < 1 ? 1 : targetSize;
        KList<KList<T>> gg = new KList<>();
        KList<T> b = new KList<>();
        for (T i : this) {
            if (b.size() >= sizeActual) {
                gg.add(b.copy());
                b.clear();
            }
            b.add(i);
        }
        if (!b.isEmpty()) gg.add(b);
        return gg;
    }

    public KList<T> rewrite(Function<T, T> t) {
        KList<T> m = copy();
        clear();
        for (T i : m) addNonNull(t.apply(i));
        return this;
    }

    public T[] array() {
        return (T[]) toArray();
    }

    public KList<T> copy() {
        return new KList<T>().add(this);
    }

    public KList<T> shuffle() {
        Collections.shuffle(this);
        return this;
    }

    public KList<T> shuffle(Random rng) {
        Collections.shuffle(this, rng);
        return this;
    }

    public KList<T> sort() {
        this.sort(Comparator.comparing(Object::toString));
        return this;
    }

    public KList<T> reverse() {
        Collections.reverse(this);
        return this;
    }

    @Override
    public String toString() {
        return "[" + toString(", ") + "]";
    }

    public String toString(String split) {
        if (isEmpty()) return "";
        if (size() == 1) return get(0).toString();
        StringBuilder b = new StringBuilder();
        for (String i : toStringList()) {
            if (b.length() > 0) b.append(split);
            b.append(i);
        }
        return b.toString();
    }

    public KList<String> toStringList() {
        return convert(Object::toString);
    }

    public <V> KList<T> addFrom(List<V> v, Function<V, T> converter) {
        v.forEach(g -> add(converter.apply(g)));
        return this;
    }

    public <V> KList<V> convert(Function<T, V> converter) {
        KList<V> v = new KList<>();
        forEach(t -> v.addNonNull(converter.apply(t)));
        return v;
    }

    public <V> KList<V> convertNasty(NastyFunction<T, V> converter) throws Throwable {
        KList<V> v = new KList<>(size());
        for (T t : this) v.addNonNull(converter.run(t));
        return v;
    }

    public KList<T> removeWhere(Predicate<T> t) {
        Iterator<T> it = iterator();
        while (it.hasNext()) {
            if (t.test(it.next())) it.remove();
        }
        return this;
    }

    public KList<T> addNonNull(T t) {
        if (t != null) add(t);
        return this;
    }

    public KList<T> swapIndexes(int a, int b) {
        T aa = remove(a);
        T bb = get(b);
        add(a, bb);
        remove(b);
        add(b, aa);
        return this;
    }

    @SafeVarargs
    public final KList<T> remove(T... t) {
        for (T i : t) super.remove(i);
        return this;
    }

    public KList<T> add(KList<T> t) {
        super.addAll(t);
        return this;
    }

    @SafeVarargs
    public final KList<T> add(T... t) {
        for (T i : t) super.add(i);
        return this;
    }

    public boolean hasIndex(int index) {
        return index >= 0 && index < size();
    }

    public int last() {
        return size() - 1;
    }

    public KList<T> dedupe() {
        LinkedHashSet<T> lhs = new LinkedHashSet<>(this);
        return qclear().add(lhs);
    }

    public KList<T> qclear() {
        super.clear();
        return this;
    }

    public boolean hasElements() {
        return !isEmpty();
    }

    public T pop() {
        if (isEmpty()) return null;
        return remove(0);
    }

    public T popLast() {
        if (isEmpty()) return null;
        return remove(last());
    }

    public T popRandom() {
        if (isEmpty()) return null;
        if (size() == 1) return pop();
        return remove(M.irand(0, last()));
    }

    public T popRandom(RNG rng) {
        if (isEmpty()) return null;
        if (size() == 1) return pop();
        return remove(rng.i(0, last()));
    }

    public KList<T> sub(int f, int t) {
        KList<T> g = new KList<>();
        for (int i = f; i < M.min(size(), t); i++) g.add(get(i));
        return g;
    }

    public JSONArray toJSONStringArray() {
        JSONArray j = new JSONArray();
        for (T i : this) j.put(i.toString());
        return j;
    }

    public KList<T> forceAdd(Object[] values) {
        for (Object i : values) add((T) i);
        return this;
    }

    public KList<T> forceAdd(int[] values) {
        for (int i : values) add((T)(Integer) i);
        return this;
    }

    public KList<T> forceAdd(double[] values) {
        for (double i : values) add((T)(Double) i);
        return this;
    }

    public KList<T> forceAdd(AtomicDoubleArray values) {
        for (int i = 0; i < values.length(); i++) add((T)(Double) values.get(i));
        return this;
    }

    public T middleValue() {
        return isEmpty() ? null : get(middleIndex());
    }

    private int middleIndex() {
        return size() % 2 == 0 ? size() / 2 : size() / 2 + 1;
    }

    public T getRandom() {
        if (isEmpty()) return null;
        if (size() == 1) return get(0);
        return get(M.irand(0, last()));
    }

    public KList<T> popRandom(RNG rng, int c) {
        KList<T> m = new KList<>();
        for (int i = 0; i < c; i++) {
            if (isEmpty()) break;
            m.add(popRandom(rng));
        }
        return m;
    }

    public T getRandom(RNG rng) {
        if (isEmpty()) return null;
        if (size() == 1) return get(0);
        return get(rng.i(0, last()));
    }

    public KList<T> qdel(T t) {
        remove(t);
        return this;
    }

    public KList<T> qadd(T t) {
        add(t);
        return this;
    }

    public KList<T> qaddIfMissing(T t) {
        addIfMissing(t);
        return this;
    }

    public KList<T> removeDuplicates() {
        return dedupe();
    }

    public boolean addIfMissing(T t) {
        if (!contains(t)) {
            add(t);
            return true;
        }
        return false;
    }

    public void addAllIfMissing(KList<T> t) {
        for (T i : t) {
            if (!contains(i)) add(i);
        }
    }

    public KList<T> shuffleCopy(Random rng) {
        KList<T> t = copy();
        t.shuffle(rng);
        return t;
    }

    public KList<T> qdrop() {
        pop();
        return this;
    }

    public static KList<String> fromJSONAny(JSONArray oo) {
        KList<String> s = new KList<>();
        for (int i = 0; i < oo.length(); i++) s.add(oo.get(i).toString());
        return s;
    }

    public static <T> Collector<T, ?, KList<T>> collector() {
        return Collectors.toCollection(KList::new);
    }

    public static KList<String> asStringList(List<?> oo) {
        KList<String> s = new KList<>();
        for (Object i : oo) s.add(i.toString());
        return s;
    }
}
