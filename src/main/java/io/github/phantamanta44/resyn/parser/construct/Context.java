package io.github.phantamanta44.resyn.parser.construct;

import java.util.Collections;
import java.util.List;

public class Context {

    private final String name;
    private final List<Rule> rules;
    private final Context parent;
    private boolean visiting;

    public Context(String name, List<Rule> rules, Context parent) {
        this.name = name;
        this.rules = Collections.unmodifiableList(rules);
        this.parent = parent;
        this.visiting = false;
    }

    public Context(String name, List<Rule> rules) {
        this(name, rules, null);
    }

    public String getName() {
        return name;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Context getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public void setVisiting() {
        this.visiting = true;
    }

    public boolean isVisiting() {
        return visiting;
    }

}
