package com.kaicao.passivedns;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.datastax.driver.core.Cluster;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaicao.passivedns.entity.PassiveDNSLookupEntity;

public class PassiveDNSMain {

    private static final MetricRegistry METRICS = new MetricRegistry();
    private static final String CLUSTER_NAME = "mycluster";
    private static final String CONTACT_POINT_IP = "127.0.0.1";
    private static final int CQL_PORT = 9042;
    private static Cluster CLUSTER;
    private static PassiveDNSRepository repository;

    public static void main(String[] args) {
        initGuice();
        String lookup = "lookup";

        Timer timer = METRICS.timer("Record1_Timer");
        for (int j = 0; j < 2; j++) {
            // insert then update
            for (int i = 0; i < 100_000; i++) {
                // TODO : Using more threads to increase concurrency
                PassiveDNSLookupAdd add = new PassiveDNSLookupAdd(
                        lookup, PassiveDNSLookupEntity.DNS_RECORD_TYPE.A,
                        i % 2 == 0 ? PassiveDNSLookupEntity.LOOKUP_TYPE.QUERY : PassiveDNSLookupEntity.LOOKUP_TYPE.ANSWER,
                        "result_" + i,
                        System.currentTimeMillis(),
                        i % 2 == 0 ? "query" : null,
                        i % 2 != 0 ? "answer" : null);

                try (Timer.Context context = timer.time()) {
                    PassiveDNSLookupEntity entity = repository.record1(add);
                    //System.out.println(entity);
                }
            }
        }

        ConsoleReporter reporter = ConsoleReporter.forRegistry(METRICS).build();
        reporter.report();
        reporter.stop();
        System.exit(0);
    }

    private static void initGuice() {
        initCassandraCluster();
        Injector injector = Guice.createInjector(new PassiveDNSModule());
        repository = injector.getInstance(PassiveDNSRepository.class);
        repository.init(CLUSTER);
    }

    private static void initCassandraCluster() {
        if (CLUSTER != null) {
            return;
        }
        CLUSTER = Cluster.builder()
                .withClusterName(CLUSTER_NAME)
                .addContactPoint(CONTACT_POINT_IP)
                .withPort(CQL_PORT)
                .build();
    }

}
