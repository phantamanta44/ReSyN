package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.construct.Rule;
import io.github.phantamanta44.resyn.parser.directive.DirectiveContextPop;
import io.github.phantamanta44.resyn.parser.directive.DirectiveContextSwitch;
import io.github.phantamanta44.resyn.parser.directive.DirectiveTokenGroup;
import io.github.phantamanta44.resyn.parser.directive.DirectiveTokenMatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ReSyNParser {

    private static final List<Rule> CTX_DIRECTIVE = Collections.emptyList();
    private static final Rule RULE_DIRECTIVE = new Rule("\\s*([\\w_]+)(?:\\s+([^;]+?))?\\s*;", new Action(Arrays.asList(
            new DirectiveContextSwitch(c -> new Context("directive", CTX_DIRECTIVE, c)),
            new DirectiveTokenGroup("identifier"),
            new DirectiveTokenGroup("parameters"),
            new DirectiveContextPop(1)
    )));
    private static final Rule RULE_EXIT_BLOCK = new Rule("\\s*}", new Action(Collections.singletonList(
            new DirectiveContextPop(1)
    )));
    private static final List<Rule> CTX_REGEX = Arrays.asList(
            new Rule("\\\\.", new Action(Collections.singletonList(
                    new DirectiveTokenMatch("string")
            ))),
            new Rule("\\/", new Action(Collections.singletonList(
                    new DirectiveContextPop(1)
            ))),
            new Rule("[^\\\\\\/]+", new Action(Collections.singletonList(
                    new DirectiveTokenMatch("string")
            )))
    );
    private static final List<Rule> CTX_ACTION = Arrays.asList(
            RULE_EXIT_BLOCK,
            RULE_DIRECTIVE
    );
    private static final List<Rule> CTX_RULE = Arrays.asList(
            new Rule("\\s*\\{", new Action(Collections.singletonList(
                    new DirectiveContextSwitch(c -> new Context("action", CTX_ACTION, c), null, true)
            ))),
            new Rule("\\s*;", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("action", CTX_ACTION, c), null, true),
                    new DirectiveContextPop(1)
            )))
    );
    private static final List<Rule> CTX_CONTEXT = Arrays.asList(
            RULE_EXIT_BLOCK,
            new Rule("\\s*\\/", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("rule", CTX_RULE, c)),
                    new DirectiveContextSwitch(c -> new Context("regex", CTX_REGEX, c))
            ))),
            new Rule("\\s*(\\**[\\w_]+)\\s*;", new Action(Collections.singletonList(
                    new DirectiveTokenGroup("include")
            )))
    );
    private static final List<Rule> CTX_ROOT = Arrays.asList(
            new Rule("\\s*action\\s+([\\w_]+)\\s*\\{", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("action", CTX_ACTION, c)),
                    new DirectiveTokenGroup("identifier")
            ))),
            new Rule("\\s*rule\\s+([\\w_]+)\\s+\\/", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("rule", CTX_RULE, c)),
                    new DirectiveTokenGroup("identifier"),
                    new DirectiveContextSwitch(c -> new Context("regex", CTX_REGEX, c))
            ))),
            new Rule("\\s*context\\s+([\\w_]+)\\s*\\{", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("context", CTX_CONTEXT, c)),
                    new DirectiveTokenGroup("identifier")
            ))),
            new Rule("\\s*context\\s+([\\w_]+)\\s*;", new Action(Arrays.asList(
                    new DirectiveContextSwitch(c -> new Context("context", CTX_CONTEXT, c)),
                    new DirectiveTokenGroup("identifier"),
                    new DirectiveContextPop(1)
            )))
    );

    static Syntax get() {
        return new Syntax(() -> new Context("root", CTX_ROOT));
    }

}
