//package com.example.homeelecation.ui;
//
//import org.checkerframework.common.aliasing.qual.Unique;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//public class orderID {
//
//   // public class IdFactoryImpl implements IdFactory {
//        private final String hostName;
//        private final long creationTimeMillis;
//        private long lastTimeMillis;
//        private long discriminator;
//
//        public IdFactoryImpl() throws UnknownHostException {
//            this.hostName = InetAddress.getLocalHost().getHostAddress();
//            this.creationTimeMillis = System.currentTimeMillis();
//            this.lastTimeMillis = creationTimeMillis;
//        }
//
//        public synchronized Serializable createId() {
//            String id;
//            long now = System.currentTimeMillis();
//
//            if (now == lastTimeMillis) {
//                ++discriminator;
//            } else {
//                discriminator = 0;
//            }
//
//            // creationTimeMillis used to prevent multiple instances of the JVM
//            // running on the same host returning clashing IDs.
//            // The only way a clash could occur is if the applications started at
//            // exactly the same time.
//            id = String.format("%s-%d-%d-%d", hostName, creationTimeMillis, now, discriminator);
//            lastTimeMillis = now;
//
//            return id;
//        }
//
//        public static void main(String[] args) throws UnknownHostException {
//            IdFactory fact = new IdFactoryImpl();
//
//            for (int i=0; i<1000; ++i) {
//                System.err.println(fact.createId());
//            }
//        }
//   // }
//}
