package io.github.phantamanta44.resyn;

import io.github.phantamanta44.resyn.parser.Parser;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: resyn <syntax file> <input file>");
            System.exit(1);
        } else {
            Parser parser = null;
            try {
                String syntax = Files.readAllLines(new File(args[0]).toPath()).stream()
                        .collect(Collectors.joining("\n"));
                parser = Parser.create(syntax);
            } catch (Exception e) {
                System.err.println("Invalid syntax specification!");
                e.printStackTrace();
                System.exit(1);
            }
            try {
                String input = Files.readAllLines(new File(args[1]).toPath()).stream()
                        .collect(Collectors.joining("\n"));
                System.out.println(parser.parse(input));
            } catch (Exception e) {
                System.err.println("Parsing failed!");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

}
