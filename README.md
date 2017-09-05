WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams
========================

**WRS (Waiting Room Sampling)** is a single-pass streaming algorithm for global and local triangle counting in real graph streams. 
**WRS** exploits a temporal dependency pattern in real dynamic graph streams.
**WRS** has the following properties: 
 * *fast and any time*: **WRS** scales linearly with the number of edges in the input graph stream, and gives estimates at any time while the input graph grows
 * *effective*: estimation error in **WRS** is up to 47% smaller than those in state-of-the-art methods
 * *theoretically sound*: **WRS** gives unbiased estimates with small variance under the temporal locality.

Datasets
========================
The download links for the datasets used in the paper are [here](http://www.cs.cmu.edu/~kijungs/codes/wrs/)

Building and Running WRS
========================
Please see [User Guide](user_guide.pdf)

Running Demo
========================
For demo, please type 'make'

Reference
========================
If you use this code as part of any published research, please acknowledge the following paper.
```
@inproceedings{shin2017wrs,
  author    = {Kijung Shin},
  title     = {WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams},
  booktitle = {ICDM},
  year      = {2017}
}
```
