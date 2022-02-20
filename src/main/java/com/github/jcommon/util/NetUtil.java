package com.github.jcommon.util;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.Set;

/**
 * 网络工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-11-16
 */
public final class NetUtil {
    /**
     * 当前主机名称
     */
    private static final String HOST_NAME;
    /**
     * 与外网通讯网卡的IP
     */
    private static final String HOST_IP;
    /**
     * 获取与外网通讯网卡MAC地址
     */
    private static final String MAC_HEX;
    /**
     * 获取与外网通讯网卡MAC地址
     */
    private static final long MAC_LONG;
    /**
     * 当前JVM进程ID
     */
    private static final int PID;
    /**
     * 服务端口
     * 在SpringBoot环境会通过META-INF/spring.factories配置的{@link SpringBootConfiguration.WebServerListener#onApplicationEvent(org.springframework.boot.web.context.WebServerInitializedEvent)}设置
     */
    private static int SERVER_PORT = -1;

    static {
        String ip = null;
        String mac = null;
        long macLong = 0;
        try {
            InetAddress inetAddress = getInetAddress();
            ip = inetAddress.getHostAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            if (networkInterface != null) {
                byte[] macBs = networkInterface.getHardwareAddress();
                mac = StringUtil.toHex(macBs).toUpperCase();
                macLong = toLong(macBs);
            }
        } catch (Exception e) {
            // LOGGER.error(e);
        }

        int pid = 0;
        String host = null;
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            int pos = name.indexOf('@');
            if (pos > -1) {
                pid = Integer.parseInt(name.substring(0, pos));
                host = name.substring(pos + 1);
            }
        } catch (Exception e) {
            // LOGGER.error(e);
        }

        HOST_NAME = host;
        HOST_IP = ip;
        MAC_HEX = mac;
        MAC_LONG = macLong;
        PID = pid;
    }

    /**
     * 获取与外网通讯网卡
     */
    private static InetAddress getInetAddress() {
        // 候选地址
        InetAddress candidateInetAddress = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress address = inetAddresses.nextElement();
                    /*
                    // 打印网卡符合类型
                    System.out.println(inetAddress);
                    for (Method method : InetAddress.class.getMethods()) {
                        if (method.getName().matches("is.*") &&
                                method.getParameterTypes().length == 0) {
                            if (Boolean.parseBoolean(method.invoke(inetAddress).toString())) {
                                System.out.println("  |-" + method.getName() + " = true");
                            }
                        }
                    }
                    */
                    if (address.isLoopbackAddress()) {
                        // 本地回环(127~,0:~)
                        continue;
                    }
                    if (address.isSiteLocalAddress()) {
                        // 局域网
                        return address;
                    }
                    if (candidateInetAddress == null) {
                        // 记录候选地址
                        candidateInetAddress = address;
                    }
                }
            }
        } catch (Exception e) {
            // LOGGER.error(e);
        }
        if (candidateInetAddress == null) {
            // 没有候选地址, 利用UDP
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.connect(InetAddress.getByName("114.114.114.114"), 53);
                candidateInetAddress = socket.getLocalAddress();
                socket.close();
            } catch (Exception e) {
                // LOGGER.error(e);
            }
        }
        if (candidateInetAddress == null) {
            try {
                candidateInetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                // LOGGER.error(e);
            }
        }
        return candidateInetAddress;
    }

    /**
     * 将字节转换为long, 超过8个长度
     */
    private static long toLong(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return 0L;
        }
        // long为8个字节
        byte[] array = new byte[8];
        // 取最少的长度
        int length = Integer.min(array.length, bytes.length);
        // 结尾对其
        System.arraycopy(bytes, 0, array, array.length - length, length);
        return ((ByteBuffer) ByteBuffer.allocate(8).put(array).flip()).getLong();
    }

    /**
     * 获取当前主机名
     */
    public static String getHostName() {
        return HOST_NAME;
    }

    /**
     * 获取与外网通讯网卡的ip
     */
    public static String getHostIp() {
        return HOST_IP;
    }

    /**
     * 获取与外网通讯网卡的MAC
     */
    public static String getMac() {
        return MAC_HEX;
    }

    /**
     * 获取与外网通讯网卡的MAC
     */
    public static long getMacAsLong() {
        return MAC_LONG;
    }

    /**
     * 获取当前JVM进程ID
     */
    public static int getPid() {
        return PID;
    }

    /**
     * 随机获取一个有效TCP通信端口
     */
    public static int getAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            // 启动端口重用, 这样当前端口关闭后当前进程可以立即重用, 前提是重用时候也需要在绑定端口之前设置'可重用'
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(0));
            return serverSocket.getLocalPort();
        } catch (Throwable e) {
            return -1;
        }
    }

    /**
     * 尝试端口未被使用可以监听
     */
    public static boolean tryPort(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port));
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 获取当前服务端口
     */
    public static int getServerPort() {
        if (SERVER_PORT == -1) {
            findServerPort();
        }
        return SERVER_PORT;
    }

    private static synchronized void findServerPort() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        if (server == null) {
            return;
        }

        try {
            // 获取Tomcat运行端口
            Set<ObjectName> objectNames = server.queryNames(new ObjectName("*:type=Connector,*"), null);
            for (ObjectName objectName : objectNames) {
                Object protocol = server.getAttribute(objectName, "protocol");
                if (protocol instanceof String) {
                    if (((String) protocol).toUpperCase().startsWith("HTTP") || "org.apache.coyote.http11.Http11NioProtocol".equals(protocol)) {
                        Object port = server.getAttribute(objectName, "port");
                        if (port instanceof Integer) {
                            SERVER_PORT = (Integer) port;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // LOGGER.error(e.getMessage(), e);
        }
    }

    private NetUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
