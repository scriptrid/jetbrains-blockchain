package blockchain;

import blockchain.chat.ChatService;
import blockchain.chat.Sender;
import blockchain.mining.Miner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class Main {
    public static void main(String[] args) {
        final int numberOfThreads = 10;
        ExecutorService minerExecutor = Executors.newFixedThreadPool(numberOfThreads);
        ExecutorService senderExecutor = Executors.newFixedThreadPool(2);
        Chain chain = new Chain();
        Sender sender = new Sender(new ChatService(chain));
        CountDownLatch shutdownLatch = new CountDownLatch(1);
        Consumer<Integer> sizeEventListener = (size) -> {
            if (size == 15) {
                minerExecutor.shutdownNow();
                senderExecutor.shutdownNow();
                shutdownLatch.countDown();
            }
        };
        chain.setSizeEventListener(sizeEventListener);
        for (int i = 1; i <= numberOfThreads; i++) {
            minerExecutor.submit(new Miner(i, chain));
        }
        senderExecutor.execute(sender);
        try {
            shutdownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        chain.printChain();
    }
}