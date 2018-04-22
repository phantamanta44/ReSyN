package io.github.phantamanta44.resyn.parser.token;

import io.github.phantamanta44.resyn.parser.ParserState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenContainer extends Token {

    private final List<Token> children;

    public TokenContainer(String name, ParserState state) {
        super(name, state.getLine(), state.getPos());
        this.children = new ArrayList<>();
    }

    @Override
    public TokenType getType() {
        return TokenType.CONTAINER;
    }

    public List<Token> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        if (children.isEmpty()) return String.format("%s: {}", getName());
        return String.format("%s: {\n%s\n}", getName(), children.stream()
                .flatMap(t -> Arrays.stream(t.toString().split("\n")))
                .map(t -> "  " + t)
                .collect(Collectors.joining("\n")));
    }

}
