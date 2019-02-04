package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.construct.Rule;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;

import java.util.regex.Matcher;

public class Parser {

    private static Boolean debugEnabled = null;

    public static boolean isDebugFlag() {
        if (debugEnabled == null) {
            debugEnabled = "true".equalsIgnoreCase(System.getProperty("resyn.debug"));
        }
        return debugEnabled;
    }

    private final Syntax syntax;
    private final Context rootContext;
    private final boolean suppressDebug;
    private ParserState state;

    public Parser(Syntax syntax, Context rootContext, boolean suppressDebug) {
        this.syntax = syntax;
        this.rootContext = rootContext;
        this.suppressDebug = suppressDebug;
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
        state = new ParserState(this, rootContext);
    }

    void throwError(String reason) throws ParsingException {
        state.throwError(reason);
    }

    public Parser parse(String code) throws ParsingException {
        code = code.trim();
        Matcher m = null;
        Action action;
        while (!state.isFinished()) {
            action = null;
            for (Rule rule : state.getContext().getRules()) {
                m = rule.getPattern().matcher(code);
                if (m.find()) {
                    if (shouldDebugPrint()) {
                        System.out.printf("Matched rule /%s/ on \"%s\"\n", rule.getRawPattern(), m.group(0));
                    }
                    action = rule.getAction();
                    break;
                }
            }
            if (action == null) {
                if (code.isEmpty()) {
                    break;
                } else {
                    state.throwError("Could not find appropriate token in context: " + state.getContext().getName());
                }
            }
            action.execute(state, m);
            state.updatePos(m.group());
            code = code.substring(m.end());
        }
        if (!code.isEmpty()) throwError("Parser terminated prematurely!");
        return this;
    }

    public Parser parseLine(String code) throws ParsingException {
        parse(code);
        state.updatePos("\n");
        return this;
    }

    boolean shouldDebugPrint() {
        return !suppressDebug && isDebugFlag();
    }

}
