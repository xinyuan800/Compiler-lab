import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

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

        //add parserErrorListener
        sysYParser.removeErrorListeners();
        ParserErrorListener parserErrorListener = new ParserErrorListener();
        sysYParser.addErrorListener( parserErrorListener);

        //start parser program
        ParseTree tree = sysYParser.program();
        if(parserErrorListener.isError()){
            parserErrorListener.printParserErrorInformation();
        }else {
            ParseTreeWalker walker = new ParseTreeWalker();
            Listener listener = new Listener();
            walker.walk(listener,tree);
        }
    }

}