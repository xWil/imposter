package gg.wil.imposter.game.component.tick;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Tick {

    TickPriority priority() default TickPriority.NORMAL;
}
