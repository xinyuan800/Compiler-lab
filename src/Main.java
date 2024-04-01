import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class Main
{
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        sysYParser.removeErrorListeners();
        ParserErrorListener parserErrorListener = new ParserErrorListener();
        sysYParser.addErrorListener( parserErrorListener);

        ParseTree tree = sysYParser.program();
        SysYParserBaseVisitor visitor = new SysYParserBaseVisitor();
        visitor.visit(tree);
        if(parserErrorListener.isError()){
            parserErrorListener.printParserErrorInformation();
        }
    }

}