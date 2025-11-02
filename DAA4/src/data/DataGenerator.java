package data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DataGenerator {

    static class Edge {
        int from, to, weight;
        Edge(int f, int t, int w) {
            from = f; to = t; weight = w;
        }
    }

    static class GraphData {
        int n;
        List<Edge> edges = new ArrayList<>();
    }

    public static void main(String[] args) {
        generateDatasets();
    }

    public static void generateDatasets() {
        generateDataset("small_1", 8, 10, true);
        generateDataset("small_2", 10, 12, false);
        generateDataset("small_3", 9, 14, true);

        generateDataset("medium_1", 15, 25, true);
        generateDataset("medium_2", 18, 28, false);
        generateDataset("medium_3", 20, 35, true);

        generateDataset("large_1", 30, 80, false);
        generateDataset("large_2", 40, 120, true);
        generateDataset("large_3", 50, 160, false);
    }

    public static void generateDataset(String name, int n, int edgeCount, boolean cyclic) {
        GraphData g = new GraphData();
        g.n = n;
        Random rand = new Random();

        for (int i = 0; i < edgeCount; i++) {
            int u = rand.nextInt(n);
            int v = rand.nextInt(n);
            if (!cyclic && u >= v) continue; // roughly make DAG
            if (u == v) continue; // skip self-loops
            int w = 1 + rand.nextInt(9);
            g.edges.add(new Edge(u, v, w));
        }

        String json = toJson(g);
        String filePath = "data/" + name + ".json";

        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(json);
            System.out.println("✅ Dataset generated: " + filePath + " (" + g.edges.size() + " edges)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- simple JSON serializer ---
    private static String toJson(GraphData g) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"n\": ").append(g.n).append(",\n  \"edges\": [\n");
        for (int i = 0; i < g.edges.size(); i++) {
            Edge e = g.edges.get(i);
            sb.append("    {\"from\": ").append(e.from)
                    .append(", \"to\": ").append(e.to)
                    .append(", \"weight\": ").append(e.weight).append("}");
            if (i < g.edges.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        return sb.toString();
    }
}
