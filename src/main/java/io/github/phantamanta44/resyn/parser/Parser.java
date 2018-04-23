package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.construct.Rule;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;

import java.util.regex.Matcher;

public class Parser {

    private final Syntax syntax;
    private final Context rootContext;
    private ParserState state;

    public Parser(Syntax syntax, Context rootContext) {
        this.syntax = syntax;
        this.rootContext = rootContext;
        flush();
    }

    public Syntax getSyntax() {
        return syntax;
    }

    public Context getRootContext() {
        return rootContext;
    }

    public Context getCurrentContext() {
        return state.getContext();
    }

    public boolean isRootContext() {
        return state.getContext() == rootContext;
    }

    public TokenContainer getTree() {
        return state.getRootToken();
    }

    public int getLine() {
        return state.getLine();
    }

    public int getPos() {
        return state.getPos();
    }

    public void flush() {
        state = new ParserState(rootContext);
    }

    void throwError(String reason) throws ParsingException {
        state.throwError(reason);
    }

    public Parser parse(String code) throws ParsingException {
        code = code.trim();
        Matcher m = null;
        Action action;
        while (!code.isEmpty()) {
            action = null;
            for (Rule rule : state.getContext().getRules()) {
                m = rule.getPattern().matcher(code);
                if (m.find()) {
                    action = rule.getAction();
                    break;
                }
            }
            if (action == null) {
                state.throwError("Could not find appropriate token in context: " + state.getContext().getName());
            }
            action.execute(state, m);
            state.updatePos(m.group());
            code = code.substring(m.end());
        }
        return this;
    }

    public Parser parseLine(String code) throws ParsingException {
        parse(code);
        state.updatePos("\n");
        return this;
    }

}
