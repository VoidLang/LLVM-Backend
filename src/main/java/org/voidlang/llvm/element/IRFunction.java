package org.voidlang.llvm.element;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import static org.bytedeco.llvm.global.LLVM.*;

public class IRFunction extends IRValue {
    private final IRModule module;
    private final String name;
    private final IRFunctionType type;

    IRFunction(LLVMValueRef handle, IRModule module, String name, IRFunctionType type) {
        super(handle);
        this.module = module;
        this.name = name;
        this.type = type;
    }

    public IRValue getParameter(int index) {
        return new IRValue(LLVMGetParam(getHandle(), index));
    }

    public IRModule getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public IRFunctionType getType() {
        return type;
    }

    public static IRFunction create(IRModule module, String name, IRFunctionType type) {
        return new IRFunction(LLVMAddFunction(module.getHandle(), name, type.getHandle()), module, name, type);
    }

    public static IRFunction getByName(IRModule module, String name, IRFunctionType type) {
        LLVMValueRef handle = LLVMGetNamedFunction(module.getHandle(), name);
        if (handle == null)
            return null;

        return new IRFunction(handle, module, name, type);
    }
}
