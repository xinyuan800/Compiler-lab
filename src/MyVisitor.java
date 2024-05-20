import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
    @Override
    public LLVMValueRef visit(ParseTree tree) {
    }

    @Override
    public LLVMValueRef visitTerminal(TerminalNode node) {
    }

    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
    }

    @Override
    public LLVMValueRef visitCompUnit(SysYParser.CompUnitContext ctx) {
    }
}