import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ErrorListener  extends BaseErrorListener {
    private boolean hasErrorInformation;
    private int errorLine;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        hasErrorInformation = true;
        errorLine = line;
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }

    public boolean isError(){
        return hasErrorInformation;
    }

    public void printLexerErrorInformation() {
        System.err.println("Error type A at Line "+errorLine+":"+" UNKNOWN TOKEN");
    }

}
