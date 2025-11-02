package test;

import graph.*;
import graph.dagsp.DAGShortestPaths;

import java.util.*;

public class TestDAGShortestPaths {
    public static void main(String[] args) {
        System.out.println("=== TestDAGShortestPaths ===");

        List<String> nodes = Arrays.asList("A", "B", "C", "D", "E");
        List<String[]> edges = Arrays.asList(
                new String[]{"A", "B"},
                new String[]{"A", "C"},
                new String[]{"B", "D"},
                new String[]{"C", "D"},
                new String[]{"D", "E"}
        );
        Map<String, Integer> weights = new HashMap<>();
        weights.put("A-B", 2);
        weights.put("A-C", 3);
        weights.put("B-D", 1);
        weights.put("C-D", 1);
        weights.put("D-E", 2);

        DirectedGraph g = new DirectedGraph(nodes, edges, weights);
        Metrics m = new SimpleMetrics();

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3, 4); // A, B, C, D, E
        DAGShortestPaths sp = new DAGShortestPaths(g, m);
        Map<Integer, Integer> shortest = sp.shortestPaths(0, topoOrder);
        Map<Integer, Integer> longest = sp.longestPaths(0, topoOrder);

        System.out.println("Shortest distances: " + shortest);
        System.out.println("Longest distances: " + longest);
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
