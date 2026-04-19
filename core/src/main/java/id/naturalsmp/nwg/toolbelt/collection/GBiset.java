package id.naturalsmp.nwg.toolbelt.collection;

import java.util.Objects;

/**
 * A generic bi-directional set (ordered pair) holding two values.
 * Used as a map key where (a,b) == (a,b) but NOT equal to (b,a).
 */
public class GBiset<A, B> {
    private final A a;
    private final B b;

    public GBiset(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GBiset)) return false;
        GBiset<?, ?> gBiset = (GBiset<?, ?>) o;
        return Objects.equals(a, gBiset.a) && Objects.equals(b, gBiset.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "GBiset{" + a + ", " + b + "}";
    }
}
