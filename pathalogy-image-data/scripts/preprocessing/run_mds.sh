#!/bin/bash
#Simple script to run on local machine

cp=$HOME/.m2/repository/com/google/guava/guava/15.0/guava-15.0.jar:$HOME/sali/software/jdk1.8.0/jre/../lib/tools.jar:$HOME/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/common/1.0/common-1.0.jar:$HOME/.m2/repository/habanero-java-lib/habanero-java-lib/0.1.4-SNAPSHOT/habanero-java-lib-0.1.4-SNAPSHOT.jar:$HOME/.m2/repository/net/java/dev/jna/jna/4.1.0/jna-4.1.0.jar:$HOME/.m2/repository/net/java/dev/jna/jna-platform/4.1.0/jna-platform-4.1.0.jar:$HOME/.m2/repository/net/openhft/affinity/3.0/affinity-3.0.jar:$HOME/.m2/repository/net/openhft/compiler/2.2.0/compiler-2.2.0.jar:$HOME/.m2/repository/net/openhft/lang/6.7.2/lang-6.7.2.jar:$HOME/.m2/repository/ompi/ompijavabinding/1.10.1/ompijavabinding-1.10.1.jar:$HOME/.m2/repository/org/kohsuke/jetbrains/annotations/9.0/annotations-9.0.jar:$HOME/.m2/repository/org/ow2/asm/asm/5.0.3/asm-5.0.3.jar:$HOME/.m2/repository/org/slf4j/slf4j-api/1.7.12/slf4j-api-1.7.12.jar:$HOME/.m2/repository/org/xerial/snappy/snappy-java/1.1.1.6/snappy-java-1.1.1.6.jar:$HOME/.m2/repository/edu/indiana/soic/spidal/damds/1.1/damds-1.1.jar

confFile=/home/pulasthi/work/FushengWang/tutorial/config.properties

mpirun -n 2 java -cp $cp edu.indiana.soic.spidal.damds.Program -c $confFile -n 2 -t 1
