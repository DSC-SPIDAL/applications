#!/bin/bash

name=1n
nodefile=nodes.$name.txt
nodes=1

sbatch ./run.generic.sh 1 24 $name perf 2 $nodes $nodefile g
