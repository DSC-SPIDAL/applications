#!/bin/bash
paste $1 $2 $2 | column -s $'\t' -t > added.txt
awk '{print $1,$2,$3,$4,$7,$9}' added.txt > merged.txt
sed 's/\t/ /g' merged.txt > points.formatted.txt
rm added.txt
rm merged.txt
