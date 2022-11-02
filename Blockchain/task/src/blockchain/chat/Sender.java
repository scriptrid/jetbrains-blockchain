package blockchain.chat;

import java.util.concurrent.ThreadLocalRandom;

public class Sender implements Runnable {
    private final ChatService service;

    public Sender(ChatService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (true) {
            service.send();
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(0, 150));
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
