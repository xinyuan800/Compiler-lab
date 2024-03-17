import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

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
        ErrorListener myErrorListener = new ErrorListener();
        sysYLexer.addErrorListener(myErrorListener);

        List<? extends Token> myTokens = sysYLexer.getAllTokens();
        for(Token t : myTokens){
            String text = t.getText();
            int line = t.getLine();
            int type = t.getType();
            Vocabulary vocabulary = sysYLexer.getVocabulary();
            String typeName = vocabulary.getSymbolicName(type);
            if(!Objects.equals(typeName, "WS")&& !Objects.equals(typeName, "MULTILINE_COMMENT") && !Objects.equals(typeName, "LINE_COMMENT")){
                System.err.println(typeName+" "+text+" at Line "+line+'.');
            }
        }


    }
}