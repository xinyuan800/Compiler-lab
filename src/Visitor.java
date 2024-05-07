import java.util.ArrayList;
import java.util.HashMap;

public class Visitor extends SysYParserBaseVisitor {
    private Scope currentScope = null;

    private HashMap<String, Symbol> temSymbolTable = new HashMap<>();

    @Override
    public Void visitProgram(SysYParser.ProgramContext ctx) {
        currentScope = new GlobalScope(null);
        visitCompUnit(ctx.compUnit());
        return null;
    }

    @Override
    public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
        String funcName = ctx.IDENT().getText();
        if (currentScope.findWholeScope(funcName) != null) { // curScope为当前的作用域
            OutputHelper.printSemanticError(ErrorType.REDEF_FUNC, ctx.IDENT().getSymbol().getLine(),
                    ctx.IDENT().getText());
            return null;
        }
        Type retType = new IntType();
        if (ctx.funcFParams() != null) { // 如有入参，处理形参，添加形参信息等
            temSymbolTable.clear();
            temSymbolTable = visitFuncFParams(ctx.funcFParams());
        }
        ArrayList<Type> paramsTyList = new ArrayList<>();
        temSymbolTable.forEach((key, value) -> {
            paramsTyList.add(value.getType());
        });
        FunctionType functionType = new FunctionType(retType, paramsTyList);
        FuncSymbol funcSymbol = new FuncSymbol(funcName, functionType);
        //顶层作用域中压入此函数
        currentScope.define(funcSymbol);
        visit(ctx.block());
        return null;
    }

    @Override
    public HashMap<String, Symbol> visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        HashMap<String, Symbol> paramsSymbol = new HashMap<>();
        for (int i = 0; i < ctx.funcFParam().size(); i++) {
            String name = ctx.funcFParam(i).IDENT().getText();
            Symbol symbol = visitFuncFParam(ctx.funcFParam(i));
            if (symbol != null) {
                paramsSymbol.put(name, symbol);
            }
        }
        return paramsSymbol;
    }

    @Override
    public Symbol visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String name = ctx.IDENT().getText();
        if (temSymbolTable.containsKey(name)) {
            return null;
        } else if (!ctx.L_BRACKT().isEmpty()) {
            return new VariableSymbol(name, new ArrayType(new IntType(), 0));
        } else {
            return new VariableSymbol(name, new IntType());
        }
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        for (int i = 0; i < ctx.varDef().size(); i++) {
            visit(ctx.varDef(i)); // 依次visit def，即依次visit c=4 和 d=5
        }
        return null;
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText(); // c or d
        if (currentScope.findCurrentScope(varName) != null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                Type type =  visitInitVal(ctx.initVal());
                if(type==null){return null;}
                else if(!comType(type,new IntType())){
                    OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                    return null;
                }
            }
            currentScope.define(new VariableSymbol(varName, new IntType()));
        } else { // 数组
            ArrayType arrayType = new ArrayType();
            VariableSymbol variableSymbol = new VariableSymbol(varName, arrayType);
            for (int i = 0; i < ctx.L_BRACKT().size() - 1; i++) {
                arrayType.setContained(new ArrayType());
                arrayType = (ArrayType) arrayType.getContained();
            }
            arrayType.setContained(new IntType());
            currentScope.define(variableSymbol);
        }
        return null;
    }

    @Override
    public Type visitInitVal(SysYParser.InitValContext ctx) {
        return (Type) visit(ctx.exp());
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (int i = 0; i < ctx.constDef().size(); i++) {
            visit(ctx.constDef(i)); // 依次visit def，即依次visit c=4 和 d=5
        }
        return null;
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String varName = ctx.IDENT().getText(); // c or d
        if (currentScope.findCurrentScope(varName) != null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                Type type = visitConstInitVal(ctx.constInitVal());
                if(type==null){return null;}
                else if(!comType(type,new IntType())){
                    OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                    return null;
                }
            }
            currentScope.define(new VariableSymbol(varName, new IntType()));
        } else { // 数组
            ArrayType arrayType = new ArrayType();
            VariableSymbol variableSymbol = new VariableSymbol(varName, arrayType);
            for (int i = 0; i < ctx.L_BRACKT().size() - 1; i++) {
                arrayType.setContained(new ArrayType());
                arrayType =(ArrayType) arrayType.getContained();
            }
            arrayType.setContained(new IntType());
            currentScope.define(variableSymbol);
        }
        return null;
    }

    @Override
    public Type visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        return (Type) visitConstExp(ctx.constExp());
    }

    @Override
    public Type visitConstExp(SysYParser.ConstExpContext ctx) {
        return (Type) visit(ctx.exp());
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        currentScope = new LocalScope(currentScope);

        // 将形参添加到作用域里
        temSymbolTable.forEach((key, value) -> {
            currentScope.define(value);
        });

        ctx.blockItem().forEach(this::visit); // 依次visit block中的节点
        //切换回父级作用域
        currentScope = currentScope.getEnclosingScope();

        return null;
    }

    @Override
    public Type visitLVal(SysYParser.LValContext ctx) {
        String name = ctx.IDENT().getText();
        if (currentScope.findWholeScope(name) == null) {
            OutputHelper.printSemanticError(ErrorType.VAR_UNDEF, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        } else if (!(currentScope.findWholeScope(name).getType() instanceof ArrayType) && (!ctx.L_BRACKT().isEmpty())) {
            OutputHelper.printSemanticError(ErrorType.INDEX_ON_NON_ARRAY, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        }
        Type type = currentScope.findWholeScope(name).getType();
        if (type instanceof ArrayType) {
            if (ctx.L_BRACKT().isEmpty()) {
                return type;
            } else {
                ArrayType arrayType = (ArrayType) type;
                for (int i = 0; i < ctx.L_BRACKT().size() - 1; i++) {
                    if (arrayType.getContained() instanceof IntType) {
                        OutputHelper.printSemanticError(ErrorType.INDEX_ON_NON_ARRAY, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
                        return null;
                    } else {
                        arrayType = (ArrayType) arrayType.getContained();
                    }
                }
                return arrayType.getContained();
            }
        }
        return currentScope.findWholeScope(name).getType();
    }

    @Override
    public Type visitExp1(SysYParser.Exp1Context ctx) {
        return (Type) visit(ctx.exp());
    }

    @Override
    public Type visitExp2(SysYParser.Exp2Context ctx) {
        return visitLVal(ctx.lVal());
    }

    @Override
    public Type visitExp4(SysYParser.Exp4Context ctx) {
        Type type = (Type) visit(ctx.exp());
        if (!(type instanceof IntType)) {
            OutputHelper.printSemanticError(ErrorType.OP_DISMATCH, ctx.start.getLine(), ctx.getText());
            return null;
        }
        return type;
    }

    @Override
    public Type visitExp5(SysYParser.Exp5Context ctx) {
        Type typeL = (Type) visit(ctx.exp(0));
        if (typeL == null) {
            return null;
        }
        Type typeR = (Type) visit(ctx.exp(1));
        if (typeR == null) {
            return null;
        }
        if (!(typeL instanceof IntType && typeR instanceof IntType)) {
            OutputHelper.printSemanticError(ErrorType.OP_DISMATCH, ctx.start.getLine(), ctx.getText());
            return null;
        }
        return typeL;
    }

    @Override
    public Object visitExp6(SysYParser.Exp6Context ctx) {
        Type typeL = (Type) visit(ctx.exp(0));
        if (typeL == null) {
            return null;
        }
        Type typeR = (Type) visit(ctx.exp(1));
        if (typeR == null) {
            return null;
        }
        if (!(typeL instanceof IntType && typeR instanceof IntType)) {
            OutputHelper.printSemanticError(ErrorType.OP_DISMATCH, ctx.start.getLine(), ctx.getText());
            return null;
        }
        return typeR;
    }

    @Override
    public Type visitFuncCall(SysYParser.FuncCallContext ctx) {
        String name = ctx.IDENT().getText();
        if (currentScope.findWholeScope(name) == null) {
            OutputHelper.printSemanticError(ErrorType.FUNC_UNDEF, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        } else if (currentScope.findWholeScope(name) instanceof VariableSymbol) {
            OutputHelper.printSemanticError(ErrorType.FUNC_CALL_ON_VARIABLE, ctx.IDENT().getSymbol().getLine(), ctx.IDENT().getText());
            return null;
        }
        ArrayList<Type> typeArrayList = ((FunctionType)currentScope.findWholeScope(name).getType()).paramsType;
        if(ctx.funcRParams()==null){
            if(!typeArrayList.isEmpty()){
                OutputHelper.printSemanticError(ErrorType.PARAM_NOT_APPLICABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                return null;
            }
        }else{
            ArrayList<Type> paramsTypeList = visitFuncRParams(ctx.funcRParams());
            if(paramsTypeList.size()!=typeArrayList.size()){
                OutputHelper.printSemanticError(ErrorType.PARAM_NOT_APPLICABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                return null;
            }else {
                for(int i=0;i< paramsTypeList.size();i++){
                    if(!comType(paramsTypeList.get(i),typeArrayList.get(i))){
                        OutputHelper.printSemanticError(ErrorType.PARAM_NOT_APPLICABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                        return null;
                    }
                }
            }
        }
        return ((FunctionType)currentScope.findWholeScope(name).getType()).retTy;
    }

    @Override
    public ArrayList<Type> visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        ArrayList<Type> arrayList = new ArrayList<>();
        for(int i=0;i<ctx.param().size();i++){
            arrayList.add(visitParam(ctx.param(i)));
        }
        return arrayList;
    }

    @Override
    public Type visitParam(SysYParser.ParamContext ctx) {
        return (Type) visit(ctx.exp());
    }

    @Override
    public Type visitExp3(SysYParser.Exp3Context ctx) {
        return new IntType();
    }

    @Override
    public Void visitStmt1(SysYParser.Stmt1Context ctx) {
        Type typeL = visitLVal(ctx.lVal());
        if(typeL==null){
            return null;
        }
        if (typeL instanceof FunctionType) {
            OutputHelper.printSemanticError(ErrorType.SIGN_ON_FUNC, ctx.lVal().IDENT().getSymbol().getLine(), ctx.getText());
            return null;
        }
        Type typeR = (Type) visit(ctx.exp());
        if (typeR == null) {
            return null;
        }
        if (typeR instanceof FunctionType) {
            typeR = ((FunctionType) typeR).retTy;
        }
        if (!comType(typeL, typeR)) {
            OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH, ctx.lVal().IDENT().getSymbol().getLine(), ctx.getText());
            return null;
        }
        return null;
    }

    @Override
    public Void visitStmt5(SysYParser.Stmt5Context ctx) {
        if (ctx.exp()==null) {
            OutputHelper.printSemanticError(ErrorType.FUNR_DISMATCH, ctx.getStart().getLine(), ctx.getText());
            return null;
        } else {
            Type type = (Type) visit(ctx.exp());
            if (type == null) {
                return null;
            }
            if (!(type instanceof IntType)) {
                OutputHelper.printSemanticError(ErrorType.FUNR_DISMATCH, ctx.getStart().getLine(), ctx.getText());
                return null;
            }
        }
        return null;
    }

    @Override
    public Type visitCond(SysYParser.CondContext ctx) {
        if (ctx.exp()!=null){
            return (Type) visit(ctx.exp());
        } else {
            Type typeL = visitCond(ctx.cond(0));
            Type typeR = visitCond(ctx.cond(1));
            if(typeR==null||typeL==null){return null;}
            if (!(typeL instanceof IntType && typeR instanceof IntType)) {
                OutputHelper.printSemanticError(ErrorType.OP_DISMATCH, ctx.start.getLine(), ctx.getText());
                return null;
            }
        }
        return null;
    }

    private boolean comType(Type l, Type r) {
        if (l instanceof IntType && r instanceof IntType) {
            return true;
        } else if (l instanceof ArrayType && r instanceof ArrayType) {
            ArrayType arrayTypeL = (ArrayType) l;
            ArrayType arrayTypeR = (ArrayType) r;
            while (arrayTypeL.getContained() instanceof ArrayType && arrayTypeR.getContained() instanceof ArrayType) {
                arrayTypeL = (ArrayType) arrayTypeL.getContained();
                arrayTypeR = (ArrayType) arrayTypeR.getContained();
            }
            if (arrayTypeL.getContained() instanceof IntType && arrayTypeR.getContained() instanceof IntType) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}