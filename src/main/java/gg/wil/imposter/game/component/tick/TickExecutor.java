package gg.wil.imposter.game.component.tick;

import gg.wil.imposter.game.component.Component;

import java.lang.reflect.Method;

public class TickExecutor {

    private final Component component;
    private final Method method;

    public TickExecutor(final Component component, final Method method) {
        this.component = component;
        this.method = method;
    }

    public Component getComponent() {
        return this.component;
    }

    public Method getMethod() {
        return this.method;
    }

    public void execute() throws ReflectiveOperationException {
        this.method.invoke(this.component);
    }
}
