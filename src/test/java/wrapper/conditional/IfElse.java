package wrapper.conditional;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.voidlang.llvm.element.*;

import java.util.ArrayList;
import java.util.Collections;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeNativeTarget;

public class IfElse {
    public static void main(String[] args) {
        // Initialize LLVM components
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();

        // Create the LLVM context and module
        IRContext context = IRContext.create();
        IRModule module = IRModule.create(context, "my_module");
        IRBuilder builder = IRBuilder.create(context);

        // declare return types in the context
        IRType i32type = IRType.int32(context);

        // create the test function that takes in an i32 parameter
        IRFunctionType testType = IRFunctionType.create(i32type, Collections.singletonList(i32type));
        IRFunction testFunction = IRFunction.create(module, "test", testType);

        // add the "entry" block to the function, so instructions can be added
        IRBlock testEntry = IRBlock.create(testFunction, "entry");
        builder.positionAtEnd(testEntry);

        // get the i32 value from the function parameters
        IRValue parameter = testFunction.getParameter(0);

        // Create basic blocks for the if-then and else clauses
        IRBlock ifBlock = IRBlock.create(testFunction, "if");
        IRBlock elseBlock = IRBlock.create(testFunction, "else");

        // Compare the argument with 30
        IRValue operand = i32type.constInt(30);
        Comparator operator = Comparator.SIGNED_INTEGER_GREATER_THAN;
        IRValue condition = builder.compareInt(operator, parameter, operand, "cmp");

        // Emit the branch instruction based on the condition
        builder.jumpIf(condition, ifBlock, elseBlock);

        // Emit the if-then clause
        builder.positionAtEnd(ifBlock);
        builder.returnValue(i32type.constInt(200));

        // Emit the else clause
        builder.positionAtEnd(elseBlock);
        builder.returnValue(i32type.constInt(100));

        // create the program entry point
        IRFunctionType mainType = IRFunctionType.create(context, i32type, new ArrayList<>(), false);
        IRFunction main = IRFunction.create(module, "main", mainType);

        // create the entry section of the main method
        IRBlock block = IRBlock.create(context, main, "entry");
        builder.positionAtEnd(block);

        IRValue call = builder.call(testFunction, Collections.singletonList(i32type.constInt(40)));
        builder.returnValue(call);

        // Verify the module
        BytePointer error = new BytePointer((Pointer) null);
        if (!module.verify(IRModule.VerifierFailureAction.PRINT_MESSAGE, error)) {
            System.err.println("Error: " + error.getString());
            LLVMDisposeMessage(error);
            return;
        }

        // Dump the module IR
        module.dump();

        // Stage 5: Execute the code using MCJIT
        ExecutionEngine engine = ExecutionEngine.create();
        MMCJITCompilerOptions options = MMCJITCompilerOptions.create();
        if (!engine.createMCJITCompilerForModule(module, options, error)) {
            System.err.println("Failed to create JIT compiler: " + error.getString());
            LLVMDisposeMessage(error);
            return;
        }

        IRGenericValue result = engine.runFunction(main, new ArrayList<>());
        System.out.println();
        System.out.println("Result: " + result.toInt());

        // Dispose of the allocated resources
        builder.dispose();
        module.dispose();
        context.dispose();
    }
}
