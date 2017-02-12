#!/bin/bash
cp=/home/pulasthi/git/spidal/applications/pathalogy-image-data/target/pathology_image_dat-jar-with-dependencies.jar
dataFolder=/home/pulasthi/git/spidal/applications/pathalogy-image-data/sampledata/data
FileName=TCGA-55-8094-01Z-00-DX1.8dc29615-e124-4f17-81a1-c0b20c38d12c
OuputFolder=/home/pulasthi/git/spidal/applications/pathalogy-image-data/sampledata
OutputFileName=singleimage
numPoints=2000

java -cp $cp org.pulasthi.dsctools.SingleFileGeneration $dataFolder $FileName $OuputFolder $OutputFileName $numPoints
