package io.github.phantamanta44.resyn.parser.directive;

import io.github.phantamanta44.resyn.parser.ParserState;
import io.github.phantamanta44.resyn.parser.ParsingException;
import io.github.phantamanta44.resyn.parser.construct.Action;
import io.github.phantamanta44.resyn.parser.construct.Context;
import io.github.phantamanta44.resyn.parser.token.TokenContainer;
import io.github.phantamanta44.resyn.parser.token.TokenNode;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@FunctionalInterface
public interface IDirective {

    String[] NO_ARGS = new String[0];

    static IDirective parse(TokenContainer token, Map<String, Function<Context, Context>> contexts, Map<String, Action> actions)
            throws ParsingException {
        String directive = ((TokenNode)token.getChildren().get(0)).getContent();
        String argText = ((TokenNode)token.getChildren().get(1)).getContent();
        String[] args = argText != null ? argText.split("\\s+") : NO_ARGS;
        switch (directive) {
            case "do":
                if (args.length != 1) throw new ParsingException("Wrong number of parameters to do!");
                if (!actions.containsKey(args[0])) throw new ParsingException("Unknown action: " + args[0]);
                return new DirectiveExecuteAction(actions.get(args[0]));
            case "enter":
                if (args.length < 1 || args.length > 2) {
                    throw new ParsingException("Wrong number of parameters to enter!");
                }
                if (!contexts.containsKey(args[0])) throw new ParsingException("Unknown context: " + args[0]);
                return new DirectiveContextSwitch(contexts.get(args[0]), args.length == 2 ? args[1] : null, false);
            case "visit":
                if (args.length < 1 || args.length > 2) {
                    throw new ParsingException("Wrong number of parameters to enter!");
                }
                if (!contexts.containsKey(args[0])) throw new ParsingException("Unknown context: " + args[0]);
                return new DirectiveContextSwitch(contexts.get(args[0]), args.length == 2 ? args[1] : null, true);
            case "exit":
                if (args.length > 1) throw new ParsingException("Wrong number of parameters to exit!");
                int count = 1;
                try {
                    if (args.length != 0) count = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    throw new ParsingException("Invalid parameter to exit: " + args[0]);
                }
                return new DirectiveContextPop(count);
            case "take":
                if (args.length != 1) throw new ParsingException("Wrong number of parameters to take!");
                return new DirectiveTokenGroup(args[0]);
            case "grab":
                if (args.length != 1) throw new ParsingException("Wrong number of parameters to grab!");
                return new DirectiveTokenMatch(args[0]);
        }
        throw new ParsingException("Unknown directive: " + directive);
    }

    void execute(ParserState state, String match, Supplier<String> tokens) throws ParsingException;

}
