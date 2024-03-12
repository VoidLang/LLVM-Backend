package memory;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import org.voidlang.llvm.element.IRString;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMModuleCreateWithNameInContext;

public class MemCopy {
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
        LLVMBuilderRef builder = LLVMCreateBuilderInContext(context);

        LLVMTypeRef i1Type = LLVMInt1TypeInContext(context);
        LLVMTypeRef i8Type = LLVMInt8TypeInContext(context);
        LLVMTypeRef i32Type = LLVMInt32TypeInContext(context);
        LLVMTypeRef i64Type = LLVMInt64TypeInContext(context);

        /*
        LLVMTypeRef[] memCopyParams = new LLVMTypeRef[] {
            LLVMPointerType(i8Type, 0),
            LLVMPointerType(i8Type, 0),
            i64Type,
            i32Type,
            i1Type
        };
        LLVMTypeRef memCopyReturn = LLVMVoidTypeInContext(context);

        LLVMTypeRef memCopyType = LLVMFunctionType(memCopyReturn, new PointerPointer<>(memCopyParams), memCopyParams.length, 0);
        LLVMValueRef memCopy = LLVMAddFunction(module, "llvm.memcpy.p0i8.p0i8.i64", memCopyType);
         */

        LLVMTypeRef mainType = LLVMFunctionType(i32Type, new PointerPointer<>(), 0, 0);
        LLVMValueRef mainFunction = LLVMAddFunction(module, "main", mainType);
        LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlockInContext(context, mainFunction, "entry");
        LLVMPositionBuilderAtEnd(builder, mainEntry);

        String fooMessage = "abcdef";
        LLVMValueRef foo = LLVMConstStringInContext(context, fooMessage, fooMessage.length(), 1);
        LLVMTypeRef fooType = LLVMArrayType(i8Type, fooMessage.length());

        String barMessage = "123456";
        LLVMValueRef bar = LLVMConstStringInContext(context, barMessage, barMessage.length(), 1);
        LLVMTypeRef barType = LLVMArrayType(i8Type, barMessage.length());

        LLVMValueRef constFoo = LLVMAddGlobal(module, fooType, "foo");
        LLVMSetInitializer(constFoo, foo);

        LLVMValueRef constBar = LLVMAddGlobal(module, barType, "bar");
        LLVMSetInitializer(constBar, bar);

        LLVMTypeRef bufferType = LLVMArrayType(i8Type, 6);
        LLVMValueRef buffer = LLVMBuildAlloca(builder, bufferType, "buffer");

        LLVMBuildMemCpy(
            builder,
            buffer,
            1,
            LLVMGetNamedGlobal(module, "foo"),
            1,
            LLVMConstInt(i32Type, 1, 0)
        );

        /*
        LLVMValueRef[] copyArgs = new LLVMValueRef[] {
            buffer,
            buffer,
            LLVMConstInt(i64Type, 6, 0),
            LLVMConstInt(i32Type, 1, 0),
            LLVMConstInt(i1Type, 0, 0)
        };
        LLVMBuildCall2(builder, memCopyType, memCopy, new PointerPointer<>(copyArgs), copyArgs.length, "");
         */

        LLVMBuildRet(builder, LLVMConstInt(i32Type, 123, 0));

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

        LLVMGenericValueRef result = LLVMRunFunction(engine, mainFunction, 0, new PointerPointer<LLVMGenericValueRef>());

        System.out.println();
        System.out.println("Result: " + LLVMGenericValueToInt(result, 0));

        // Dispose of the allocated resources
        LLVMDisposeBuilder(builder);
        LLVMDisposeModule(module);
        LLVMContextDispose(context);
    }
}
