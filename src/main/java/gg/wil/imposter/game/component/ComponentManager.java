package gg.wil.imposter.game.component;

import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.component.tick.Tick;
import gg.wil.imposter.game.component.tick.TickExecutor;
import gg.wil.imposter.game.component.tick.TickPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class ComponentManager {

    private final Logger logger;
    private final Game game;

    private final Map<TickPriority, Set<TickExecutor>> tickMethods = new EnumMap<>(TickPriority.class);

    public ComponentManager(Game game, String identifier) {
        this.game = game;
        this.logger = LoggerFactory.getLogger("ComponentManager - " + identifier);
    }

    public void registerComponent(final Component component) {
        if(component == null) throw new IllegalArgumentException("Component cannot be null");

        Set<Method> methods;
        try {
            Method[] privateMethods = component.getClass().getDeclaredMethods();
            Method[] publicMethods = component.getClass().getMethods();
            methods = new HashSet<>(privateMethods.length + publicMethods.length, 1.0f);
            methods.addAll(Arrays.asList(privateMethods));
            methods.addAll(Arrays.asList(publicMethods));
        } catch(NoClassDefFoundError e) {
            logger.error("Failed to register component: {}", component.getClass().getName(), e);
            return;
        }

        for(Method method : methods) {
            final Tick tick = method.getAnnotation(Tick.class);
            if(tick == null || method.isBridge() || method.isSynthetic()) continue;
            if(method.getParameterCount() != 0) {
                throw new IllegalStateException("Method annotated with @Tick must not have parameters: " + method.getName() + " in class " + component.getClass().getName());
            }
            method.setAccessible(true);

            tickMethods.computeIfAbsent(tick.priority(), priority -> new HashSet<>()).add(new TickExecutor(component, method));
        }
    }

    public void deregisterComponent(final Component component) {
        tickMethods.values().forEach(executors -> executors.removeIf(executor -> executor.getComponent() == component));
    }

    public void tickComponents() {
        tickMethods.forEach((priority, executors) -> executors.forEach(executor -> {
            try { executor.execute();
            } catch(Throwable t) { logger.error("Failed to invoke tick method: {} on component {}", executor.getMethod().getName(), executor.getComponent() , t); }
        }));
    }
}
