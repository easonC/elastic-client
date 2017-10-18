package easonc.elastic.client.enums;

/**
 * Created by zhuqi on 2016/11/17.
 */
public enum FieldIndexOption {

    Analyzed("analyzed"),
    NotAnalyzed("not_analyzed"),
    No("no");
    private final String text;

    private FieldIndexOption(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
