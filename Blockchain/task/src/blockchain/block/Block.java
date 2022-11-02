package blockchain.block;

import blockchain.mining.MiningResult;

import java.time.Instant;

public class Block {

    private final long minerId;

    private final int id;
    private final long time;
    private final String previousBlockHash;
    private final String blockHash;
    private final long magicNumber;
    private final int zeroesDelta;
    private final int countOfZeroes;

    private final long timeToGenerateMs;

    private final BlockData data;

    public Block(MiningResult result, Block previousBlock, int zeroesDelta, int countOfZeroes, BlockData data) {
        this.minerId = result.minerId();
        this.id = previousBlock.getId() + 1;
        this.zeroesDelta = zeroesDelta;
        this.countOfZeroes = countOfZeroes;
        this.time = Instant.now().toEpochMilli();
        this.previousBlockHash = previousBlock.getBlockHash();
        this.blockHash = result.blockHash();
        this.magicNumber = result.magicNumber();
        this.timeToGenerateMs = result.timeToGenerate();
        this.data = data;
    }

    public Block(MiningResult result, int zeroesDelta, int countOfZeroes) {
        this.minerId = result.minerId();
        this.zeroesDelta = zeroesDelta;
        this.countOfZeroes = countOfZeroes;
        this.id = 1;
        this.time = Instant.now().toEpochMilli();
        this.previousBlockHash = "0";
        this.blockHash = result.blockHash();
        this.magicNumber = result.magicNumber();
        this.timeToGenerateMs = result.timeToGenerate();
        this.data = BlockEmptyData.getInstance();
    }


    public int getId() {
        return id;
    }

    public long getMinerId() {
        return minerId;
    }

    public long getTime() {
        return time;
    }

    public long getMagicNumber() {
        return magicNumber;
    }

    public int getZeroesDelta() {
        return zeroesDelta;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public long getTimeToGenerateMs() {
        return timeToGenerateMs;
    }

    public int getCountOfZeroes() {
        return countOfZeroes;
    }

    public BlockData getData() {
        return data;
    }
}