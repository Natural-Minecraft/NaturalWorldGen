package id.naturalsmp.nwg.toolbelt.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A generic list adapter that converts elements from type F to type T.
 * Subclass and implement {@link #onAdapt(Object)} to define the conversion.
 */
public abstract class GListAdapter<F, T> {

    public abstract T onAdapt(F from);

    public List<T> adapt(Collection<F> from) {
        List<T> result = new ArrayList<>(from.size());
        for (F f : from) {
            T t = onAdapt(f);
            if (t != null) result.add(t);
        }
        return result;
    }
}
