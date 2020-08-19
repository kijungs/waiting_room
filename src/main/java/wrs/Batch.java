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

import java.io.*;
import java.util.Map;
import java.util.Random;

/**
 * Batch Process
 */
public class Batch {

    /**
     * Example Code for WRS
     * @throws IOException
     */
    final private static int INS = 0;

    private static int graphType = 0;
    public static void main(String[] args) throws IOException {

        if(args.length < 5) {
            printError();
            System.exit(-1);
        }

        final String inputPath = args[0];
        System.out.println("input_path: " + inputPath);
        final String outputPath = args[1];
        System.out.println("output_path: " + outputPath);
        final int maxSampleNum = Integer.valueOf(args[2]);
        System.out.println("k: " + maxSampleNum);
        final double alpha = Double.valueOf(args[3]);
        System.out.println("alpha: " + alpha);

        graphType = Integer.valueOf(args[4]);
        String graphTypeString = (graphType == INS) ? "Insertion-only" : "Fully Dynamic";
        System.out.println("graph_type: " + graphTypeString);

        WRS wrs;
        if (graphType == INS) {
            wrs = new WRSIns(maxSampleNum, alpha, new Random().nextInt());
        } else {
            wrs = new WRSDel(maxSampleNum, alpha, new Random().nextInt(), true);
        }

        run(wrs, inputPath, "\t");
        output(wrs, outputPath);
    }

    private static void printError() {
        System.err.println("Usage: run.sh graph_type input_path output_path k alpha");
        System.err.println("- k (maximum number of samples) should be an integer greater than or equal to 2.");
        System.err.println("- alpha (relative size of the waiting room) should be a real number in [0,1).");
        System.err.println("- graph_type should be 0 or 1. 0: Insertion-only graph, 1: Fully dynamic graph");
    }

    private static void run(WRS wrs, String inputPath, String delim) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        int count = 0;

        if (graphType == INS) {
            System.out.println("start running WRS_INS...");
        } else {
            System.out.println("start running WRS_DEL...");
        }


        while(true) {

            final String line = br.readLine();
            if(line == null) {
                break;
            }

            int[] edge = parseEdge(line, delim);
            if (graphType == INS) {
                wrs.processEdge(edge[0], edge[1], true);
            } else {
                wrs.processEdge(edge[0], edge[1], edge[2] >= 0);
            }


            if((++count) % 10000 == 0) {
                System.out.println("Number of edges processed: " + count +", estimated number of global triangles: " + wrs.getGlobalTriangle());
            }
        }

        if (graphType == INS) {
            System.out.println("WRS_INS terminated ...");
        } else {
            System.out.println("WRS_DEL terminated ...");
        }
        System.out.println("Estimated number of global triangles: " + wrs.getGlobalTriangle());

        br.close();

    }

    private static void output(WRS wrs, String outputPath) throws IOException {

        System.out.println("writing outputs...\n\n");

        File dir = new File(outputPath);
        try{
            dir.mkdir();
        }
        catch(Exception e){

        }

        BufferedWriter bw = null;
        if (graphType == INS) {
            bw = new BufferedWriter(new FileWriter(outputPath + "/global_count_ins.out"));
        } else {
            bw = new BufferedWriter(new FileWriter(outputPath + "/global_count_del.out"));
        }

        bw.write(String.valueOf(wrs.getGlobalTriangle()));
        bw.newLine();
        bw.close();

        if (graphType == INS) {
            bw = new BufferedWriter(new FileWriter(outputPath + "/local_counts_ins.out"));
        } else {
            bw = new BufferedWriter(new FileWriter(outputPath + "/local_counts_del.out"));
        }
        Map<Integer, Double> localCounts = wrs.getLocalTriangle();
        for(int node : localCounts.keySet()) {
            bw.write(node+"\t"+localCounts.get(node));
            bw.newLine();
        }
        bw.close();

    }

    private static int[] parseEdge(String line, String delim) {

        String[] tokens = line.split(delim);
        int src = Integer.valueOf(tokens[0]);
        int dst = Integer.valueOf(tokens[1]);

        if (graphType == INS) {
            return new int[]{src, dst};
        } else {
            int add = Integer.valueOf(tokens[2]);
            return new int[]{src, dst, add};
        }
    }

}
