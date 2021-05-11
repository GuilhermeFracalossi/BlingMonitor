package org.network;

import org.controllers.AutoScanLoadingController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GetCameraUrls {
    private static DoubleProperty progress = new SimpleDoubleProperty();;
    public final static List<Future<ScanResult>> futures = new ArrayList<>();
    public List<Object[]> main(int startIp, int endIp, int startPort, int endPort, int numberThreads, int timeout) throws Exception {

        List<Object[]> camerasFound = new ArrayList<Object[]>();

        final ExecutorService es = Executors.newFixedThreadPool(numberThreads);

        String ipCutted = AutoScanLoadingController.ipSelected;

        int i = 1;
        double scanningProgress;

        for (int ipTest = startIp; ipTest <= endIp; ipTest++) {
           for (int portTest = startPort; portTest <= endPort; portTest++) {

               futures.add(portIsOpen(es, ipCutted + ipTest, portTest, timeout));
               scanningProgress = (double) i/((endIp-startIp+1)*(endPort-startPort+1));
               setProgress((double) scanningProgress*30/100);
               i++;
            }
        }
        es.shutdown();
        i=1;
        int allPortsSize = futures.size();

        for (final Future<ScanResult> f : futures) {
            if (f.get().isOpen()) {
                Object[] cameraAddress = new Object[2];

                cameraAddress[0] = f.get().getIp();
                cameraAddress[1] = f.get().getPort();
                camerasFound.add(cameraAddress);
            }
            setProgress(0.3+ ((double)i/allPortsSize)*70/100);
            i++;
        }
        setProgress(1);
        return camerasFound;
}

    public static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port,
                                                final int timeout) {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port), timeout);
                    socket.close();

                    return new ScanResult(ip,port, true);
                } catch (Exception ex) {
                    return new ScanResult(ip,port, false);
                }
            }
        });
    }
    public DoubleProperty progressProperty() {
        return progress ;
    }

    public final double getProgress() {
        return progressProperty().get();
    }

    public final void setProgress(double progress) {
        progressProperty().set(progress);
    }

    public static class ScanResult {
        private int port;
        private String ip;
        private boolean isOpen;

        public ScanResult(String ip,  int port, boolean isOpen) {
            super();
            this.ip = ip;
            this.port = port;
            this.isOpen = isOpen;
        }

        public int getPort() {
            return port;
        }

        public String getIp(){
            return ip;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }

    }
}
