import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class Type {
    private String  name;
    private LLVMValueRef value;

    public Type(String name,LLVMValueRef value){
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public LLVMValueRef getValue() {
        return value;
    }
}
