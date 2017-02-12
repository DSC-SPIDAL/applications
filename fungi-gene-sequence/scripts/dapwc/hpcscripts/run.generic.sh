#!/bin/bash

#SBATCH -N 4
#SBATCH --tasks-per-node=24
#SBATCH --time=12:00:00

cp=$HOME/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:$HOME/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:$HOME/.m2/repository/habanero-java-lib/habanero-java-lib/0.1.1/habanero-java-lib-0.1.1.jar:$HOME/.m2/repository/ompi/ompijavabinding/1.10.1/ompijavabinding-1.10.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/dapwc/1.0-ompi1.8.1/dapwc-1.0-ompi1.8.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/common/1.0/common-1.0.jar

wd=`pwd`
x='x'

cps=6
spn=4
cpn=$(($cps*$spn))

tpp=$1
ppn=$2
nodes=$6

memmultype=g
xmx=1
if [ "$5" ];then
    xmx=$5
    memmultype=$8
fi

bindToUnit=core
if [ $tpp -gt 1 ];then
    bindToUnit=none
fi

if [ $ppn -gt $cpn ];then
    bindToUnit=hwthread
fi

pat=$tpp$x$ppn$x$nodes

#TimingFile=$pat.$xmx.$memmultype.$4.$3.timing.txt
#SummaryFile=$pat.$xmx.$memmultype.$4.$3.summary.txt
#ClusterFile=$pat.$xmx.$memmultype.$4.$3.cluster.txt

PWC_OPS=""


opts="-XX:+UseG1GC -Xms256m -Xmx"$xmx"$memmultype"

#kill.java.sh $7
echo "Running $pat on `date`" >> status.txt

$BUILD/bin/mpirun --mca btl ^tcp --report-bindings java $opts  $PWC_OPS -cp $cp edu.indiana.soic.spidal.dapwc.Program -c config.properties -n $nodes -t $tpp 2>&1 | tee $pat.$xmx.$memmultype.$4.$3.out.txt
echo "Finished $pat on `date`" >> status.txt

