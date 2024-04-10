import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;
import java.util.Stack;

public class Listener extends SysYParserBaseListener {

    private Stack<String> position = new Stack<>();
    private int depthOfBrackets = 0;

    private boolean isFunName = false;

    private boolean isFirstLine = true;

    private boolean isWhile = false;
    private boolean isWhileStmt = false;

    private boolean isIf = false;

    private boolean isIfStmt = false;

    private String lastPrint = "";
    private boolean isUnaryOP = false;

    private int indentation = 0;


    @Override
    public void enterProgram(SysYParser.ProgramContext ctx){
        position.push("program");
    }

    public void enterDecl(SysYParser.DeclContext ctx) {
        position.push("Decl");
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {
        position.pop();
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        isFunName = true;
        if(!isFirstLine){
            System.out.println();
            lastPrint = "\n";
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
        if(isWhile){
            isWhileStmt = true;
        }
        if(isIf){
            isIfStmt = true;
        }
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        position.pop();
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        position.push("block");
        if(isWhileStmt){
            isWhileStmt = false;
            isWhile = false;
        }
        if(isIfStmt){
            isIfStmt = false;
            isIf = false;
        }
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
    public void visitTerminal(TerminalNode node) {
        int i=0;
        if(isWhileStmt){
            System.out.println();
            lastPrint = "\n";
            isWhileStmt = false;
            isWhile = false;
            i=-4;
        }
        if(isIfStmt){
            System.out.println();
            isIf = false;
            isIfStmt = false;
            lastPrint = "\n";
            i=-4;
        }
        if(lastPrint.equals("return")&&!node.getText().equals(";")){
            printSpace();
        }
        if(lastPrint.equals("else")&&!node.getText().equals("if")&&!node.getText().equals("{")){
            System.out.println();
            lastPrint = "\n";
            i=-4;
        }
        if(node.getText().equals("}"))indentation--;
        if(lastPrint.equals("\n")){
            for(;i<indentation*4;i++){
                printSpace();
            }
        }
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
            if(node.getText().equals("{"))indentation++;
        }else if (node.getText().equals("}") || node.getText().equals("]") || node.getText().equals(")")) {

            printBracket(node);
            depthOfBrackets--;
        }
        else if (node.getSymbol().getType() == SysYLexer.IDENT) {
            printIdent(node);
        }
    }

    private void printBracket(TerminalNode node) {
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
        if(!position.peek().equals("Decl")){
            if(node.getText().equals("{")||node.getText().equals("}")){
                System.out.println();
                lastPrint = "\n";
                isFirstLine = false;
            }
        }
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
        else if(node.getText().equals(";")){
            isFirstLine = false;
            System.out.println();
            lastPrint = "\n";
        }
        else if(lastPrint.equals(",")){
            printSpace();
        }
    }

    private void printKeyWord(TerminalNode node) {
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
        if(node.getText().equals("if")){
            isIf = true;
        }else if(node.getText().equals("while")){
            isWhile = true;
        }
        String out = node.getText();
        if(!out.equals("break")&&!out.equals("continue")&&!out.equals("return")){
            printSpace();
        }
        lastPrint = node.getText();
    }

    private void printSpace(){
        System.out.print(SGR_Name.Reset+" ");
        lastPrint = " ";
    }
}
