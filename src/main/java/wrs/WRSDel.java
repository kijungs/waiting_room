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

import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;

import java.util.Random;

/**
 * Implementation of WRS_DEL
 */
public class WRSDel extends WRS {

    private Int2ObjectOpenHashMap<Int2BooleanOpenHashMap> srcToDsts = new Int2ObjectOpenHashMap(); // graph composed of the sampled edges
    private Int2DoubleOpenHashMap nodeToTriangles = new Int2DoubleOpenHashMap(); // local triangle counts
    private double globalTriangle = 0; // global triangles

    private long ER = 0;
    private int nb = 0; // number of bad uncompensated deletions
    private int ng = 0; // number of good uncompensated deletions

    private final int sizeOfWaitingRoom; // size of the waiting room
    private final int sizeOfReservoir; // size of the reservoir
    private final LongLinkedOpenHashSet samplesWaitingRoom;
    private final long[] samplesReservoir;
    private final Long2IntOpenHashMap edgeToIndex = new Long2IntOpenHashMap();

    private final Random random;
    private final boolean lowerBound;

    /**
     * create an instance for WRS_INS
     *
     * @param k maximum number of samples
     * @param alpha relative size of the waiting room (between 0 and 1)
     * @param randomSeed random seed
     */
    public WRSDel(int k, double alpha, int randomSeed, boolean lowerBound) {
        this.random = new Random(randomSeed);
        this.sizeOfWaitingRoom = (int) (k * alpha);
        this.sizeOfReservoir = k - sizeOfWaitingRoom;
        this.samplesWaitingRoom = new LongLinkedOpenHashSet(sizeOfWaitingRoom);
        this.samplesReservoir = new long[sizeOfReservoir];
        this.nodeToTriangles.defaultReturnValue(0);
        this.lowerBound = lowerBound;
    }

    /**
     * process an edge
     *
     * @param src source node of the new edge
     * @param dst destination node of the new edge
     * @param add flag to determine edge insertion and deletion
     */
    public void processEdge(int src, int dst, boolean add) {

        if (src == dst) { //ignore self loop
            return;
        }

        count(src, dst, add);

        if (add) { //add

            if (samplesWaitingRoom.size() >= sizeOfWaitingRoom) {
                long toMoveKey = samplesWaitingRoom.firstLong();
                int reserviorSize = edgeToIndex.size();
                if (ng + nb == 0) {
                    if (reserviorSize < sizeOfReservoir) {
                        moveEdge(toMoveKey);
                    } else if (random.nextDouble() < sizeOfReservoir / (ER + 1.0)) {
                        int indexForSamplesProb = chooseIndex(reserviorSize);
                        deleteEdgeFromReservoir(samplesReservoir[indexForSamplesProb]);
                        moveEdge(toMoveKey);
                    } else {
                        deleteEdgeFromWaitingRoom(toMoveKey);
                    }
                } else if (random.nextDouble() < nb / (nb + ng + 0.0)) {
                    moveEdge(toMoveKey);
                    nb--;
                } else {
                    deleteEdgeFromWaitingRoom(toMoveKey);
                    ng--;
                }
                ER++;
            }

            addEdgeToWaitingRoom(src, dst);


        } else { //delete

            long key = ((long) src * Integer.MAX_VALUE) + dst;
            if (samplesWaitingRoom.contains(key)) { // removed from waiting room
                deleteEdgeFromWaitingRoom(key);
            }
            else if(edgeToIndex.containsKey(key)){ //removed from reservoir
                deleteEdgeFromReservoir(key);
                nb++;
                ER--;
            } else {
                ng++;
                ER--;
            }
        }
    }

    /**
     * choose an index randomly
     * @param n maximum index
     * @return random index
     */
    private int chooseIndex(int n) {
        return random.nextInt(n);
    }

    /**
     * move an edge from the waiting room to the reservoir
     * @param key key of the edge to be moved
     */
    private void moveEdge(long key) {

        samplesWaitingRoom.remove(key);

        int src = (int) (key / Integer.MAX_VALUE);
        int dst = (int) (key - ((long) src * Integer.MAX_VALUE));

        int reservoirSize = edgeToIndex.size();
        srcToDsts.get(src).put(dst, false);
        srcToDsts.get(dst).put(src, false);

        samplesReservoir[reservoirSize] = key;
        edgeToIndex.put(key, reservoirSize);
    }

    /**
     * add an edge to the waiting room
     * @param src source of the edge to be added
     * @param dst destination of the edge to be added
     */
    private void addEdgeToWaitingRoom(int src, int dst) {
        long key = ((long) src * Integer.MAX_VALUE) + dst;
        samplesWaitingRoom.add(key);
        if (!srcToDsts.containsKey(src)) {
            srcToDsts.put(src, new Int2BooleanOpenHashMap());
        }
        srcToDsts.get(src).put(dst, true);
        if (!srcToDsts.containsKey(dst)) {
            srcToDsts.put(dst, new Int2BooleanOpenHashMap());
        }
        srcToDsts.get(dst).put(src, true);
    }

