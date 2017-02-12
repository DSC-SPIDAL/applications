#!/bin/bash
outname=$1".formatted"
sed 's/\t/ /g' $1 > $outname
