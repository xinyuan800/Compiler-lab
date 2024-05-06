public abstract class OutputHelper {
    public static void printSemanticError(ErrorType errorType, int line, String errorMsg){
        System.err.println("Error type "+(errorType.ordinal()+1)+" at Line "+line+":");
    }

}
