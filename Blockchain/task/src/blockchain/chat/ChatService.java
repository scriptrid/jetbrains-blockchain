package blockchain.chat;

import blockchain.Chain;
import blockchain.block.Message;

import java.util.concurrent.ThreadLocalRandom;

public class ChatService {
    public static final String[] NICKS = new String[]{"Ann", "Sergey", "Ivan", "Scott", "Boris", "Anastasia", "Vladimir"};
    private final Chain chain;

    public ChatService(Chain chain) {
        this.chain = chain;
    }

    public void send() {
        chain.addMessage(generateMessage());
    }

    private Message generateMessage() {
        String nick = NICKS[ThreadLocalRandom.current().nextInt(0, NICKS.length)];
        String text = String.format("miner%d sent %d VC to", ThreadLocalRandom.current().nextInt(1,10), ThreadLocalRandom.current().nextInt(1,100));
//        String text = String.format("I sent the message for the %d block!", chain.getSize() > 0 ? chain.getLastBlock().getId() + 1 : 1);
        return new Message(nick, text);
    }
}
