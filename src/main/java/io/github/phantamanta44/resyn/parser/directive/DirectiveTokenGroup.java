package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;
import io.github.phantamanta44.resyn.parser.token.TokenNode;

import java.util.function.Supplier;

public class DirectiveTokenGroup implements IDirective {

    private final String name;

    public DirectiveTokenGroup(String name) {
        this.name = name;
    }

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException {
        state.putToken(new TokenNode(name, tokens.get()));
    }

}
