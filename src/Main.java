import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.IOException;
import java.util.List;

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
        ErrorListener myErrorListener = new ErrorListener();
        sysYLexer.addErrorListener(myErrorListener);

        List<? extends Token> myTokens = sysYLexer.getAllTokens();
        for(Token t : myTokens){
            printSysTokenInformation(t);
        }


    }

    private static void printSysTokenInformation(Token t){
        String text = t.getText();
        int line = t.getLine();
        System.err.println(text+" at Line "+line );
    }
}