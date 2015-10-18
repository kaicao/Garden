package com.kaicao.fx.importer;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.datastax.driver.core.Cluster;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kaicao.fx.FXModule;
import com.kaicao.fx.repository.FXTickDataRepository;
import com.kaicao.fx.service.FXTickDataService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by kaicao on 29/09/15.
 */
public class TrueFXTickDataImporter {

    private static final MetricRegistry METRICS = new MetricRegistry();
    private static FXTickDataService service;

    private static final String CLUSTER_NAME = "mycluster";
    private static final String CONTACT_POINT_IP = "127.0.0.1";
    private static final int CQL_PORT = 9042;
    private static Cluster CLUSTER;

    public static void main(String[] args) throws Exception {
        initGuice();
        File fxDataFolder = new File("resources/fx_data");
        if (!fxDataFolder.exists()) {
            throw new IllegalStateException("fx_data folder doesn™t exist");
        }
        for (File file : fxDataFolder.listFiles((dir, name) -> {
            return name.endsWith(".zip");
        })) {
            streamReadFxDataFile(file);
            break;  // Intended to break, took around 15m to finish 1 file, unless needed just 1 file test is enough
        }
        ConsoleReporter reporter = ConsoleReporter.forRegistry(METRICS).build();
        reporter.report();
        reporter.stop();
        System.exit(0);
    }

    private static void initGuice() {
        initCassandraCluster();
        Injector injector = Guice.createInjector(new FXModule());
        injector.getInstance(FXTickDataRepository.class).init(CLUSTER);
        service = injector.getInstance(FXTickDataService.class);
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

    private static void streamReadFxDataFile(File file) {
        Timer timer = METRICS.timer(file.getName() + "_Timer");
        try (ZipFile zipFile = new ZipFile(file);
             Timer.Context context = timer.time()
        ) {
            ZipEntry entry = zipFile.entries().nextElement();
            if (entry == null) {
                throw new RuntimeException("Entry is null for " + file.getAbsolutePath());
            }
            Timer entryTimer = METRICS.timer(file.getName() + "_" + entry.getName() + "_Timer");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(zipFile.getInputStream(entry)));
            ) {
                System.out.println("Read from " + entry.getName());
                CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
                // DON'T use parser.getRecords (load everything to memory)
                parser.forEach(record -> {
                    try (Timer.Context context1 = entryTimer.time()) {
                        String currencyPair = record.get(0);
                        String dateTimeStr = record.get(1);
                        double bid = Double.parseDouble(record.get(2));
                        double ask = Double.parseDouble(record.get(3));
                        service.saveFXTickRecord(currencyPair, dateTimeStr, bid, ask);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
