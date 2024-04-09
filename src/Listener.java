import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;

public class Listener extends SysYParserBaseListener {

    private String position = "";
    private int depthOfStmt = 0;

    private int depthOfBrackets = 0;

    private boolean isFunName = false;

    public void enterDecl(SysYParser.DeclContext ctx) {
        position = "Decl";
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {
        position = "";
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        isFunName = true;
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {
        isFunName = false;
    }



    @Override
    public void enterStmt(SysYParser.StmtContext ctx) {
        depthOfStmt++;
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        depthOfStmt--;
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        position = "block";
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        position = "";
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
        if(Objects.equals(position, "Decl")){
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
    }

    private void printIdent(TerminalNode node){
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined+SGR_Name.LightMagenta);
        }
        if(depthOfStmt>0){
            System.out.print(SGR_Name.White);
        }
        if (node.getParent() instanceof SysYParser.FuncDefContext || node.getParent() instanceof SysYParser.ExpContext) {
            System.out.print(SGR_Name.LightYellow );
        }
        System.out.print(node.getText()+SGR_Name.Reset);
    }

    private void printNumber(TerminalNode node) {
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.Magenta + node.getText() + SGR_Name.Reset);
    }

    private void printOP(TerminalNode node) {
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset);
    }

    private void printKeyWord(TerminalNode node) {
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined);
        }
        System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
    }
}
