package easonc.elastic.client.enums;

/**
 * Created by zhuqi on 2016/11/17.
 */
public enum TermVectorOption {

    No("no"),

    Yes("yes"),

    WithOffsets("with_offsets"),

    WithPositions("with_positions"),

    WithPositionsOffsets("with_positions_offsets"),

    WithPositionsOffsetsPayloads("with_positions_offsets_payloads");

    private final String text;

    private TermVectorOption(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
