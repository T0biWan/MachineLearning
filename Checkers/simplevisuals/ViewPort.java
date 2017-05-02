package simplevisuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;



/**
 * Eine Klasse für einfache Grafiken.
 * 
 * <br/><br/>
 * 
 * Damit ist es möglich ein Grafikfenster zu öffnen, in dem
 * beliebige Grafiken dargestellt werden können.
 * Dabei wird mit einem Hintergrundspeicher gearbeitet.
 * Das heißt, dass alle Punkte, die mit den Methoden
 * {@link #drawPix(int, int, Color)} und 
 * {@link #drawPix(int, int, int, int, int)} 
 * gezeichnet werden zunächst in einen Hintergrundspeicher
 * geschrieben werden (sie erscheinen also nicht sofort sichtbar
 * auf dem Bildschirm).
 * Um den Hintergrundspeicher sichtbar zumachen, sollte die 
 * Methode {@link #copyBackgroundBuffer()}
 * benutzt werden. 
 * 
 * <br/><br/>
 * 
 * Die Klassen {@link ViewPortDemo} und {@link AnimationDemo} enthalten kleine Beispiele,
 * die den Umgang mit ViewPort verdeutlichen.
 * 
 * @author Elmar Böhler
 */
public class ViewPort  {

    
    /* ----------------------------- Attribute ---------------------------- */

    private JFrame mainWindow; 
    private Dimension rawSize;
    private BufferedImage viewPort;
    private BufferedImage preparationBuffer;
    private BufferedImage background;   
    private ViewPortPanel panel;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();;
    private String title;
    private LinkedList<FinalizationMethod> finalizer;
    private LinkedList<Character> keyHistory;
    
    

    /* --------------------------- Konstruktoren --------------------------------- */
    
    /**
     * Erzeugt ein neues Grafikfenster.
     * 
     * @param title Der Titel des erzeugten Fensters.
     * @param width Die Breite des erzeugten Fensters.
     * @param height Die Höhe des erzeugten Fensters.
     * @param xPos Die x-Position der linken oberen Ecke des Grafikfensters auf dem Bildschirm.
     * @param yPos Die y-Position der linken oberen Ecke des Grafikfensters auf dem Bildschirm.
     */
    public ViewPort(String title, int width, int height, int xPos, int yPos, boolean show) {
        this.title = title;
        rawSize = new Dimension(width, height);
        
        viewPort = new BufferedImage(rawSize.width, rawSize.height, BufferedImage.TYPE_3BYTE_BGR);
        preparationBuffer = new BufferedImage(rawSize.width, rawSize.height, BufferedImage.TYPE_3BYTE_BGR);
        background = new BufferedImage(rawSize.width, rawSize.height, BufferedImage.TYPE_3BYTE_BGR);
        panel = new ViewPortPanel(rawSize.width, rawSize.height);
        
        mainWindow = new JFrame(this.title);        
        keyHistory = new LinkedList<>(); 
        mainWindow.addKeyListener(new MyKeyListener());

        mainWindow.setLocation(xPos, yPos);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.addWindowListener(new ViewPortalWindowListener());
        JPanel globalPanel = new JPanel();
        globalPanel.setLayout(new BoxLayout(globalPanel, BoxLayout.Y_AXIS));
        mainWindow.getContentPane().add(panel);
        mainWindow.pack();
        mainWindow.setVisible(show);
        
        setBackgroundColor(new Color(0,0,0));
        clearViewPort();
        finalizer = new LinkedList<FinalizationMethod>();
    }
    
    

    /**
     * Erzeugt ein neues Grafikfenster und positioniert es
     * mittig im Bildschirm.
     * 
     * @param title Der Titel des erzeugten Fensters.
     * @param width Die Breite des erzeugten Fensters.
     * @param height Die Höhe des erzeugten Fensters.
     */
    public ViewPort(String title, int width, int height, boolean show) {
        this(title, width, height, (screenSize.width - width) / 2, (screenSize.height -height) /2, show);
    }
    
    
    
    /* ------------------------------ Public Methoden --------------------------------- */
    
    
    
    public void close() {
        mainWindow.setVisible(false);
        mainWindow.dispose();
    }
    
    
    
