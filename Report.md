Assignment 4
------------

# Team Members
- Emma Kozmér
- Luisa Ella Müller

# GitHub link to your (forked) repository (if submitting through GitHub)

https://github.com/luisaellamaria/Assignment4.git

# Task 4

> 1. What is the causal consistency? Explain it using the happened-before relation.

Causal consistency is a consistency model. It is used to make sure that if one process causally depends on the result of another process’s update, it will observe that update using the same order. The concept of causality order is a happens-before relationship between any two operations in a given execution. To understand this, we should first explain the happened-Before Relation. 

This is a concept to ensure the causal ordering of events in a distributed system. If one operation (say, A) happens before another operation (say, B), then the system should reflect that A influenced or caused B.

An example: 
 
Suppose we have operations A, B, and C in a distributed data store.
Operation A writes a value to a key.
Operation B, in the same thread or a different thread, reads the value written by A and then performs another write based on A's value.
Operation C reads the value written by B. 

According to the happened-before relation:

A → B (since B reads the result of A) \
and B → C (since C reads the result of B). \
A → C. (by transitivity) 

In a causally consistent system, a client that observes the effect of C must also see the effects of A and B (in that order), or at least the most recent state that includes A and B's changes.

> 2. You are responsible for designing a distributed system that maintains a partial ordering of operations on a data store (for instance, maintaining a time-series log database receiving entries from multiple independent processes/sensors with minimum or no concurrency). When would you choose Lamport timestamps over vector clocks? Explain your argument. 
   What are the design objectives you can meet with both? 

In the following cases I would prefer Lamport timestamps over vector clocks:
- In scenarios where the number of nodes in your system is dynamic or very large, Lamport timestamps are more manageable as they do not grow in size with the addition of new nodes.
- If I only need to know that one event happened before another, without the need to track the exact sequence of events across all processes, Lamport timestamps are generally better and more lightweight.
- If I need to have little overhead it is better to choose Lamport timestamps as they are smaller in size than vector clocks.
> 3. Vector clocks are an extension of the Lamport timestamp algorithm. However, scaling a vector clock to handle multiple processes can be challenging. Propose some solutions to this and explain your argument. 
 
To better understand the advantage of our proposal, we first need to understand the problem with standard vector clocks.

Standard vector clocks have a fixed size based on the number of processes. This becomes problematic in systems where processes can frequently join or leave.

The dynamic vector clocks proposed by Tobias Landes aim to address these challenges. 
Dynamic vector clocks can grow or shrink in size as processes join or leave the system. This means that they need to update knowledge about other processes and including new processes it was previously unaware of. 
Dynamic vector clocks use a two-column matrix. One column represents the process IDs, and the other represents the corresponding clock values. They can implement a garbage collection mechanism to remove entries for terminated processes, keeping the clock size in check.

We have found this solution on the paper proposed on the assignment sheet. 

Sources for these Answers: 

https://www.geeksforgeeks.org/causal-consistency-model-in-system-design/
https://www.geeksforgeeks.org/vector-clocks-in-distributed-systems/
