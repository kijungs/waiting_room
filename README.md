WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams
========================

**WRS (Waiting Room Sampling)** is a single-pass streaming algorithm for global and local triangle counting in (fully-dynamic) real graph streams. 
**WRS** exploits a temporal dependency pattern in real dynamic graph streams.
**WRS** has the following properties: 
 * *fast and any time*: **WRS** scales linearly with the number of edges in the input graph stream, and gives estimates at any time while the input graph grows
 * *effective*: estimation error in **WRS** is up to 47% smaller than those in state-of-the-art methods
 * *theoretically sound*: **WRS** gives unbiased estimates with small variance under the temporal locality.

Datasets
========================
The download links for the datasets used in the paper are [here](http://dmlab.kaist.ac.kr/wrs/)

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
  author       = {Kijung Shin},
  title        = {WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams},
  booktitle    = {ICDM},
  pages        = {1087--1092},
  year         = {2017},
  organization = {IEEE}
}

@article{lee2020temporal,
  title     = {Temporal locality-aware sampling for accurate triangle counting in real graph streams},
  author    = {Lee, Dongjin and Shin, Kijung and Faloutsos, Christos},
  journal   = {The VLDB Journal},
  pages     = {1--25},
  year      = {2020},
  publisher = {Springer}
}
```
