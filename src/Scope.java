import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private String name;
    private final Map<String, LLVMValueRef> symbols = new LinkedHashMap<>();
    private final Scope enclosingScope;


    public Scope(Scope enclosingScope) {
        this.enclosingScope = enclosingScope;
    }

    public Scope getEnclosingScope() {
        return this.enclosingScope;
    }

    public Map<String, LLVMValueRef> getSymbols() {
        return this.symbols;
    }

    public void define(String name,LLVMValueRef value) {
        this.symbols.put(name,value);
    }

    public LLVMValueRef resolve(String name) {
        LLVMValueRef value = symbols.get(name);
        if (value!= null) {
            return value;
        }

        if (this.enclosingScope != null) {
            return this.enclosingScope.resolve(name);
        }

        return null;
    }

}
