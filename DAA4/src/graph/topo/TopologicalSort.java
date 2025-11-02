package graph.topo;

import graph.DirectedGraph;
import graph.Metrics;

import java.util.*;

/**
 * Implements Kahn's algorithm for topological sorting.
 */
public class TopologicalSort {
    private final DirectedGraph graph;
    private final Metrics metrics;

    public TopologicalSort(DirectedGraph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Computes topological order using Kahn's algorithm.
     * @return list of node indices in topological order, or null if cycle
     */
    public List<Integer> kahnSort() {
        metrics.startTiming();
        int n = graph.getNodes().size();
        int[] indegree = new int[n];
        for (List<Integer> neighbors : graph.getAdj()) {
            for (int v : neighbors) {
                indegree[v]++;
            }
        }
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                metrics.incrementCounter("kahn_pushes");
            }
        }
        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("kahn_pops");
            order.add(u);
            for (int v : graph.getAdj().get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) {
                    queue.add(v);
                    metrics.incrementCounter("kahn_pushes");
                }
            }
        }
        metrics.stopTiming();
        return order.size() == n ? order : null;  // Null if cycle
    }

    /**
     * Converts order to node names.
     * @param order list of indices
     * @return list of node names
     */
    public static List<String> getOrderAsNames(List<Integer> order, DirectedGraph graph) {
        List<String> named = new ArrayList<>();
        for (int idx : order) {
            named.add(graph.getNodes().get(idx));
        }
        return named;
    }
}