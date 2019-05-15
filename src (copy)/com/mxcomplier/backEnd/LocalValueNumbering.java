package com.mxcomplier.backEnd;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.BasicBlockIR;
import com.mxcomplier.Ir.FuncIR;
import com.mxcomplier.Ir.Instructions.*;
import com.mxcomplier.Ir.Operands.ImmediateIR;
import com.mxcomplier.Ir.Operands.OperandIR;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;
import com.mxcomplier.Ir.ProgramIR;

import java.util.HashMap;
import java.util.HashSet;


public class LocalValueNumbering extends IRScanner {


    class Pair {
        BinaryInstIR.Op op;
        Integer lhs, rhs;

        Pair(BinaryInstIR.Op op, Integer lhs, Integer rhs) {
            this.op = op;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public int hashCode() {
            return op.ordinal() * 100000000 + lhs * 10000 + rhs;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair) {
                Pair other = (Pair) obj;
                return op == other.op && lhs.equals(other.lhs) && rhs.equals(other.rhs);

            }
            return false;
        }
    }

    private int valueCountor;
    private HashMap<VirtualRegisterIR, Integer> registerValueMap = new HashMap<>();
    private HashMap<Pair, Integer> pairValueMap = new HashMap<>();
    private HashMap<Integer, HashSet<VirtualRegisterIR>> valueRegisterMap = new HashMap<>();
    private HashMap<Integer, Integer> immValueMap = new HashMap<>();
    private HashMap<Integer, Integer> valueImmMap = new HashMap<>();
    private FuncIR curFunc = null;

    private Integer getOperandValue(OperandIR oper) {
        if (oper instanceof VirtualRegisterIR)
            return getRegValue((VirtualRegisterIR) oper);
        else if (oper instanceof ImmediateIR)
            return getImmValue((ImmediateIR) oper);
        else
            return ++valueCountor;
    }

    private OperandIR getValueOperand(Integer value) {
        if (valueImmMap.containsKey(value))
            return new ImmediateIR(valueImmMap.get(value));
        else if (valueRegisterMap.containsKey(value)) {
            HashSet<VirtualRegisterIR> vregSet = valueRegisterMap.get(value);
            if (vregSet.isEmpty())
                return null;
            else
                return vregSet.iterator().next();
        } else
            return null;
    }

    private Integer getImmValue(ImmediateIR imm) {
        Integer immInt = (int) imm.getValue();
        if (!immValueMap.containsKey(immInt)) {

            ++valueCountor;
            immValueMap.put(immInt, valueCountor);
            valueImmMap.put(valueCountor, immInt);
        }
        return immValueMap.get(immInt);
    }

    private Integer getRegValue(VirtualRegisterIR reg) {
        if (!registerValueMap.containsKey(reg)) {
            addRegisterValue(reg, ++valueCountor);
        }
        return registerValueMap.get(reg);
    }

    private void addRegisterValue(VirtualRegisterIR vreg, Integer val) {
        if (!valueRegisterMap.containsKey(val))
            valueRegisterMap.put(val, new HashSet<>());
        valueRegisterMap.get(val).add(vreg);
        registerValueMap.put(vreg, val);
    }

    private void deleteRegisterValue(VirtualRegisterIR vreg) {
        if (registerValueMap.containsKey(vreg)) {
            Integer value = registerValueMap.get(vreg);
            if (valueRegisterMap.containsKey(value))
                valueRegisterMap.get(value).remove(vreg);
            registerValueMap.remove(vreg);
        }
    }

    private void changeRegisterValue(VirtualRegisterIR vreg, Integer newVal) {
        deleteRegisterValue(vreg);
        addRegisterValue(vreg, newVal);
    }


    @Override
    public void visit(ProgramIR node) {
        for (FuncIR func : node.getFuncs())
            func.accept(this);
    }

    @Override
    public void visit(FuncIR node) {
        curFunc = node;
        for (BasicBlockIR bb : node.getBBList())
            bb.accept(this);
        curFunc = null;
    }

    @Override
    public void visit(BasicBlockIR node) {
        valueCountor = 0;
        registerValueMap.clear();
        pairValueMap.clear();
        valueRegisterMap.clear();
        immValueMap.clear();
        valueImmMap.clear();
        for (InstIR inst = node.getHead().next; inst != node.getTail(); ) {
            InstIR next = inst.next;
            inst.accept(this);
            inst = next;
        }
    }


    @Override
    public void visit(CallInstIR node) {
        changeRegisterValue((VirtualRegisterIR) node.getReturnValue(), ++valueCountor);
    }

