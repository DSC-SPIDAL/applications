#!/bin/bash
cp=../../target/pathology_image_dat-jar-with-dependencies.jar
dataFolder=../../sampledata/data
FileName=TCGA-55-8094-01Z-00-DX1.8dc29615-e124-4f17-81a1-c0b20c38d12c
OuputFolder=../../sampledata
OutputFileName=singleimage
numPoints=2000

java -cp $cp org.pulasthi.dsctools.SingleFileGeneration $dataFolder $FileName $OuputFolder $OutputFileName $numPoints
