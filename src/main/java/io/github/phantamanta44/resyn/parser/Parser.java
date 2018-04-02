package io.github.phantamanta44.resyn.parser;

import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.construct.Rule;
import io.github.phantamanta44.resyn.parser.directive.DirectiveContextPop;
import io.github.phantamanta44.resyn.parser.directive.DirectiveExecuteAction;
import io.github.phantamanta44.resyn.parser.directive.IDirective;
import io.github.phantamanta44.resyn.parser.token.IToken;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;
import io.github.phantamanta44.resyn.parser.token.TokenNode;
import io.github.phantamanta44.resyn.parser.token.TokenType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Parser {

    private static final Parser PARSER_PARSER = ParserParser.get();

    public static Parser create(String syntax) throws ParsingException {
        TokenContainer parsed = PARSER_PARSER.parse(syntax);
        Map<String, TokenContainer> actionTokens = new HashMap<>();
        Map<String, TokenContainer> ruleTokens = new HashMap<>();
        Map<String, TokenContainer> contextTokens = new HashMap<>();
        for (IToken token0 : parsed.getChildren()) {
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
                    if (contextTokens.containsKey(identifier)) {
                        throw new ParsingException("Duplicate context: " + identifier);
                    }
                    contextTokens.put(identifier, token);
                    break;
            }
        }
        if (!contextTokens.containsKey("root")) throw new ParsingException("No root context!");

        Map<String, Action> actions = new HashMap<>();
        Map<String, Rule> rules = new HashMap<>();
        Map<Rule, List<IToken>> uninitializedRules = new HashMap<>();
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
        for (Map.Entry<String, TokenContainer> context : contextTokens.entrySet()) {
            List<Rule> contextRules = new LinkedList<>();
            List<IToken> tokens = context.getValue().getChildren();
            for (int i = 1; i < tokens.size(); i++) {
                IToken token = tokens.get(i);
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
            contexts.put(context.getKey(), c -> new Context(context.getKey(), contextRules, c));
        }

        for (Map.Entry<String, Action> action : actions.entrySet()) {
            List<IToken> tokens = actionTokens.get(action.getKey()).getChildren();
            for (int i = 1; i < tokens.size(); i++) {
                action.getValue().getDirectives().add(
                        IDirective.parse((TokenContainer)tokens.get(i), contexts, actions));
            }
        }
        for (Map.Entry<Rule, List<IToken>> rule : uninitializedRules.entrySet()) {
            for (IToken dir : rule.getValue()) {
                rule.getKey().getAction().getDirectives()
                        .add(IDirective.parse((TokenContainer)dir, contexts, actions));
            }
        }
        Function<Context, Context> rootFactory = contexts.get("root");
        return new Parser(() -> rootFactory.apply(null));
    }

    private final Supplier<Context> rootContextFactory;

    Parser(Supplier<Context> rootContextFactory) {
        this.rootContextFactory = rootContextFactory;
    }

    public TokenContainer parse(String code) throws ParsingException {
        code = code.trim();
        ParserState state = new ParserState(rootContextFactory.get());
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
        return state.getRootToken();
    }

}
