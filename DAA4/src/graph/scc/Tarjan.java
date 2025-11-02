    package graph.scc;

    import graph.DirectedGraph;
    import graph.Metrics;

    import java.util.*;

    /**
     * Implements Tarjan's algorithm for finding Strongly Connected Components (SCCs).
     */
    public class Tarjan {
        private int index = 0;
        private final Stack<Integer> stack = new Stack<>();
        private final List<List<Integer>> sccs = new ArrayList<>();
        private final int[] disc, low, inStack;
        private final DirectedGraph graph;
        private final Metrics metrics;

        public Tarjan(DirectedGraph graph, Metrics metrics) {
            this.graph = graph;
            this.metrics = metrics;
            int n = graph.getNodes().size();
            disc = new int[n];
            low = new int[n];
            inStack = new int[n];
            Arrays.fill(disc, -1);
            Arrays.fill(low, -1);
            Arrays.fill(inStack, 0);
        }

        /**
         * Computes SCCs using Tarjan's algorithm.
         * @return list of SCCs (each as list of node indices)
         */
        public List<List<Integer>> findSCCs() {
            metrics.startTiming();
            for (int i = 0; i < graph.getNodes().size(); i++) {
                if (disc[i] == -1) {
                    dfs(i);
                }
            }
            metrics.stopTiming();
            return sccs;
        }

        private void dfs(int u) {
            metrics.incrementCounter("dfs_visits");
            disc[u] = low[u] = index++;
            stack.push(u);
            inStack[u] = 1;

            for (int v : graph.getAdj().get(u)) {
                metrics.incrementCounter("dfs_edges");
                if (disc[v] == -1) {
                    dfs(v);
                    low[u] = Math.min(low[u], low[v]);
                } else if (inStack[v] == 1) {
                    low[u] = Math.min(low[u], disc[v]);
                }
            }

            if (low[u] == disc[u]) {
                List<Integer> scc = new ArrayList<>();
                int v;
                do {
                    v = stack.pop();
                    inStack[v] = 0;
                    scc.add(v);
                } while (v != u);
                sccs.add(scc);
            }
        }

        /**
         * Converts SCCs to node names and computes sizes.
         * @param sccs list of SCCs (indices)
         * @return list of SCCs (node names) and their sizes
         */
        public static List<List<String>> getSCCsAsNames(List<List<Integer>> sccs, DirectedGraph graph) {
            List<List<String>> namedSCCs = new ArrayList<>();
            for (List<Integer> scc : sccs) {
                List<String> named = new ArrayList<>();
                for (int idx : scc) {
                    named.add(graph.getNodes().get(idx));
                }
                namedSCCs.add(named);
            }
            return namedSCCs;
        }

        public static List<Integer> getSCCSizes(List<List<String>> sccs) {
            List<Integer> sizes = new ArrayList<>();
            for (List<String> scc : sccs) {
                sizes.add(scc.size());
            }
            return sizes;
        }
    }