    private Integer doUnary(UnaryInstIR.Op op, Integer imm) {
        switch (op) {
            case INV:
                return ~imm;
            case NEG:
                return -imm;
            case INC:
                return imm + 1;
            case DEC:
                return imm - 1;
            default:
                throw new IRError("Unary error in LVN");
        }
    }

    private Integer doBinary(BinaryInstIR.Op op, Integer limm, Integer rimm) {
        switch (op) {
            case SUB:
                return limm - rimm;
            case XOR:
                return limm ^ rimm;
            case MUL:
                return limm * rimm;
            case MOD:
                if (rimm == 0) rimm = 1;
                return limm % rimm;
            case DIV:
                if (rimm == 0) rimm = 1;
                return limm / rimm;
            case AND:
                return limm & rimm;
            case ADD:
                return limm + rimm;
            case OR:
                return limm | rimm;
            case SHL:
                return limm << rimm;
            case SHR:
                return limm >> rimm;
            default:
                throw new IRError("Binary error in LVN");
        }
    }

    @Override
    public void visit(UnaryInstIR node) {
        Integer value = getOperandValue(node.getDest());
        if (valueImmMap.containsKey(value)) {
            Integer imm = doUnary(node.getOp(), valueImmMap.get(value));
            value = immValueMap.containsKey(imm) ? immValueMap.get(imm) : ++valueCountor;
            node.append(new MoveInstIR(node.getDest(), new ImmediateIR(imm)));
            node.remove();
        } else
            value = ++valueCountor;

        if (node.getDest() instanceof VirtualRegisterIR)
            changeRegisterValue((VirtualRegisterIR) node.getDest(), value);
    }

    private boolean doComp(CJumpInstIR.Op op, long lhs, long rhs) {
        switch (op) {
            case L:
                return lhs < rhs;
            case G:
                return lhs > rhs;
            case LE:
                return lhs <= rhs;
            case GE:
                return lhs >= rhs;
            case E:
                return lhs == rhs;
            case NE:
                return lhs != rhs;
            case ERROR:
                throw new IRError("unknow Cmp");
        }
        return false;
    }

    @Override
    public void visit(CJumpInstIR node) {
        Integer lhs = getOperandValue(node.getLhs());
        Integer rhs = getOperandValue(node.getRhs());
        if (valueImmMap.containsKey(lhs))
            node.lhs = new ImmediateIR(valueImmMap.get(lhs));
        if (valueImmMap.containsKey(rhs))
            node.rhs = new ImmediateIR(valueImmMap.get(rhs));
        if (node.lhs instanceof ImmediateIR && node.rhs instanceof ImmediateIR) {
            boolean res = doComp(node.getOp(), ((ImmediateIR) node.lhs).getValue(), ((ImmediateIR) node.rhs).getValue());
            if (res)
                node.append(new JumpInstIR(node.getTrueBB()));
            else
                node.append(new JumpInstIR(node.getFalseBB()));
            node.remove();
        }
    }

    @Override
    public void visit(BinaryInstIR node) {
        Integer src = getOperandValue(node.getSrc());
        Integer dest = getOperandValue(node.getDest());
        Integer result;
        Pair binaryPair = new Pair(node.getOp(), src, dest);
        if (pairValueMap.containsKey(binaryPair)) {
            result = pairValueMap.get(binaryPair);
            OperandIR oper = getValueOperand(result);
            if (oper != null) {
                node.append(new MoveInstIR(node.dest, oper));
                node.remove();
            }
        } else {
            result = ++valueCountor;
            pairValueMap.put(binaryPair, result);
        }
        if (node.getDest() instanceof VirtualRegisterIR)
            changeRegisterValue((VirtualRegisterIR) node.getDest(), result);
    }

    @Override
    public void visit(MoveInstIR node) {
        Integer src = getOperandValue(node.getSrc());
        if (valueImmMap.containsKey(src))
            node.src = new ImmediateIR(valueImmMap.get(src));
        if (node.getDest() instanceof VirtualRegisterIR)
            changeRegisterValue((VirtualRegisterIR) node.getDest(), src);
    }

    @Override
    public void visit(PopInstIR node) {
        if (node.getDest() instanceof VirtualRegisterIR)
            changeRegisterValue((VirtualRegisterIR) node.getDest(), ++valueCountor);
    }

    @Override
    public void visit(LeaInstIR node) {
        if (node.getDest() instanceof VirtualRegisterIR)
            changeRegisterValue((VirtualRegisterIR) node.getDest(), ++valueCountor);
    }
}
