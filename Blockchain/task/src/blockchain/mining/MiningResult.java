package blockchain.mining;

public record MiningResult(int minerId, long time, long magicNumber, long timeToGenerate, String blockHash) {
}

