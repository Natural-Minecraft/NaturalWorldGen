package id.naturalsmp.nwg.toolbelt.collection;

import id.naturalsmp.nwg.toolbelt.function.Consumer2;
import id.naturalsmp.nwg.toolbelt.function.Consumer3;
import id.naturalsmp.nwg.toolbelt.scheduling.Queue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@SuppressWarnings("unchecked")
public class KMap<K, V> extends ConcurrentHashMap<K, V> {
    private static final long serialVersionUID = 7288942695300448163L;

    public KMap() { super(16); }

    public KMap(int initialCapacity) { super(initialCapacity, 0.75f, 1); }

    public KMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    public KMap(Map<K, V> gMap) {
        this();
        putAll(gMap);
    }

    public K getKey(V value) {
        for (KeyPair<K, V> i : keypair()) {
            if (i.v != null && i.v.equals(value)) return i.k;
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <S> KMap<K, V> putValueList(K k, S... vs) {
        try {
            if (!containsKey(k)) put(k, (V) new KList<S>());
            ((KList) get(k)).add(vs);
        } catch (Throwable ignored) {}
        return this;
    }

    public KList<K> sortK() {
        KList<K> k = new KList<>();
        KList<V> v = v();
        v.sort(Comparator.comparing(Object::toString));
        for (V i : v) {
            for (K j : k()) {
                if (get(j) != null && get(j).equals(i)) k.add(j);
            }
        }
        k.dedupe();
        return k;
    }

    public KList<K> sortKNumber() {
        KList<K> k = new KList<>();
        KList<V> v = v();
        v.sort(Comparator.comparingDouble(x -> ((Number) x).doubleValue()));
        for (V i : v) {
            for (K j : k()) {
                if (get(j) != null && get(j).equals(i)) k.add(j);
            }
        }
        k.dedupe();
        return k;
    }

    public KMap<K, V> qput(K key, V value) {
        super.put(key, value);
        return this;
    }

    public KMap<K, V> putNonNull(K key, V value) {
        if (key != null && value != null) put(key, value);
        return this;
    }

    public V putThen(K key, V valueIfKeyNotPresent) {
        if (!containsKey(key)) put(key, valueIfKeyNotPresent);
        return get(key);
    }

    public KMap<K, V> copy() {
        return new KMap<>(this);
    }

    public KMap<K, V> rewrite(Consumer3<K, V, KMap<K, V>> f) {
        KMap<K, V> m = copy();
        for (K i : m.k()) f.accept(i, get(i), this);
        return this;
    }

    public KMap<K, V> each(Consumer2<K, V> f) {
        for (K i : k()) f.accept(i, get(i));
        return this;
    }

    public KMap<V, K> flipFlatten() {
        KMap<V, KList<K>> f = flip();
        KMap<V, K> m = new KMap<>();
        for (V i : f.k()) {
            KList<K> list = f.get(i);
            m.putNonNull(i, (list == null || list.isEmpty()) ? null : list.get(0));
        }
        return m;
    }

    public KMap<V, KList<K>> flip() {
        KMap<V, KList<K>> flipped = new KMap<>();
        for (K i : keySet()) {
            if (i == null) continue;
            V val = get(i);
            if (!flipped.containsKey(val)) flipped.put(val, new KList<>());
            flipped.get(val).add(i);
        }
        return flipped;
    }

    public KList<V> sortV() {
        KList<V> v = new KList<>();
        KList<K> k = k();
        k.sort(Comparator.comparing(Object::toString));
        for (K i : k) {
            for (V j : v()) {
                if (get(i) != null && get(i).equals(j)) v.add(j);
            }
        }
        v.dedupe();
        return v;
    }

    public KList<V> sortVNoDedupe() {
        KList<V> v = new KList<>();
        KList<K> k = k();
        k.sort(Comparator.comparing(Object::toString));
        for (K i : k) {
            for (V j : v()) {
                if (get(i) != null && get(i).equals(j)) v.add(j);
            }
        }
        return v;
    }

    public KList<K> k() {
        KList<K> k = new KList<>();
        Enumeration<K> kk = keys();
        while (kk.hasMoreElements()) k.add(kk.nextElement());
        return k;
    }

    public KList<V> v() {
        return new KList<>(values());
    }

    public KMap<K, V> qclear() {
        super.clear();
        return this;
    }

    public KMap<K, V> qclear(BiConsumer<K, V> action) {
        Iterator<Entry<K, V>> it = entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            it.remove();
            try {
                action.accept(entry.getKey(), entry.getValue());
            } catch (Throwable ignored) {}
        }
        return this;
    }

    public KList<KeyPair<K, V>> keypair() {
        KList<KeyPair<K, V>> g = new KList<>();
        each((k, v) -> g.add(new KeyPair<>(k, v)));
        return g;
    }

    public Queue<KeyPair<K, V>> enqueue() {
        return Queue.create(keypair());
    }

    public Queue<K> enqueueKeys() {
        return Queue.create(k());
    }

    public Queue<V> enqueueValues() {
        return Queue.create(v());
    }
}
