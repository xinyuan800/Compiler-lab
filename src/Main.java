import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;

import static org.bytedeco.llvm.global.LLVM.*;
import java.io.IOException;

public class Main {

    public static final BytePointer error = new BytePointer();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        //start parser program
        ParseTree tree = sysYParser.program();
        MyVisitor visitor = new MyVisitor();
        visitor.visit(tree);

        LLVMModuleRef module = visitor.getModule();
//        BytePointer errorMessage = new BytePointer((Pointer) null);
//        if (LLVMVerifyModule(module, LLVMPrintMessageAction, errorMessage) != 0) {
//            System.err.println("Error verifying module: " + errorMessage.getString());
//            LLVMDisposeMessage(errorMessage);
//        } else {
//            System.out.println("Module verified successfully.");
//        }

        if (LLVMPrintModuleToFile(module, args[1], error) != 0) {    // module是你自定义的LLVMModuleRef对象
            LLVMDisposeMessage(error);
        }
    }
}