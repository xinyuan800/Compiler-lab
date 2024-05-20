import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
    private LLVMModuleRef module;

    private LLVMBuilderRef builder;

    LLVMTypeRef i32Type;

    public LLVMModuleRef getModule() {
        return module;
    }

    @Override
    public LLVMValueRef visit(ParseTree tree) {
        //创建module
        module = LLVMModuleCreateWithName("module");

        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        builder = LLVMCreateBuilder();

        //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
         i32Type = LLVMInt32Type();
        return null;
    }

    @Override
    public LLVMValueRef visitTerminal(TerminalNode node) {
        return null;
    }

    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        return null;
    }

    @Override
    public LLVMValueRef visitCompUnit(SysYParser.CompUnitContext ctx) {
        return null;
    }
}