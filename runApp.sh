#!/bin/bash
hdfs dfs -rm -f homework2-1.0-SNAPSHOT-jar-with-dependencies.jar
hdfs dfs -put target/homework2-1.0-SNAPSHOT-jar-with-dependencies.jar
spark-submit \
--class 'bd.homework2.SparkSQLApp' \
--master yarn \
--deploy-mode cluster \
--num-executors 1 \
--executor-memory 2g \
--executor-cores 4 \
--driver-memory 2g \
--driver-cores 4 \
hdfs:///user/root/homework2-1.0-SNAPSHOT-jar-with-dependencies.jar \
1m
