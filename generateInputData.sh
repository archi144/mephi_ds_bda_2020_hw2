#!/bin/bash
if [ $# -ne 2 ]; then
echo "Please enter two parameters - count of generated metricId and count generated timestamps per metricId"
else
metricId_count=$1
timestamps_count=$2
#echo "Count of metricId:$metric_Id_count \t count of timestamps per metricId: $timestamps_count"
#echo "==============================="
metricId=1
date=$(date +%s000)  # zeros transform to ms
values=($(seq 10 10 90))
while [ $metricId -le $metricId_count ]; do
for ((i=0; i<$timestamps_count; i++)); do
date=$(expr $date + 10000)
metric=$(echo "$metricId,$date,${values[$(expr $RANDOM % ${#values[@]})]}")
echo "$metric"
echo "$metric" >> input/log.txt
done
let "metricId += 1"
done
fi
