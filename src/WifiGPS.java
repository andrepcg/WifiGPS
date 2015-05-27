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
            "00:05:ca:93:2c:08",
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

            System.out.println(grids.get(0)[y][x]);

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public WifiGPS(){

        for(int i = 0; i < wlans_ssid.length; i++)
            grids.add(new int[grelhaY][grelhaX]);



    }


    public static void main(String[] a) {
        EventQueue.invokeLater(new Runnable() {
            protected JComponent makeTextPanel(String text) {
                JPanel panel = new JPanel(false);
                JLabel filler = new JLabel(text);
                filler.setHorizontalAlignment(JLabel.CENTER);
                panel.setLayout(new GridLayout(1, 1));
                panel.add(filler);
                return panel;
            }

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                final WifiGPS gps = new WifiGPS();
                Grid grid = new Grid(36, 25, gps);

                JFrame window = new JFrame();
                window.setSize(1240 / 2, 1000 / 2);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //window.add(grid);
                window.setVisible(true);

                JMenuBar menuBar;
                JMenu menu;
                JMenuItem menuItem;

                menuBar = new JMenuBar();

//Build the first menu.
                menu = new JMenu("A Menu");
                menuBar.add(menu);

//a group of JMenuItems
                menuItem = new JMenuItem("Exportar dados");


                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //exporta os dados
                        System.out.println("A exportar dados");
                        try {
                            gps.exportarDados();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                menu.add(menuItem);
                window.setJMenuBar(menuBar);

                JTabbedPane tabbedPane = new JTabbedPane();
                //JComponent panel1 = makeTextPanel("Panel #1");
                tabbedPane.addTab("Tab 1", null, grid, "Does nothing");
                JComponent panel2 = makeTextPanel("Panel #2");
                tabbedPane.addTab("Tab 2", null, panel2,
                        "Does twice as much nothing");

                window.add(tabbedPane);
            }
        });


    }

    static class Listener implements MouseListener {

        Grid g;
        WifiGPS gps;

        public Listener(WifiGPS gps, Grid g){
            this.g = g;
            this.gps = gps;

        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            Thread thread = new Thread(new Runnable() {

                public void run() {
                    float x = e.getX() - 10;
                    float y = e.getY() - 10;
                    x = x / GRID_WIDTH;
                    y = y / GRID_HEIGHT;

                    g.fillCell((int) x, (int) y);
                    gps.setRSSI((int)x, (int)y);
                    g.setCellGreen((int) x, (int) y);
                }
            });
            thread.start();


        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

    }

    public static class Grid extends JPanel {

        int width = 1168 / 2;
        int height = 800 / 2;
        int x, y;
        Image backImg;

        int squareWidth = 32 / 2, squareHeight = 32 / 2;

        private ArrayList<Square> fillCells;

        private WifiGPS gps;

        public Grid(int xSquares, int ySquares, WifiGPS gps) {
            this.width = xSquares * squareWidth;
            this.height = squareHeight * ySquares;
            x = xSquares;
            y = ySquares;
            this.gps = gps;

            fillCells = new ArrayList<>();

            try {
                backImg = ImageIO.read(new File("./planta_casa_nogrid.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Listener l = new Listener(gps, this);
            this.addMouseListener(l);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(backImg, 10, 10, backImg.getWidth(this) / 2, backImg.getHeight(this) / 2, this);

            // desenha grelha
            g.setColor(Color.BLACK);
            g.drawRect(10, 10, width, height);

            for (double i = 10; i <= width; i += width / x)
                g.drawLine((int)i, 10, (int)i, height+10);


            for (double i = 10; i <= height; i += height / y)
                g.drawLine(10, (int)i, width+10, (int)i);
            //

            for (Square fillCell : fillCells) {
                int cellX = 10 + (fillCell.x * squareWidth);
                int cellY = 10 + (fillCell.y * squareHeight);
                g.setColor(fillCell.color);
                g.fillRect(cellX, cellY, squareWidth, squareHeight);
            }
        }

        public void fillCell(int x, int y) {
            fillCells.add(new Square(x, y, new Color(255, 0, 0, 64)));
            repaint();
        }

        public void setCellGreen(int x, int y) {
            for(Square s : fillCells){
                if(s.x == x && s.y == y){
                    s.color = new Color(0, 255, 0, 64);
                }
            }
            //fillCells.add(new Square(x, y, Color.RED));
            repaint();
        }

    }
}
