import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;

public class Listener extends SysYParserBaseListener {
    private String position = "";
    private int depthOfBrackets = 0;
    private String lastPrint = "";
    private boolean firstLine = true;

    private int indentation = 0;

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {
        position = "Decl";
    }

    @Override
    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        if (!firstLine) {
            System.out.println();
        }
        firstLine = false;
    }

    @Override
    public void enterBlock(SysYParser.BlockContext ctx) {
        position = "block";
    }


    @Override
    public void enterStmt(SysYParser.StmtContext ctx) {
        position = "stmt";
    }

    @Override
    public void exitStmt(SysYParser.StmtContext ctx) {
        position = "";
    }

    @Override
    public void enterUnaryOp(SysYParser.UnaryOpContext ctx) {
        position = "unaryOP";
    }

    public void visitTerminal(TerminalNode node) {
        if (node.getText().equals(";")) {
            System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset);
            lastPrint = ";";
            System.out.println();
            return;
        }
        if (lastPrint.equals("return")) {
            System.out.print(" ");
        }
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined + SGR_Name.LightMagenta);
        }
        if (node.getText().equals("const") || node.getText().equals("int") || node.getText().equals("void") || node.getText().equals("if") || node.getText().equals("else") || node.getText().equals("while") || node.getText().equals("break") || node.getText().equals("continue") || node.getText().equals("return")) {
            System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
            if (!(node.getText().equals("break") || node.getText().equals("continue") || node.getText().equals("return"))) {
                System.out.print(" ");
            }
        } else if (node.getText().equals("+") || node.getText().equals("-") || node.getText().equals("*") || node.getText().equals("/") || node.getText().equals("%") || node.getText().equals("=") || node.getText().equals("==") || node.getText().equals("!=") || node.getText().equals("<") || node.getText().equals(">") || node.getText().equals(">=") || node.getText().equals("<=") || node.getText().equals("!") || node.getText().equals("&&") || node.getText().equals("||") || node.getText().equals(",")) {
            printOP(node);
        } else if (node.getSymbol().getType() == SysYLexer.INTEGER_CONST) {
            System.out.print(SGR_Name.Magenta + node.getText() + SGR_Name.Reset);
        } else if (node.getSymbol().getType() == SysYLexer.IDENT && (node.getParent() instanceof SysYParser.FuncDefContext || node.getParent() instanceof SysYParser.ExpContext)) {
            System.out.print(SGR_Name.LightYellow + node.getText() + SGR_Name.Reset);
        }
        //deal with brackets
        else if (node.getSymbol().getType() == SysYLexer.L_BRACE || node.getSymbol().getType() == SysYLexer.L_BRACKT || node.getSymbol().getType() == SysYLexer.L_PAREN) {
            depthOfBrackets++;
            if (depthOfBrackets == 7) {
                depthOfBrackets = 1;
            }
            printBrackets(node);
        } else if (node.getSymbol().getType() == SysYLexer.R_BRACE || node.getSymbol().getType() == SysYLexer.R_BRACKT || node.getSymbol().getType() == SysYLexer.R_PAREN) {
            printBrackets(node);
            depthOfBrackets--;
        } else if (Objects.equals(position, "stmt")) {
            System.out.print(SGR_Name.White + node.getText() + SGR_Name.Reset);
        } else if (!node.getText().equals("<EOF>")) {
            System.out.print(node.getText());
        }
        lastPrint = node.getText();
        firstLine = false;
    }

    private void printOP(TerminalNode node) {
        if (node.getText().equals(",")) {
            System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset + " ");
            return;
        }
        if (!(position.equals("unaryOP"))) {
            if(position.equals("Decl")){
                System.out.print(SGR_Name.Reset+" ");
                System.out.print(SGR_Name.Underlined);
                System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset + " ");
            }else{
                System.out.print(" " + SGR_Name.LightRed + node.getText() + SGR_Name.Reset + " ");
            }
        } else {
            System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset);
        }
    }

    private void printBrackets(TerminalNode node) {
        if(node.getText().equals("{")&&!position.equals("Decl")){
            if(lastPrint.equals(")")){
                System.out.print(" ");
            }
        }
        if (depthOfBrackets == 1) {
            System.out.print(SGR_Name.LightRed);
        } else if (depthOfBrackets == 2) {
            System.out.print(SGR_Name.LightGreen);
        } else if (depthOfBrackets == 3) {
            System.out.print(SGR_Name.LightYellow);
        } else if (depthOfBrackets == 4) {
            System.out.print(SGR_Name.LightBlue);
        } else if (depthOfBrackets == 5) {
            System.out.print(SGR_Name.LightMagenta);
        } else if (depthOfBrackets == 6) {
            System.out.print(SGR_Name.LightCyan);
        }
        System.out.print(node.getText() + SGR_Name.Reset);
        if(!position.equals("Decl")){
            if(node.getText().equals("}")||node.getText().equals("{")) {
                System.out.println();
            }
        }

    }

}
