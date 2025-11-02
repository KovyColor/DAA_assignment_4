package test;

import graph.*;
import graph.scc.Tarjan;
import graph.dagsp.DAGShortestPaths;

import java.util.*;

public class TestPipeline {
    public static void main(String[] args) {
        System.out.println("=== TestPipeline ===");

        // small example with one small cycle
        List<String> nodes = Arrays.asList("t0", "t1", "t2", "t3");
        List<String[]> edges = Arrays.asList(
                new String[]{"t0", "t1"},
                new String[]{"t1", "t2"},
                new String[]{"t2", "t0"}, // cycle
                new String[]{"t2", "t3"}
        );
        Map<String, Integer> weights = new HashMap<>();
        for (String[] e : edges) weights.put(e[0] + "-" + e[1], 1);

        DirectedGraph g = new DirectedGraph(nodes, edges, weights);
        SimpleMetrics m = new SimpleMetrics();

        Tarjan t = new Tarjan(g, m);
        List<List<Integer>> sccs = t.findSCCs();
        DirectedGraph condensation = g.buildCondensation(sccs);

        System.out.println("SCC count: " + sccs.size());
        System.out.println("Condensation nodes: " + condensation.getNodes());

        // Simple topo order manually
        List<Integer> topo = new ArrayList<>();
        for (int i = 0; i < condensation.getNodes().size(); i++) topo.add(i);

        DAGShortestPaths sp = new DAGShortestPaths(condensation, m);
        Map<Integer, Integer> shortest = sp.shortestPaths(0, topo);
        System.out.println("Shortest distances on condensation: " + shortest);
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
