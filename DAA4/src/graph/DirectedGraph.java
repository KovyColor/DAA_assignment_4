package graph;

import java.util.*;

/**
 * Represents a directed graph with nodes (strings) and weighted edges.
 */
public class DirectedGraph {
    private final List<String> nodes;
    private final Map<String, Integer> nodeIndex;
    private final List<List<Integer>> adj;  // Adjacency list (indices)
    private final List<Map<Integer, Integer>> weights;  // Weights per edge (from index to map of to-index -> weight)

    public DirectedGraph(List<String> nodes, List<String[]> edges, Map<String, Integer> edgeWeights) {
        this.nodes = new ArrayList<>(nodes);
        this.nodeIndex = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndex.put(nodes.get(i), i);
        }
        this.adj = new ArrayList<>();
        this.weights = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            adj.add(new ArrayList<>());
            weights.add(new HashMap<>());
        }
        for (String[] edge : edges) {
            int from = nodeIndex.get(edge[0]);
            int to = nodeIndex.get(edge[1]);
            adj.get(from).add(to);
            if (edgeWeights.containsKey(edge[0] + "-" + edge[1])) {
                weights.get(from).put(to, edgeWeights.get(edge[0] + "-" + edge[1]));
            }
        }
    }

    public List<String> getNodes() { return nodes; }
    public Map<String, Integer> getNodeIndex() { return nodeIndex; }
    public List<List<Integer>> getAdj() { return adj; }
    public List<Map<Integer, Integer>> getWeights() { return weights; }

    /**
     * Builds the condensation graph (DAG of SCCs).
     * @param sccs list of SCCs (each as list of node indices)
     * @return condensation graph
     */
    public DirectedGraph buildCondensation(List<List<Integer>> sccs) {
        Map<Integer, Integer> componentMap = new HashMap<>();
        List<String> componentNodes = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) {
            componentNodes.add("SCC" + i);
            for (int node : sccs.get(i)) {
                componentMap.put(node, i);
            }
        }
        Set<String> componentEdgesSet = new HashSet<>();
        Map<String, Integer> componentWeights = new HashMap<>();
        for (int u = 0; u < adj.size(); u++) {
            int compU = componentMap.get(u);
            for (int v : adj.get(u)) {
                int compV = componentMap.get(v);
                if (compU != compV) {
                    String edgeKey = "SCC" + compU + "-SCC" + compV;
                    if (!componentEdgesSet.contains(edgeKey)) {
                        componentEdgesSet.add(edgeKey);
                        componentWeights.put(edgeKey, weights.get(u).getOrDefault(v, 1));  // Default weight 1 if none
                    }
                }
            }
        }
        List<String[]> componentEdges = new ArrayList<>();
        for (String edge : componentEdgesSet) {
            String[] parts = edge.split("-");
            componentEdges.add(new String[]{parts[0], parts[1]});
        }
        return new DirectedGraph(componentNodes, componentEdges, componentWeights);
    }
}