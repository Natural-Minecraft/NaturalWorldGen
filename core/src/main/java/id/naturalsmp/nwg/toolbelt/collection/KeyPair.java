package id.naturalsmp.nwg.toolbelt.collection;

public class KeyPair<K, V> {
    public final K k;
    public final V v;

    public KeyPair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() { return k; }
    public V getV() { return v; }

    @Override
    public String toString() {
        return k + "=" + v;
    }
}
