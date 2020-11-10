package gg.codie.mineonline.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;

import gg.codie.mineonline.api.MineOnlineServer;
import gg.codie.mineonline.api.MineOnlineServerRepository;
import gg.codie.mineonline.gui.screens.GuiMultiplayer;
import gg.codie.mineonline.gui.screens.GuiSlotServer;

public class ThreadPollServers extends Thread
{
    public static final HashMap<String, Long> serverLatencies = new HashMap<>();

    public static void pollServer(MineOnlineServer server) {
        new ThreadPollServers(server).run();
        synchronized (ThreadPollServers.serverLatencies) {
            System.out.println(ThreadPollServers.serverLatencies);
        }
    }

    private ThreadPollServers(MineOnlineServer mineOnlineServer)
    {
        this.mineOnlineServer = mineOnlineServer;
    }

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
        DataInputStream datainputstream = null;
        DataOutputStream dataoutputstream = null;
        try
        {
            socket = new Socket();
            socket.setSoTimeout(3000);
            socket.setTcpNoDelay(true);
            socket.setTrafficClass(18);
            socket.connect(new InetSocketAddress(s1, j), 3000);
            datainputstream = new DataInputStream(socket.getInputStream());
            dataoutputstream = new DataOutputStream(socket.getOutputStream());
            dataoutputstream.write(254);
//            if(datainputstream.read() != 255)
//            {
//                throw new IOException("Bad message");
//            }
//            String s4 = Packet.readString(datainputstream, 64);
//            char ac[] = s4.toCharArray();
//            for(int k = 0; k < ac.length; k++)
//            {
//                if(ac[k] != '\247' && ChatAllowedCharacters.allowedCharacters.indexOf(ac[k]) < 0)
//                {
//                    ac[k] = '?';
//                }
//            }
//
//            s4 = new String(ac);
//            String as1[] = s4.split("\247");
//            s4 = as1[0];
//            int l = -1;
//            int i1 = -1;
//            try
//            {
//                l = Integer.parseInt(as1[1]);
//                i1 = Integer.parseInt(as1[2]);
//            }
//            catch(Exception exception) { }
//            servernbtstorage.field_35791_d = (new StringBuilder()).append("\2477").append(s4).toString();
//            if(l >= 0 && i1 > 0)
//            {
//                servernbtstorage.field_35794_c = (new StringBuilder()).append("\2477").append(l).append("\2478/\2477").append(i1).toString();
//            } else
//            {
//                servernbtstorage.field_35794_c = "\2478???";
//            }
        }
        finally
        {
            try
            {
                if(datainputstream != null)
                {
                    datainputstream.close();
                }
            }
            catch(Throwable throwable) { }
            try
            {
                if(dataoutputstream != null)
                {
                    dataoutputstream.close();
                }
            }
            catch(Throwable throwable1) { }
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
