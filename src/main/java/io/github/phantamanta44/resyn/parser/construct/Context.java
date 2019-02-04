package io.github.phantamanta44.resyn.parser.construct;

import java.util.Collections;
import java.util.List;

public class Context {

    private final String name;
    private final List<Rule> rules;
    private final Context parent;
    private final boolean trans;
    private boolean visiting;

    public Context(String name, List<Rule> rules, boolean trans, Context parent) {
        this.name = name;
        this.rules = Collections.unmodifiableList(rules);
        this.parent = parent;
        this.trans = trans;
        this.visiting = false;
    }

    public Context(String name, List<Rule> rules, Context parent) {
        this(name, rules, false, parent);
    }

    public Context(String name, List<Rule> rules) {
        this(name, rules, false, null);
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

    public boolean isTransparent() {
        return trans;
    }

    public void setVisiting() {
        this.visiting = true;
    }

    public boolean isVisiting() {
        return visiting;
    }

}
