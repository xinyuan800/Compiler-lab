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

    private Scope currentScope = null;
    private Scope globalScope = null;

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
        module = LLVMModuleCreateWithName("module");
        builder = LLVMCreateBuilder();
        i32Type = LLVMInt32Type();
        globalScope = new Scope(null);
        currentScope = globalScope;
        visitCompUnit(ctx.compUnit());

        return null;
    }

    @Override
    public LLVMValueRef visitCompUnit(SysYParser.CompUnitContext ctx) {
        return super.visitCompUnit(ctx);
    }

    @Override
    public LLVMValueRef visitConstDef(SysYParser.ConstDefContext ctx) {

        if(currentScope.equals(globalScope)){
            //创建名为globalVar的全局变量
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());

            LLVMValueRef n = visit(ctx.constInitVal());
            //为全局变量设置初始化器
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/n);

            currentScope.define(ctx.IDENT().getText(),globalVar);
        }else{
            //int型变量
            //申请一块能存放int型的内存
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.IDENT().getText());
            LLVMValueRef n = visit(ctx.constInitVal());
            //将数值存入该内存
            LLVMBuildStore(builder, n, pointer);

            currentScope.define(ctx.IDENT().getText(),pointer);
        }
        return null;
    }

    @Override
    public LLVMValueRef visitVarDef(SysYParser.VarDefContext ctx) {
        if(currentScope.equals(globalScope)){
            //创建名为globalVar的全局变量
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());
            //创建一个常量
            LLVMValueRef n = visit(ctx.initVal());
            //为全局变量设置初始化器
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/n);

            currentScope.define(ctx.IDENT().getText(),globalVar);
        }else{
            //int型变量
            //申请一块能存放int型的内存
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.IDENT().getText());
            //创建一个常量,这里是常数0
            LLVMValueRef n = visit(ctx.initVal());
            //将数值存入该内存
            LLVMBuildStore(builder, n, pointer);

            currentScope.define(ctx.IDENT().getText(),pointer);
        }
        return null;
    }

    @Override
    public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
        Scope scope = new Scope(currentScope);
        currentScope = scope;
        for(int i=0;i<ctx.blockItem().size();i++){
            visit(ctx.blockItem(i));
        }
        currentScope = scope.getEnclosingScope();
        return null;
    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        LLVMTypeRef returnType = i32Type;
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(0);
        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, 0, 0);
        LLVMValueRef function = LLVMAddFunction(module, ctx.IDENT().getText(), ft);
        LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(function, ctx.IDENT().getText()+"Entry");
        LLVMPositionBuilderAtEnd(builder, mainEntry);
        visitBlock(ctx.block());
        return function;
    }

    @Override
    public LLVMValueRef visitStmt1(SysYParser.Stmt1Context ctx) {
        LLVMValueRef lastValue = currentScope.resolve(ctx.lVal().getText());
        LLVMValueRef value = visit(ctx.exp());
        currentScope.replace(ctx.lVal().IDENT().getText(),value);
        LLVMBuildStore(builder,lastValue,value);
        return super.visitStmt1(ctx);
    }

    @Override
    public LLVMValueRef visitStmt5(SysYParser.Stmt5Context ctx) {
        LLVMValueRef result = visit(ctx.exp()); // 获取exp的结果
        LLVMBuildRet(builder, result); // 使用result作为返回值
        return null;
    }

    @Override
    public LLVMValueRef visitExp1(SysYParser.Exp1Context ctx) {
        return visit(ctx.exp());
    }

    @Override
    public LLVMValueRef visitExp2(SysYParser.Exp2Context ctx) {
        return super.visitExp2(ctx);
    }

    @Override
    public LLVMValueRef visitNum(SysYParser.NumContext ctx) {
        String num = ctx.number().INTEGER_CONST().getText();
        int value = 0;
        if(num.startsWith("0x")||num.startsWith("0X")){
            value =  Integer.parseInt(num.substring(2), 16);
        }else if(num.startsWith("0")&&num.length()>1){
            value =  Integer.parseInt(num.substring(1), 8);
        }else{
            value = Integer.parseInt(ctx.number().INTEGER_CONST().getText());
        }
        return LLVMConstInt(i32Type, value, 0);
    }

    @Override
    public LLVMValueRef visitFunCall(SysYParser.FunCallContext ctx) {
        return super.visitFunCall(ctx);
    }

    @Override
    public LLVMValueRef visitExp3(SysYParser.Exp3Context ctx) {
        LLVMValueRef operand = visit(ctx.exp());
        if (ctx.unaryOp().PLUS() != null) {
            return operand; // +exp
        } else if (ctx.unaryOp().MINUS() != null) {
            return LLVMBuildNeg(builder, operand, "negtmp"); // -exp
        } else if (ctx.unaryOp().NOT() != null) {
            LLVMValueRef  tmp = LLVMBuildICmp(builder, LLVMIntNE, LLVMConstInt(i32Type, 0, 0), operand, "tmp_");
            tmp = LLVMBuildXor(builder, tmp, LLVMConstInt(LLVMInt1Type(), 1, 0), "tmp_");
            tmp = LLVMBuildZExt(builder, tmp, i32Type, "tmp_");
            return tmp;// !exp
        } else {
            throw new RuntimeException("Unknown unary operator");
        }
    }

    @Override
    public LLVMValueRef visitExp4(SysYParser.Exp4Context ctx) {
        LLVMValueRef left = visit(ctx.exp(0));
        LLVMValueRef right = visit(ctx.exp(1));
        if (ctx.MUL() != null) {
            return LLVMBuildMul(builder, left, right, "multmp");
        } else if (ctx.DIV() != null) {
            return LLVMBuildSDiv(builder, left, right, "divtmp");
        } else if (ctx.MOD() != null) {
            return LLVMBuildSRem(builder, left, right, "modtmp");
        } else {
            throw new RuntimeException("Unknown binary operator");
        }
    }

    @Override
    public LLVMValueRef visitExp5(SysYParser.Exp5Context ctx) {
        LLVMValueRef left = visit(ctx.exp(0));
        LLVMValueRef right = visit(ctx.exp(1));
        if (ctx.PLUS() != null) {
            return LLVMBuildAdd(builder, left, right, "addtmp");
        } else if (ctx.MINUS() != null) {
            return LLVMBuildSub(builder, left, right, "subtmp");
        } else {
            throw new RuntimeException("Unknown binary operator");
        }
    }

    @Override
    public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
        LLVMValueRef value = currentScope.resolve(ctx.IDENT().getText());
        return LLVMBuildLoad(builder,value,ctx.IDENT().getText());
    }
}