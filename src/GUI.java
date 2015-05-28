import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Andr� on 28/05/2015.
 */
public class GUI {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    Grid mapa;
    private JPanel nn;
    private JTextArea caixa_info;
    private JButton button1;
    private JButton button2;
    private static JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    WifiGPS gps;

    String dados;
    static RedeNeuronal redeneuronal;

    public GUI() {
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float[] sinais = gps.getSignalForNetworks();
                printInputs(sinais);
                float[] resultados = redeneuronal.runInputs(sinais);
                //mapa.fill
                mapa.addMarker((int)(resultados[0] * 35), (int)(resultados[1] * 22), new Color(0, 255,255, 64));
            }
        });
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }



        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        redeneuronal = new RedeNeuronal();

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        gps = new WifiGPS();
        mapa = new Grid(26,22,gps);

        menuBar = new JMenuBar();

        menu = new JMenu("A Menu");
        menuBar.add(menu);


        menuItem = new JMenuItem("Exportar dados");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    gps.exportarDados();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Gerar dados treino");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // gerar dados treino
                System.out.println("A gerar dados treino");
                try {
                    dados = gps.gerarDadosTreino();
                    caixa_info.append(dados);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        menu.add(menuItem);



    }

    void printInputs(float[] t){
        for(float i : t){
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
