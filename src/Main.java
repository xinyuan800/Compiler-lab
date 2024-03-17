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

        if(myErrorListener.isError()){
            myErrorListener.printLexerErrorInformation();
        } else{
            for(Token t : myTokens){
                String text = t.getText();
                int line = t.getLine();
                int type = t.getType();
                Vocabulary vocabulary = sysYLexer.getVocabulary();
                String typeName = vocabulary.getSymbolicName(type);
                if(text.startsWith("0x")||text.startsWith("0X")){
                    text = String.valueOf(Integer.parseInt(text.substring(2),16));
                }else if(text.startsWith("0")&&text.length()>1){
                    text = String.valueOf(Integer.parseInt(text.substring(1),8));
                }
                System.err.println(typeName+" "+text+" at Line "+line+'.');
            }
        }
    }
}