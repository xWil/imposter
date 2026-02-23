package gg.wil.imposter.game.component;

import java.lang.reflect.Method;

public abstract class Executor {

    protected final Component component;
    protected final Method method;

    public Executor(final Component component, final Method method) {
        this.component = component;
        this.method = method;
    }

    public Component getComponent() {
        return this.component;
    }

    public Method getMethod() {
        return this.method;
    }

}
