package gg.wil.imposter.game;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveIconChangeMessage;

import java.util.Set;

public class GameThread extends Thread {

    private final Game game;

    private boolean running = true;

    public GameThread(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        long tickCount = 0;

        final long TARGET_TPS = 5;
        final long TARGET_TICK_TIME = 1000000000/TARGET_TPS;
        final long START_TIME = System.nanoTime();

        while(running) {
            long start = System.nanoTime();
            // calculate timeDebt
            long correctStartTime = START_TIME + (tickCount * TARGET_TICK_TIME);
            long timeDebt = Math.abs(correctStartTime - start);

            // handle game stuff
            this.tick();

            // wait for the next tick
            tickCount++;
            long tickTime = System.nanoTime()-start;
            long sleepTime = TARGET_TICK_TIME-tickTime;
            sleepTime -= timeDebt;

            if(sleepTime > 0) {
                long finishTime = System.nanoTime() + sleepTime;
                while(System.nanoTime() <= finishTime) {
                    try { Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    private void tick() {
        processMessages();
    }

    private void processMessages() {
        Set<WebSocketReceiveMessage> messages = game.getUnprocessedMessages(true);
        System.out.println("Handling " + messages.size() + " messages");
        for(WebSocketReceiveMessage message : messages) {
            switch (message) {
                case ReceiveIconChangeMessage receiveIconChangeMessage -> {
                    System.out.println("Icon change message received from " + receiveIconChangeMessage.getFrom().getUUID());
                }
                default -> throw new IllegalStateException("Unexpected value: " + message);
            }
        }
    }

    public void stopGame() {
        running = false;
    }
}
