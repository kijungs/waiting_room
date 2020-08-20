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


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Example using WRS_DELj
 */
public class ExampleDel {

    /**
     * Example Code for WRS_DEL
     * @throws IOException
     */
    public static void main(String[] ar) throws IOException {
        final String dataPath = "example_graph_dynamic.txt";
        final String delim = "\t";
        final WRSDel wrs = new WRSDel(35000, 0.1, 0);

        BufferedReader br = new BufferedReader(new FileReader(dataPath));

        int count = 0;
        while(true) {

            final String line = br.readLine();
            if(line == null) {
                break;
            }

            int[] edge = parseEdge(line, delim);
            wrs.processEdge(edge[0], edge[1], edge[2] >= 0);

            if((++count) % 10000 == 0) {
                System.out.println("Number of Edges Processed: " + count +", Estimated Number of Global Triangles: " + wrs.getGlobalTriangle());
            }
        }

        br.close();
    }

    private static int[] parseEdge(String line, String delim) {
        String[] tokens = line.split(delim);
        int src = Integer.valueOf(tokens[0]);
        int dst = Integer.valueOf(tokens[1]);
        int add = Integer.valueOf(tokens[2]);

        return new int[]{src, dst, add};
    }
}
