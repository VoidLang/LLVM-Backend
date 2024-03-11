package wrapper.array;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithNameInContext;

public class ArrayTest {
    public static void main(String[] args) {
        // Initialize LLVM components
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();

        // Create the LLVM context and module
        LLVMContextRef context = LLVMContextCreate();
        LLVMModuleRef module = LLVMModuleCreateWithNameInContext("my_module", context);

        // Create the function type
        LLVMTypeRef i32Type = LLVMInt32TypeInContext(context);

        LLVMTypeRef functionType = LLVMFunctionType(i32Type, new PointerPointer<LLVMTypeRef>(0), 0, 0);

        // Create the function
        LLVMValueRef function = LLVMAddFunction(module, "my_function", functionType);

        // Create the entry basic block
        LLVMBasicBlockRef entryBlock = LLVMAppendBasicBlockInContext(context, function, "entry");
        LLVMBuilderRef builder = LLVMCreateBuilderInContext(context);

        LLVMPositionBuilderAtEnd(builder, entryBlock);

        int arraySize = 5;

        LLVMTypeRef i32ArrayType = LLVMArrayType(i32Type, arraySize);

        // allocate an array on the stack
        LLVMValueRef array = LLVMBuildAlloca(builder, i32ArrayType, "array");

        LLVMValueRef[] values = {
            LLVMConstInt(i32Type, 10, 0),
            LLVMConstInt(i32Type, 20, 0),
            LLVMConstInt(i32Type, 30, 0),
            LLVMConstInt(i32Type, 40, 0),
            LLVMConstInt(i32Type, 50, 0)
        };

        for (int i = 0; i < arraySize; i++) {
            LLVMValueRef pointer = LLVMBuildStructGEP2(builder, i32ArrayType, array, i, "array[" + i + "]");
            LLVMBuildStore(builder, values[i], pointer);
        }

        // dynamically resolve array index pointer using a value from the stack
        LLVMValueRef dynamicIndex = LLVMConstInt(i32Type, 4, 0);

        LLVMValueRef pointer = LLVMBuildGEP2(
            builder, i32ArrayType, array,
            new PointerPointer<>(
                LLVMConstInt(i32Type, 0, 0), // I have no idea
                dynamicIndex // the array index
            ),
            2, // because below are two indices
            "index"
        );

        // LLVMValueRef pointer = LLVMBuildStructGEP2(builder, i32ArrayType, array, 1, "ptr");
        LLVMValueRef value = LLVMBuildLoad2(builder, i32Type, pointer, "value");

        // Create the constant value
        // LLVMValueRef constantValue = LLVMConstInt(i32Type, 1337, 0);

        // Build the return instruction
        LLVMBuildRet(builder, value);

        // Verify the module
        BytePointer error = new BytePointer((Pointer) null);
        if (LLVMVerifyModule(module, LLVMPrintMessageAction, error) != 0) {
            System.err.println("Error: " + error.getString());
            LLVMDisposeMessage(error);
            return;
        }

        // Dump the module IR
        LLVMDumpModule(module);

        // Stage 5: Execute the code using MCJIT
        LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
        LLVMMCJITCompilerOptions options = new LLVMMCJITCompilerOptions();
        if (LLVMCreateMCJITCompilerForModule(engine, module, options, options.sizeof(), error) != 0) {
            System.err.println("Failed to create JIT compiler: " + error.getString());
            LLVMDisposeMessage(error);
            return;
        }

        LLVMGenericValueRef result = LLVMRunFunction(engine, function, /* argumentCount */ 0, new PointerPointer<LLVMGenericValueRef>());

        System.out.println();
        System.out.println("Result: " + LLVMGenericValueToInt(result, /* signExtend */ 0));

        // Dispose of the allocated resources
        LLVMDisposeBuilder(builder);
        LLVMDisposeModule(module);
        LLVMContextDispose(context);
    }
}
