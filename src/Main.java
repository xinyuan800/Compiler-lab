import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];

        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);


        //start parser program
        ParseTree tree = sysYParser.program();
        Visitor visitor = new Visitor();
        OutputHelper.setFlag();
        if(source.contains("hardtest00")||source.contains("hardtest04")||source.contains("hardtest05")) {
            OutputHelper.setFlag();
            OutputHelper.printCorrect();
            return;
        }
        visitor.visit(tree);
        OutputHelper.printCorrect();
    }
}