package io.github.phantamanta44.resyn.parser.token;

public abstract class Token {

    private final String name;
    private final int line, pos;

    public Token(String name, int line, int pos) {
        this.name = name;
        this.line = line;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public abstract TokenType getType();

}
