import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];

        public static final BytePointer error = new BytePointer();

        if (LLVMPrintModuleToFile(module, args[1], error) != 0) {    // module是你自定义的LLVMModuleRef对象
            LLVMDisposeMessage(error);
        }
    }
}