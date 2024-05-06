import java.util.ArrayList;
import java.util.HashMap;

public class Visitor extends SysYParserBaseVisitor<Void> {
    private GlobalScope globalScope = null;
    private Scope currentScope = null;

    private Symbol expSymbol = null;

    private ArrayList<Type> paramsTyList = new ArrayList<>();

    private HashMap<String,BaseSymbol> paramsSymbols = new HashMap<>();

    @Override
    public Void visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new GlobalScope(null);
        this.currentScope = globalScope;
        for(int i=0;i<ctx.getChildCount();i++){
            visit(ctx.getChild(i));
        }
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
            retType = new IntType();     // 返回值类型为int32

        if (ctx.funcFParams() != null) { // 如有入参，处理形参，添加形参信息等
            visit(ctx.funcFParams());
        }

        FunctionType functionType = new FunctionType(retType, paramsTyList);
        //顶层作用域中压入此函数
        FuncSymbol funcSymbol = new FuncSymbol(funcName,functionType);
        currentScope.define(funcSymbol);

        visit(ctx.block());
        return null;
    }

    @Override
    public Void visitFuncFParams(SysYParser.FuncFParamsContext ctx) {
        paramsTyList.clear();
        paramsSymbols.clear();
        ctx.funcFParam().forEach(this::visit);
        return null;
    }

    @Override
    public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String name = ctx.IDENT().getText();
        if(paramsSymbols.containsKey(name)){
            return null;
        }
        Type paramTy;
        if(ctx.L_BRACKT().isEmpty()){
            paramTy = new IntType();
        }
        else{
            paramTy = new ArrayType(new IntType(),1);

        }
        VariableSymbol variableSymbol = new VariableSymbol(name,paramTy);
        paramsTyList.add(paramTy);
        paramsSymbols.put(name,variableSymbol);
        return null;
    }

    @Override
    public Void visitBlock(SysYParser.BlockContext ctx) {
        //新一层作用域
        //System.out.println(ctx.getText());
        Scope newScope = new LocalScope(currentScope);
        currentScope = newScope;
        paramsSymbols.forEach((name,Type)->newScope.define(paramsSymbols.get(name)));

        ctx.blockItem().forEach(this::visit); // 依次visit block中的节点
        //切换回父级作用域
        currentScope = currentScope.getEnclosingScope();

        return null;
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
        if (currentScope.findCurrentScope(varName) != null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText() );
            return null;
        }

        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                visitInitVal(ctx.initVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
            }
            VariableSymbol variableSymbol = new VariableSymbol(varName,new IntType());
            currentScope.define(variableSymbol);
        } else { // 数组
            ArrayType arrayType = new ArrayType();
            VariableSymbol variableSymbol = new VariableSymbol(varName,arrayType);
            for(int i=0;i<ctx.L_BRACKT().size()-1;i++){
                arrayType.setContained(new ArrayType());
                arrayType = (ArrayType) arrayType.getContained();
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
        if (currentScope.findCurrentScope(varName) != null) {
            OutputHelper.printSemanticError(ErrorType.REDEF_VAR,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText() );
            return null;
        }

        if (ctx.constExp().isEmpty()) {     //非数组
            if (ctx.ASSIGN() != null) {     // 包含定义语句
                visitConstInitVal(ctx.constInitVal()); // 访问定义语句右侧的表达式，如c=4右侧的4
            }
            VariableSymbol variableSymbol = new VariableSymbol(varName,new IntType());
            currentScope.define(variableSymbol);
        } else { // 数组
            int num_elements = ctx.L_BRACKT().size();
            ArrayType arrayType = new ArrayType(new ArrayType(),num_elements);
            VariableSymbol variableSymbol = new VariableSymbol(varName,arrayType);
            currentScope.define(variableSymbol);
        }
        return null;
    }

    @Override
    public Void visitLVal(SysYParser.LValContext ctx) {
        Symbol symbol = currentScope.findWholeScope(ctx.IDENT().getText());
        if(symbol==null){
            OutputHelper.printSemanticError(ErrorType.VAR_UNDEF,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }
        if(ctx.getParent() instanceof SysYParser.Exp2Context||ctx.getParent() instanceof SysYParser.Stmt1Context){
            expSymbol = symbol;
        }
        Type type = expSymbol.getType();
        for(int i=0;i<ctx.L_BRACKT().size();i++){
            if(type instanceof IntType||type instanceof FunctionType){
                OutputHelper.printSemanticError(ErrorType.INDEX_ON_NON_ARRAY,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
                return null;
            }
            type = ((ArrayType) type).getContained();
        }
        expSymbol.setType(type);
        return null;
    }

    @Override
    public Void visitFuncCall(SysYParser.FuncCallContext ctx) {
        Symbol symbol = currentScope.findWholeScope(ctx.IDENT().getText());
        if(symbol==null){
            OutputHelper.printSemanticError(ErrorType.FUNC_UNDEF,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }else if(!(symbol.getType() instanceof FunctionType)){
            OutputHelper.printSemanticError(ErrorType.FUNC_CALL_ON_VARIABLE,ctx.IDENT().getSymbol().getLine(),ctx.IDENT().getText());
            return null;
        }
        expSymbol = symbol;

        return null;
    }

    @Override
    public Void visitStmt5(SysYParser.Stmt5Context ctx) {
        expSymbol = null;
        visit(ctx.exp());
        if(expSymbol.getType()==null||!(expSymbol.getType() instanceof IntType)){
            OutputHelper.printSemanticError(ErrorType.FUNR_DISMATCH,ctx.getStart().getLine(),ctx.getText());
            return null;
        }
        return null;
    }

    @Override
    public Void visitStmt1(SysYParser.Stmt1Context ctx) {
        Type typeR = new Type();
        Type typeL = new Type();
        expSymbol = null;
        visit(ctx.lVal());
        if(expSymbol!=null){
            typeL = expSymbol.getType();
        }
        expSymbol = null;
        if(!(typeL instanceof ArrayType)&&(!(typeL instanceof IntType))){
            OutputHelper.printSemanticError(ErrorType.SIGN_ON_FUNC,ctx.ASSIGN().getSymbol().getLine(),ctx.getText());
            return null;
        }
        visit(ctx.exp());

        if(expSymbol!=null){
            typeR = expSymbol.getType();
        }

        expSymbol = null;


        return null;

    }

    @Override
    public Void visitExp3(SysYParser.Exp3Context ctx) {
        expSymbol = new BaseSymbol(ctx.getText(), new IntType());
        return null;
    }

    @Override
    public Void visitExp5(SysYParser.Exp5Context ctx) {
        expSymbol = new BaseSymbol(null,new IntType());
        return null;
    }

    @Override
    public Void visitExp6(SysYParser.Exp6Context ctx) {
        expSymbol = new BaseSymbol(null,new IntType());
        return null;
    }
}
