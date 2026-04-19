package id.naturalsmp.nwg.engine.object.annotations;

import id.naturalsmp.nwg.engine.framework.ListFunction;
import id.naturalsmp.nwg.toolbelt.collection.KList;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER, TYPE, FIELD})
public @interface RegistryListFunction {
    Class<? extends ListFunction<KList<String>>> value();
}
