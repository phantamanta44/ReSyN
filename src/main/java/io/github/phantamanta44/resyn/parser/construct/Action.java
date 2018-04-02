package io.github.phantamanta44.resyn.parser.construct;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;
import io.github.phantamanta44.resyn.parser.directive.IDirective;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Matcher;

public class Action {

    private final List<IDirective> directives;

    public Action(List<IDirective> directives) {
        this.directives = directives;
    }

    public void execute(ParserState state, Matcher m) throws ParsingException {
        AtomicInteger tokenIndex = new AtomicInteger(1);
        execute(state, m.group(), () -> m.group(tokenIndex.getAndIncrement()));
    }

    public void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException {
        for (IDirective dir : directives) dir.execute(state, match, tokens);
    }

    public List<IDirective> getDirectives() {
        return directives;
    }

}
