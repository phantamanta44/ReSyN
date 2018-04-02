package io.github.phantamanta44.resyn.parser;

public class ParsingException extends Exception {

    public final String reason;
    public final int line;
    public final int pos;

    public ParsingException(String reason, int line, int pos) {
        super(String.format("%s (%d:%d)", reason, line, pos));
        this.reason = reason;
        this.line = line;
        this.pos = pos;
    }

    public ParsingException(String reason) {
        super(reason);
        this.reason = reason;
        this.line = this.pos = -1;
    }

}
