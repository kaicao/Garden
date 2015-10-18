CREATE KEYSPACE fx WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : 2
};

CREATE KEYSPACE passivedns WITH REPLICATION = {
    'class' : 'SimpleStrategy',
    'replication_factor' : 1
};