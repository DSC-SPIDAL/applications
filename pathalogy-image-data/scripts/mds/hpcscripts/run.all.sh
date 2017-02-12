#!/bin/bash

nodes=4
name="$nodes"n
nodefile=nodes.txt

sbatch ./run.generic.sh 1 24 $name samplerun 2 g $nodes $nodefile 
