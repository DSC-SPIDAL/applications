#!/bin/bash
cp=fusheng_wang_bio-1.0-SNAPSHOT-jar-with-dependencies.jar
dataFolder=/home/pulasthi/work/FushengWang/tutorial/data
FileName=TCGA-55-8094-01Z-00-DX1.8dc29615-e124-4f17-81a1-c0b20c38d12c
OuputFolder=/home/pulasthi/work/FushengWang/tutorial/results
OutputFileName=singleimage
numPoints=2000

java -cp $cp org.pulasthi.dsctools.SingleFileGeneration $dataFolder $FileName $OuputFolder $OutputFileName $numPoints
