#!/bin/bash

nodes=28
name="$nodes"n
nodefile=nodes.txt

sbatch ./run.generic.sh 2 12 $name samplerun 2 g $nodes $nodefile 
