package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.construct.Context;

import java.util.function.Function;
import java.util.function.Supplier;

public class DirectiveContextSwitch implements IDirective {

    private final Function<Context, Context> contextBinder;
    private final String name;
    private final boolean visiting;

    public DirectiveContextSwitch(Function<Context, Context> contextBinder, String name, boolean visiting) {
        this.contextBinder = contextBinder;
        this.name = name;
        this.visiting = visiting;
    }

    public DirectiveContextSwitch(Function<Context, Context> contextBinder) {
        this(contextBinder, null, false);
    }

    @Override
    public void execute(ParserState state, String match, Supplier<String> tokens) {
        Context ctx = contextBinder.apply(state.getContext());
        if (visiting) ctx.setVisiting();
        state.setContext(ctx, name != null ? name : ctx.getName());
    }

}
