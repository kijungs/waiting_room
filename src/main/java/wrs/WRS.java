package wrs;

/**
 * =================================================================================
 *
 * WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams
 * Authors: Kijung Shin
 *
 * Temporal Locality-Aware Sampling for Accurate Triangle Counting in Real Graph Streams
 * Authors: Dongjin Lee, Kijung Shin, and Christos Faloutsos
 *
 * Version: 2.0
 * Date: Aug 18, 2020
 * Main Contact: Kijung Shin (kijungs@kaist.ac.kr)
 *
 * This software is free of charge under research purposes.
 * For commercial purposes, please contact the author.
 =================================================================================
 */

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;

/**
 * WRS Interface
 */
public abstract class WRS {

    abstract public void processEdge(int src, int dst, boolean add);

    abstract public double getGlobalTriangle();

    abstract public Int2DoubleMap getLocalTriangle();

}