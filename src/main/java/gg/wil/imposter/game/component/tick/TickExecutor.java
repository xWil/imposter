package gg.wil.imposter.game.component.tick;

import gg.wil.imposter.game.component.Component;
import gg.wil.imposter.game.component.Executor;

import java.lang.reflect.Method;

public class TickExecutor extends Executor {

    public TickExecutor(final Component component, final Method method) {
        super(component, method);
    }

    public void execute() throws ReflectiveOperationException {
        this.method.invoke(this.component);
    }
}
