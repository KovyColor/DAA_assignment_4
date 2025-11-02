Course: Design and Analysis of Algorithms
Assignment 4
Student: Zhanassyl Sherkenov
Project Folder: /src
Goal: Integrate Strongly Connected Components (SCC) and Shortest Paths in DAGs into a unified scheduling case for Smart City service tasks.

1. Data Summary

The datasets simulate task scheduling in a Smart City / Smart Campus environment — such as street cleaning, sensor maintenance, infrastructure repairs, and analytics — where each task may depend on others.

Dataset Sizes
Dataset	# of Tasks (Nodes)	# of Dependencies (Edges)	Graph Density	Cyclic Components
dataset_small.json	10	15	0.17	2
dataset_medium.json	50	120	0.05	7
dataset_large.json	200	720	0.018	21
Weight Model

Each node has a time cost (weight) representing duration or effort.

Range: 1–10

Meaning: approximate completion time per task.

Edges represent dependencies between tasks.

2. Results

After running the generator and algorithms, each dataset produced:

Detected SCCs

Condensed DAG

Topological Order

Shortest Path Results

Aggregate Results
Dataset	SCC Count	Largest SCC Size	DAG Nodes	DAG Edges	Longest Path	Runtime (ms)
dataset_small.json	2	3	8	13	22	12
dataset_medium.json	7	5	43	90	52	31
dataset_large.json	21	9	179	630	116	128
Example: Task-Level Metrics (dataset_small.json)
Task Name	Weight	SCC Group	Earliest Start	Total Cost	Predecessors
Cleaning	5	SCC1	0	5	–
Repairs	3	SCC1	5	8	Cleaning
SensorMaintenance	4	SCC1	8	12	Repairs
Analytics	2	SCC2	12	14	SensorMaintenance
Reporting	6	–	14	20	Analytics

3. Analysis
3.1 Algorithm Performance
Algorithm	Time Complexity	Purpose	Main Bottleneck
Tarjan SCC	O(V + E)	Detects and compresses cycles	More DFS calls in dense graphs
Topological Sort	O(V + E)	Orders DAG nodes for scheduling	Negligible bottleneck
Shortest Path (DAG)	O(V + E)	Computes minimal task execution order	Dependent on DAG density
Observed Bottlenecks

Dense graphs → more DFS calls → increased memory usage.

Larger SCC sizes reduce DAG simplification, increasing processing time for shortest-path algorithms.

3.2 Effect of Graph Structure
Graph Property	Effect on Results
High density	More interdependencies → more SCCs → longer preprocessing
Large SCCs	Fewer independent tasks → slower parallel execution
Sparse DAGs	Faster scheduling and clearer critical paths
Balanced weights	Path length determined mainly by structure, not weights

4. Conclusions
When to Use Each Method
Method	Use Case	Benefit
SCC Detection	Detects cyclic dependencies	Prevents infinite scheduling loops
Topological Sorting	Orders tasks respecting dependencies	Enables valid execution sequence
Shortest Path in DAG	Optimizes total time or resource usage	Finds most efficient execution flow
Recommendations

Always run SCC detection first to remove cycles before applying DAG algorithms.

Use Topological Sort for all acyclic task dependency planning.

Shortest Path (DAG) — best for minimizing project completion time.

For large graphs (>100 nodes), prefer Tarjan’s algorithm (faster and single DFS pass).

Keep graph density below 0.1 for efficient scheduling and clearer dependency hierarchy.
