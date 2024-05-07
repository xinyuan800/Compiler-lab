import java.util.HashMap;

public abstract class OutputHelper {
    private static boolean flag = true;
    public static void printSemanticError(ErrorType errorType, int line, String errorMsg){
        flag = false;
        System.err.println("Error type "+(errorType.ordinal()+1)+" at Line "+line+":");
    }

    public static void setFlag(){
        flag  = true;
    }

    public static void printCorrect(){
        if(flag){
            System.err.print("No semantic errors in the program!");
        }
    }

}
