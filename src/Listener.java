import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

public class Listener extends SysYParserBaseListener {

    private Stack<String> position = new Stack<>();
    private int depthOfBrackets = 0;

    private boolean isFunName = false;

    private boolean isFirstLine = true;

    private boolean isWhileStmt = false;

    private boolean firstIfStmt = false;

    private boolean firstWhileStmt = false;

    private int isIfStmt = 0;

    private boolean isElseStatement = false;

    private String lastPrint = "";
    private boolean isUnaryOP = false;

    private int indentation = 0;


    @Override
    public void enterProgram(SysYParser.ProgramContext ctx){
        position.push("program");
    }

    public void enterDecl(SysYParser.DeclContext ctx) {
        position.push("Decl");
        printNewLine();
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {
        position.pop();
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        isFunName = true;
        if(isFirstLine){
            isFirstLine = false;
        }else{
            printNewLine();
            printNewLine();
        }
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {
        isFunName = false;
    }

    @Override
    public void enterFuncFParams(SysYParser.FuncFParamsContext ctx) {
        isFunName = false;
    }

    @Override
    public void enterFuncCall(SysYParser.FuncCallContext ctx) {
        isFunName = true;
    }

    @Override
    public void exitFuncCall(SysYParser.FuncCallContext ctx) {
        isFunName = false;
    }

    @Override
    public void enterFuncRParams(SysYParser.FuncRParamsContext ctx) {
        isFunName = false;
    }

    @Override
    public void enterStmt(SysYParser.StmtContext ctx) {
        position.push("stmt");
        if(isIfStmt>0){
            firstIfStmt = true;
        }
        if(isWhileStmt){
            firstWhileStmt =true;
        }
        if(ctx.WHILE()!=null){
            isWhileStmt = true;
        }
        if(ctx.IF()!=null){
            isIfStmt++;
        }
        if(isIfStmt>1||isWhileStmt||isElseStatement){
            return;
        }
        printNewLine();
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        position.pop();
        if(isIfStmt>0){
            isIfStmt --;
            indentation--;
        }
        if(isWhileStmt){
            isWhileStmt = false;
            indentation--;
        }
        if(isElseStatement){
            isElseStatement = false;
            indentation--;
        }
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        position.push("block");
        if(isIfStmt>0) {
            firstIfStmt = false;
            isIfStmt --;
        }
        if(isWhileStmt){
            isWhileStmt = false;
            firstWhileStmt = false;
        }
        if(isElseStatement)isElseStatement = false;
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        position.pop();
    }

    @Override
    public void enterUnaryOp(SysYParser.UnaryOpContext ctx) {
        isUnaryOP = true;
    }

    @Override
    public void exitUnaryOp(SysYParser.UnaryOpContext ctx) {
        isUnaryOP = false;
    }

    @Override
    public void enterBlockItem(SysYParser.BlockItemContext ctx) {
        indentation++;
    }

    @Override
    public void exitBlockItem(SysYParser.BlockItemContext ctx) {
        indentation--;
    }

    private void enterElseStmt(){
        indentation++;
        printNewLine();
    }

    private void enterIfStmt(){
        firstIfStmt = false;
        indentation++;
        printNewLine();
    }

    private void enterWhileStmt(){
        firstWhileStmt = false;
        indentation++;
        printNewLine();
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if(lastPrint.equals("else")){
            if(node.getText().equals("if")||node.getText().equals("{")){
                printSpace();
            }else{
                enterElseStmt();
            }
        }
        if(firstIfStmt)enterIfStmt();
        if(firstWhileStmt)enterWhileStmt();
        if (node.getText().equals("const") || node.getText().equals("int") || node.getText().equals("void") || node.getText().equals("if") || node.getText().equals("else") || node.getText().equals("while") || node.getText().equals("break") || node.getText().equals("continue") || node.getText().equals("return")) {
            printKeyWord(node);
        }
        else if (node.getText().equals("+") || node.getText().equals("-") || node.getText().equals("*") || node.getText().equals("/") || node.getText().equals("%") || node.getText().equals("=") || node.getText().equals("==") || node.getText().equals("!=") || node.getText().equals("<") || node.getText().equals(">") || node.getText().equals("<=") || node.getText().equals(">=") || node.getText().equals("!") || node.getText().equals("&&") || node.getText().equals("||") || node.getText().equals(",") || node.getText().equals(";")) {
            printOP(node);
        }
        else if (node.getSymbol().getType() == SysYLexer.INTEGER_CONST) {
            printNumber(node);
        }
        else if (node.getText().equals("{") || node.getText().equals("[") || node.getText().equals("(")) {
            depthOfBrackets++;
            if (depthOfBrackets == 7) depthOfBrackets = 1;
            printBracket(node);
        }else if (node.getText().equals("}") || node.getText().equals("]") || node.getText().equals(")")) {
            printBracket(node);
            depthOfBrackets--;
        }
        else if (node.getSymbol().getType() == SysYLexer.IDENT) {
            printIdent(node);
        }
    }

    private void printBracket(TerminalNode node) {
        if(node.getText().equals("}")&&!position.peek().equals("Decl")){
            printNewLine();
        }
        if(node.getText().equals("{")&&!lastPrint.equals("\n")&&!lastPrint.equals(" ")){
            printSpace();
        }
        if(position.peek().equals("Decl")){
            System.out.print(SGR_Name.Underlined);
        }
        if (depthOfBrackets == 1) {
            System.out.print(SGR_Name.LightRed );
        } else if (depthOfBrackets == 2) {
            System.out.print(SGR_Name.LightGreen );
        } else if (depthOfBrackets == 3) {
            System.out.print(SGR_Name.LightYellow );
        } else if (depthOfBrackets == 4) {
            System.out.print(SGR_Name.LightBlue );
        } else if (depthOfBrackets == 5) {
            System.out.print(SGR_Name.LightMagenta );
        } else if (depthOfBrackets == 6) {
            System.out.print(SGR_Name.LightCyan );
        }
        System.out.print(node.getText()+SGR_Name.Reset);
        lastPrint = node.getText();
    }

    private void printIdent(TerminalNode node){
        if (position.peek().equals("Decl")){
            System.out.print(SGR_Name.Underlined+SGR_Name.LightMagenta);
        }
        if(position.peek().equals("stmt")){
            System.out.print(SGR_Name.White);
        }
        if (isFunName) {
            System.out.print(SGR_Name.LightYellow );
            isFunName = false;
        }
        System.out.print(node.getText()+SGR_Name.Reset);
        lastPrint = node.getText();
    }

    private void printNumber(TerminalNode node) {
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.Magenta + node.getText() + SGR_Name.Reset);
        lastPrint = node.getText();
    }

    private void printOP(TerminalNode node) {
        if(!isUnaryOP&&!node.getText().equals(",")&&!node.getText().equals(";")){
            printSpace();
        }
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset);
        lastPrint = node.getText();
        if(!isUnaryOP&&!node.getText().equals(",")&&!node.getText().equals(";")){
            printSpace();
        }
        else if(lastPrint.equals(",")){
            printSpace();
        }
    }

    private void printKeyWord(TerminalNode node) {
        if(node.getText().equals("else")){
            isElseStatement = true;
            printNewLine();
        }
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
        String out = node.getText();
        if(!out.equals("break")&&!out.equals("continue")&&!out.equals("return")&&!out.equals("else")){
            printSpace();
        }
        lastPrint = node.getText();
    }

    private void printSpace(){
        System.out.print(SGR_Name.Reset+" ");
        lastPrint = " ";
    }

    private void printNewLine(){
            System.out.println();
            lastPrint = "\n";
            for(int i=0;i<4*indentation;i++){
                printSpace();
            }
    }
}
