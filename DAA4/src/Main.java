import data.DataGenerator;
import graph.*;
import graph.scc.Tarjan;
import graph.topo.TopologicalSort;
import graph.dagsp.DAGShortestPaths;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Main class to run the Smart City Scheduling pipeline: SCC -> Condensation -> Topo -> DAG SP.
 * Parses tasks.json manually (no external JSON libs).
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        DataGenerator.generateDatasets();
        System.out.println("Datasets generated successfully!");
        if (args.length < 1) {
            System.out.println("Usage: java Main <dataset.json>");
            return;
        }
        String filePath = args[0];

        // Parse JSON manually
        SimpleJsonParser parser = new SimpleJsonParser();
        Map<String, Object> data = parser.parse(filePath);
        List<String> nodes = (List<String>) data.get("nodes");
        List<List<String>> edgesList = (List<List<String>>) data.get("edges");
        List<String[]> edges = new ArrayList<>();
        for (List<String> edge : edgesList) {
            edges.add(edge.toArray(new String[0]));
        }
        Map<String, Integer> weights = (Map<String, Integer>) data.get("weights");

        // Build graph
        DirectedGraph graph = new DirectedGraph(nodes, edges, weights);

        // Metrics instance (simple implementation)
        MetricsImpl metrics = new MetricsImpl();

        // 1. SCC using Tarjan
        Tarjan tarjan = new Tarjan(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();
        List<List<String>> namedSCCs = Tarjan.getSCCsAsNames(sccs, graph);
        List<Integer> sccSizes = Tarjan.getSCCSizes(namedSCCs);
        System.out.println("SCCs: " + namedSCCs);
        System.out.println("SCC Sizes: " + sccSizes);
        System.out.println("SCC Time: " + metrics.getTiming() + " ns, DFS Visits: " + metrics.getCounter("dfs_visits") + ", DFS Edges: " + metrics.getCounter("dfs_edges"));

        // Build condensation graph (DAG)
        DirectedGraph condensation = graph.buildCondensation(sccs);

        // 2. Topological Sort on condensation
        TopologicalSort topoSort = new TopologicalSort(condensation, metrics);
        List<Integer> topoOrder = topoSort.kahnSort();
        if (topoOrder == null) {
            System.out.println("Cycle detected in condensation graph!");
            return;
        }
        List<String> namedTopoOrder = TopologicalSort.getOrderAsNames(topoOrder, condensation);
        System.out.println("Topological Order of Components: " + namedTopoOrder);
        System.out.println("Topo Time: " + metrics.getTiming() + " ns, Pushes: " + metrics.getCounter("kahn_pushes") + ", Pops: " + metrics.getCounter("kahn_pops"));

        // Derive order of original tasks (flatten SCCs in topo order)
        List<String> originalOrder = new ArrayList<>();
        for (int compIdx : topoOrder) {
            originalOrder.addAll(namedSCCs.get(compIdx));
        }
        System.out.println("Derived Order of Original Tasks: " + originalOrder);

        // 3. DAG Shortest/Longest Paths (use first in topo order as source)
        int source = topoOrder.get(0);
        DAGShortestPaths dagSp = new DAGShortestPaths(condensation, metrics);
        Map<Integer, Integer> shortestDist = dagSp.shortestPaths(source, topoOrder);
        Map<Integer, Integer> longestDist = dagSp.longestPaths(source, topoOrder);

        // Find critical path (longest)
        int maxDist = Integer.MIN_VALUE;
        int target = -1;
        for (Map.Entry<Integer, Integer> entry : longestDist.entrySet()) {
            if (entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                target = entry.getKey();
            }
        }
        System.out.println("Critical Path Length: " + maxDist);
        // Note: Path reconstruction would need parent tracking; omitted for brevity, but can be added.

        System.out.println("Shortest Distances from Source: " + shortestDist);
        System.out.println("DAG SP Time: " + metrics.getTiming() + " ns, Relaxations: " + metrics.getCounter("relaxations"));
    }

    // Simple JSON parser for the specific format
    static class SimpleJsonParser {
        public Map<String, Object> parse(String filePath) throws IOException {
            String json = readFile(filePath);
            Map<String, Object> result = new HashMap<>();
            json = json.trim().substring(1, json.length() - 1); // Remove {}
            String[] pairs = json.split(",(?=\\s*\"\\w+\":)"); // Split on , before keys
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                String key = keyValue[0].trim().replaceAll("\"", "");
                String value = keyValue[1].trim();
                if (key.equals("nodes") || key.equals("edges")) {
                    result.put(key, parseArray(value));
                } else if (key.equals("weights")) {
                    result.put(key, parseObject(value));
                }
            }
            return result;
        }

        private List<?> parseArray(String value) {
            value = value.trim();
            if (value.startsWith("[")) {
                value = value.substring(1, value.length() - 1);
                List<Object> list = new ArrayList<>();
                if (value.contains("[")) { // Nested arrays for edges
                    String[] items = value.split("\\],\\s*\\[");
                    for (String item : items) {
                        item = item.replaceAll("\\[|\\]", "").trim();
                        list.add(Arrays.asList(item.split(",\\s*")));
                    }
                } else { // Simple array for nodes
                    String[] items = value.split(",\\s*");
                    for (String item : items) {
                        list.add(item.replaceAll("\"", ""));
                    }
                }
                return list;
            }
            return new ArrayList<>();
        }

        private Map<String, Integer> parseObject(String value) {
            value = value.trim().substring(1, value.length() - 1); // Remove {}
            Map<String, Integer> map = new HashMap<>();
            if (!value.isEmpty()) {
                String[] pairs = value.split(",(?=\\s*\"[^\"]+-\")");
                for (String pair : pairs) {
                    String[] kv = pair.split(":", 2);
                    String k = kv[0].trim().replaceAll("\"", "");
                    int v = Integer.parseInt(kv[1].trim());
                    map.put(k, v);
                }
            }
            return map;
        }

        private String readFile(String filePath) throws IOException {
            StringBuilder sb = new StringBuilder();
            try (FileReader fr = new FileReader(filePath)) {
                int ch;
                while ((ch = fr.read()) != -1) {
                    sb.append((char) ch);
                }
            }
            return sb.toString();
        }
    }

    // Simple Metrics implementation
    static class MetricsImpl implements Metrics {
        private long startTime;
        private final Map<String, Long> counters = new HashMap<>();

        @Override
        public void startTiming() { startTime = System.nanoTime(); }

        @Override
        public long stopTiming() { return System.nanoTime() - startTime; }

        @Override
        public void incrementCounter(String name) { counters.put(name, counters.getOrDefault(name, 0L) + 1); }

        @Override
        public long getCounter(String name) { return counters.getOrDefault(name, 0L); }

        @Override
        public void reset() { counters.clear(); startTime = 0; }

        public long getTiming() { return stopTiming(); } // For output
    }
}
