import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMModuleRef;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import static org.bytedeco.llvm.global.LLVM.*;
import java.io.IOException;

public class Main {

    public static final BytePointer error = new BytePointer();
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("input path is required");
        }
        String source = args[0];
        SysYLexer

        //创建module
        LLVMModuleRef module = LLVMModuleCreateWithName("module");

        //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
        LLVMBuilderRef builder = LLVMCreateBuilder();

        //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
        LLVMTypeRef i32Type = LLVMInt32Type();
//        if (LLVMPrintModuleToFile(module, args[1], error) != 0) {    // module是你自定义的LLVMModuleRef对象
//            LLVMDisposeMessage(error);
//        }
    }
}