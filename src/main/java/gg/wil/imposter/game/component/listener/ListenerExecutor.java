package gg.wil.imposter.game.component.listener;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.game.component.Component;
import gg.wil.imposter.game.component.Executor;

import java.lang.reflect.Method;

public class ListenerExecutor extends Executor {

    private final Class<?> messageClass;

    public ListenerExecutor(Component component, Method method, Class<?> messageClass) {
        super(component, method);
        this.messageClass = messageClass;
    }

    public void execute(WebSocketReceiveMessage message) throws ReflectiveOperationException {
        if(message == null) throw new IllegalArgumentException("Message cannot be null");
        if(!messageClass.isAssignableFrom(message.getClass())) throw new IllegalArgumentException("Message is not of type " + messageClass.getName());

        this.method.invoke(this.component, message);
    }
}
