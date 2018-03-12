package wrs;

/**
 * =================================================================================
 *
 * WRS: Waiting Room Sampling for Accurate Triangle Counting in Real Graph Streams
 * Authors: Kijung Shin
 *
 * Version: 1.0
 * Date: May 24, 2017
 * Main Contact: Kijung Shin (kijungs@cs.cmu.edu)
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
 *
 * @author kijungs (kijungs@cs.cmu.edu)
 */
public class Batch {

    /**
     * Example Code for WRS
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        if(args.length < 4) {
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
        final WRS wrs = new WRS(maxSampleNum, alpha, new Random().nextInt());
        run(wrs, inputPath, "\t");
        output(wrs, outputPath);
        return;
    }

    private static void printError() {
        System.err.println("Usage: run.sh input_path output_path k alpha");
        System.err.println("- k (maximum number of samples) should be an integer greater than or equal to 2.");
        System.err.println("- alpha (relative size of the waiting room) should be a real number in [0,1).");
    }

    private static void run(WRS wrs, String inputPath, String delim) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        int count = 0;

        System.out.println("start running WRS...");

        while(true) {

            final String line = br.readLine();
            if(line == null) {
                break;
            }

            int[] edge = parseEdge(line, delim);
            wrs.processEdge(edge[0], edge[1]);

            if((++count) % 10000 == 0) {
                System.out.println("Number of edges processed: " + count +", estimated number of global triangles: " + wrs.getGlobalTriangle());
            }
        }

        System.out.println("WRS terminated ...");
        System.out.println("Estimated number of global triangles: " + wrs.getGlobalTriangle());

        br.close();

    }

    private static void output(WRS wrs, String outputPath) throws IOException {

        System.out.println("writing outputs...");

        File dir = new File(outputPath);
        try{
            dir.mkdir();
        }
        catch(Exception e){

        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath + "/global_count.out"));
        bw.write(String.valueOf(wrs.getGlobalTriangle()));
        bw.newLine();
        bw.close();

        bw = new BufferedWriter(new FileWriter(outputPath + "/local_counts.out"));
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

        return new int[]{src, dst};
    }

}
