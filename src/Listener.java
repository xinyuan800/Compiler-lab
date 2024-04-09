import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;
import java.util.Stack;

public class Listener extends SysYParserBaseListener {

    private Stack<String> position = new Stack<>();
    private int depthOfBrackets = 0;

    private boolean isFunName = false;

    private boolean isFirstLine = true;

    private boolean isWhile = false;

    private boolean isIfElse = false;

    private String lastPrint = "";


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
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        position.pop();
    }

    publi

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        position.push("block");
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        position.pop();
    }

    @Override
    public void visitTerminal(TerminalNode node) {
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
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset);
        lastPrint = node.getText();
        if(node.getText().equals(";")){
            isFirstLine = false;
            System.out.println();
            lastPrint = node.getText();
        }
    }

    private void printKeyWord(TerminalNode node) {
        if (position.peek().equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
        lastPrint = node.getText();
    }
}