    /**
     * Setzt die Hintergrundfarbe auf die gegebene Farbe.
     * Die Methode bereitet dabei nur den Hintergrund vor,
     * so dass bei einem Aufruf von {@link #clearViewPort()}
     * das gesamte Grafikfenster mit der gegebenen Farbe
     * befüllt wird. 
     * 
     * Ein Aufruf dieser Methode befüllt aber das Grafikfenster
     * nicht unmittelbar.
     * 
     * @param c Die neue Farbe des Hintergrunds.
     */
    public void setBackgroundColor(Color c) {
        prepareBackground(c);
    }
    
    
    /**
     * Gibt die Farbe des Bildpunktes an den gegebenen Koordinaten zurück.
     * 
     * @param x Die x-Koordinate des gefragten Pixels.
     * @param y Die y-Koordinate des gefragten Pixels.
     * @return Der Farbwert des Bildpunktes an den gegebenen Koordinaten.
     */
    public Color getPix(int x, int y) {
        int col = preparationBuffer.getRGB(x, y);
        return new Color ((col & 0xFF0000) >> 16, (col & 0xFF00) >> 8, col & 0xFF);
    }
    
    
    /**
     * Befüllt den Hintergrundspeicher 
     * mit der durch die Rot-, Grün-, und Blaukomponente
     * gegebenen Farbe an die gegebenen Koordinaten.
     * 
     * @param x Die x-Koordinate des neu im Hintergrund zu setzenden Punktes. 
     * @param y Die y-Koordinate des neu im Hintergrund zu setzenden Punktes.
     * @param red Der Rotwert der Farbe des neuen Punktes. Zulässiger Bereich: 0-255.
     * @param green Der Grünwert der Farbe des neuen Punktes. Zulässiger Bereich: 0-255.
     * @param blue Der Blauwert der Farbe des neuen Punktes. Zulässiger Bereich: 0-255.
     */
    public void drawPix(int x, int y, int red, int green, int blue) {
        if ((x>=0) && (y>=0) && (x<rawSize.width) && (y<rawSize.height))
            preparationBuffer.setRGB(x, y, getColorCode(red, green, blue));      
    }
    
    
    
    /**
     * Leert den Hintergrundspeicher, bzw. setzt alle Pixel des
     * Hintergrundspeichers mit der Hintergrundfarbe, die in
     * {@link #setBackgroundColor(Color)} eingestellt werden kann.
     */
    public void clearViewPort() {
        byte[] backbuf = ((DataBufferByte) background.getRaster().getDataBuffer()).getData();
        byte[] viewPortbuf = ((DataBufferByte) preparationBuffer.getRaster().getDataBuffer()).getData();
        System.arraycopy(backbuf, 0, viewPortbuf , 0, backbuf.length);
    }
    
    
    /**
     * Kopiert den Hintergrundspeicher ins Grafikfenster. 
     */
    public void copyBackgroundBuffer() {
        byte[] backbuf = ((DataBufferByte) preparationBuffer.getRaster().getDataBuffer()).getData();
        byte[] viewPortbuf = ((DataBufferByte) viewPort.getRaster().getDataBuffer()).getData();
        System.arraycopy(backbuf, 0, viewPortbuf , 0, backbuf.length);
        if (!mainWindow.isVisible()) mainWindow.setVisible(true);
        mainWindow.repaint();
    }
    
    
    /**
     * Kopiert einen Ausschnitt des Hintergrundspeichers ins Grafikfenster. 
     */
    public void copyBackgroundBuffer(int x, int y, int width, int height) {
        byte[] backbuf = ((DataBufferByte) preparationBuffer.getRaster().getDataBuffer()).getData();
        byte[] viewPortbuf = ((DataBufferByte) viewPort.getRaster().getDataBuffer()).getData();
        
        int offset = y * rawSize.width * 3 + x * 3;
        int copySize = width * 3;
        int stepWidth = rawSize.width * 3;
        
        //System.out.println("offset: " + offset + ", size: " + copySize);
        for (int i = 0; i < height; i++) {
            System.arraycopy(backbuf, offset, viewPortbuf , offset, copySize);
            offset = offset + stepWidth;
        }
        if (!mainWindow.isVisible()) mainWindow.setVisible(true);
        mainWindow.repaint();
    }
    
    
    /**
     * Befüllt den Hintergrundspeicher 
     * mit der gegebenen Farbe an die gegebenen Koordinaten.
     * 
     * @param x Die x-Koordinate des neu im Hintergrund zu setzenden Punktes. 
     * @param y Die y-Koordinate des neu im Hintergrund zu setzenden Punktes.
     * @param col Die Farbe des neuen Punktes. Zulässiger Bereich: 0-255.
     */
    public void drawPix(int x, int y, Color col) {
        if ((x>=0) && (y>=0) && (x<rawSize.width) && (y<rawSize.height))
            preparationBuffer.setRGB(x, y, getColorCode(col));
    }
    
    
    
    
    /**
     * @return Die Größe des ViewPorts in horizontaler Richtung.
     */
    public int getXSize() {
    	return rawSize.width;
    }
    

