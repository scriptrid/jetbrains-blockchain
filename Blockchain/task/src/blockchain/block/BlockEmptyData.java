package blockchain.block;

public final class BlockEmptyData implements BlockData {
    private static final BlockEmptyData instance = new BlockEmptyData();

    private BlockEmptyData() {
    }

    public static BlockEmptyData getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "Block data: no messages";
    }
}
