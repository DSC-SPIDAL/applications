package org.pulasthi.dsctools;

import mpi.MPIException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by pulasthi on 11/18/16.
 */
public class DistanceCalulationPatchBasedParallel {
    public static void main(String args[]){
        Map<String, ArrayList<double[]>> patchNucli = new HashMap<String, ArrayList<double[]>>();
        Map<String, String> patchLabel = new HashMap<String, String>();
        Map<Integer, String> indexXgroup = new HashMap<Integer, String>();
        ArrayList<String> lines = new ArrayList<String>();
        String dataFolderName = args[0];
        String outputFolderName = args[1];
        String outFileName = args[2];
        double[][] localDistances;
        File dataFolder = new File(dataFolderName);
        String subfolders[] = dataFolder.list();
        BufferedReader reader;
        double means[] =  new double[17];
        double sd[] =  new double[17];
        double newMean = 0;
        double newSd = 0.1;
        int totalcount = 0;

        try {
            ParallelOps.setupParallelism(args);
            Utils.printMessage("Starting with " + ParallelOps.worldProcsCount + "Processes");


            for (String subfolder : subfolders) {
                File datasubFolder = new File(dataFolderName+ "/" + subfolder);
                String files[] = datasubFolder.list();
                String label = "";
                if(subfolder.contains("good")){
                    label = "good";
                }else if(subfolder.contains("under")){
                    label = "under";
                }else if(subfolder.contains("over")){
                    label = "over";
                }

                for(String fileName : files) {
                    if (!fileName.endsWith(".csv")) continue;

                    patchNucli.put(fileName, new ArrayList<double[]>());
                    patchLabel.put(fileName, label);

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
                        IntStream.range(0, linedata.length).forEach(i -> means[i] += linedata[i]);
                        totalcount++;
                        patchNucli.get(fileName).add(linedata);
                    }
                    reader.close();

                }
            }
            ParallelOps.setParallelDecomposition(patchNucli.size(),17);
            final int finalTotalcount = totalcount;
            IntStream.range(0, means.length).forEach(i -> means[i] /= finalTotalcount);
            Utils.printMessage("means " + Arrays.toString(means));
            Utils.printMessage("Number of patches " + patchNucli.size());
            totalcount = 0;
            ArrayList<double[]> temp;
            for (String s : patchNucli.keySet()) {
                temp = patchNucli.get(s);
                for (double[] row : temp) {
                    for (int j = 0; j < row.length; j++) {
                        sd[j] += (row[j] - means[j])*(row[j] - means[j]);
                    }
                    totalcount++;
                }
            }
            final int finalTotalcount1 = totalcount;
            IntStream.range(0, sd.length).forEach(i -> sd[i] = Math.sqrt(sd[i]/ finalTotalcount1));
            Utils.printMessage("sd's : " + Arrays.toString(sd));

            for (String s : patchNucli.keySet()) {
                temp = patchNucli.get(s);
                for (double[] row : temp) {
                    for (int j = 0; j < row.length; j++) {
                        if (sd[j] == 0) continue;
                        // special case remove the if later
                        if (j == 4) {
                            row[j] = newMean + ((row[j] - means[j]) / sd[j]) * 0.4;
                        } else {
                            row[j] = newMean + ((row[j] - means[j]) / sd[j]) * newSd;
                        }
                    }
                }
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MPIException e) {
            e.printStackTrace();
        }


        localDistances = new double[ParallelOps.procRowCount][patchNucli.size()];
        double tempsingledist;
        double temppatchtopatchdist;
        double max = Double.MIN_VALUE;
        int tempnuCount;
        ArrayList<double[]> temppatch1;
        ArrayList<double[]> temppatch2;
        int matrixSize = patchNucli.size();
        String[] patch1 = new String[matrixSize];
        patchNucli.keySet().toArray(patch1);
        Arrays.sort(patch1);
        String[] patch2 = new String[matrixSize];
        patchNucli.keySet().toArray(patch2);
        Arrays.sort(patch2);
        if(ParallelOps.worldProcRank == 0) {
            try {
                PrintWriter printWriterIndexes = new PrintWriter(new FileWriter(outputFolderName + "/" + outFileName + "_indexInfo.data"));
                int index = 0;
                for (String s : patch2) {
                    String label = patchLabel.get(s);
                    int clusterNum = 0;
                    if (label.equals("under")) {
                        clusterNum = 0;
                    } else if (label.equals("good")) {
                        clusterNum = 1;
                    } else if (label.equals("over")) {
                        clusterNum = 2;
                    }
                    printWriterIndexes.println(index + "\t" + s + "\t" + label + "\t" + clusterNum);
                    index++;
                }
                printWriterIndexes.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for (int i = 0; i < ParallelOps.procRowCount; i++) {
            for (int j = 0; j < patch2.length; j++) {
                tempsingledist = 0;
                temppatchtopatchdist = 0;
                tempnuCount = 0;
                temppatch1 = patchNucli.get(patch1[i + ParallelOps.procRowStartOffset]);
                temppatch2 = patchNucli.get(patch2[j]);

                for (double[] doubles1 : temppatch1) {
                    for (double[] doubles2 : temppatch2) {
                        tempsingledist = Utils.calculateEuclideanDistance(doubles1,doubles2,doubles1.length);
                        temppatchtopatchdist += tempsingledist;
                        tempnuCount++;
                    }

                }
                temppatchtopatchdist = temppatchtopatchdist/tempnuCount;
                localDistances[i][j] = temppatchtopatchdist;
                if(temppatchtopatchdist > max){
                    max = temppatchtopatchdist;
                }

            }
        }

        try {
            max = ParallelOps.allReduceMax(max);

            FileOutputStream fos = new FileOutputStream(outputFolderName + "/" + outFileName);
            FileChannel fc = fos.getChannel();

            short[] row = new short[matrixSize];
            long filePosition = ((long) ParallelOps.procRowStartOffset) * matrixSize * 2;
            for (int i = 0; i < ParallelOps.procRowCount; i++) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(matrixSize * 2);
                byteBuffer.order(ByteOrder.BIG_ENDIAN);
                for (int j = 0; j < matrixSize; j++) {
//                        if(i == 0){
//                            Utils.printMessage(" Distances with " + j + " is : " + localDistances[i][j]);
//                        }

                    row[j] = (short) ((localDistances[i][j] / max) * Short.MAX_VALUE);
                }
                byteBuffer.clear();
                byteBuffer.asShortBuffer().put(row);
                if (i % 500 == 0) Utils.printMessage(".");
                fc.write(byteBuffer, (filePosition + ((long) i) * matrixSize * 2));
            }

            fc.close();
            ParallelOps.tearDownParallelism();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MPIException e) {
            e.printStackTrace();
        }

    }
}
