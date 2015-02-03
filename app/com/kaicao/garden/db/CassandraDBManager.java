package com.kaicao.garden.db;

import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import com.kaicao.garden.models.Garden;
import com.kaicao.garden.utils.DBManagerException;
import com.netflix.astyanax.AstyanaxConfiguration;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.Host;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.SimpleAuthenticationCredentials;
import com.netflix.astyanax.cql.CqlFamilyFactory;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by kaicao on 03/02/15.
 */
@Singleton
public class CassandraDBManager implements DBManager {

    private class CassandraProperties {
        private final static String PROPERTY_CASSANDRA_HOSTS = "cassandra.hosts";
        private final static String PROPERTY_CASSANDRA_CQL_PORT = "cassandra.cql.port";
        private final static String PROPERTY_CASSANDRA_USERNAME = "cassandra.username";
        private final static String PROPERTY_CASSANDRA_PASSWORD = "cassandra.password";
        private final static String PROPERTY_CASSANDRA_VERSION = "cassandra.version";
        private final static String PROPERTY_CASSANDRA_CQL_VERSION = "cassandra.cql.version";
        private final static String PROPERTY_CASSANDRA_CLUSTERNAME = "cassandra.clustername";
        private final static String PROPERTY_CASSANDRA_KEYSPACE = "cassandra.keyspace";
        private final Properties properties;

        private CassandraProperties() throws IOException {
            properties = new Properties();
            try (InputStream inputStream = CassandraDBManager.class.getClassLoader().getResourceAsStream("cassandra.properties")) {
                properties.load(inputStream);
            }
        }

        private String[] getHosts() {
            return get(PROPERTY_CASSANDRA_HOSTS).split(",");
        }

        private int getPort() {
            return Integer.parseInt(get(PROPERTY_CASSANDRA_CQL_PORT));
        }

        private List<HostAndPort> getSeeds() {
            List<HostAndPort> hostAndPorts = new ArrayList<>();
            int port = getPort();
            for (String host : getHosts()) {
                hostAndPorts.add(HostAndPort.fromParts(host, port));
            }
            return hostAndPorts;
        }

        private String getUsername() {
            return get(PROPERTY_CASSANDRA_USERNAME);
        }

        private String getPassword() {
            return get(PROPERTY_CASSANDRA_PASSWORD);
        }

        private String getVersion() {
            return get(PROPERTY_CASSANDRA_VERSION);
        }

        private String getCQLVersion() {
            return get(PROPERTY_CASSANDRA_CQL_VERSION);
        }

        private String getClusterName() {
            return get(PROPERTY_CASSANDRA_CLUSTERNAME);
        }

        private String getKeyspace() {
            return get(PROPERTY_CASSANDRA_KEYSPACE);
        }

        private String get(String property) {
            return properties.getProperty(property);
        }
    }

    private final static Logger LOG = LoggerFactory.getLogger(CassandraDBManager.class);
    private AstyanaxContext<Keyspace> context;
    private Keyspace keyspace;

    @Override
    public void init() {
        try {
            CassandraProperties properties = new CassandraProperties();
            // Astyanax Reference: https://github.com/Netflix/astyanax/wiki/Getting-Started
            Supplier<List<Host>> hostSupplier = new Supplier<List<Host>>() {
                @Override
                public List<Host> get() {
                    List<Host> hosts = new ArrayList<>();
                    for (HostAndPort hostAndPort : properties.getSeeds()) {
                        hosts.add(new Host(hostAndPort.toString(), 9160));
                    }
                    return hosts;
                }
            };
            AstyanaxConfiguration astyanaxConfiguration = new AstyanaxConfigurationImpl()
                    .setDiscoveryType(NodeDiscoveryType.TOKEN_AWARE)    // Can't use NONE or RING with CqlFamilyFactory, otherwise session won't be created since those won't use hostSupplier provided to init session
                    .setConnectionPoolType(ConnectionPoolType.ROUND_ROBIN)
                    .setCqlVersion(properties.getCQLVersion())
                    .setTargetCassandraVersion(properties.getVersion());
            ConnectionPoolConfiguration poolConfiguration = new ConnectionPoolConfigurationImpl("GardenConnectionPool")
                    .setSeeds(Joiner.on(",").join(properties.getSeeds()))
                    .setPort(properties.getPort())
                    .setMaxConnsPerHost(3)
                    .setAuthenticationCredentials(new SimpleAuthenticationCredentials(properties.getUsername(), properties.getPassword()));
            context = new AstyanaxContext.Builder()
                    .forCluster(properties.getClusterName())
                    .forKeyspace(properties.getKeyspace())
                    .withHostSupplier(hostSupplier)
                    .withAstyanaxConfiguration(astyanaxConfiguration)
                    .withConnectionPoolConfiguration(poolConfiguration)
                    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                    .buildKeyspace(CqlFamilyFactory.getInstance());

            context.start();
            keyspace = context.getClient();
            ImmutableMap<String, Object> keyspaceConfigs = ImmutableMap.<String, Object>builder()
                    .put("strategy_class", "SimpleStrategy")
                    .put("strategy_options", ImmutableMap.builder().put("replication_factor", "3").build())
                    .put("durable_writes", true)
                    .build();
            //keyspace.createKeyspaceIfNotExists(keyspaceConfigs);    // BUG in Astyanax createKeyspaceIfNotExists, system.local keyspace_name does not exist, in Cassandra 2.0 should check system.schema_keyspaces
            try {
                keyspace.dropKeyspace();
            } catch (InvalidQueryException ignored) {}
            keyspace.createKeyspace(keyspaceConfigs);
        } catch (Exception e) {
            LOG.error("Failed to init Cassandra", e);
            throw new DBManagerException("Failed to init Cassandra", e);
        }
    }

    @Override
    public void insert(Garden garden) throws DBManagerException {

    }

    @Override
    public int insert(List<Garden> gardens) throws DBManagerException {
        return 0;
    }

    @Override
    public Garden findById(String id) throws DBManagerException {
        return null;
    }

    @Override
    public List<Garden> queryValueRange(QueryRange<Integer> range) throws DBManagerException {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void close() throws DBManagerException {
        context.shutdown();
    }
}
