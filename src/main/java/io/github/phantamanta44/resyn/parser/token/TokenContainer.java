package io.github.phantamanta44.resyn.parser.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TokenContainer implements IToken {

    private final String name;
    private final List<IToken> children;

    public TokenContainer(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    @Override
    public TokenType getType() {
        return TokenType.CONTAINER;
    }

    public List<IToken> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        if (children.isEmpty()) return String.format("%s: {}", name);
        return String.format("%s: {\n%s\n}", name, children.stream()
                .flatMap(t -> Arrays.stream(t.toString().split("\n")))
                .map(t -> "  " + t)
                .collect(Collectors.joining("\n")));
    }

}
