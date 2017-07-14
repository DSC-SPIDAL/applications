package org.pulasthi.dsctools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by pulasthi on 11/18/16.
 */
public class SingleFileGenerationV2 {
    public static void main(String args[]){
        String fileLocation = args[0];
        boolean both = Boolean.valueOf(args[1]);
        String prefix = args[2];
        String outFileName = args[3];
        double prob = 0.0;
        int clusterKey = -1;
        int pointsLimitperFile = 30000;
        int count = 0;

        ArrayList<String> fileList = new ArrayList<String>();
        fileList.add("lgg_G2_good_1214.csv");
        fileList.add("lgg_G2_over_1214.csv");
        fileList.add("lgg_G2_under_1214.csv");
        fileList.add("luad_good_0113.csv");
        fileList.add("luad_over_0113.csv");
        fileList.add("luad_under_0113.csv");

        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(fileLocation + "/" + outFileName + ".data"));
            PrintWriter printWriterIndexes = new PrintWriter(new FileWriter(fileLocation + "/" + outFileName + "_indexInfo.data"));
            BufferedReader reader;
            for (String file : fileList) {

                //filter the wanted files
                if(!both){
                    if(!file.startsWith(prefix)) continue;
                }

                if (file.equals("lgg_G2_good_1214.csv")) {
                    prob = 0.219901045;
                    clusterKey = 0;
                }
                if (file.equals("lgg_G2_over_1214.csv")) {
                    prob = 0.30580728;
                    clusterKey = 1;
                }
                if (file.equals("lgg_G2_under_1214.csv")) {
                    prob = 0.271803142;
                    clusterKey = 2;
                }
                if (file.equals("luad_good_0113.csv")) {
                    prob = 0.185448476;
                    clusterKey = 3;
                }
                if (file.equals("luad_over_0113.csv")) {
                    prob = 0.235386426;
                    clusterKey = 4;
                }
                if (file.equals("luad_under_0113.csv")) {
                    prob = 0.302727575;
                    clusterKey = 5;
                }
                reader = Files.newBufferedReader(Paths.get(fileLocation + "/" + file));
                String line = "";
                line = reader.readLine();
                Random random = new Random();
                int tempcount = 0;

                while((line = reader.readLine()) != null) {
                    // skip if the prob is not met
                    if(! (random.nextDouble() < prob)) continue;

                    if(tempcount > pointsLimitperFile) break;

                    String splits[] = line.split(",");
                    double[] linedata = Arrays.stream(splits).mapToDouble(Double::parseDouble).toArray();

                    //perform normalizations

//                  >> > Of order Length^1  scale by 1/"EquivalentSphericalRadius - 4"
//                  >> > EquivalentEllipsoidDiameter0 - 1
                    linedata[1] = linedata[1]/linedata[4];
//                  >> > EquivalentEllipsoidDiameter1 - 2
                    linedata[2] = linedata[2]/linedata[4];
//                  >> > EquivalentSphericalPerimeter - 3
                    linedata[3] = linedata[3]/linedata[4];
//                  >> > Perimeter - 9
                    linedata[9] = linedata[9]/linedata[4];
//                  >> > PrincipalMoments0 - 10
                    linedata[9] = linedata[9]/linedata[4];
//                  >> > PrincipalMoments1 - 11
                    linedata[11] = linedata[11]/linedata[4];
//
//                  >> > Of order Length^1 but leave unscaled
//                  >> > EquivalentSphericalRadius
//                  >> >
//
//                  >> > Of Order Length^0 DO NOT SCALE
//                  >> > Elongation - 0
//                  >> > Flatness - 5
//                  >> > Roundness - 12
//                  >> > StdB - 14
//                  >> > StdG - 15
//                  >> > StdR - 16
//                  >> > MeanB - 6
//                  >> > MeanG - 7
//                  >> > MeanR - 8
//
//                  >> >
//                  >> > Of Order Length ^2 scale by (1/"EquivalentSphericalRadius")^2
//                  >> > SizeInPixels - 13
                    linedata[13] = linedata[13]/(linedata[4]*linedata[4]);

                    printWriter.println(Arrays.toString(linedata).substring(1,Arrays.toString(linedata).length() - 1));
                    printWriterIndexes.println("" + count + "," + file + "," + clusterKey);
                    tempcount++;
                    count++;
                }


            }
            printWriter.flush();
            printWriter.close();
            printWriterIndexes.flush();
            printWriterIndexes.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
