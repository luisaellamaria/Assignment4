Assignment 4
------------

# Team Members

# GitHub link to your (forked) repository (if submitting through GitHub)

...


# Task 4

1. What is the causal consistency? Explain it using the happened-before relation.
>

2. You are responsible for designing a distributed system that maintains a partial ordering of operations on a data store (for instance, maintaining a time-series log database receiving entries from multiple independent processes/sensors with minimum or no concurrency). When would you choose Lamport timestamps over vector clocks? Explain your argument. 
   What are the design objectives you can meet with both?
>  In the following cases I would prefer Lamport timestamps over vector clocks:
- In scenarios where the number of nodes in your system is dynamic or very large, Lamport timestamps are more manageable as they do not grow in size with the addition of new nodes.
- If I only need to know that one event happened before another, without the need to track the exact sequence of events across all processes, Lamport timestamps are generally better and more lightweight.
- If I need to have little overhead it is better to choose Lamport timestamps as they are smaller in size than vector clocks.
 3. Vector clocks are an extension of the Lamport timestamp algorithm. However, scaling a vector clock to handle multiple processes can be challenging. Propose some solutions to this and explain your argument. 
>