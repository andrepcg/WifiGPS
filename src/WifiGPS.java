import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.ws.WebServiceException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by André on 22/02/2015.
 */
public class WifiGPS {


    public static final int GRID_WIDTH = 32 / 2;
    public static final int GRID_HEIGHT = 32 / 2;

    int grelhaX = 35;
    int grelhaY = 22;

    public ArrayList<int[][]> grids = new ArrayList();

    public String[] wlans_ssid = {
            "a4:b1:e9:44:08:ad",
            "0c:47:3d:09:df:c8",
            "00:17:ca:d5:97:13",
            "0c:47:3d:09:df:c9",
            "00:26:5b:16:bf:08",
            "00:26:5b:16:bf:09",
            "a4:b1:e9:ed:46:74",
            "08:76:ff:87:fe:7e",
            "00:26:5b:1c:b4:99",
            "00:1f:9f:ff:37:36",
            "9c:97:26:9b:94:47",
            "64:70:02:b2:6c:2a"
    };

    HashMap<String, Double> foundAPS = new HashMap<>();


    Random rand = new Random();

    static Pattern ssid_rssi_regex = Pattern.compile("(([a-f0-9]{2}:)+[a-f0-9]{2}) (-[0-9]+)");

    public String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public int getSignalSSID(String ssid, String cmdOut) throws java.io.IOException {
        String[] cmdSplit = cmdOut.split("\n");

        for(String network : cmdSplit){

            Matcher m = ssid_rssi_regex.matcher(network);
            if(m.find()){
                String id = m.group(1);
                int signal = Integer.parseInt(m.group(3));
                if(id.compareTo(ssid) == 0)
                    return signal;

            }


        }

        return 0;
    }


    public void setRSSI(int x, int y){
        int v = -100;

        try {
            for(int te = 0; te < 3; te++) {
                String wificmdout = execCmd("airport -s");

                for (int i = 0; i < wlans_ssid.length; i++) {
                    int signal = getSignalSSID(wlans_ssid[i], wificmdout);
                    int index = Arrays.asList(wlans_ssid).indexOf(wlans_ssid[i]);
                    int[][] t = grids.get(index);
                    t[y][x] += signal;
                    //System.out.println("BSSID: " + wlans_ssid[i] + " | Signal: " + signal);
                }
            }

            for (int i = 0; i < wlans_ssid.length; i++) {
                int index = Arrays.asList(wlans_ssid).indexOf(wlans_ssid[i]);
                int[][] t = grids.get(index);
                t[y][x] /= 3;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(x + " " + y);

        for(int i = 0 ; i < grids.size(); i++)
            System.out.println(wlans_ssid[i] +": " + grids.get(i)[y][x]);
        System.out.println("----");


    }

    public void exportarDados() throws IOException {
        FileWriter fw = new FileWriter("dados_exportados.txt");

        int rede = 0;
        for(int[][] g : grids){
            fw.write(wlans_ssid[rede++] + "\n" + grelhaX + " " + grelhaY + "\n");
            for (int y = 0; y < grelhaY; y++) {
                for (int x = 0; x < grelhaX; x++)
                    fw.write(g[y][x] + " ");
                fw.write("\n");
            }
            fw.write("\n\n");
        }

        fw.close();
    }

    public String gerarDadosTreino() throws IOException {
        String dados = "";
        FileWriter fw = new FileWriter("train.data");
        for(int y = 0 ; y < grelhaY; y++){
            for(int x = 0 ; x < grelhaX; x++){

                int count = 0;
                for(int i = 0 ; i < grids.size(); i++){
                    if(grids.get(i)[y][x] == 0)
                        count++;
                }

                if(count == grids.size())
                    continue;

                for(int i = 0 ; i < grids.size(); i++){
                    fw.write(grids.get(i)[y][x] + " " + x + " " + y);
                    dados += grids.get(i)[y][x] + " " + x + " " + y;
                }
                fw.write("\n");
                dados += "\n";
            }
        }
        fw.close();

        return dados;

    }

    public WifiGPS(){

        for(int i = 0; i < wlans_ssid.length; i++)
            grids.add(new int[grelhaY][grelhaX]);



    }

    static JTextArea caixa_texto = new JTextArea();
    static JTextField input = new JTextField();
/*

                final WifiGPS gps = new WifiGPS();
                Grid grid = new Grid(36, 25, gps);

*/



}
