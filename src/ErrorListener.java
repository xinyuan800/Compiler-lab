import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.util.ArrayList;

public class ErrorListener  extends BaseErrorListener {
    private boolean hasErrorInformation;
    private ArrayList<Integer> errorLine = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        hasErrorInformation = true;
        errorLine.add(line);
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }

    public boolean isError(){
        return hasErrorInformation;
    }

    public void printLexerErrorInformation() {
        for(int line : errorLine){
            System.err.println("Error type A at Line "+line+":"+" UNKNOWN TOKEN");
        }
    }

}