    /**
     * @return Die Größe des ViewPorts in vertikaler Richtung.
     */
    public int getYSize() {
    	return rawSize.height;
    }
    
    

    public void drawBlock(int x, int y, int width, int height, Color col) {
        byte[] backbuf = ((DataBufferByte) preparationBuffer.getRaster().getDataBuffer()).getData();
        byte b = (byte)col.getBlue();
        byte g = (byte)col.getGreen();
        byte r = (byte)col.getRed();
        
        int offset = y * rawSize.width * 3 + x * 3;
        int copySize = width * 3;
        int stepWidth = rawSize.width * 3;
        
        byte[] line = new byte[copySize];
        for (int i = 0; i < width; i++) {
            line[i*3] = b;
            line[i*3+1] = g;
            line[i*3+2] = r;
        }
        
        for (int i = 0; i < height; i++) {
            System.arraycopy(line, 0, backbuf, offset, copySize);
            offset = offset + stepWidth;
        }
    }

    
    
    
    public void drawBlock(Rectangle r, Color col) {
        drawBlock(r.x, r.y, r.width, r.height, col);
    }
    
    
    public void drawRectangle(int x, int y, int width, int height, Color col) {
        int cc = getColorCode(col);
        int y1 = y + height - 1;
        int x1 = x + width - 1;
        for (int i = x; i < x + width; i++) {
            preparationBuffer.setRGB(i, y, cc);
            preparationBuffer.setRGB(i, y1, cc);
        }
        for (int j = y; j < y + height; j++) {
            preparationBuffer.setRGB(x, j, cc);
            preparationBuffer.setRGB(x1, j, cc);
        }
    }
    
    public void drawRectangle(Rectangle r, Color col) {
        drawRectangle(r.x, r.y, r.width, r.height, col);
    }
    
    
    
    
    public void drawCircle(int x, int y, int r, Color col) {
        int cc = getColorCode(col);
        for (int i = 0; i < 3*r/4; i++) {
            int j = (int)Math.round(Math.sqrt(r*r - i*i));
            preparationBuffer.setRGB(x + i, y + j, cc);
            preparationBuffer.setRGB(x - i, y + j, cc);
            preparationBuffer.setRGB(x + i, y - j, cc);
            preparationBuffer.setRGB(x - i, y - j, cc);
            
            preparationBuffer.setRGB(x + j, y + i, cc);
            preparationBuffer.setRGB(x - j, y + i, cc);
            preparationBuffer.setRGB(x + j, y - i, cc);
            preparationBuffer.setRGB(x - j, y - i, cc);
        }
    }
    
    
    public void drawDisk(int x, int y, int radius, Color col) {
        final double cos45 = Math.cos(Math.PI/4 - Math.PI/256);
        byte[] backbuf = ((DataBufferByte) preparationBuffer.getRaster().getDataBuffer()).getData();
        byte b = (byte)col.getBlue();
        byte g = (byte)col.getGreen();
        byte r = (byte)col.getRed();
        
        int copySize = radius * 6;
        
        byte[] line = new byte[copySize];
        for (int i = 0; i < copySize/3; i++) {
            line[i*3] = b;
            line[i*3+1] = g;
            line[i*3+2] = r;
        }
        
        for (int i = 0; i < radius*cos45; i++) {
            int j = (int)Math.round(Math.sqrt(radius*radius - i*i));

            /*
            for (int o = 0; o < radius*2; o++) {
                line[o*3] = b;
                line[o*3+1] = g;
                line[o*3+2] = r;
            }
            */
            
            
            int offset = (y+j-1) * rawSize.width * 3 + (x-i) * 3;
            System.arraycopy(line, 0, backbuf, offset, i*6);
            
            offset = (y-j) * rawSize.width * 3 + (x-i) * 3;
            System.arraycopy(line, 0, backbuf, offset, i*6);
            
            /*
            for (int o = 0; o < radius*2; o++) {
                line[o*3] = 100;
                line[o*3+1] = 100;
                line[o*3+2] = 127;
            }
            */

            
            offset = (y+i) * rawSize.width * 3 + (x-j) * 3;
            System.arraycopy(line, 0, backbuf, offset, j*6);
            
            offset = (y-i) * rawSize.width * 3 + (x-j) * 3;
            System.arraycopy(line, 0, backbuf, offset, j*6);
            
        }
    }
    
    
    /**
     * Draws a string into the background buffer where the size of the font is 
     * adapted, such that the string fills the rectangle given by width and height.
     * x and y specify the upper left corner of the rectangle containing the string.
     */
    public void drawString(String s, int x, int y, int width, int height, Color c) {        
        
        Graphics2D g = preparationBuffer.createGraphics();
        Font f = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D r = f.getStringBounds(s, frc);
        
        AffineTransform at = AffineTransform.getScaleInstance(width/r.getWidth(), height/r.getHeight());
        f = f.deriveFont(at);
        g.setFont(f);
        
        g.setColor(c);
        
        FontMetrics fm = g.getFontMetrics();
        fm.getAscent();
        
        //drawRectangle(new Rectangle(x, y, width, height), Color.gray);
        
        g.drawString(s, x, y + fm.getAscent());
        
    }
    
    
    
