package id.naturalsmp.NaturalWorldGen.engine.mantle;

import id.naturalsmp.NaturalWorldGen.util.mantle.flag.ReservedFlag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentFlag {
    ReservedFlag value();
}
