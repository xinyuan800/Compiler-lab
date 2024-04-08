import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Objects;

public class Listener extends SysYParserBaseListener{
    private String position="";
    private int depthOfBrackets;

    @Override
    public void enterDecl(SysYParser.DeclContext ctx) {
        position = "Decl";
    }

    @Override
    public void exitDecl(SysYParser.DeclContext ctx) {
        position = "";
    }

    public void visitTerminal(TerminalNode node) {
        if(position.equals("Decl")){
            System.out.print(SGR_Name.Underlined);
        }
        if(node.getText().equals("const")||node.getText().equals("int")||node.getText().equals("void")||node.getText().equals("if")||node.getText().equals("else")||node.getText().equals("while")||node.getText().equals("break")||node.getText().equals("continue")||node.getText().equals("return")){
            System.out.print(SGR_Name.LightCyan+node.getText()+SGR_Name.Reset);
            if(!(node.getText().equals("break")||node .getText().equals("continue"))){
                System.out.print(" ");
            }
        }
        else if(node.getText().equals("+")||node.getText().equals("-")||node.getText().equals("*")||node.getText().equals("/")||node.getText().equals("%")||node.getText().equals("=")||node.getText().equals("==")||node.getText().equals("!=")||node.getText().equals("<")||node.getText().equals(">")||node.getText().equals(">=")||node.getText().equals("<=")||node.getText().equals("!")||node.getText().equals("&&")||node.getText().equals("||")||node.getText().equals(",")||node.getText().equals(";")){
            System.out.print(SGR_Name.LightRed+node.getText()+SGR_Name.Reset);
        }
        else if(node.getSymbol().getType()==SysYLexer.INTEGER_CONST){
            System.out.print(SGR_Name.Magenta+node.getText()+SGR_Name.Reset);
        }
        else if(node.getSymbol().getType()==SysYLexer.IDENT){
            if(node.getParent() instanceof SysYParser.FuncDefContext||node.getParent() instanceof SysYParser.ExpContext){
                System.out.print(SGR_Name.LightYellow+node.getText()+SGR_Name.Reset);
            }
        }
        else if(node.getParent() instanceof SysYParser.StmtContext){

        }

    }

}
