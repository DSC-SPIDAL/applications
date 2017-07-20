package org.pulasthi.dsctools;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by pulasthi on 11/18/16.
 */
public class DistanceCalulationPatchBased {
    public static void main(String args[]){
        Map<String, ArrayList<double[]>> patchNucli = new HashMap<String, ArrayList<double[]>>();
        Map<Integer, String> indexXgroup = new HashMap<Integer, String>();
        ArrayList<String> lines = new ArrayList<String>();
        String dataFolderName = args[0];
        String outputFile = args[1];
        double[][] distances;
        File dataFolder = new File(dataFolderName);
        String subfolders[] = dataFolder.list();
        BufferedReader reader;
        try {

        for (String subfolder : subfolders) {
            File datasubFolder = new File(dataFolderName+ "/" + subfolder);
            String files[] = datasubFolder.list();

            for(String fileName : files) {
                if (!fileName.endsWith(".csv")) continue;

                patchNucli.put(fileName, new ArrayList<double[]>());

                    reader = Files.newBufferedReader(Paths.get(dataFolderName+ "/" +subfolder + "/" + fileName));
                    String line = "";
                    line = reader.readLine();
                    Random random = new Random();
                    int tempcount = 0;

                    while ((line = reader.readLine()) != null) {
                        String splits[] = line.split(",");
                        double[] linedata = Arrays.stream(splits).mapToDouble(Double::parseDouble).toArray();

                        //perform normalizations

//                  >> > Of order Length^1  scale by 1/"EquivalentSphericalRadius - 4"
//                  >> > EquivalentEllipsoidDiameter0 - 1
                        linedata[1] = linedata[1] / linedata[4];
//                  >> > EquivalentEllipsoidDiameter1 - 2
                        linedata[2] = linedata[2] / linedata[4];
//                  >> > EquivalentSphericalPerimeter - 3
                        linedata[3] = linedata[3] / linedata[4];
//                  >> > Perimeter - 9
                        linedata[9] = linedata[9] / linedata[4];
//                  >> > PrincipalMoments0 - 10
                        linedata[9] = linedata[9] / linedata[4];
//                  >> > PrincipalMoments1 - 11
                        linedata[11] = linedata[11] / linedata[4];
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
                        linedata[13] = linedata[13] / (linedata[4] * linedata[4]);

                        patchNucli.get(fileName).add(linedata);
                    }
                    reader.close();

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        distances = new double[patchNucli.size()][patchNucli.size()];
        double tempsingledist;
        double temppatchtopatchdist;
        double max = Double.MIN_VALUE;
        int tempnuCount;
        ArrayList<double[]> temppatch1;
        ArrayList<double[]> temppatch2;
        int matrixSize = patchNucli.size();
        String[] patch1 = new String[matrixSize];
        patchNucli.keySet().toArray(patch1);
        String[] patch2 = new String[matrixSize];
        patchNucli.keySet().toArray(patch2);

        for (int i = 0; i < patch1.length; i++) {
            for (int j = 0; j < patch2.length; j++) {

                if(i == j){
                    continue;
                }
                tempsingledist = 0;
                temppatchtopatchdist = 0;
                tempnuCount = 0;
                temppatch1 = patchNucli.get(patch1[i]);
                temppatch2 = patchNucli.get(patch2[j]);

                for (double[] doubles1 : temppatch1) {
                    for (double[] doubles2 : temppatch2) {
                        tempsingledist = Utils.calculateEuclideanDistance(doubles1,doubles2,doubles1.length);
                        temppatchtopatchdist += tempsingledist;
                        tempnuCount++;
                    }

                }
                temppatchtopatchdist = temppatchtopatchdist/tempnuCount;
                distances[i][j] = temppatchtopatchdist;
                if(temppatchtopatchdist > max){
                    max = temppatchtopatchdist;
                }

            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            FileChannel fc = fos.getChannel();

            short[] row = new short[matrixSize];
            long filePosition = 0l;
            for (int i = 0; i < matrixSize; i++) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(matrixSize * 2);
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
                for (int j = 0; j < matrixSize; j++) {
//                        if(i == 0){
//                            Utils.printMessage(" Distances with " + j + " is : " + localDistances[i][j]);
//                        }

                    row[j] = (short) ((distances[i][j] / max) * Short.MAX_VALUE);
                }
                byteBuffer.clear();
                byteBuffer.asShortBuffer().put(row);
                if (i % 500 == 0) Utils.printMessage(".");
                fc.write(byteBuffer, (filePosition + ((long) i) * matrixSize * 2));
            }

            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
