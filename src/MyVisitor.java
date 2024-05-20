import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

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
        return super.visit(tree);
    }

    @Override
    public LLVMValueRef visitTerminal(TerminalNode node) {
        return null;
    }

    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        //创建module
        module = LLVMModuleCreateWithName("module");

        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        builder = LLVMCreateBuilder();

        //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
        i32Type = LLVMInt32Type();
        visitCompUnit(ctx.compUnit());
        return null;
    }

    @Override
    public LLVMValueRef visitCompUnit(SysYParser.CompUnitContext ctx) {
        return super.visitCompUnit(ctx);
    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        //生成返回值类型
        LLVMTypeRef returnType = i32Type;

        //生成函数类型
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(0);
        //若仅需一个参数也可以使用如下方式直接生成函数类型
        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, /* argumentCount */ 0, /* isVariadic */ 0);

        //生成函数，即向之前创建的module中添加函数
        LLVMValueRef function = LLVMAddFunction(module, /*functionName:String*/ctx.IDENT().getText(), ft);
        LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(function,"mainEntry");
        LLVMPositionBuilderAtEnd(builder, mainEntry);//后续生成的指令将追加在block1的后面

        return super.visitFuncDef(ctx);
    }

    @Override
    public LLVMValueRef visitStmt5(SysYParser.Stmt5Context ctx) {
        LLVMValueRef result =

        return super.visitStmt5(ctx);
    }
}