import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



class Listener implements MouseListener {

    public static final int GRID_WIDTH = 32 / 1;
    public static final int GRID_HEIGHT = 32 / 1;

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

public class Grid extends JPanel {

    int width = 1168 / 2;
    int height = 800 / 2;
    int x, y;
    Image backImg;

    public static final int GRID_WIDTH = 32 / 1;
    public static final int GRID_HEIGHT = 32 / 1;

    int squareWidth = GRID_WIDTH, squareHeight = GRID_HEIGHT;

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
            backImg = ImageIO.read(new File("./planta_pequena.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Listener l = new Listener(gps, this);
        this.addMouseListener(l);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backImg, 10, 10, backImg.getWidth(this) / 1, backImg.getHeight(this) / 1, this);

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

    public void addMarker(int x, int y, Color c){
        fillCells.add(new Square(x, y, c));
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