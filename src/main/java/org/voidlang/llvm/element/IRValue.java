package org.voidlang.llvm.element;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
import static org.bytedeco.llvm.global.LLVM.*;

public class IRValue {
    private final LLVMValueRef handle;

    IRValue(LLVMValueRef handle) {
        this.handle = handle;
    }

    public LLVMValueRef getHandle() {
        return handle;
    }

    public IRType typeOf(IRContext context) {
        return new IRType(LLVMTypeOf(handle), context);
    }

    public IRType typeOf() {
        return typeOf(IRContext.global());
    }

    public int getAlignment() {
        return LLVMGetAlignment(handle);
    }
}
