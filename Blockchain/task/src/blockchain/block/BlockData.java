package blockchain.block;

public sealed interface BlockData permits BlockEmptyData, BlockMessageData {
    @Override
    String toString();
}

