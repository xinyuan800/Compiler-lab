import java.util.HashMap;

public abstract class OutputHelper {
    private static boolean flag = true;
    private static  HashMap<Integer,ErrorType> hashMap = new HashMap<>();
    public static void printSemanticError(ErrorType errorType, int line, String errorMsg){
        flag = false;
        if(hashMap.containsKey(line)&&hashMap.get(line)!=errorType){
            return;
        }
        System.err.println("Error type "+(errorType.ordinal()+1)+" at Line "+line+":");
        hashMap.put(line,errorType);
    }

    public static void printCorrect(){
        if(flag){
            System.err.println("No semantic errors in the program!");
        }
    }

}
