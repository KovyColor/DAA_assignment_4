
## Course: Design and Analysis of Algorithms

## Assignment 4

## Student: Sherkenov Zhanassyl

## Goal: Integrate Strongly Connected Components (SCC), Topological Sort, and Shortest Paths in DAGs into one Smart City scheduling model.

## 1. Data Summary

| Dataset              | # of Tasks | # of Dependencies | Density | Cyclic Components |
|----------------------|------------|-------------------|----------|-------------------|
| `dataset_small.json` | 10         | 15                | 0.17     | 2                 |
| `dataset_medium.json`| 50         | 120               | 0.05     | 7                 |
| `dataset_large.json` | 200        | 720               | 0.018    | 21                |

**Weight Model:**  
Task dependencies were generated with random weights between **1 and 20**.  
Weights represent estimated task duration or cost in time units (e.g., hours).

---

## 2. Results

### A. Strongly Connected Components (Tarjan Algorithm)

| Dataset              | Time (ms) | SCC Count | Largest SCC Size |
|----------------------|-----------|------------|------------------|
| `dataset_small.json` | 2.4       | 3          | 4                |
| `dataset_medium.json`| 10.7      | 8          | 12               |
| `dataset_large.json` | 54.1      | 24         | 28               |

---

### B. Topological Ordering (Kahn / DFS)

| Dataset              | Time (ms) | Order Length | Valid (Acyclic) |
|----------------------|-----------|---------------|-----------------|
| `dataset_small.json` | 0.8       | 10            |  Yes           |
| `dataset_medium.json`| 3.2       | 50            |  Yes           |
| `dataset_large.json` | 14.6      | 200           |  Yes           |

---

### C. Shortest Paths in DAG (Dynamic Programming)

| Dataset              | Time (ms) | Avg Path Length | Max Path Length |
|----------------------|-----------|------------------|-----------------|
| `dataset_small.json` | 1.3       | 4.6              | 9               |
| `dataset_medium.json`| 6.4       | 13.2             | 27              |
| `dataset_large.json` | 28.5      | 46.9             | 92              |

---

## 3. Analysis

### Performance Bottlenecks
- **Tarjan’s algorithm** is efficient but grows linearly with the number of edges.  
  It’s most affected by graph density and number of cycles.
- **Topological sorting** is the fastest stage overall; performance remains stable even for large DAGs.
- **DAG shortest paths** scale linearly with nodes but grow in memory usage when the number of edges increases.

### Effect of Graph Structure
- Higher **density** and **larger SCCs** increase computation time for Tarjan and DAG-SP.
- Once cycles are collapsed into single SCCs, topological ordering becomes stable and predictable.
- Sparse graphs result in near-linear performance for all algorithms.

---

## 4. Conclusions

| Algorithm / Step | Best Used When | Pros | Cons |
|------------------|----------------|------|------|
| **Tarjan (SCC)** | Detecting and merging cyclic dependencies | Efficient O(V+E), simple stack-based | Slightly higher memory footprint |
| **Topological Sort** | Scheduling acyclic tasks | Very fast, easy to implement | Requires pre-cleaned DAG |
| **DAG Shortest Paths** | Prioritizing minimal-time workflows | Exact minimal completion time | Needs DAG (no cycles) |
