import java.util.ArrayList;
import java.util.HashMap;

public class Visitor extends SysYParserBaseVisitor{
    private Scope currentScope = null;

    private HashMap<String,Symbol> temSymbolTable = new HashMap<>();

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
        Type retType = new VoidType();
        String typeStr = ctx.getChild(0).getText();
        if (typeStr.equals("int"))
            retType = new IntType();
        if (ctx.funcFParams() != null) { // 如有入参，处理形参，添加形参信息等
            temSymbolTable.clear();
            temSymbolTable = visitFuncFParams(ctx.funcFParams());
        }
        ArrayList<Type> paramsTyList = new ArrayList<>();
        temSymbolTable.forEach((key, value) -> {
            paramsTyList.add(value.getType());
        });
        FunctionType functionType = new FunctionType(retType, paramsTyList);
        FuncSymbol funcSymbol = new FuncSymbol(funcName,functionType);
        //顶层作用域中压入此函数
        currentScope.define(funcSymbol);
        visit(ctx.block());
        return null;
    }

    @Override
    public HashMap<String,Symbol> visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        HashMap<String,Symbol> paramsSymbol = new HashMap<>();
        for(int i=0;i<ctx.funcFParam().size();i++){
            String name = ctx.funcFParam(i).IDENT().getText();
            Symbol symbol = visitFuncFParam(ctx.funcFParam(i));
            if(symbol!=null){
                paramsSymbol.put(name,symbol);
            }
        }
        return paramsSymbol;
    }

    @Override
    public Symbol visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String name = ctx.IDENT().getText();
        if(temSymbolTable.containsKey(name)){
            return null;
        }else if(ctx.L_BRACKT()!=null){
            return new VariableSymbol(name,new ArrayType(new IntType(),0));
        }else{
            return new VariableSymbol(name,new IntType());
        }
    }

    @Override
    public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
        for (int i = 0; i < ctx.varDef().size(); i ++) {
            visit(ctx.varDef(i)); // 依次visit def，即依次visit c=4 和 d=5
        }
        return null;
    }

    @Override
    public Void visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText(); // c or d
        if (currentScope.findCurrentScope(varName)!=null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                Symbol symbol = (Symbol) visitInitVal(ctx.initVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
                if(symbol==null){return null;}
                if(comType(symbol.getType(), new IntType())){
                    OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                    return null;
                }
            }
            currentScope.define(new VariableSymbol(varName,new IntType()));
        } else { // 数组
            ArrayType arrayType = new ArrayType();
            VariableSymbol variableSymbol = new VariableSymbol(varName,arrayType);
            for(int i=0;i<ctx.L_BRACKT().size()-1;i++){
                arrayType.setContained(new ArrayType());
            }
            arrayType.setContained(new IntType());
            currentScope.define(variableSymbol);
        }
        return null;
    }

    @Override
    public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
        for (int i = 0; i < ctx.constDef().size(); i ++) {
            visit(ctx.constDef(i)); // 依次visit def，即依次visit c=4 和 d=5
        }
        return null;
    }

    @Override
    public Void visitConstDef(SysYParser.ConstDefContext ctx) {
        String varName = ctx.IDENT().getText(); // c or d
        if (currentScope.findCurrentScope(varName)!=null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }
        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                Symbol symbol = (Symbol) visitConstInitVal(ctx.constInitVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
                if(symbol==null){return null;}
                if(comType(symbol.getType(), new IntType())){
                    OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                    return null;
                }
            }
            currentScope.define(new VariableSymbol(varName,new IntType()));
        } else { // 数组
            ArrayType arrayType = new ArrayType();
            VariableSymbol variableSymbol = new VariableSymbol(varName,arrayType);
            for(int i=0;i<ctx.L_BRACKT().size()-1;i++){
                arrayType.setContained(new ArrayType());
            }
            arrayType.setContained(new IntType());
            currentScope.define(variableSymbol);
        }
        return null;
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

