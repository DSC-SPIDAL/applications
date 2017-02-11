#!/bin/bash
#Simple script to run on local machine

cp=$HOME/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:$HOME/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:$HOME/.m2/repository/habanero-java-lib/habanero-java-lib/0.1.1/habanero-java-lib-0.1.1.jar:$HOME/.m2/repository/ompi/ompijavabinding/1.10.1/ompijavabinding-1.10.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/dapwc/1.0-ompi1.8.1/dapwc-1.0-ompi1.8.1.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/common/1.0/common-1.0.jar

confFile=/home/tutorial/dapwc/config.properties

mpirun -n 4 java -cp $cp edu.indiana.soic.spidal.dapwc.Program -c $confFile -n 1 -t 4
