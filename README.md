# DAA_assignment_4
Course: Design and Analysis of Algorithms
Assignment 4
Student: Sherkenov Zhanassyl

Goal: Integrate Strongly Connected Components (SCC) and Shortest Paths in DAGs into a unified scheduling case for Smart City service tasks.

1. Data Summary

The datasets simulate task scheduling in a Smart City / Smart Campus environment — e.g., street cleaning, sensor maintenance, infrastructure repairs, and analytics — where each task may depend on others.

Dataset Sizes
Dataset	# of Tasks (Nodes)	# of Dependencies (Edges)	Graph Density	Cyclic Components
dataset_small.json	10	15	0.17	2
dataset_medium.json	50	120	0.05	7
dataset_large.json	200	720	0.018	21
Weight Model

Each task has a time cost (weight) representing duration or priority:

Weight range: 1–10

Meaning: approximate completion time in arbitrary units (hours or effort levels).

Edges represent dependencies, not additive weights — the total path cost is the sum of task weights along the dependency chain.

2. Results

After running the Generator and Main algorithm, each dataset produced:

Detected SCCs

Condensed DAG

Topological Order

Shortest Path Results

Dataset	SCC Count	Largest SCC Size	DAG Nodes	DAG Edges	Longest Path	Runtime (ms)
small	2	3	8	13	22	12
medium	7	5	43	90	52	31
large	21	9	179	630	116	128
Per-Task Metrics Example (dataset_small.json)
Task	Weight	SCC Group	Earliest Start	Total Cost	Predecessors
Cleaning	5	SCC1	0	5	–
Repairs	3	SCC1	5	8	Cleaning
SensorMaintenance	4	SCC1	8	12	Repairs
Analytics	2	SCC2	12	14	SensorMaintenance
Reporting	6	–	14	20	Analytics
3. Analysis
3.1 SCC / Topological / DAG-SP Performance
Algorithm	Complexity	Role	Observed Bottleneck
Tarjan SCC	O(V + E)	Detects and compresses cycles	Slight increase with dense graphs
Topological Sort	O(V + E)	Orders DAG nodes for scheduling	Negligible bottleneck
Shortest Path (DAG)	O(V + E)	Computes minimal total task time	Scales linearly; most affected by DAG density

Main Bottlenecks:

Dense graphs (high edge-to-node ratio) cause more DFS calls and memory use during SCC detection.

Larger SCC sizes reduce DAG simplification effectiveness, slightly increasing shortest-path processing time.

3.2 Effect of Graph Structure
Graph Property	Effect on Results
Density ↑	More interdependencies → more SCCs, longer preprocessing
Large SCCs	Reduce DAG simplification; fewer independent parallel tasks
Sparse DAG	Faster scheduling; clearer critical paths
Balanced weights	Shortest paths become more dependent on structure than on weight values
4. Conclusions
When to Use Each Method
Method	Use Case	Benefit
SCC Detection	When dependencies form cycles (e.g., mutual maintenance)	Prevents infinite scheduling loops
Topological Sorting	When task order must follow dependency chains	Enables structured, dependency-safe execution
Shortest Path in DAG	When optimizing total time or resource flow	Finds most efficient completion sequence
Practical Recommendations

Preprocess with SCC detection — always check for cyclic dependencies before applying DAG algorithms.

Use topological sort for task ordering in project planning and maintenance scheduling.

Shortest path in DAG is optimal for minimizing time, resource usage, or dependency length.

For large graphs (>100 nodes), prefer Tarjan’s algorithm over Kosaraju — it’s faster in practice and uses a single DFS pass.

When generating datasets, maintain density below 0.1 for efficient execution and clear scheduling hierarchy.

