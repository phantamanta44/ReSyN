package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;

import java.util.function.Supplier;

public class DirectiveFinish implements IDirective {

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException {
        state.finish();
    }

}
