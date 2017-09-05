=================================================================================

 WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams
 Authors: Kijung Shin

 Version: 1.0
 Date: May 24, 2017
 Main Contact: Kijung Shin (kijungs@cs.cmu.edu)

 This software is free of charge under research purposes.
 For commercial purposes, please contact the author.

 =================================================================================

WRS (Waiting Room Sampling) is a single-pass streaming algorithm for global and local triangle counting in real graph streams. 
WRS exploits a temporal dependency pattern in real dynamic graph streams.
WRS has the following properties: 
- fast and any time: WRS scales linearly with the number of edges in the input graph stream, and gives estimates at any time while the input graph grows
- effective: estimation error in WRS is up to 47% smaller than those in state-of-the-art methods
- theoretically sound: WRS gives unbiased estimates with small variance under the temporal locality.

For detailed information, see 'user_guide.pdf'

For demo, type 'make'