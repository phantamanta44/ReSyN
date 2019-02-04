package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.construct.Rule;
import io.github.phantamanta44.resyn.parser.directive.DirectiveContextPop;
import io.github.phantamanta44.resyn.parser.directive.DirectiveExecuteAction;
import io.github.phantamanta44.resyn.parser.directive.IDirective;
import io.github.phantamanta44.resyn.parser.token.Token;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;
import io.github.phantamanta44.resyn.parser.token.TokenNode;
import io.github.phantamanta44.resyn.parser.token.TokenType;
import io.github.phantamanta44.resyn.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Syntax {

    private static final Syntax SYNTAX_PARSER = ReSyNParser.get();

    public static Syntax create(String syntax) throws ParsingException {
        TokenContainer parsed = SYNTAX_PARSER.parse(syntax);
        Map<String, TokenContainer> actionTokens = new HashMap<>();
        Map<String, TokenContainer> ruleTokens = new HashMap<>();
        Map<String, Pair<Boolean, TokenContainer>> contextTokens = new HashMap<>();
        for (Token token0 : parsed.getChildren()) {
            TokenContainer token = (TokenContainer)token0;
            String identifier = ((TokenNode)token.getChildren().get(0)).getContent();
            switch (token.getName()) {
                case "action":
                    if (actionTokens.containsKey(identifier)) {
                        throw new ParsingException("Duplicate action: " + identifier);
                    }
                    actionTokens.put(identifier, token);
                    break;
                case "rule":
                    if (ruleTokens.containsKey(identifier)) {
                        throw new ParsingException("Duplicate rule: " + identifier);
                    }
                    ruleTokens.put(identifier, token);
                    break;
                case "context":
                    boolean trans = identifier.endsWith("!");
                    if (trans) identifier = identifier.substring(0, identifier.length() - 1);
                    if (contextTokens.containsKey(identifier)) {
                        throw new ParsingException("Duplicate context: " + identifier);
                    }
                    contextTokens.put(identifier, new Pair<>(trans, token));
                    break;
            }
        }
        if (!contextTokens.containsKey("root")) throw new ParsingException("No root context!");

        Map<String, Action> actions = new HashMap<>();
        Map<String, Rule> rules = new HashMap<>();
        Map<Rule, List<Token>> uninitializedRules = new HashMap<>();
        Map<String, Function<Context, Context>> contexts = new HashMap<>();
        for (String action : actionTokens.keySet()) actions.put(action, new Action(new LinkedList<>()));
        for (Map.Entry<String, TokenContainer> rule : ruleTokens.entrySet()) {
            String pattern = ((TokenContainer)rule.getValue().getChildren().get(1)).getChildren().stream()
                    .map(t -> ((TokenNode)t).getContent())
                    .collect(Collectors.joining());
            Rule newRule = new Rule(pattern, new Action(new LinkedList<>()));
            rules.put(rule.getKey(), newRule);
            uninitializedRules.put(newRule, ((TokenContainer)rule.getValue().getChildren().get(2)).getChildren());
        }
        for (Map.Entry<String, Pair<Boolean, TokenContainer>> context : contextTokens.entrySet()) {
            List<Rule> contextRules = new LinkedList<>();
            List<Token> tokens = context.getValue().b.getChildren();
            for (int i = 1; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token.getType() == TokenType.NODE) {
                    String identifier = ((TokenNode)token).getContent();
                    if (identifier.startsWith("*")) {
                        int exitCount = identifier.lastIndexOf("*") + 1;
                        identifier = identifier.substring(exitCount);
                        if (!rules.containsKey(identifier)) throw new ParsingException("Unknown rule: " + identifier);
                        Rule parent = rules.get(identifier);
                        Rule rule = new Rule(parent.getRawPattern(), new Action(new LinkedList<>()));
                        rule.getAction().getDirectives().add(new DirectiveExecuteAction(parent.getAction()));
                        rule.getAction().getDirectives().add(new DirectiveContextPop(exitCount));
                        contextRules.add(rule);
                    } else {
                        if (!rules.containsKey(identifier)) throw new ParsingException("Unknown rule: " + identifier);
                        contextRules.add(rules.get(identifier));
                    }
                } else {
                    TokenContainer rule = (TokenContainer)token;
                    String pattern = ((TokenContainer)rule.getChildren().get(0))
                            .getChildren().stream()
                            .map(t -> ((TokenNode)t).getContent())
                            .collect(Collectors.joining());
                    Rule newRule = new Rule(pattern, new Action(new LinkedList<>()));
                    contextRules.add(newRule);
                    uninitializedRules.put(newRule, ((TokenContainer)rule.getChildren().get(1)).getChildren());
                }
            }
            contexts.put(context.getKey(), c -> new Context(context.getKey(), contextRules, context.getValue().a, c));
        }

        for (Map.Entry<String, Action> action : actions.entrySet()) {
            List<Token> tokens = actionTokens.get(action.getKey()).getChildren();
            for (int i = 1; i < tokens.size(); i++) {
                action.getValue().getDirectives().add(
                        IDirective.parse((TokenContainer)tokens.get(i), contexts, actions));
            }
        }
        for (Map.Entry<Rule, List<Token>> rule : uninitializedRules.entrySet()) {
            for (Token dir : rule.getValue()) {
                rule.getKey().getAction().getDirectives()
                        .add(IDirective.parse((TokenContainer)dir, contexts, actions));
            }
        }
        Function<Context, Context> rootFactory = contexts.get("root");
        return new Syntax(() -> rootFactory.apply(null));
    }

    final Supplier<Context> rootContextFactory;

    Syntax(Supplier<Context> rootContextFactory) {
        this.rootContextFactory = rootContextFactory;
    }

    public TokenContainer parse(String code) throws ParsingException {
        Parser parser = newPartialParser().parse(code);
        if (!parser.isRootContext()) {
            parser.throwError("Unexpected end of source in context: " + parser.getCurrentContext().getName());
        }
        return parser.getTree();
    }

    public Parser newPartialParser() {
        return new Parser(this, rootContextFactory.get(), false);
    }

}
