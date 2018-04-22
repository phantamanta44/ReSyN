package io.github.phantamanta44.resyn.parser.token;

import io.github.phantamanta44.resyn.parser.ParserState;

public class TokenNode extends Token {

    private final String content;

    public TokenNode(String name, String content, ParserState state) {
        super(name, state.getLine(), state.getPos());
        this.content = content;
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
        return String.format("%s: %s", getName(), content);
    }

}
