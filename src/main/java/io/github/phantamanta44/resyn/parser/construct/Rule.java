package io.github.phantamanta44.resyn.parser.construct;

import java.util.regex.Pattern;

public class Rule {

    private final String rawPattern;
    private final Pattern pattern;
    private final Action action;

    public Rule(String pattern, Action action) {
        this.rawPattern = pattern;
        this.pattern = Pattern.compile("^" + pattern, Pattern.DOTALL);
        this.action = action;
    }

    public String getRawPattern() {
        return rawPattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Action getAction() {
        return action;
    }

}
