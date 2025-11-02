package test;

import graph.*;
import graph.scc.Tarjan;

import java.util.*;

public class TestTarjan {
    public static void main(String[] args) {
        System.out.println("=== TestTarjan ===");

        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<String[]> edges = Arrays.asList(
                new String[]{"A", "B"},
                new String[]{"B", "C"},
                new String[]{"C", "A"},  // cycle A-B-C
                new String[]{"B", "D"},
                new String[]{"D", "E"}
        );
        Map<String, Integer> weights = new HashMap<>();
        for (String[] e : edges) weights.put(e[0] + "-" + e[1], 1);

        DirectedGraph g = new DirectedGraph(nodes, edges, weights);
        Metrics m = new SimpleMetrics();

        Tarjan tarjan = new Tarjan(g, m);
        List<List<Integer>> sccs = tarjan.findSCCs();
        List<List<String>> named = Tarjan.getSCCsAsNames(sccs, g);

        System.out.println("SCCs (indices): " + sccs);
        System.out.println("SCCs (names): " + named);
        System.out.println("SCC sizes: " + Tarjan.getSCCSizes(named));
    }

    static class SimpleMetrics implements Metrics {
        long start;
        Map<String, Long> c = new HashMap<>();
        public void startTiming() { start = System.nanoTime(); }
        public long stopTiming() { return System.nanoTime() - start; }
        public void incrementCounter(String n) { c.put(n, c.getOrDefault(n, 0L) + 1); }
        public long getCounter(String n) { return c.getOrDefault(n, 0L); }
        public void reset() { c.clear(); }
    }
}
