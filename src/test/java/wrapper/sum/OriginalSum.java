package wrapper.sum;

import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class OriginalSum {
    public static void main(String[] args) {
        // Initialize LLVM components
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();

        // Create the LLVM context and module
        LLVMContextRef context = LLVMContextCreate();
        LLVMModuleRef module = LLVMModuleCreateWithNameInContext("sum_module", context);

        // Define the function type
        LLVMTypeRef[] paramTypes = new LLVMTypeRef[] { LLVMInt32TypeInContext(context), LLVMInt32TypeInContext(context) };
        LLVMTypeRef returnType = LLVMInt32TypeInContext(context);
        LLVMTypeRef functionType = LLVMFunctionType(returnType, new PointerPointer<>(paramTypes), paramTypes.length, 0);

        // Add the sum function to the module
        LLVMValueRef sumFunction = LLVMAddFunction(module, "sum", functionType);

        // Create the entry basic block
        LLVMBasicBlockRef entryBlock = LLVMAppendBasicBlockInContext(context, sumFunction, "entry");
        LLVMBuilderRef builder = LLVMCreateBuilderInContext(context);
        LLVMPositionBuilderAtEnd(builder, entryBlock);

        // Get the function arguments
        LLVMValueRef arg1 = LLVMGetParam(sumFunction, 0);
        LLVMValueRef arg2 = LLVMGetParam(sumFunction, 1);

        // Perform the sum operation
        LLVMValueRef result = LLVMBuildAdd(builder, arg1, arg2, "result");

        // Return the result
        LLVMBuildRet(builder, result);



        LLVMTypeRef mainType = LLVMFunctionType(returnType, new PointerPointer<>(), /* argumentCount */ 0, /* isVariadic */ 0);
        LLVMValueRef mainFunction = LLVMAddFunction(module, "main", mainType);
        LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlockInContext(context, mainFunction, "entry");
        LLVMPositionBuilderAtEnd(builder, mainEntry);

        PointerPointer<Pointer> mArgs = new PointerPointer<>(2)
            .put(0, LLVMConstInt(returnType, 10, 0))
            .put(1, LLVMConstInt(returnType, 20, 0));

        LLVMValueRef res = LLVMBuildCall2(builder, functionType, sumFunction, mArgs, 2, "call_res");
        LLVMBuildRet(builder, res);



        // Stage 3: Verify the module using LLVMVerifier
        BytePointer error = new BytePointer();
        if (LLVMVerifyModule(module, LLVMPrintMessageAction, error) != 0) {
            LLVMDisposeMessage(error);
            return;
        }

        // Stage 4: Create a pass pipeline using the legacy pass manager
        LLVMPassManagerRef pm = LLVMCreatePassManager();
        // LLVMAddAggressiveInstCombinerPass(pm);
        LLVMAddNewGVNPass(pm);
        LLVMAddCFGSimplificationPass(pm);
        LLVMRunPassManager(pm, module);
        LLVMDumpModule(module);


        // Stage 5: Execute the code using MCJIT
        LLVMExecutionEngineRef engine = new LLVMExecutionEngineRef();
        LLVMMCJITCompilerOptions options = new LLVMMCJITCompilerOptions();
        if (LLVMCreateMCJITCompilerForModule(engine, module, options, 3, error) != 0) {
            System.err.println("Failed to create JIT compiler: " + error.getString());
            LLVMDisposeMessage(error);
            return;
        }

        LLVMGenericValueRef val = LLVMRunFunction(engine, mainFunction, /* argumentCount */ 0, new PointerPointer<Pointer>());
        System.out.println();
        System.out.println("; Running factorial(10) with MCJIT...");
        System.out.println("; Result: " + LLVMGenericValueToInt(val, /* signExtend */ 0));
    }
}
