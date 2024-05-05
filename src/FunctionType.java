import java.lang.reflect.Array;
import java.util.ArrayList;

public class FunctionType extends Type{
    Type retTy;
    ArrayList<Type> paramsType;

    public FunctionType(Type retTy,ArrayList<Type> paramsType){
        this.paramsType = paramsType;
        this.retTy = retTy;
    }
}
