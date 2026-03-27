package gg.wil.imposter.game.component;

import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.component.listener.ListenerExecutor;
import gg.wil.imposter.game.component.listener.ListenerPriority;
import gg.wil.imposter.game.component.listener.MessageListener;
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
    private final Map<Class<? extends WebSocketReceiveMessage>, EnumMap<ListenerPriority, Set<ListenerExecutor>>> messageListeners = new HashMap<>();

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
            if(method.isBridge() || method.isSynthetic()) continue;
            // @Tick annotation
            final Tick tick = method.getAnnotation(Tick.class);
            if(tick != null) {
                this.processTickMethod(method, tick, component);
                continue;
            }

            // @MessageListener annotation
            final MessageListener messageListener = method.getAnnotation(MessageListener.class);
            if(messageListener != null) {
                this.processMessageListenerMethod(method, messageListener, component);
                continue;
            }
        }
    }

    private void processTickMethod(Method method, Tick tick, Component component) {
        if(method.getParameterCount() != 0) {
            throw new IllegalStateException("Method annotated with @Tick must not have parameters: " + method.getName() + " in class " + component.getClass().getName());
        }

        method.setAccessible(true);
        tickMethods.computeIfAbsent(tick.priority(), priority -> new HashSet<>()).add(new TickExecutor(component, method));
    }

    private void processMessageListenerMethod(Method method, MessageListener messageListener, Component component) {
        if(method.getParameterCount() != 1) {
            throw new IllegalStateException("Method annotated with @MessageListener must have exactly one parameter: " + method.getName() + " in class " + component.getClass().getName());
        }

        final Class<?> parameterType = method.getParameterTypes()[0];
        if(!WebSocketReceiveMessage.class.isAssignableFrom(parameterType)) {
            throw new IllegalStateException("Method annotated with @MessageListener must have parameter of type WebSocketReceiveMessage: " + method.getName() + " in class " + component.getClass().getName());
        }
        final Class<? extends WebSocketReceiveMessage> messageType = parameterType.asSubclass(WebSocketReceiveMessage.class);

        method.setAccessible(true);
        ListenerPriority priority = messageListener.priority();
        ListenerExecutor listenerExecutor = new ListenerExecutor(component, method, parameterType);

        this.messageListeners.computeIfAbsent(messageType, type -> new EnumMap<>(ListenerPriority.class))
                .computeIfAbsent(priority, p -> new HashSet<>()).add(listenerExecutor);
    }

    public void deregisterComponent(final Component component) {
        this.tickMethods.values().forEach(executors -> executors.removeIf(executor -> executor.getComponent() == component));
        this.messageListeners.values().forEach(listeners -> listeners.values()
                .forEach(executors -> executors.removeIf(executor -> executor.getComponent() == component)));
    }

    public void tickComponents() {
        Map<TickPriority, Set<TickExecutor>> tickMethodsSnapshot = Map.copyOf(this.tickMethods); // snapshot before iteration to prevent concurrent modification
        tickMethodsSnapshot.forEach((priority, executors) -> {
            Set<TickExecutor> executorsSnapshot = new HashSet<>(executors);
            executorsSnapshot.forEach(executor -> {
                try { executor.execute();
                } catch(Throwable t) { logger.error("Failed to invoke tick method: {} on component {}", executor.getMethod().getName(), executor.getComponent() , t); }
            });
        });
    }

    public void broadcastMessage(WebSocketReceiveMessage message) {
        Map<ListenerPriority, Set<ListenerExecutor>> listeners = this.messageListeners.get(message.getClass());
        if(listeners == null) return;
        listeners = Map.copyOf(listeners); // snapshot before iteration to prevent concurrent modification
        listeners.forEach((priority, executors) -> {
            Set<ListenerExecutor> executorsSnapshot = new HashSet<>(executors);
            executorsSnapshot.forEach(executor -> {
                try { executor.execute(message);
                } catch (Throwable t) { logger.error("Failed to invoke message listener method: {} on component {}", executor.getMethod().getName(), executor.getComponent(), t); }
            });
        });
    }
}
