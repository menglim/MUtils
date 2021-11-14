import java.io.IOException;
import java.net.*;

public class CheckPortMainTest {
    public static void main(String[] args) {
        int exitStatus = 1;
        if (args.length != 3) {
            System.out.println("Usage: CheckSocket node port timeout");
        } else {
            String node = args[0];
            int port = Integer.parseInt(args[1]);
            int timeout = Integer.parseInt(args[2]);

            Socket s = null;
            String reason = null;
            try {
                s = new Socket();
                s.setReuseAddress(true);
                SocketAddress sa = new InetSocketAddress(node, port);
                s.connect(sa, timeout * 1000);
            } catch (IOException e) {
                if (e.getMessage().equals("Connection refused")) {
                    reason = "port " + port + " on " + node + " is closed.";
                }
                if (e instanceof UnknownHostException) {
                    reason = "node " + node + " is unresolved.";
                }
                if (e instanceof SocketTimeoutException) {
                    reason = "timeout while attempting to reach node " + node + " on port " + port;
                }
            } finally {
                if (s != null) {
                    if (s.isConnected()) {
                        System.out.println("Port " + port + " on " + node + " is reachable!");
                        exitStatus = 0;
                    } else {
                        System.out.println("Port " + port + " on " + node + " is not reachable; reason: " + reason);
                    }
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
