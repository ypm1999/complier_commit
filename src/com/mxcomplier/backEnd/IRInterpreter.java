//FIXME CAN'T USE


//package com.mxcomplier.backEnd;
//
//import com.mxcomplier.Config;
//import com.mxcomplier.FrontEnd.IRBuilder;
//import com.mxcomplier.Ir.BasicBlockIR;
//import com.mxcomplier.Ir.FuncIR;
//import com.mxcomplier.Ir.Instructions.*;
//import com.mxcomplier.Ir.Operands.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Scanner;
//
//import static com.mxcomplier.Ir.Operands.VirtualRegisterIR.getVregId;
//
//public class IRInterpreter {
//    private static int MemorySize = (1 << 20) * 256;
//    private static int pt = 0;
//    private FuncIR main, init;
//    private HashMap<StaticDataIR, Integer> staticDataMap = new HashMap<>();
//    private long[] regSet;
//    private char[] memory;
//    private Scanner sc = new Scanner(System.in);
//
//    public IRInterpreter(IRBuilder ir) {
//        this.main = ir.funcMap.get("main");
//        this.init = ir.funcMap.get("__init");
//        regSet = new long[getVregId()];
//        memory = new char[MemorySize];
//        for (StaticDataIR data : ir.root.getStaticData()) {
//            int addr = (int) malloc(data.getSize());
//            staticDataMap.put(data, addr);
//            if (data.getConstString() != null)
//                writeString(addr, data.getConstString());
//        }
//    }
//
//    public void run() {
////        runFunc(init, new ArrayList<>());
//        System.err.println("main return Value:" + runFunc(main, new ArrayList<>()));
//    }
//
//    private long runFunc(FuncIR func, List<OperandIR> args) {
//        if (func.getType() == FuncIR.Type.LIBRARY)
//            return runLibFunc(func, args);
//
//        for (int i = 0; i < args.size(); i++) {
//            runInst(new MoveInstIR(func.getParameters().get(i), args.get(i)));
//        }
//
//        BasicBlockIR bb = func.entryBB;
//        while (bb != null) {
////            System.err.println(bb.toString());
//            InstIR inst = bb.getHead().next;
//            BasicBlockIR nextBB;
//            while (inst != bb.getTail()) {
//                if (inst instanceof ReturnInstIR) {
////                    System.err.println(func.getName() + " return Value:" + getValue(((ReturnInstIR) inst).getSrc()));
//                    return getValue(((ReturnInstIR) inst).getSrc());
//                }
//                nextBB = runInst(inst);
//                if (nextBB != null) {
//                    bb = nextBB;
//                    break;
//                }
//                inst = inst.next;
//            }
//        }
//        assert false;
//        return 0;
//    }
//
//    private long runLibFunc(FuncIR func, List<OperandIR> args) {
//        switch (func.getName()) {
//            case "print":
//                System.out.print(getString(args.get(0)));
//                break;
//            case "println":
//                System.out.println(getString(args.get(0)));
//                break;
//            case "getString": {
//                String str = sc.next();
//                long res = malloc(str.length() + Config.getREGSIZE() + 1);
//                writeString((int) res, str);
//                return res;
//            }
//            case "getInt": {
//                return sc.nextInt();
//            }
//            case "toString": {
//                String str = String.valueOf(getValue(args.get(0)));
//                long res = malloc(str.length() + Config.getREGSIZE() + 1);
//                writeString((int) res, str);
//                return res;
//            }
//            case "_string_length":
//                return memoryGetInt((int) getValue(args.get(0)));
//            case "_string_parseInt":
//                return Integer.parseInt(getString(args.get(0)));
//            case "_string_ord":
//                return getString(args.get(0)).charAt((int) getValue(args.get(1)));
//            case "_string_substring": {
//                String str = getString(args.get(0)).substring((int) getValue(args.get(1)), (int) getValue(args.get(2)));
//                long res = malloc(str.length() + Config.getREGSIZE() + 1);
//                writeString((int) res, str);
//                return res;
//            }
//            case "___array_size":
//                return memoryGetInt((int) getValue(args.get(0)) - 8);
//            case "malloc":
//                return malloc(getValue(args.get(0)));
//            case "__stradd": {
//                String str = getString(args.get(0)) + getString(args.get(1));
//                long res = malloc(str.length() + Config.getREGSIZE() + 1);
//                writeString((int) res, str);
//                return res;
//            }
//            case "__strcmp": {
//                String str1 = getString(args.get(0));
//                String str2 = getString(args.get(1));
//                int res = str1.compareTo(str2);
//                return Integer.compare(res, 0);
//            }
//            default:
//                throw new Error();
//        }
//        return 0;
//    }
//
//    private BasicBlockIR runInst(InstIR instruction) {
////        System.err.println(instruction.toString());
//        switch (instruction.getClass().getName().substring(31)) {
//            case "BinaryInstIR": {
//                BinaryInstIR inst = (BinaryInstIR) instruction;
//                long lhs = getValue(inst.getDest());
//                long rhs = getValue(inst.getSrc());
//                switch (inst.getOp()) {
//                    case ADD:
//                        write(inst.getDest(), lhs + rhs);
//                        break;
//                    case SUB:
//                        write(inst.getDest(), lhs - rhs);
//                        break;
//                    case MUL:
//                        write(inst.getDest(), lhs * rhs);
//                        break;
//                    case DIV:
//                        write(inst.getDest(), lhs / rhs);
//                        break;
//                    case MOD:
//                        write(inst.getDest(), lhs % rhs);
//                        break;
//                    case SHL:
//                        write(inst.getDest(), lhs << rhs);
//                        break;
//                    case SHR:
//                        write(inst.getDest(), lhs >> rhs);
//                        break;
//                    case AND:
//                        write(inst.getDest(), lhs & rhs);
//                        break;
//                    case OR:
//                        write(inst.getDest(), lhs | rhs);
//                        break;
//                    case XOR:
//                        write(inst.getDest(), lhs ^ rhs);
//                        break;
//                    default:
//                        assert false;
//                }
//                break;
//            }
//            case "UnaryInstIR": {
//                UnaryInstIR inst = (UnaryInstIR) instruction;
//                long dest = getValue(inst.getDest());
//                switch (inst.getOp()) {
//                    case NEG:
//                        write(inst.getDest(), -dest);
//                        break;
//                    case INV:
//                        write(inst.getDest(), ~dest);
//                        break;
//                    case INC:
//                        write(inst.getDest(), dest + 1);
//                        break;
//                    case DEC:
//                        write(inst.getDest(), dest - 1);
//                        break;
//                    default:
//                        assert false;
//                }
//                break;
//            }
//            case "MoveInstIR": {
//                MoveInstIR inst = (MoveInstIR) instruction;
//                write(inst.getDest(), getValue(inst.getSrc()));
//                break;
//            }
//            case "LeaInstIR": {
//                LeaInstIR inst = (LeaInstIR) instruction;
//                write(inst.getDest(), getMemaddr((MemoryIR) inst.getSrc()));
//                break;
//            }
////            case "PopInstIR":{
////
////            }
////            case "PushInstIR":{
////
////            }
//            case "JumpInstIR": {
//                return ((JumpInstIR) instruction).getTarget();
//
//            }
//            case "CJumpInstIR": {
//                CJumpInstIR inst = (CJumpInstIR) instruction;
//                long lhs = getValue(inst.getLhs());
//                long rhs = getValue(inst.getRhs());
//                boolean result = false;
//                switch (inst.getOp()) {
//                    case L:
//                        result = lhs < rhs;
//                        break;
//                    case G:
//                        result = lhs > rhs;
//                        break;
//                    case LE:
//                        result = lhs <= rhs;
//                        break;
//                    case GE:
//                        result = lhs >= rhs;
//                        break;
//                    case E:
//                        result = lhs == rhs;
//                        break;
//                    case NE:
//                        result = lhs != rhs;
//                        break;
//                    default:
//                        assert false;
//                }
//                if (result)
//                    return inst.getTrueBB();
//                else
//                    return inst.getFalseBB();
//            }
//            case "CallInstIR": {
//                CallInstIR inst = (CallInstIR) instruction;
//                long ret = runFunc(inst.getFunc(), inst.getArgs());
//                write(inst.getReturnValue(), ret);
////                System.err.println("run " + inst.getFunc().getName() + " return :" + ret);
//                break;
//            }
//
//            default:
//                assert false;
//        }
//        return null;
//    }
//
//    private int getMemaddr(MemoryIR mem) {
//        if (mem.getConstant() != null) {
//            return staticDataMap.getOrDefault(mem.getConstant(), -1);
//        }
//        VirtualRegisterIR base = (VirtualRegisterIR) mem.getBase();
//        VirtualRegisterIR offset = (VirtualRegisterIR) mem.getOffset();
//        int addrOffset = (int) regSet[base.getId()] + mem.getNum();
//        if (offset != null)
//            addrOffset += (int) regSet[offset.getId()] * mem.getScale();
//        return addrOffset;
//    }
//
//    private void write(AddressIR dest, long src) {
//        if (dest instanceof MemoryIR) {
//            memoryWriteInt(getMemaddr((MemoryIR) dest), src);
//        } else if (dest instanceof VirtualRegisterIR) {
//            regSet[((VirtualRegisterIR) dest).getId()] = src;
//        } else
//            assert false;
//    }
//
//    private long getValue(OperandIR src) {
//        if (src == null)
//            return 0;
//        if (src instanceof ImmediateIR) {
//            return ((ImmediateIR) src).getValue();
//
//        } else if (src instanceof VirtualRegisterIR) {
//            return regSet[((VirtualRegisterIR) src).getId()];
//        } else if (src instanceof MemoryIR) {
//            if (((MemoryIR) src).getConstant() != null) {
//                return staticDataMap.get((StaticDataIR) ((MemoryIR) src).getConstant());
//            } else
//                return memoryGetInt(getMemaddr((MemoryIR) src));
//        } else
//            assert false;
//        return 0;
//    }
//
//    private void writeString(int dest, String str) {
//        memoryWriteInt(dest, str.length());
//        dest += Config.getREGSIZE();
//        for (int i = 0; i < str.length(); i++) {
//            memory[dest++] = str.charAt(i);
//        }
//        memory[dest] = '\0';
//    }
//
//    private String getString(OperandIR src) {
//        int addr = (int) getValue(src);
//        long length = memoryGetInt(addr);
//        addr += 8;
//        StringBuilder str = new StringBuilder();
//        while (length-- > 0) {
//            str.append(memory[addr++]);
//        }
//        return str.toString();
//    }
//
//    private long memoryGetInt(int addr) {
//        long res = 0;
//        for (int i = 0; i < Config.getREGSIZE(); i++) {
//            res |= ((long) memory[addr++]) << (i * 8);
//        }
//        return res;
//    }
//
//    private void memoryWriteInt(int addr, long src) {
//        for (int i = 0; i < Config.getREGSIZE(); i++, src >>= 8)
//            memory[addr++] = (char) src;
//    }
//
//    private long malloc(long size) {
//        pt += size;
//        assert pt < MemorySize;
//        return pt - size;
//    }
//}
