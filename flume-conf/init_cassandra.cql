CREATE KEYSPACE IF NOT EXISTS app_metrics WITH REPLICATION =  { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
CREATE TABLE IF NOT EXISTS app_metrics.raw_metrics (
                               id int,
                               timestamp timestamp,
                               value int,
                               PRIMARY KEY (id, timestamp));
