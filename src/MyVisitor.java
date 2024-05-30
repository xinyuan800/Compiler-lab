import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
    private LLVMModuleRef module;
    private LLVMBuilderRef builder;

    private int idx = 0;

    private LLVMValueRef currentFunction;

    LLVMTypeRef i32Type;
    private final LLVMValueRef zero = LLVMConstInt(LLVMInt32Type(), 0, 0);
    private Deque<LLVMBasicBlockRef> breakLabel = new ArrayDeque<>();
    private Deque<LLVMBasicBlockRef> continueLabel = new ArrayDeque<>();
    ArrayList<Type> temTable = new ArrayList<>();

    HashMap<String, LLVMValueRef> functions = new HashMap<>();

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

        if (currentScope.equals(globalScope)) {
            //创建名为globalVar的全局变量
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());

            LLVMValueRef n = visit(ctx.constInitVal());
            //创建一个常量,这里是常数0
            //为全局变量设置初始化器
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/n);

            currentScope.define(ctx.IDENT().getText(), globalVar);
        } else {
            //int型变量
            //申请一块能存放int型的内存
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.IDENT().getText());
            LLVMValueRef n = visit(ctx.constInitVal());
            //将数值存入该内存
            LLVMBuildStore(builder, n, pointer);

            currentScope.define(ctx.IDENT().getText(), pointer);
        }
        return null;
    }

    @Override
    public LLVMValueRef visitVarDef(SysYParser.VarDefContext ctx) {
        if (currentScope.equals(globalScope)) {
            //创建名为globalVar的全局变量
            LLVMValueRef globalVar = LLVMAddGlobal(module, i32Type, /*globalVarName:String*/ctx.IDENT().getText());
            //创建一个常量
            LLVMValueRef n = zero;
            if(ctx.initVal()!=null){
              n = visit(ctx.initVal());
            }
            //为全局变量设置初始化器
            LLVMSetInitializer(globalVar, /* constantVal:LLVMValueRef*/n);

            currentScope.define(ctx.IDENT().getText(), globalVar);
        } else {
            //int型变量
            //申请一块能存放int型的内存
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.IDENT().getText());
            LLVMValueRef n = zero;
            if(ctx.initVal()!=null){
              n = visit(ctx.initVal());
            }
            //将数值存入该内存
            LLVMBuildStore(builder, n, pointer);

            currentScope.define(ctx.IDENT().getText(), pointer);
        }
        return null;
    }

    @Override
    public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
        Scope scope = new Scope(currentScope);
        currentScope = scope;
        for (int i = 0; i < temTable.size(); i++) {
            currentScope.define(temTable.get(i).getName(), temTable.get(i).getValue());
        }
        temTable.clear();
        for (int i = 0; i < ctx.blockItem().size(); i++) {
            visit(ctx.blockItem(i));
        }
        currentScope = scope.getEnclosingScope();
        return null;
    }

    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        LLVMTypeRef returnType = i32Type;;
        if(ctx.funcType().VOID()!=null){
            returnType = LLVMVoidType();
        }

        int funcRCount = 0;
        PointerPointer<Pointer> argumentTypes = new PointerPointer<>(funcRCount);
        if (ctx.funcFParams() != null) {
            funcRCount = ctx.funcFParams().funcFParam().size();
            for (int i = 0; i < funcRCount; i++) {
                argumentTypes.put(i, i32Type);
            }
        }
        LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, funcRCount, 0);
        LLVMValueRef function = LLVMAddFunction(module, ctx.IDENT().getText(), ft);
        functions.put(ctx.IDENT().getText(), function);
        LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(function, ctx.IDENT().getText() + "Entry");
        LLVMPositionBuilderAtEnd(builder, mainEntry);
        for (int i = 0; i < funcRCount; i++) {
            //int型变量
            //申请一块能存放int型的内存
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, /*pointerName:String*/ctx.funcFParams().funcFParam(i).IDENT().getText());
            //创建一个常量,这里是常数0
            LLVMValueRef param = LLVMGetParam(function, i);
            //将数值存入该内存
            LLVMBuildStore(builder, param, pointer);
            Type pram = new Type(ctx.funcFParams().funcFParam(i).IDENT().getText(), pointer);
            temTable.add(pram);
        }
        currentFunction = function;
        visitBlock(ctx.block());
        if(!returnType.equals(i32Type)){
            LLVMBuildRetVoid(builder);
        }
        return null;
    }

    @Override
    public LLVMValueRef visitStmt1(SysYParser.Stmt1Context ctx) {
        LLVMValueRef lastValue = currentScope.resolve(ctx.lVal().getText());
        LLVMValueRef value = visit(ctx.exp());
        LLVMBuildStore(builder, value, lastValue);
        return null;
    }

    @Override
    public LLVMValueRef visitIfStmt(SysYParser.IfStmtContext ctx) {
        LLVMBasicBlockRef trueLabel = LLVMAppendBasicBlock(currentFunction,getNewLabel("true"));
        LLVMBasicBlockRef falseLabel= LLVMAppendBasicBlock(currentFunction,getNewLabel("false"));
        LLVMBasicBlockRef endLabel = LLVMAppendBasicBlock(currentFunction,getNewLabel("end"));
        LLVMValueRef cond = visit(ctx.cond());
        LLVMTypeRef type = LLVMTypeOf(cond);
        int bitWidth = LLVMGetIntTypeWidth(type);
        if(bitWidth==32){
            cond = LLVMBuildICmp(builder, LLVMIntNE, zero, cond, "cond");
        }
        LLVMBuildCondBr(builder,cond,trueLabel,falseLabel);
        LLVMPositionBuilderAtEnd(builder,trueLabel);
        visit(ctx.stmt(0));
        LLVMBuildBr(builder,endLabel);
        LLVMPositionBuilderAtEnd(builder,falseLabel);
        if(ctx.ELSE()!=null){
            visit(ctx.stmt(1));
        }
        LLVMBuildBr(builder,endLabel);
        LLVMPositionBuilderAtEnd(builder,endLabel);
        return null;
    }

    @Override
    public LLVMValueRef visitWhileStmt(SysYParser.WhileStmtContext ctx) {
//        LLVMBasicBlockRef whileCondition= LLVMAppendBasicBlock(currentFunction,getNewLabel("whileCondition"));
//        LLVMBasicBlockRef whileBody= LLVMAppendBasicBlock(currentFunction,getNewLabel("whileBody"));
//        LLVMBasicBlockRef endLabel= LLVMAppendBasicBlock(currentFunction,getNewLabel("end"));
//        LLVMBuildBr(builder,whileCondition);
//        LLVMPositionBuilderAtEnd(builder,whileCondition);
//        LLVMValueRef cond = visit(ctx.cond());
//        LLVMTypeRef type = LLVMTypeOf(cond);
//        int bitWidth = LLVMGetIntTypeWidth(type);
//        if(bitWidth==32){
//            cond = LLVMBuildICmp(builder, LLVMIntNE, zero, cond, "cond");
//        }
//        LLVMBuildCondBr(builder,cond,whileBody,endLabel);
//        breakLabel.push(endLabel);
//        continueLabel.push(whileCondition);
//        LLVMPositionBuilderAtEnd(builder,whileBody);
//        visit(ctx.stmt());
//        breakLabel.pop();
//        continueLabel.pop();
//        LLVMBuildBr(builder,whileCondition);
//        LLVMPositionBuilderAtEnd(builder,endLabel);
//
            return null;
    }

    @Override
    public LLVMValueRef visitStmt3(SysYParser.Stmt3Context ctx) {
        LLVMBuildBr(builder,breakLabel.peek());
        return null;
    }

    @Override
    public LLVMValueRef visitStmt4(SysYParser.Stmt4Context ctx) {
        LLVMBuildBr(builder,continueLabel.peek());
        return null;
    }

    @Override
    public LLVMValueRef visitCond1(SysYParser.Cond1Context ctx) {
        return visit(ctx.exp());
    }

    @Override
    public LLVMValueRef visitOr(SysYParser.OrContext ctx) {
        LLVMValueRef lhs = LLVMBuildICmp(builder, LLVMIntNE, zero, visit(ctx.cond(0)), "lhs");
        // Create blocks for true, false, and the rest of the evaluation
        LLVMBasicBlockRef trueLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("or.true"));
        LLVMBasicBlockRef falseLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("or.false"));
        LLVMBasicBlockRef endLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("or.end"));

        // Allocate a temporary variable to hold the result
        LLVMValueRef resultVar = LLVMBuildAlloca(builder, LLVMInt1Type(), "result");

        // Compare lhs with true (1) and branch to the appropriate block
        LLVMBuildCondBr(builder, lhs, trueLabel, falseLabel);

        LLVMPositionBuilderAtEnd(builder, trueLabel);
        LLVMBuildStore(builder, LLVMConstInt(LLVMInt1Type(), 1, 0), resultVar);
        LLVMBuildBr(builder, endLabel);

        LLVMPositionBuilderAtEnd(builder, falseLabel);
        LLVMValueRef rhs = LLVMBuildICmp(builder, LLVMIntNE, zero, visit(ctx.cond(1)), "lhs");
        LLVMBuildStore(builder, rhs, resultVar);
        LLVMBuildBr(builder, endLabel);

        LLVMPositionBuilderAtEnd(builder, endLabel);

        return LLVMBuildLoad(builder, resultVar, "result");
    }


    @Override
    public LLVMValueRef visitAnd(SysYParser.AndContext ctx) {
        LLVMValueRef lhs = LLVMBuildICmp(builder, LLVMIntNE, zero, visit(ctx.cond(0)), "lhs");  // Evaluate left-hand side

        LLVMBasicBlockRef trueLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("and.true"));
        LLVMBasicBlockRef falseLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("and.false"));
        LLVMBasicBlockRef endLabel = LLVMAppendBasicBlock(currentFunction, getNewLabel("and.end"));

        LLVMValueRef resultVar = LLVMBuildAlloca(builder, LLVMInt1Type(), "result");

        LLVMBuildCondBr(builder, lhs, trueLabel, falseLabel);

        LLVMPositionBuilderAtEnd(builder, trueLabel);
        LLVMValueRef rhs = LLVMBuildICmp(builder, LLVMIntNE, zero, visit(ctx.cond(1)), "lhs");  // Evaluate left-hand side
        LLVMBuildStore(builder, rhs, resultVar);
        LLVMBuildBr(builder, endLabel);

        LLVMPositionBuilderAtEnd(builder, falseLabel);
        LLVMBuildStore(builder, LLVMConstInt(LLVMInt1Type(), 0, 0), resultVar);
        LLVMBuildBr(builder, endLabel);

        LLVMPositionBuilderAtEnd(builder, endLabel);

        return LLVMBuildLoad(builder, resultVar, "result");
    }


    @Override
    public LLVMValueRef visitCom(SysYParser.ComContext ctx) {
        LLVMValueRef lhs = LLVMBuildZExt(builder, visit(ctx.cond(0)), LLVMInt32Type(), "zexttmp");
        LLVMValueRef rhs = LLVMBuildZExt(builder, visit(ctx.cond(1)), LLVMInt32Type(), "zexttmp");
        LLVMValueRef cmp;
        if(ctx.GT()!=null){
            cmp = LLVMBuildICmp(builder, LLVMIntSGT, lhs, rhs, "cmptmp");
        }else if(ctx.GE()!=null){
            cmp = LLVMBuildICmp(builder, LLVMIntSGE, lhs, rhs, "cmptmp");
        }else if(ctx.LT()!=null){
            cmp = LLVMBuildICmp(builder, LLVMIntSLT, lhs, rhs, "cmptmp");
        }else {
            cmp = LLVMBuildICmp(builder, LLVMIntSLE, lhs, rhs, "cmptmp");
        }
        LLVMValueRef zext = LLVMBuildZExt(builder, cmp, LLVMInt32Type(), "zexttmp");

        // 生成 icmp ne 指令，比较扩展结果是否不等于 0

        return  LLVMBuildICmp(builder, LLVMIntNE, zero, zext, "finalcmp");
    }

    @Override
    public LLVMValueRef visitEqOrNor(SysYParser.EqOrNorContext ctx) {
        LLVMValueRef lhs = LLVMBuildZExt(builder, visit(ctx.cond(0)), LLVMInt32Type(), "zexttmp");
        LLVMValueRef rhs = LLVMBuildZExt(builder, visit(ctx.cond(1)), LLVMInt32Type(), "zexttmp");
        // 生成等于或不等于的比较指令
        LLVMValueRef cmp;
        if (ctx.EQ() != null) {
            cmp = LLVMBuildICmp(builder, LLVMIntEQ, lhs, rhs, "cmptmp");
        } else {
            cmp = LLVMBuildICmp(builder, LLVMIntNE, lhs, rhs, "cmptmp");
        }

        // 生成 zext 指令，将比较结果从 i1 扩展到 i32
        LLVMValueRef zext = LLVMBuildZExt(builder, cmp, LLVMInt32Type(), "zexttmp");

        // 生成 icmp ne 指令，比较扩展结果是否不等于 0
        LLVMValueRef zero = LLVMConstInt(LLVMInt32Type(), 0, 0);
        LLVMValueRef finalCmp;
        if(ctx.EQ()!=null){
            finalCmp = LLVMBuildICmp(builder, LLVMIntEQ, zero, zext, "finalcmp");
        }else{
            finalCmp = LLVMBuildICmp(builder, LLVMIntNE, zero, zext, "finalcmp");
        }

        return finalCmp;
    }

    @Override
    public LLVMValueRef visitStmt5(SysYParser.Stmt5Context ctx) {
        if(ctx.exp()!=null){
            LLVMValueRef result = visit(ctx.exp()); // 获取exp的结果
            LLVMBuildRet(builder, result); // 使用result作为返回值
        }
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
        if (num.startsWith("0x") || num.startsWith("0X")) {
            value = Integer.parseInt(num.substring(2), 16);
        } else if (num.startsWith("0") && num.length() > 1) {
            value = Integer.parseInt(num.substring(1), 8);
        } else {
            value = Integer.parseInt(ctx.number().INTEGER_CONST().getText());
        }
        return LLVMConstInt(i32Type, value, 0);
    }

    @Override
    public LLVMValueRef visitFunCall(SysYParser.FunCallContext ctx) {
        ArrayList<LLVMValueRef> args = new ArrayList<>();
        if(ctx.funcRParams() != null) {
            for (int i = 0; i < ctx.funcRParams().param().size(); i++) {
                // Visit each parameter node individually
                args.add(visit(ctx.funcRParams().param(i)));
            }
        }

        // Convert ArrayList<LLVMValueRef> to LLVMValueRef[]
        LLVMValueRef[] llvmValueArray = new LLVMValueRef[args.size()];
        llvmValueArray = args.toArray(llvmValueArray);

        // Create a PointerPointer from LLVMValueRef[]
        PointerPointer<LLVMValueRef> argsPointer = new PointerPointer<>(llvmValueArray);

        // Retrieve the function reference using the function name
        String functionName = ctx.IDENT().getText();
        LLVMValueRef function = functions.get(functionName);

        // Check if the function return type is void
        if (LLVMGetReturnType(LLVMGetElementType(LLVMTypeOf(function))).equals(LLVMVoidType())) {
            // Build the function call instruction without storing the return value
            LLVMBuildCall(builder, function, argsPointer, llvmValueArray.length, "");
            return null; // No return value for void functions
        } else {
            // Build the function call instruction and store the return value
            return LLVMBuildCall(builder, function, argsPointer, llvmValueArray.length, "returnValue");
        }
    }


    @Override
    public LLVMValueRef visitExp3(SysYParser.Exp3Context ctx) {
        LLVMValueRef operand = visit(ctx.exp());
        if (ctx.unaryOp().PLUS() != null) {
            return operand; // +exp
        } else if (ctx.unaryOp().MINUS() != null) {
            return LLVMBuildNeg(builder, operand, "negtmp"); // -exp
        } else if (ctx.unaryOp().NOT() != null) {
            LLVMValueRef tmp = LLVMBuildICmp(builder, LLVMIntNE, LLVMConstInt(i32Type, 0, 0), operand, "tmp_");
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
        return LLVMBuildLoad(builder, value, ctx.IDENT().getText());
    }

    private String getNewLabel(String label){
        return label+idx;
    }
}