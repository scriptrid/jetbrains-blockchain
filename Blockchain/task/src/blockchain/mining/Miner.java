package blockchain.mining;

import blockchain.Chain;
import blockchain.block.Block;
import blockchain.block.BlockData;
import blockchain.block.BlockEmptyData;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class Miner implements Runnable {

    private final int number;
    private final Chain chain;


    public Miner(int number, Chain chain) {
        this.number = number;
        this.chain = chain;
    }

    private MiningResult createFirstMiningResult() throws InterruptedException {
        long time = Instant.now().toEpochMilli();
        return tryToMine(toHashTemplate(0, time, "0"), chain.getCountOfZeroes(), 0, time);
    }

    private MiningResult createMiningResult() throws InterruptedException {
        if (chain.isEmpty()) {
            return createFirstMiningResult();
        }
        Block lastBlock = chain.getLastBlock();
        int id = lastBlock.getId() + 1;
        int sizeOfChain = chain.getSize();
        long time = Instant.now().toEpochMilli();
        String previousBlockHash = lastBlock.getBlockHash();
        return tryToMine(toHashTemplate(id, time, previousBlockHash), chain.getCountOfZeroes(), sizeOfChain, time);
    }

    private MiningResult tryToMine(String toHash, int countOfZeroes, int sizeOfChain, long time) throws InterruptedException {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        long start = Instant.now().toEpochMilli();
        while (true) {
            if (sizeOfChain != chain.getSize()) {
                return null;
            }
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
            long magicNumber = rnd.nextInt();
            String hash = Sha256.applySha256(magicNumber + toHash);
            if (hash.substring(0, countOfZeroes).chars().noneMatch(c -> c != '0')) {
                long finish = Instant.now().toEpochMilli();
                long timeToGenerateMs = finish - start;
                return new MiningResult(number, time, magicNumber, timeToGenerateMs, hash);
            }
        }
    }

    private String toHashTemplate(int id, long time, String previousBlockHash) {
        return toHashTemplate(id, time, previousBlockHash, BlockEmptyData.getInstance());
    }

    private String toHashTemplate(int id, long time, String previousBlockHash, BlockData data) {
        return String.format("%d:%d:%s:%s:", id, time, previousBlockHash, data.toString());
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                MiningResult result = createMiningResult();
                if (result == null) {
                    continue;
                }
                chain.addBlock(result);
            }
        } catch (InterruptedException ignored) {

        }
    }
}