    /**
     * delete an edge to the waiting room
     * @param key key of the edge to be deleted
     */
    private void deleteEdgeFromWaitingRoom(long key) {

        samplesWaitingRoom.remove(key);
        int src = (int) (key / Integer.MAX_VALUE);
        int dst = (int) (key - ((long) src * Integer.MAX_VALUE));

        Int2BooleanOpenHashMap map = srcToDsts.get(src);
        map.remove(dst);
        if (map.isEmpty()) {
            srcToDsts.remove(src);
        }
        map = srcToDsts.get(dst);
        map.remove(src);
        if (map.isEmpty()) {
            srcToDsts.remove(dst);
        }
    }

    /**
     * delete an edge to the reservoir
     * @param key key of the edge to be deleted
     */
    private void deleteEdgeFromReservoir(long key) {

        int sampleNum = edgeToIndex.size();
        int index = edgeToIndex.remove(key);
        int src = (int) (key / Integer.MAX_VALUE);
        int dst = (int) (key - ((long) src * Integer.MAX_VALUE));

        Int2BooleanOpenHashMap map = srcToDsts.get(src);
        map.remove(dst);
        if (map.isEmpty()) {
            srcToDsts.remove(src);
        }

        map = srcToDsts.get(dst);
        map.remove(src);
        if (map.isEmpty()) {
            srcToDsts.remove(dst);
        }

        if (index < sampleNum - 1) {
            samplesReservoir[index] = samplesReservoir[sampleNum - 1];
            edgeToIndex.put(samplesReservoir[index], index);
        }
    }

    /**
     * get estimated global triangle count
     * @return estimate of global triangle count
     */
    public double getGlobalTriangle() {
        return globalTriangle;
    }

    /**
     * get estimated local triangle counts
     * @return map from nodes to counts
     */
    public Int2DoubleOpenHashMap getLocalTriangle() {
        return nodeToTriangles;
    }

    /**
     * counts triangles with the given edge
     * @param src the source node of the given edge
     * @param dst the destination node of the given edge
     * @param add flag to determine edge insertion and deletion
     */
    private void count(int src, int dst, boolean add) {

        if(!srcToDsts.containsKey(src) || !srcToDsts.containsKey(dst)) {
            return;
        }

        Int2BooleanOpenHashMap srcMap = srcToDsts.get(src);
        Int2BooleanOpenHashMap dstMap = srcToDsts.get(dst);

        if(srcMap.size() > dstMap.size()) {
            Int2BooleanOpenHashMap temp = srcMap;
            srcMap = dstMap;
            dstMap = temp;
        }

        final double weightTwo = Math.max((ER + nb + ng + 0.0) / sizeOfReservoir * (ER + nb + ng - 1.0) / (sizeOfReservoir - 1.0), 1);
        final double weightOne = Math.max((ER + nb + ng + 0.0) / sizeOfReservoir, 1);

        if(add) {
            double countSum = 0;
            for (int neighbor : srcMap.keySet()) {
                if (dstMap.containsKey(neighbor)) {
                    boolean srcFlag = srcMap.get(neighbor);
                    boolean dstFlag = dstMap.get(neighbor);
                    double count = 1;
                    if (srcFlag == false && dstFlag == false) {
                        count = weightTwo;
                    } else if (srcFlag == false || dstFlag == false) {
                        count = weightOne;
                    }
                    nodeToTriangles.addTo(neighbor, count);
                    countSum += count;
                }
            }

            if (countSum > 0) {
                nodeToTriangles.addTo(src, countSum);
                nodeToTriangles.addTo(dst, countSum);
                globalTriangle += countSum;
            }

        }

        else if(lowerBound){

            double countSum = 0;
            for (int neighbor : srcMap.keySet()) {
                if (dstMap.containsKey(neighbor)) {
                    boolean srcFlag = srcMap.get(neighbor);
                    boolean dstFlag = dstMap.get(neighbor);
                    double count = 1;
                    if (srcFlag == false && dstFlag == false) {
                        count = weightTwo;
                    } else if (srcFlag == false || dstFlag == false) {
                        count = weightOne;
                    }
                    double value = nodeToTriangles.addTo(neighbor, - count);
                    if(value < count) {
                        nodeToTriangles.put(neighbor, 0);
                    }
                    countSum += count;
                }
            }

            if (countSum > 0) {
                double value = nodeToTriangles.addTo(src, - countSum);
                if(value < countSum) {
                    nodeToTriangles.put(src, 0);
                }
                value = nodeToTriangles.addTo(dst, - countSum);
                if(value < countSum) {
                    nodeToTriangles.put(dst, 0);
                }
                globalTriangle -= countSum;
                globalTriangle = Math.max(0, globalTriangle);
            }
        }

        else {

            double countSum = 0;
            for (int neighbor : srcMap.keySet()) {
                if (dstMap.containsKey(neighbor)) {
                    boolean srcFlag = srcMap.get(neighbor);
                    boolean dstFlag = dstMap.get(neighbor);
                    double count = 1;
                    if (srcFlag == false && dstFlag == false) {
                        count = weightTwo;
                    } else if (srcFlag == false || dstFlag == false) {
                        count = weightOne;
                    }
                    nodeToTriangles.addTo(neighbor, - count);
                    countSum += count;
                }
            }

            if (countSum > 0) {
                nodeToTriangles.addTo(src, - countSum);
                nodeToTriangles.addTo(dst, - countSum);
                globalTriangle -= countSum;
            }
        }
    }
}
