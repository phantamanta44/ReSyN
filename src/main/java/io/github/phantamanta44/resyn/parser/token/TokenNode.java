package io.github.phantamanta44.resyn.parser.token;

public class TokenNode implements IToken {

    private final String name;
    private final String content;

    public TokenNode(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    @Override
    public TokenType getType() {
        return TokenType.NODE;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name, content);
    }

}
