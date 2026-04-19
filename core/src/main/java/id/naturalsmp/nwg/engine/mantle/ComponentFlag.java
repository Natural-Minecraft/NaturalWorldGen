package id.naturalsmp.nwg.engine.mantle;

import id.naturalsmp.nwg.utilities.mantle.flag.ReservedFlag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentFlag {
    ReservedFlag value();
}
