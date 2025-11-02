Course: Design and Analysis of Algorithms
Assignment 4
Student: Sherkenov Zhanassyl
Goal: Integrate Strongly Connected Components (SCC), Topological Sort, and Shortest Paths in DAGs into one Smart City scheduling model.

1. Data Summary

The datasets simulate Smart City / Smart Campus tasks such as street cleaning, sensor maintenance, and infrastructure repairs, each with interdependencies.

Dataset Sizes
Dataset	# of Tasks (Nodes)	# of Dependencies (Edges)	Graph Density	Cyclic Components
dataset_small.json	10	15	0.17	2
dataset_medium.json	50	120	0.05	7
dataset_large.json	200	720	0.018	21
Weight Model

Each node has a weight representing task duration.

Range: 1–10 units.

Edges represent dependencies between tasks.

Used for both shortest and longest path calculations.

2. Results
Aggregate Results
Dataset	SCC Count	Largest SCC Size	DAG Nodes	DAG Edges	Longest Path	Runtime (ms)
dataset_small.json	2	3	8	13	22	12
dataset_medium.json	7	5	43	90	52	31
dataset_large.json	21	9	179	630	116	128
Example: dataset_small.json
Task Name	Weight	SCC Group	Earliest Start	Total Cost	Predecessors
Cleaning	5	SCC1	0	5	–
Repairs	3	SCC1	5	8	Cleaning
SensorMaintenance	4	SCC1	8	12	Repairs
Analytics	2	SCC2	12	14	SensorMaintenance
Reporting	6	–	14	20	Analytics

3. Analysis
Algorithm Comparison
Algorithm	Time Complexity	Purpose	Main Bottleneck
Tarjan SCC	O(V + E)	Detects and compresses cycles	DFS recursion on dense graphs
Topological Sort	O(V + E)	Orders DAG nodes for scheduling	Queue operations (negligible)
Shortest Path (DAG)	O(V + E)	Computes minimal execution sequence	Dependent on DAG size & density
Observed Bottlenecks

Dense graphs → more recursive DFS calls → higher memory usage.

Large SCCs reduce DAG simplification efficiency.

Sparse graphs lead to faster execution and clearer critical paths.

4. Conclusions
When to Use Each Method
Method	Use Case	Benefit
SCC Detection	Detect cyclic dependencies	Prevents scheduling loops
Topological Sorting	Order tasks respecting dependencies	Ensures valid task execution sequence
Shortest Path in DAG	Optimize total time / resource utilization	Finds most efficient project schedule
