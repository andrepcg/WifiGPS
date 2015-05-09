import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.ws.WebServiceException;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by André on 22/02/2015.
 */
public class GUI extends JDialog {

    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JPanel panel1;
    private JTextArea textArea1;
    private JLabel imageLabel;
    Squares squares;

    public static final int GRID_WIDTH = 32;
    public static final int GRID_HEIGHT = 32;

    public ArrayList<int[][]> grids = new ArrayList();

    public String[] wlans_ssid = {"a4:b1:e9:44:08:ad"};


    Random rand = new Random();

    Pattern ssid_rssi_regex = Pattern.compile("(([a-f0-9]{2}:)+[a-f0-9]{2}) (-[0-9]+));

    public static String execCmd(String cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static int getSignalSSID(String ssid, String cmdOut) throws java.io.IOException {
        String[] cmdSplit = wificmdout.split("\r\n\r\n");

        for(String network : cmdSplit){

            Matcher m = ssid_rssi_regex.matcher(network);
            if(m.find()){
                String id = m.group(1);
                int signal = Integer.parseInt(m.group(3));
                int ind;
                if(id.compareTo(ssid) == 0)
                    return signal;

            }


        }

        return -1;
    }


    public void setRSSI(int x, int y){
        int v = -100;

        try {
            String wificmdout = execCmd("airport -s");

            for(int i = 0; i < wlans_ssid.length; i++){
                int signal = getSignalSSID(wlans_ssid[i], wificmdout);
                int index = Arrays.asList(wlans_ssid).indexOf(wlans_ssid[i]);
                int[][] t = grids.get(index);
                t[y][x] = signal;
                System.out.println("BSSID: " + wlans_ssid[i] + " | Signal: " + signal);
            }


            //System.out.println(wificmdout);
            /*
            String[] cmdSplit = wificmdout.split("\r\n\r\n");

            for(String network : cmdSplit){

            	Matcher m = ssid_rssi_regex.matcher(network);
            	if(m.find()){
            		String ssid = m.group(1);
            		int signal = Integer.parseInt(m.group(3));
                    int ind;
            		if((ind = Arrays.asList(wlans_ssid).indexOf(ssid)) >= 0){
                        int[][] t = grids.get(ind);
                        t[y][x] = signal;
                        System.out.println("BSSID: " + ssid + " | Signal: " + signal);

                    }
            	}


            }
            */
        } catch (IOException e) {
            e.printStackTrace();
        }



        //grid[y][x] = rand.nextInt(100);
    }

    public GUI() throws IOException {
        setContentPane(contentPane);
        setVisible(true);
        setModal(true);
        pack();


        for(int i = 0; i < wlans_ssid.length; i++)
            grids.add(new int[22][35]);


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        panel1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                x = x / GRID_WIDTH;
                y = y / GRID_HEIGHT;

                setRSSI(x,y);
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
        });

    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        GUI dialog = new GUI();
        dialog.pack();
        dialog.setSize(1168,800);
        dialog.setResizable(false);

        //System.exit(0);
    }

    private void createUIComponents() throws IOException {
        // TODO: place custom component creation code here

        BufferedImage myPicture = ImageIO.read(new File("./planta_casa_grid.png"));
        imageLabel = new JLabel(new ImageIcon(myPicture));



    }


    class Squares extends JPanel {
        private static final int PREF_W = 500;
        private static final int PREF_H = PREF_W;
        private java.util.List<Rectangle> squares = new ArrayList<Rectangle>();

        public void addSquare(int x, int y, int width, int height) {
            Rectangle rect = new Rectangle(x, y, width, height);
            squares.add(rect);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(PREF_W, PREF_H);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            for (Rectangle rect : squares) {
                g2.draw(rect);
            }
        }

    }
}
