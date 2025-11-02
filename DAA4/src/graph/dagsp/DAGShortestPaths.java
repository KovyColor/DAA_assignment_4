package graph.dagsp;

import graph.DirectedGraph;
import graph.Metrics;

import java.util.*;

/**
 * Implements shortest and longest paths in a DAG using topological order.
 */
public class DAGShortestPaths {
    private final DirectedGraph graph;
    private final Metrics metrics;

    public DAGShortestPaths(DirectedGraph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    /**
     * Computes single-source shortest paths from source.
     * @param source source node index
     * @param topoOrder topological order
     * @return distances and parents for reconstruction
     */
    public Map<Integer, Integer> shortestPaths(int source, List<Integer> topoOrder) {
        metrics.startTiming();
        int n = graph.getNodes().size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;

        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (int v : graph.getAdj().get(u)) {
                    metrics.incrementCounter("relaxations");
                    int weight = graph.getWeights().get(u).getOrDefault(v, 1);
                    if (dist[v] > dist[u] + weight) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }
        metrics.stopTiming();

        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < n; i++) {
            result.put(i, dist[i]);
        }
        return result;
    }

    /**
     * Computes longest paths (critical path) by negating weights.
     * @param source source node index
     * @param topoOrder topological order
     * @return distances and parents
     */
    public Map<Integer, Integer> longestPaths(int source, List<Integer> topoOrder) {
        // Negate weights for longest path
        DirectedGraph negatedGraph = negateWeights();
        DAGShortestPaths sp = new DAGShortestPaths(negatedGraph, metrics);
        Map<Integer, Integer> dist = sp.shortestPaths(source, topoOrder);
        // Negate back
        for (Map.Entry<Integer, Integer> entry : dist.entrySet()) {
            if (entry.getValue() != Integer.MAX_VALUE) {
                entry.setValue(-entry.getValue());
            }
        }
        return dist;
    }

    private DirectedGraph negateWeights() {
        List<String[]> edges = new ArrayList<>();
        Map<String, Integer> negWeights = new HashMap<>();
        for (int u = 0; u < graph.getNodes().size(); u++) {
            for (int v : graph.getAdj().get(u)) {
                String from = graph.getNodes().get(u);
                String to = graph.getNodes().get(v);
                edges.add(new String[]{from, to});
                negWeights.put(from + "-" + to, -graph.getWeights().get(u).get(v));
            }
        }
        return new DirectedGraph(graph.getNodes(), edges, negWeights);
    }

    /**
     * Reconstructs path from source to target.
     * @param parent parent array
     * @param target target node index
     * @return path as list of node names
     */
    public static List<String> reconstructPath(int[] parent, int target, DirectedGraph graph) {
        List<String> path = new ArrayList<>();
        for (int at = target; at != -1; at = parent[at]) {
            path.add(graph.getNodes().get(at));
        }
        Collections.reverse(path);
        return path;
    }
}