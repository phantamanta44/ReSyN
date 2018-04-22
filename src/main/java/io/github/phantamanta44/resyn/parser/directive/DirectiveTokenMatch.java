package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.token.TokenNode;

import java.util.function.Supplier;

public class DirectiveTokenMatch implements IDirective {

    private final String name;

    public DirectiveTokenMatch(String name) {
        this.name = name;
    }

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) {
        state.putToken(new TokenNode(name, match, state));
    }

}
