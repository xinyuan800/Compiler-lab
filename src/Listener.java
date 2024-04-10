import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Stack;

public class Listener extends SysYParserBaseListener {

    private Stack<String> position = new Stack<>();
    private int depthOfBrackets = 0;

    private boolean isFunName = false;

    private boolean isFirstLine = true;

    private String lastPrint = "";
    private boolean isUnaryOP = false;

    private int indentation = 0;

    private boolean isElseStmt = false;

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
    public void enterBlock(SysYParser.BlockContext ctx) {
        position.push("block");
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

    @Override
    public void enterStmt1(SysYParser.Stmt1Context ctx) {
       position.push("stmt1");
           printNewLine();
    }

    @Override
    public void exitStmt1(SysYParser.Stmt1Context ctx) {
        position.pop();
    }

    @Override
    public void enterStmt2(SysYParser.Stmt2Context ctx) {
        position.push("stmt2");
            printNewLine();
    }

    @Override
    public void exitStmt2(SysYParser.Stmt2Context ctx) {
        position.pop();
    }

    @Override
    public void enterStmt3(SysYParser.Stmt3Context ctx) {
        printNewLine();
        position.push("stmt3");
    }

    @Override
    public void exitStmt3(SysYParser.Stmt3Context ctx) {
        position.pop();
    }

    @Override
    public void enterStmt4(SysYParser.Stmt4Context ctx) {
        printNewLine();
        position.push("stmt4");
    }

    @Override
    public void exitStmt4(SysYParser.Stmt4Context ctx) {
        position.pop();
    }

    @Override
    public void enterWhileStmt(SysYParser.WhileStmtContext ctx) {
        printNewLine();
        position.push("while");
        indentation++;
    }

    @Override
    public void exitWhileStmt(SysYParser.WhileStmtContext ctx) {
        position.pop();
        indentation--;
    }

    @Override
    public void enterIfStmt(SysYParser.IfStmtContext ctx) {
        position.push("if");
        if(lastPrint.equals("else")){
            isElseStmt  =true;
            return;
        }
        printNewLine();
        indentation++;
    }

    @Override
    public void enterBlock1(SysYParser.Block1Context ctx) {
        if(!position.peek().equals("if")&&!position.peek().equals("while")){
            printNewLine();
        }
        if(position.peek().equals("while")||position.peek().equals("if")){
            indentation--;
        }
        position.push("block1");
    }

    @Override
    public void exitBlock1(SysYParser.Block1Context ctx) {
        position.pop();
        if(position.peek().equals("while")||position.peek().equals("if")||position.peek().equals("else")){
            indentation++;
        }
    }


    @Override
    public void exitIfStmt(SysYParser.IfStmtContext ctx) {
        position.pop();
        if(isElseStmt){
            isElseStmt = false;
            return;
        }
        indentation--;
    }

    @Override
    public void enterStmt5(SysYParser.Stmt5Context ctx) {
        position.push("stmt5");
        printNewLine();
    }

    @Override
    public void exitStmt5(SysYParser.Stmt5Context ctx) {
        position.pop();
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if(lastPrint.equals("return")&&!node.getText().equals(";")){
            printSpace();
        }
        if(lastPrint.equals("else")&&(node.getText().equals("if")||node.getText().equals("{"))){
            printSpace();
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
        }else if (node.getText().equals("}") || node.getText().equals("]") || node.getText().equals(")")) {
            printBracket(node);
            depthOfBrackets--;
            if(depthOfBrackets==0)  depthOfBrackets = 6;
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
        if(position.peek().equals("stmt1")||position.peek().equals("stmt2")||position.peek().equals("if")||position.peek().equals("while")||position.peek().equals("stmt5")||position.peek().equals("stmt3")||position.
        peek().equals("stmt4")||position.peek().equals("block1")){
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
            indentation--;
            printNewLine();
            indentation++;
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
        if(isFirstLine){
            isFirstLine = false;
            return;
        }
            System.out.println();
            lastPrint = "\n";
            for(int i=0;i<4*indentation;i++){
                printSpace();
            }
    }
}