//       @Override
//    public Void visitStmt1(SysYParser.Stmt1Context ctx) {
//        Symbol symbolL = visitLVal(ctx.lVal());
//        if(symbolL==null||symbolL.getType()==null){return null;}
//        Symbol symbolR = (Symbol) visit(ctx.exp());
//        if(symbolR==null||symbolR.getType()==null){return null;}
//        if(symbolR.getType() instanceof FunctionType){
//            symbolR.setType( ((FunctionType) symbolR.getType()).retTy);
//        }
//        else if(symbolL instanceof FuncSymbol){
//          OutputHelper.printSemanticError(ErrorType.SIGN_ON_FUNC,ctx.ASSIGN().getSymbol().getLine(),ctx.getText());
//          return null;
//        }
//        if(comType(symbolL.getType(), symbolR.getType())){
//            OutputHelper.printSemanticError(ErrorType.SIGN_DISMATCH,ctx.ASSIGN().getSymbol().getLine(),ctx.getText());
//            return null;
//        }
//        return null;
//    }
//
//    @Override
//    public Void visitStmt5(SysYParser.Stmt5Context ctx) {
//        Symbol symbol = (Symbol) visit(ctx.exp());
//        if(symbol==null){return null;}
//        if(!(symbol.getType() instanceof IntType)||symbol instanceof FuncSymbol){
//            OutputHelper.printSemanticError(ErrorType.FUNR_DISMATCH,ctx.getStart().getLine(),ctx.getText());
//            return null;
//        }
//        return null;
//    }
//
//    @Override
//    public Symbol visitExp3(SysYParser.Exp3Context ctx) {
//        return new BaseSymbol(ctx.number().getText(),new IntType());
//    }
//
    @Override
    public Symbol visitLVal(SysYParser.LValContext ctx) {
        String name = ctx.IDENT().getText();
        Symbol symbol = currentScope.findWholeScope(name);
        if(symbol==null){
            OutputHelper.printSemanticError(ErrorType.VAR_UNDEF,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }else if(symbol.getType() instanceof IntType||symbol instanceof FuncSymbol){
            if(!ctx.L_BRACKT().isEmpty()){
                OutputHelper.printSemanticError(ErrorType.INDEX_ON_NON_ARRAY,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                return null;
            }
            return symbol;
        }else if(symbol.getType() instanceof ArrayType){
            ArrayType arrayType = (ArrayType) symbol.getType();
            for(int i=0;i<ctx.L_BRACKT().size()-1;i++){
                if(arrayType.getContained() instanceof IntType){
                    OutputHelper.printSemanticError(ErrorType.INDEX_ON_NON_ARRAY,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                    return null;
                }else{
                    arrayType = (ArrayType) arrayType.getContained();
                }
            }
            symbol.setType(arrayType);
        }
        return symbol;
    }
//
//    @Override
//    public Symbol visitFuncCall(SysYParser.FuncCallContext ctx) {
//        String fucName = ctx.IDENT().getText();
//        Symbol symbol = currentScope.findWholeScope(fucName);
//        if(symbol==null){
//            OutputHelper.printSemanticError(ErrorType.FUNC_UNDEF,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
//            return null;
//        }
//        if(symbol instanceof VariableSymbol){
//            OutputHelper.printSemanticError(ErrorType.FUNC_CALL_ON_VARIABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
//            return null;
//        }
//        ArrayList<Type> arrayList = new ArrayList<>();
//        ArrayList<Type> paraList = new ArrayList<>();
//        FunctionType functionType = (FunctionType) symbol.getType();
//        arrayList = functionType.getParamsType();
//        if(!(ctx.funcRParams()==null)){
//            paraList = visitFuncRParams(ctx.funcRParams());
//        }
//        if(arrayList.size()==paraList.size()){
//            if(arrayList.containsAll(paraList)&&paraList.containsAll(arrayList)){
//                return symbol;
//            }
//        }
//        OutputHelper.printSemanticError(ErrorType.PARAM_NOT_APPLICABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
//        return null;
//    }
//
//    @Override
//    public ArrayList<Type> visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
//        ArrayList<Type> params = new ArrayList<>();
//        for(int i=0;i<ctx.param().size();i++){
//            params.add(visitParam(ctx.param(i)));
//        }
//        return params;
//    }
//
//    @Override
//    public Type visitParam(SysYParser.ParamContext ctx) {
//        Symbol symbol = (Symbol) visit(ctx.exp());
//        return symbol.getType();
//    }
//
//    @Override
//    public Symbol visitExp5(SysYParser.Exp5Context ctx) {
//        Symbol symbolL = (Symbol) visit(ctx.exp(0));
//        Symbol symbolR = (Symbol) visit(ctx.exp(1));
//        if(symbolL==null||symbolR==null){return null;}
//        if(comType(symbolL.getType(), symbolR.getType())){
//            OutputHelper.printSemanticError(ErrorType.OP_DISMATCH,ctx.start.getLine(),ctx.getText());
//            return null;
//        }
//        return new BaseSymbol(ctx.getText(),symbolL.getType());
//    }
//
//    @Override
//    public Symbol visitExp6(SysYParser.Exp6Context ctx) {
//        Symbol symbolL = (Symbol) visit(ctx.exp(0));
//        Symbol symbolR = (Symbol) visit(ctx.exp(1));
//        if(symbolL==null||symbolR==null){return null;}
//        if(comType(symbolL.getType(), symbolR.getType())){
//            OutputHelper.printSemanticError(ErrorType.OP_DISMATCH,ctx.start.getLine(),ctx.getText());
//            return null;
//        }
//        return new BaseSymbol(ctx.getText(),symbolL.getType());
//    }
//
    private boolean comType(Type type1,Type type2) {
        if (type1 instanceof IntType && type2 instanceof IntType) {
            return false;
        } else if (type1 instanceof ArrayType && type2 instanceof ArrayType ) {
            ArrayType typeL = (ArrayType) type1;
            ArrayType typeR = (ArrayType) type2;
            while (typeR.equals(typeL)) {
                if (typeR.getContained().equals(typeL.getContained())) {
                    if (typeR.getContained() instanceof IntType) {
                        break;
                    } else {
                        typeL = (ArrayType) typeL.getContained();
                        typeR = (ArrayType) typeR.getContained();
                    }
                } else {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}