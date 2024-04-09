import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;

public class Listener extends SysYParserBaseListener {
    private String position = "";
    private int depthOfBrackets = 0;
    private String lastPrint = "";
    private boolean firstLine = true;

    private boolean isNewLine = false;
    private int indentation = 0;

    public void enterDecl(SysYParser.DeclContext ctx) {
        position = "Decl";
    }

    public void exitDecl(SysYParser.DeclContext ctx){
        position = "";
        System.out.println();
        isNewLine = true;
    }

    public void enterFuncDef(SysYParser.FuncDefContext ctx) {
        if (!firstLine) {
            System.out.println();
            isNewLine = true;
        }else{
            firstLine = false;
        }
    }

    @Override
    public void exitFuncDef(SysYParser.FuncDefContext ctx) {
        System.out.println();
        isNewLine = true;
    }

    public void enterBlock(SysYParser.BlockContext ctx) {
        position = "block";
    }

    @Override
    public void exitBlock(SysYParser.BlockContext ctx) {
        position = "";
    }

    @Override
    public void enterBlockItem(SysYParser.BlockItemContext ctx) {
        indentation++;
    }

    @Override
    public void exitBlockItem(SysYParser.BlockItemContext ctx) {
        indentation--;
    }

    public void enterStmt(SysYParser.StmtContext ctx) {
        position = "stmt";
    }

    public void exitStmt(SysYParser.StmtContext ctx) {
        position = "";
        if(!isNewLine){
            System.out.println();
            isNewLine = true;
        }
    }

    public void enterUnaryOp(SysYParser.UnaryOpContext ctx) {
        position = "unaryOP";
    }

    @Override
    public void enterNumber(SysYParser.NumberContext ctx) {
        position = "number";
    }

    @Override
    public void exitNumber(SysYParser.NumberContext ctx) {
        position = "";
    }

    public void visitTerminal(TerminalNode node) {
        int i=0;
        if(lastPrint.equals("else")&&position.equals("stmt")&&!node.getText().equals("if")){
            System.out.println();
            i=-4;
            isNewLine = true;
        }
        if(isNewLine){
            for(;i<4*indentation;i++){
                printSpace();
            }
            isNewLine = false;
        }
        if (position.equals("Decl")) {
            System.out.print(SGR_Name.Underlined + SGR_Name.LightMagenta);
        }
        if (lastPrint.equals("return")&&!node.getText().equals(";")) {
            printSpace();
        }
        if (node.getText().equals("const") || node.getText().equals("int") || node.getText().equals("void") || node.getText().equals("if") || node.getText().equals("else") || node.getText().equals("while") || node.getText().equals("break") || node.getText().equals("continue") || node.getText().equals("return")) {
            System.out.print(SGR_Name.LightCyan + node.getText() + SGR_Name.Reset);
            if (!node.getText().equals("break") &&!node.getText().equals("continue") &&!node.getText().equals("return")) {
                printSpace();
            }
        } else if (node.getText().equals("+") || node.getText().equals("-") || node.getText().equals("*") || node.getText().equals("/") || node.getText().equals("%") || node.getText().equals("=") || node.getText().equals("==") || node.getText().equals("!=") || node.getText().equals("<") || node.getText().equals(">") || node.getText().equals(">=") || node.getText().equals("<=") || node.getText().equals("!") || node.getText().equals("&&") || node.getText().equals("||") || node.getText().equals(",")||node.getText().equals(";")) {
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
        }else if (Objects.equals(position, "stmt")) {
            System.out.print(SGR_Name.White+node.getText()+SGR_Name.Reset);
        } else if (!node.getText().equals("<EOF>")) {
            System.out.print(node.getText()+SGR_Name.Reset);
        }
        lastPrint = node.getText();
        firstLine = false;
    }

    private void printOP(TerminalNode node) {
        if (node.getText().equals(",")) {
            System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset + " ");
            return;
        }
        if (!(position.equals("unaryOP"))&&!node.getText().equals(";")) {
            if(position.equals("Decl")){
                printSpace();
                System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset );
                printSpace();
            }else{
                printSpace();
                System.out.print(SGR_Name.LightRed + node.getText() + SGR_Name.Reset );
                printSpace();
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
        if(position.equals("block")&&node.getText().equals("{")){
            System.out.println();
            isNewLine = true;
        }
    }

    private void printSpace(){
        System.out.print(SGR_Name.Reset+" ");
        if(position.equals("Decl")){
            System.out.print(SGR_Name.Underlined);
        }
    }

}
