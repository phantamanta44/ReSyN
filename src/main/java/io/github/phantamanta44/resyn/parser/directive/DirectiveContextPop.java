package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;

import java.util.function.Supplier;

public class DirectiveContextPop implements IDirective {

    private final int count;

    public DirectiveContextPop(int count) {
        this.count = count;
    }

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException {
        if (count < 1) state.throwError("Invalid exit count!");
        for (int i = 0; i < count; i++) {
            if (!state.getContext().hasParent()) {
                state.finish();
                break;
            }
            state.popContext();
        }
    }

}
