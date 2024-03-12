package org.voidlang.llvm.element;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.LLVMBuilderRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;

/**
 * Represents a handle to an LLVM IR builder. It is a pointer to an opaque structure that represents the builder
 * object in the LLVM API.
 * <br>
 * The LLVM IR builder is used to construct LLVM intermediate representation (IR) code. It provides a convenient
 * and structured way to create and insert instructions into basic blocks within an LLVM function.
 * <br>
 * This class type is typically used as a parameter type in various LLVM builder-related functions,
 * allowing you to pass and manipulate the builder object.
 */

public class IRBuilder implements Disposable {
    private final LLVMBuilderRef handle;
    private final IRContext context;

    IRBuilder(LLVMBuilderRef handle, IRContext context) {
        this.handle = handle;
        this.context = context;
    }

    /**
     * Position the LLVM IR builder at the end of a basic block. It is used to specify the insertion point for
     * new instructions within a basic block.
     * <br>
     * This function is used to set the insertion point for new instructions within a basic block.
     * By specifying the basic block using Block, the builder will be positioned at the end of that basic block,
     * ready to insert new instructions.
     * <br>
     * Once the builder is positioned at the desired basic block, you can use the functions of this class to create
     * and insert instructions at that location.
     *
     * @param block the LLVM basic block where the builder should be positioned.
     */
    public void positionAtEnd(IRBlock block) {
        LLVMPositionBuilderAtEnd(handle, block.getHandle());
    }

    /**
     * Position the builder before a specific instruction within a basic block-
     * <br>
     * This function sets the insertion point of the builder before the specified instruction,
     * allowing you to insert new instructions before it.
     * <br>
     * Once the builder is positioned before the desired instruction, you can use the functions of this class to
     * create and insert instructions at that location.
     *
     * @param value the LLVM instruction before which the builder should be positioned.
     */
    public void positionBefore(IRValue value) {
        LLVMPositionBuilderBefore(handle, value.getHandle());
    }

    /**
     * Create a return instruction. It is used to define the return value of a function and terminate its execution.
     * <br>
     * This function is used to create a return instruction within the body of a function. It specifies the value to
     * be returned by the function. The type of the return value should match the return type of the function.
     * <br>
     * When the execution of the function reaches the return instruction, it will terminate and return the specified
     * value. If the function has a void return type (i.e., no return value), you can pass NULL or a value of type void.
     *
     * @param value the value to be returned by the function.
     * @return an IRValue that represents the return instruction.
     */
    public IRValue returnValue(IRValue value) {
        return new IRValue(LLVMBuildRet(handle, value.getHandle()));
    }

    /**
     * Create a return instruction with no return value (void return type).
     * It is used to terminate the execution of a function that does not have a return value.
     * <br>
     * This function is used when you want to create a return instruction that does not have a return value.
     * It is typically used for functions with a void return type. When the execution of the function reaches
     * the return instruction, it will terminate without returning a value.
     * <br>
     * Note that in LLVM, void return type is represented as the absence of a value, so this does
     * not require a separate value argument.
     *
     * @return an IRValue that represents the return instruction.
     */
    public IRValue returnVoid() {
        return new IRValue(LLVMBuildRetVoid(handle));
    }

