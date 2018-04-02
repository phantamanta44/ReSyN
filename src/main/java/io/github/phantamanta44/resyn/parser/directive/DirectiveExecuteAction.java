package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;
import io.github.phantamanta44.resyn.parser.construct.Action;

import java.util.function.Supplier;

public class DirectiveExecuteAction implements IDirective {

    private final Action action;

    public DirectiveExecuteAction(Action action) {
        this.action = action;
    }

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException {
        action.execute(state, match, tokens);
    }

}
