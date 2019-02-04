package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.token.Token;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;
import io.github.phantamanta44.resyn.util.StackNode;

public class ParserState {

    private Context context;
    private final TokenContainer rootContainer;
    private StackNode<TokenContainer> contextualContainer;
    private int line;
    private int pos;
    private boolean terminated;

    ParserState(Context root) {
        this.context = root;
        this.rootContainer = new TokenContainer("root", this);
        this.contextualContainer = new StackNode<>(rootContainer);
        this.line = 0;
        this.pos = 0;
        this.terminated = false;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context newContext, String tokenName) {
        if (terminated) throw new IllegalStateException("Parser already terminated!");
        this.context = newContext;
        TokenContainer token = new TokenContainer(tokenName, this);
        contextualContainer.getValue().getChildren().add(token);
        contextualContainer = contextualContainer.extend(token);
    }

    public void popContext() {
        if (terminated) throw new IllegalStateException("Parser already terminated!");
        while (context.isVisiting()) doPopContext();
        doPopContext();
    }

    private void doPopContext() {
        if (terminated) throw new IllegalStateException("Parser already terminated!");
        context = context.getParent();
        contextualContainer = contextualContainer.getParent();
    }

    public void putToken(Token token) {
        if (terminated) throw new IllegalStateException("Parser already terminated!");
        contextualContainer.getValue().getChildren().add(token);
    }

    TokenContainer getRootToken() {
        return rootContainer;
    }

    public int getLine() {
        return line + 1;
    }

    public int getPos() {
        return pos + 1;
    }

    void updatePos(String match) {
        int index = 0;
        for (int i; (i = match.indexOf('\n', index)) != -1;) {
            index = i + 1;
            line++;
            pos = 0;
        }
        pos += match.length() - index;
    }

    public void finish() {
        terminated = true;
    }

    public boolean isFinished() {
        return terminated;
    }

    public void throwError(String reason) throws ParsingException {
        throw new ParsingException(reason, line + 1, pos + 1);
    }

}
