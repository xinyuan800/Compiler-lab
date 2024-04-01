import org.antlr.v4.runtime.*;

import java.io.IOException;
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

        sysYLexer.removeErrorListeners();
        LexerErrorListener myLexerErrorListener = new LexerErrorListener();
        sysYLexer.addErrorListener(myLexerErrorListener);

        List<? extends Token> myTokens = sysYLexer.getAllTokens();

        if(myLexerErrorListener.isError()){
            myLexerErrorListener.printLexerErrorInformation();
        } else{
            CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
            SysYParser sysYParser = new SysYParser(tokens);

        }
    }
}