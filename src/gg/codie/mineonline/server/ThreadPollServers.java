package gg.codie.mineonline.server;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import gg.codie.mineonline.api.MineOnlineServer;

public class ThreadPollServers extends Thread
{
    public static final HashMap<String, Long> serverLatencies = new HashMap<>();

    public static void pollServer(MineOnlineServer server) {
        new ThreadPollServers(server).start();
    }

    private ThreadPollServers(MineOnlineServer mineOnlineServer)
    {
        this.mineOnlineServer = mineOnlineServer;
    }

    @Override
    public void run() {
        boolean var27 = false;

        label183: {
           label184: {
              label185: {
                 label186: {
                    label187: {
                       try {
                          var27 = true;
                          long var1 = System.nanoTime();
                          performPoll(mineOnlineServer);
                          long var3 = System.nanoTime();
                          serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, (var3 - var1) / 1000000L);
                          var27 = false;
                          break label183;
                       } catch (UnknownHostException ex) {
                           ex.printStackTrace();
                           serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, -1L);
                          var27 = false;
                       } catch (SocketTimeoutException ex) {
                           ex.printStackTrace();
                           serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, -1L);
                          var27 = false;
                          break label187;
                       } catch (ConnectException ex) {
                           ex.printStackTrace();
                           serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, -1L);
                          var27 = false;
                          break label186;
                       } catch (IOException ex) {
                           ex.printStackTrace();
                           serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, -1L);
                          var27 = false;
                          break label185;
                       } catch (Exception ex) {
                           ex.printStackTrace();
                           serverLatencies.put(mineOnlineServer.connectAddress + ":" + mineOnlineServer.port, -1L);
                          var27 = false;
                          break label184;
                       } finally {
                          if(var27) {
                              return;
                          }
                       }

                        return;
                    }

                     return;
                 }

                  return;
              }

               return;
           }
          return;
        }
     }

    private void performPoll(MineOnlineServer mineOnlineServer)
            throws IOException
    {
        String s = mineOnlineServer.connectAddress + ":" + mineOnlineServer.port;
        String as[] = s.split(":");
        if(s.startsWith("["))
        {
            int i = s.indexOf("]");
            if(i > 0)
            {
                String s2 = s.substring(1, i);
                String s3 = s.substring(i + 1).trim();
                if(s3.startsWith(":") && s3.length() > 0)
                {
                    s3 = s3.substring(1);
                    as = new String[2];
                    as[0] = s2;
                    as[1] = s3;
                } else
                {
                    as = new String[1];
                    as[0] = s2;
                }
            }
        }
        if(as.length > 2)
        {
            as = new String[1];
            as[0] = s;
        }
        String s1 = as[0];
        int j = as.length <= 1 ? 25565 : parseIntWithDefault(as[1], 25565);
        Socket socket = null;
        try
        {
            socket = new Socket();
            socket.setSoTimeout(3000);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(18);
            socket.connect(new InetSocketAddress(s1, j), 3000);
        }
        finally
        {
            try
            {
                if(socket != null)
                {
                    socket.close();
                }
            }
            catch(Throwable throwable2) { }
        }
    }

    private int parseIntWithDefault(String s, int i)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch(Exception exception)
        {
            return i;
        }
    }

     final MineOnlineServer mineOnlineServer;
}
