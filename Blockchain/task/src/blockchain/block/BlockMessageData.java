package blockchain.block;

import java.util.List;

public record BlockMessageData(List<Message> messages) implements BlockData {
    @Override
    public String toString() {
        if (!messages.isEmpty()) {
            StringBuilder out = new StringBuilder("Block data:\n");
            messages.forEach(m -> out.append(String.format("%s %s\n", m.text(), m.nick())));
//            messages.forEach(m -> out.append(String.format("%s: %s\n", m.nick(), m.text())));
            return out.toString().trim();
        } else {
            return "Block data: no messages";
        }
    }
}
