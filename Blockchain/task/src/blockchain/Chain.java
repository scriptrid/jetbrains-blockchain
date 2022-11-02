package blockchain;

import blockchain.block.Block;
import blockchain.block.BlockData;
import blockchain.block.BlockMessageData;
import blockchain.block.Message;
import blockchain.mining.MiningResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class Chain {
    private List<Message> messagesForNextBlock = new ArrayList<>();
    private List<Message> messagesForCurrentBlock = new ArrayList<>();

    private final Deque<Block> chain;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private int countOfZeroes;

    private Consumer<Integer> sizeEventListener;

    public Chain() {
        chain = new ArrayDeque<>();
        countOfZeroes = 2;
    }

    public void addBlock(MiningResult result) {
        if (result == null) {
            return;
        }
        boolean success = false;
        try {
            writeLock.lock();
            int deltaZeroes = getDeltaZeroes(result);
            Block block;

            if (chain.isEmpty()) {
                block = new Block(result, deltaZeroes, countOfZeroes);
            } else {
                List<Message> toData = List.copyOf(messagesForCurrentBlock);
                BlockData data = new BlockMessageData(toData);
                block = new Block(result, chain.getLast(), deltaZeroes, countOfZeroes, data);
            }
            if (isValidBlock(block)) {
                chain.add(block);
                messagesForCurrentBlock = messagesForNextBlock;
                messagesForNextBlock = new ArrayList<>();
//                countOfZeroes += deltaZeroes;
                success = true;
            }
        } finally {
            writeLock.unlock();
        }
        if (success) {
            sizeEventListener.accept(chain.size());
        }

    }


    public void printChain() {
        for (Block block : chain) {
            printInfo(block);
        }
    }


    private boolean isValidBlock(Block block) {
        if (block == null) {
            return false;
        }
        if (block.getBlockHash().substring(0, countOfZeroes).chars().anyMatch(c -> c != '0')) {
            return false;
        }
        Block lastBlock = chain.peekLast();
        if (lastBlock == null) {
            return true;
        }
        if (!lastBlock.getBlockHash().equals(block.getPreviousBlockHash())) {
            return false;
        }
        return block.getId() == lastBlock.getId() + 1;
    }

    private int getDeltaZeroes(MiningResult result) {
        long lastTimeToGenerateInSeconds = TimeUnit.MILLISECONDS.toSeconds(result.timeToGenerate());
        if (lastTimeToGenerateInSeconds < 1) {
            return 1;
        } else if (lastTimeToGenerateInSeconds > 1) {
            return -1;
        }
        return 0;
    }

    public boolean isEmpty() {
        try {
            readLock.lock();
            return chain.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    public Block getLastBlock() {
        try {
            readLock.lock();
            return chain.getLast();
        } finally {
            readLock.unlock();
        }
    }

    public int getCountOfZeroes() {
        try {
            readLock.lock();
            return countOfZeroes;
        } finally {
            readLock.unlock();
        }
    }

    public int getSize() {
        try {
            readLock.lock();
            return chain.size();
        } finally {
            readLock.unlock();
        }
    }

    public void setSizeEventListener(Consumer<Integer> sizeEventListener) {
        this.sizeEventListener = sizeEventListener;
    }

    private void printInfo(Block block) {
        System.out.printf("""
                        Block:
                        Created by: miner%d
                        miner%d gets 100 VC
                        Id: %d
                        Timestamp: %d
                        Magic number: %d
                        Hash of the previous block:
                        %s
                        Hash of the block:
                        %s
                        %s
                        Block was generating for %d seconds
                        N %s
                                                
                        """,
                block.getMinerId(),
                block.getMinerId(),
                block.getId(),
                block.getTime(),
                block.getMagicNumber(),
                block.getPreviousBlockHash(),
                block.getBlockHash(),
                block.getData().toString(),
                TimeUnit.MILLISECONDS.toSeconds(block.getTimeToGenerateMs()),
                switch (block.getZeroesDelta()) {
                    case -1 -> "was decreased by 1";
                    case 1 -> "was increased to " + block.getCountOfZeroes();
                    case 0 -> "N stays the same";
                    default -> "is nothing";
                });
    }

    public void addMessage(Message message) {
        try {
            writeLock.lock();
            messagesForNextBlock.add(message);
        } finally {
            writeLock.unlock();
        }
    }
}