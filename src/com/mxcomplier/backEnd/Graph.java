package com.mxcomplier.backEnd;

import com.mxcomplier.Error.IRError;
import com.mxcomplier.Ir.Operands.VirtualRegisterIR;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Graph {
    private HashMap<VirtualRegisterIR, HashSet<VirtualRegisterIR>> graphLink = new HashMap<>();

    Graph() {
    }

    Graph(Graph other) {
        for (Map.Entry<VirtualRegisterIR, HashSet<VirtualRegisterIR>> entry : other.graphLink.entrySet()) {
            graphLink.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
    }

    void addNode(VirtualRegisterIR p) {
        if (!graphLink.containsKey(p))
            graphLink.put(p, new HashSet<>());
    }

    void removeNode(VirtualRegisterIR p) {
        for (VirtualRegisterIR vreg : graphLink.get(p))
            graphLink.get(vreg).remove(p);
        graphLink.remove(p);
    }

    void addEdge(VirtualRegisterIR u, VirtualRegisterIR v) {
        if (u == v)
            throw new IRError("add self-loop edge");
//        System.err.println(u + " " + v);
//        System.err.flush();
        graphLink.get(u).add(v);
        graphLink.get(v).add(u);
    }

    void addEdges(HashSet<VirtualRegisterIR> u, HashSet<VirtualRegisterIR> v) {
        for (VirtualRegisterIR x : u)
            for (VirtualRegisterIR y : v)
                if (x != y)
                    addEdge(x, y);
    }

    void removeEdge(VirtualRegisterIR u, VirtualRegisterIR v) {
        if (u == v)
            throw new IRError("add self-loop edge");
        graphLink.get(u).remove(v);
        graphLink.get(v).remove(u);
    }

    public Set<VirtualRegisterIR> getnodes() {
        return new HashSet<>(graphLink.keySet());
    }

    public int getDegree(VirtualRegisterIR u) {
        return graphLink.get(u).size();
    }

    public HashSet<VirtualRegisterIR> getNeighbor(VirtualRegisterIR u) {
        return new HashSet<>(graphLink.get(u));
    }

    void clear() {
        graphLink.clear();
    }
}