    /**
     * Perform an addition operation. It is used for integer addition with optional overflow checking.
     * <br>
     * This function is used to create an instruction that performs integer addition. It can be used for both
     * signed and unsigned integer addition. The behavior of the addition operation depends on the types of the
     * operands and the specific rules of the programming language or context in which LLVM is being used.
     * <br>
     * If you want to perform addition with specific overflow behavior, LLVM provides additional functions such as
     * {@link #addNoUnsignedWrap(IRValue, IRValue, String)} and {@link #addNoSignedWrap(IRValue, IRValue, String)}
     * for controlling overflow checking during addition.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     * @param name an optional name for the instruction (can be set to "" if not needed).
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue add(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildAdd(handle, left.getHandle(), right.getHandle(), name));
    }

    /**
     * Perform an addition operation. It is used for integer addition with optional overflow checking.
     * <br>
     * This function is used to create an instruction that performs integer addition. It can be used for both
     * signed and unsigned integer addition. The behavior of the addition operation depends on the types of the
     * operands and the specific rules of the programming language or context in which LLVM is being used.
     * <br>
     * If you want to perform addition with specific overflow behavior, LLVM provides additional functions such as
     * {@link #addNoUnsignedWrap(IRValue, IRValue)} and {@link #addNoSignedWrap(IRValue, IRValue)}
     * for controlling overflow checking during addition.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue add(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildAdd(handle, left.getHandle(), right.getHandle(), ""));
    }

    /**
     * Perform a floating-point addition operation. It is used to generate LLVM IR code for adding
     * two floating-point values.
     * <br>
     * Note that the LLVMBuildFAdd function is specific to floating-point addition.
     * For integer addition, you would use {@link #add(IRValue, IRValue, String)} instead.
     *
     * @param left the left-hand side floating-point value to be added.
     * @param right the right-hand side floating-point value to be added.
     * @param name an optional name for the instruction (can be set to "" if not needed).
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addFloat(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFAdd(handle, left.getHandle(), right.getHandle(), name));
    }

    /**
     * Perform a floating-point addition operation. It is used to generate LLVM IR code for adding
     * two floating-point values.
     * <br>
     * Note that the LLVMBuildFAdd function is specific to floating-point addition.
     * For integer addition, you would use {@link #add(IRValue, IRValue)} instead.
     *
     * @param left the left-hand side floating-point value to be added.
     * @param right the right-hand side floating-point value to be added.
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addFloat(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildFAdd(handle, left.getHandle(), right.getHandle(), ""));
    }

    /**
     * Perform a signed integer addition with no signed overflow checking.
     * <br>
     * This function is used when you want to perform signed integer addition and explicitly disable
     * signed overflow checking. This means that if the addition operation results in a signed overflow
     * (e.g., adding two positive numbers and the result exceeds the maximum value that can be represented),
     * the behavior is undefined.
     * <br>
     * Note that there are similar functions for other types of overflow checking, such as addNoUnsignedWarp
     * (unsigned integer addition with no unsigned overflow checking) and LLVMBuildAdd for general addition
     * without overflow checking.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     * @param name an optional name for the instruction (can be set to "" if not needed).
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addNoSignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNSWAdd(handle, left.getHandle(), right.getHandle(), name));
    }

    /**
     * Perform a signed integer addition with no signed overflow checking.
     * <br>
     * This function is used when you want to perform signed integer addition and explicitly disable
     * signed overflow checking. This means that if the addition operation results in a signed overflow
     * (e.g., adding two positive numbers and the result exceeds the maximum value that can be represented),
     * the behavior is undefined.
     * <br>
     * Note that there are similar functions for other types of overflow checking, such as addNoUnsignedWarp
     * (unsigned integer addition with no unsigned overflow checking) and LLVMBuildAdd for general addition
     * without overflow checking.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addNoSignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNSWAdd(handle, left.getHandle(), right.getHandle(), ""));
    }

    /**
     * Perform an unsigned integer addition with no unsigned overflow checking.
     * <br>
     * This function is used when you want to perform unsigned integer addition and explicitly disable
     * unsigned overflow checking. This means that if the addition operation results in an unsigned overflow
     * (e.g., adding two positive numbers and the result exceeds the maximum value that can be represented),
     * the behavior is undefined.
     * <br>
     * Note that there are similar functions for other types of overflow checking, such as addNoSignedWrap
     * (signed integer addition with no signed overflow checking) and LLVMBuildAdd for general addition without
     * overflow checking.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     * @param name an optional name for the instruction (can be set to "" if not needed).
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addNoUnsignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNUWAdd(handle, left.getHandle(), right.getHandle(), name));
    }

    /**
     * Perform an unsigned integer addition with no unsigned overflow checking.
     * <br>
     * This function is used when you want to perform unsigned integer addition and explicitly disable
     * unsigned overflow checking. This means that if the addition operation results in an unsigned overflow
     * (e.g., adding two positive numbers and the result exceeds the maximum value that can be represented),
     * the behavior is undefined.
     * <br>
     * Note that there are similar functions for other types of overflow checking, such as addNoSignedWrap
     * (signed integer addition with no signed overflow checking) and LLVMBuildAdd for general addition without
     * overflow checking.
     *
     * @param left the left-hand side integer value to be added.
     * @param right the right-hand side integer value to be added.
     *
     * @return an IRValue that represents the result of the addition operation.
     */
    public IRValue addNoUnsignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNUWAdd(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue subtract(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildSub(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue subtract(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildSub(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue subtractFloat(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFSub(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue subtractFloat(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildFSub(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue subtractNoSignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNSWSub(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue subtractNoSignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNSWSub(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue subtractNoUnsignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNUWSub(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue subtractNoUnsignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNUWSub(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue multiply(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildMul(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue multiply(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildMul(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue multiplyFloat(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFMul(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue multiplyFloat(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildFMul(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue multiplyNoSignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNSWMul(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue multiplyNoSignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNSWMul(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue multiplyNoUnsignedWrap(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildNUWMul(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue multiplyNoUnsignedWrap(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildNUWMul(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue divideFloat(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFDiv(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue divideFloat(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildFDiv(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue divideSigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildSDiv(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue divideSigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildSDiv(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue divideUnsigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildUDiv(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue divideUnsigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildUDiv(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue divideExactSigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildExactSDiv(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue divideExactSigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildExactSDiv(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue divideExactUnsigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildExactUDiv(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue divideExactUnsigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildExactUDiv(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue remainderFloat(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFRem(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue remainderFloat(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildFRem(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue remainderSigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildSRem(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue remainderSigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildSRem(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue remainderUnsigned(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildURem(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue remainderUnsigned(IRValue left, IRValue right) {
        return new IRValue(LLVMBuildURem(handle, left.getHandle(), right.getHandle(), ""));
    }

    public IRValue call(IRType type, IRValue function, List<IRValue> arguments, String name) {
        PointerPointer<Pointer> args = new PointerPointer<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            args.put(i, arguments.get(i).getHandle());
        }
        LLVMValueRef res = LLVMBuildCall2(handle, type.getHandle(), function.getHandle(), args, arguments.size(), name);
        return new IRValue(res);
    }

    public IRValue call(IRFunction function, List<IRValue> arguments, String name) {
        PointerPointer<Pointer> args = new PointerPointer<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            args.put(i, arguments.get(i).getHandle());
        }
        LLVMValueRef res = LLVMBuildCall2(handle, function.getType().getHandle(), function.getHandle(), args, arguments.size(), name);
        return new IRValue(res);
    }

    public IRValue call(IRFunction function, List<IRValue> arguments) {
        return call(function, arguments, "");
    }

    public IRValue alloc(IRType type, String name) {
        return new IRValue(LLVMBuildAlloca(handle, type.getHandle(), name));
    }

    public IRValue alloc(IRType type) {
        return alloc(type, "");
    }

    public IRValue malloc(IRType type, String name) {
        return new IRValue(LLVMBuildMalloc(handle, type.getHandle(), name));
    }

    public IRValue malloc(IRType type) {
        return malloc(type, "");
    }

    public IRValue free(IRValue pointer) {
        return new IRValue(LLVMBuildFree(handle, pointer.getHandle()));
    }

    public IRValue memCpy(IRValue dest, int destAlign, IRValue src, int srcAlign, IRValue size) {
        return new IRValue(LLVMBuildMemCpy(handle, dest.getHandle(), destAlign, src.getHandle(), srcAlign, size.getHandle()));
    }

    public IRValue memMove(IRValue dest, int destAlign, IRValue src, int srcAlign, IRValue size) {
        return new IRValue(LLVMBuildMemMove(handle, dest.getHandle(), destAlign, src.getHandle(), srcAlign, size.getHandle()));
    }

    public IRValue memSet(IRValue dest, IRValue value, IRValue size, int destAlign) {
        return new IRValue(LLVMBuildMemSet(handle, dest.getHandle(), value.getHandle(), size.getHandle(), destAlign));
    }

    public IRValue structMemberPointer(IRType type, IRValue instance, int memberIndex, String name) {
        return new IRValue(LLVMBuildStructGEP2(handle, type.getHandle(), instance.getHandle(), memberIndex, name));
    }

    public IRValue structMemberPointer(IRType type, IRValue instance, int memberIndex) {
        return structMemberPointer(type, instance, memberIndex, "");
    }

    public IRValue elementPointer(IRType type, IRValue instance, List<IRValue> indices, String name) {
        PointerPointer<Pointer> args = new PointerPointer<>(indices.size());
        for (int i = 0; i < indices.size(); i++)
            args.put(i, indices.get(i).getHandle());

        return new IRValue(LLVMBuildGEP2(handle, type.getHandle(), instance.getHandle(), args, indices.size(), name));
    }

    public IRValue elementPointer(IRType type, IRValue instance, List<IRValue> indices) {
        return elementPointer(type, instance, indices, "");
    }

    public IRValue store(IRValue value, IRValue pointer) {
        return new IRValue(LLVMBuildStore(handle, value.getHandle(), pointer.getHandle()));
    }

    public IRValue load(IRType type, IRValue pointer, String name) {
        return new IRValue(LLVMBuildLoad2(handle, type.getHandle(), pointer.getHandle(), name));
    }

    public IRValue insert(IRValue instance, IRValue value, int memberIndex, String name) {
        return new IRValue(LLVMBuildInsertValue(handle, instance.getHandle(), value.getHandle(), memberIndex, name));
    }

    public IRValue extract(IRValue instance, int memberIndex, String name) {
        return new IRValue(LLVMBuildExtractValue(handle, instance.getHandle(), memberIndex, name));
    }

    public IRValue compareInt(Comparator comparator, IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildICmp(handle, comparator.getCode(), left.getHandle(), right.getHandle(), name));
    }

    public IRValue compareInt(Comparator comparator, IRValue left, IRValue right) {
        return compareInt(comparator, left, right, "");
    }

    public IRValue compareFloat(Comparator comparator, IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildFCmp(handle, comparator.getCode(), left.getHandle(), right.getHandle(), name));
    }

    public IRValue compareFloat(Comparator comparator, IRValue left, IRValue right) {
        return compareFloat(comparator, left, right, "");
    }

    public IRValue signedRemainder(IRValue operand, IRValue divisor, String name) {
        return new IRValue(LLVMBuildSRem(handle, operand.getHandle(), divisor.getHandle(), name));
    }

    public IRValue signedRemainder(IRValue operand, IRValue divisor) {
        return signedRemainder(operand, divisor, "");
    }

    public IRValue unsignedRemainder(IRValue operand, IRValue divisor, String name) {
        return new IRValue(LLVMBuildURem(handle, operand.getHandle(), divisor.getHandle(), name));
    }

    public IRValue unsignedRemainder(IRValue operand, IRValue divisor) {
        return unsignedRemainder(operand, divisor, "");
    }

    public IRValue floatRemainder(IRValue operand, IRValue divisor, String name) {
        return new IRValue(LLVMBuildFRem(handle, operand.getHandle(), divisor.getHandle(), name));
    }

    public IRValue jump(IRBlock destination) {
        return new IRValue(LLVMBuildBr(handle, destination.getHandle()));
    }

    public IRValue jumpIf(IRValue condition, IRBlock ifBlock, IRBlock elseBlock) {
        return new IRValue(LLVMBuildCondBr(handle, condition.getHandle(), ifBlock.getHandle(), elseBlock.getHandle()));
    }

    public IRValue and(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildAnd(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue and(IRValue left, IRValue right) {
        return and(left, right, "");
    }

    public IRValue or(IRValue left, IRValue right, String name) {
        return new IRValue(LLVMBuildOr(handle, left.getHandle(), right.getHandle(), name));
    }

    public IRValue or(IRValue left, IRValue right) {
        return or(left, right, "");
    }

    public IRValue negate(IRValue operand, String name) {
        return new IRValue(LLVMBuildNeg(handle, operand.getHandle(), name));
    }

    public IRValue negate(IRValue operand) {
        return negate(operand, "");
    }

    public IRValue not(IRValue operand, String name) {
        return new IRValue(LLVMBuildNot(handle, operand.getHandle(), name));
    }

    public IRValue not(IRValue operand) {
        return not(operand, "");
    }

    public IRValue select(IRValue condition, IRValue ifCase, IRValue elseCase, String name) {
        return new IRValue(LLVMBuildSelect(handle, condition.getHandle(), ifCase.getHandle(), elseCase.getHandle(), name));
    }

    public IRValue select(IRValue condition, IRValue ifCase, IRValue elseCase) {
        return select(condition, ifCase, elseCase, "");
    }

    public IRValue signedIntToFloat(IRValue value, IRType floatType, String name) {
        return new IRValue(LLVMBuildSIToFP(handle, value.getHandle(), floatType.getHandle(), name));
    }

    public IRValue signedIntToFloat(IRValue value, IRType floatType) {
        return signedIntToFloat(value, floatType, "");
    }

    public IRValue unsignedIntToFloat(IRValue value, IRType floatType, String name) {
        return new IRValue(LLVMBuildUIToFP(handle, value.getHandle(), floatType.getHandle(), name));
    }

    public IRValue unsignedIntToFloat(IRValue value, IRType floatType) {
        return unsignedIntToFloat(value, floatType, "");
    }

    public IRValue floatToSignedInt(IRValue value, IRType intType, String name) {
        return new IRValue(LLVMBuildFPToSI(handle, value.getHandle(), intType.getHandle(), name));
    }

    public IRValue floatToSignedInt(IRValue value, IRType intType) {
        return floatToSignedInt(value, intType, "");
    }

    public IRValue floatToUnsignedInt(IRValue value, IRType intType, String name) {
        return new IRValue(LLVMBuildFPToUI(handle, value.getHandle(), intType.getHandle(), name));
    }

    public IRValue floatToUnsignedInt(IRValue value, IRType intType) {
        return floatToUnsignedInt(value, intType, "");
    }

    public IRValue truncate(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildTrunc(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue truncate(IRValue value, IRType type) {
        return truncate(value, type, "");
    }

    public IRValue zeroExtend(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildZExt(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue zeroExtend(IRValue value, IRType type) {
        return zeroExtend(value, type, "");
    }

    public IRValue signExtend(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildSExt(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue signExtend(IRValue value, IRType type) {
        return signExtend(value, type, "");
    }

    public IRValue floatExtend(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildFPExt(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue floatExtend(IRValue value, IRType type) {
        return floatExtend(value, type, "");
    }

    public IRValue floatTruncate(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildFPTrunc(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue floatTruncate(IRValue value, IRType type) {
        return floatTruncate(value, type, "");
    }

    public IRValue pointerCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildPointerCast(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue pointerCast(IRValue value, IRType type) {
        return pointerCast(value, type, "");
    }

    public IRValue intCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildIntCast(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue intCast(IRValue value, IRType type) {
        return intCast(value, type, "");
    }

    public IRValue intCast(IRValue value, IRType type, boolean isSigned, String name) {
        return new IRValue(LLVMBuildIntCast2(handle, value.getHandle(), type.getHandle(), isSigned ? 1 : 0, name));
    }

    public IRValue intCast(IRValue value, IRType type, boolean isSigned) {
        return intCast(value, type, isSigned, "");
    }

    public IRValue floatCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildFPCast(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue floatCast(IRValue value, IRType type) {
        return floatCast(value, type, "");
    }

    public IRValue bitCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildBitCast(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue bitCast(IRValue value, IRType type) {
        return bitCast(value, type, "");
    }

    public IRValue intToPointerCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildIntToPtr(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue intToPointerCast(IRValue value, IRType type) {
        return intToPointerCast(value, type, "");
    }

    public IRValue pointerToIntCast(IRValue value, IRType type, String name) {
        return new IRValue(LLVMBuildPtrToInt(handle, value.getHandle(), type.getHandle(), name));
    }

    public IRValue pointerToIntCast(IRValue value, IRType type) {
        return pointerToIntCast(value, type, "");
    }

    @Override
    public void dispose() {
        LLVMDisposeBuilder(handle);
    }

    public LLVMBuilderRef getHandle() {
        return handle;
    }

    public IRContext getContext() {
        return context;
    }

    public static IRBuilder create(IRContext context) {
        return new IRBuilder(LLVMCreateBuilderInContext(context.getHandle()), context);
    }
}