    /* ----------------------------- Private Methoden --------------------------------*/
    
    
    private void draw(Graphics g) {
        drawColorMode(g);
    }
    
    
    private void drawColorMode(Graphics g) {
        g.drawImage(viewPort, 0,0,null);
    }
    
        
    
    private int getColorCode(Color c) {
        return (c.getRed() << 16) + (c.getGreen() << 8) + c.getBlue();
    }
    
    private int getColorCode(int red, int green, int blue) {
        return (red << 16) + (green << 8) + blue;
    }

    private void prepareBackground(Color c) {
        int colorCode = getColorCode(c);
        for (int y = 0; y<rawSize.height; y++) {
            for (int x = 0; x<rawSize.width; x++) {
                background.setRGB(x, y, colorCode);
            }
        }
    }

    
    /* ------------------------- Private Klassen und Interfaces ----------------------- */
    
    private class ViewPortalWindowListener implements WindowListener {
        @Override
        public void windowClosed(WindowEvent arg0) {
            // do nothing
        }
        @Override
        public void windowClosing(WindowEvent arg0) {
            for (FinalizationMethod fm : finalizer) fm.runFinalization();
        }
        @Override
        public void windowDeactivated(WindowEvent arg0) {
            // do nothing
        }
        @Override
        public void windowDeiconified(WindowEvent arg0) {
            // do nothing
        }
        @Override
        public void windowIconified(WindowEvent arg0) {
            // do nothing
        }
        @Override
        public void windowOpened(WindowEvent arg0) {
            // do nothing
        }
        @Override
        public void windowActivated(WindowEvent e) {
            // do nothing
        }
    }
    
    
    
    private class ViewPortPanel extends JPanel {
        private static final long serialVersionUID = 923837782211117374L;

        public ViewPortPanel(int width, int height) {
            setPreferredSize(new Dimension(rawSize.width,rawSize.height));
        }
        
        protected void paintComponent(Graphics g ) {
            super.paintComponent(g);
            draw(g);
        }
    }
    
    
    private interface FinalizationMethod {
        public void runFinalization();
    }
    
    
    public char getNextTypedChar() {
        synchronized(this) {
            return keyHistory.pop();
        }
    }

    
    public boolean hasNextTypedChar() {
        synchronized(this) {
            return keyHistory.size() > 0;
        }
    }
    

    private class MyKeyListener implements KeyListener {
        
        @Override
        public void keyTyped(KeyEvent e) {
            synchronized (ViewPort.this) {
                keyHistory.add(e.getKeyChar());
            }
        }
    
    
    
        @Override
        public void keyPressed(KeyEvent e) {
            // do nothing
        }
    
    
    
        @Override
        public void keyReleased(KeyEvent e) {
            // do nothing
        }
    }
    
    public static void main(String[] args) {
    }
    
    
    
    
}
