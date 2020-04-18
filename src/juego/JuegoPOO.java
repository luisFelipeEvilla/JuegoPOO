
package juego;


import control.Teclado;
import graficos.Pantalla;
import graficos.Sprite;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImagingOpException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import mapa.Mapa;
import mapa.MapaGenerado;

public class JuegoPOO extends Canvas implements Runnable{

    private static final int ANCHO = 800;
    private static final int ALTO = 600;

    private static final String NOMBRE = "Forgotten History";

    private static int aps = 0; // actualizaciones por segundo
    private static int fps = 0;  // cuadros por segundo
    
    private static int x = 0;
    private static int y = 0;
    
    // con la palabra volatile esta variable no puede ser utilizada al mismo timepo por los dos hilos(thread)
    private volatile static boolean running = false;
    
    private static JFrame ventana;
    private static Thread thread;
    private Teclado teclado;
    private Pantalla pantalla;
    
    private static Mapa mapa;
    
    private static BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
    private static int[] pixels = ((DataBufferInt) imagen.getRaster().getDataBuffer()).getData();
    private static final ImageIcon icono =  new ImageIcon(JuegoPOO.class.getResource("/iconos/iconoPrincipal.png"));
    
    private static BufferedImage personaje ; // temporal para el dibujo del personaje
    
    private JuegoPOO() {
        setPreferredSize(new Dimension(ANCHO, ALTO));

        pantalla = new Pantalla(ANCHO, ALTO);
        
        mapa = new MapaGenerado(128, 128);
        
        // temporal para el dibujo del personaje
        try {
            personaje = ImageIO.read(JuegoPOO.class.getResource("/personajes/pidgeTemporal.png"));
        } catch (Exception e) {
            System.out.println("error");
        }
        
        teclado = new Teclado();
        addKeyListener(teclado);
        
        ventana = new JFrame(NOMBRE);
        ventana.setTitle(NOMBRE);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setResizable(false);
        ventana.setIconImage(icono.getImage()); // icono de la ventana
        ventana.setLayout(new BorderLayout());
        ventana.add(this, BorderLayout.CENTER); //añadir el canvas a la ventana
        ventana.pack(); // la ventana se autorrellenara con los elementos dentro de ella
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        
        requestFocus();
    }

    public static void main(String[] args) {
        JuegoPOO app = new JuegoPOO();
        app.start();
    }
    
    // con synchronized los dos metodos no seran capaces de ejecutarse al mismo timepo
    private synchronized void start() {
        running = true;
        
        thread = new Thread(this, "Graficos");
        thread.start();
    }
    
    private void stop() {
        running = false;
    
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void actualizar() {
        teclado.actualizar();
        
        if (teclado.arriba) {
            y--;
        }
        if (teclado.abajo) {
            y++;
        }
        if (teclado.izquierda) {
            x++;
        }
        if (teclado.derecha) {
            x--;
        }
        
        aps++;
    }
    
    private void mostrar() {
        BufferStrategy strategy = getBufferStrategy();
        
        if (strategy == null) {
            createBufferStrategy(3);
            return;
        }
        
        System.arraycopy(pantalla.pixeles, 0, pixels, 0, pixels.length);
        
        pantalla.limpiar();
        mapa.mostrar(x, y, pantalla);
         
        Graphics g = strategy.getDrawGraphics();
        g.drawImage(imagen, 0, 0, getWidth(), getHeight(), null);
        g.drawImage(personaje, ANCHO/2, ALTO/2, this); // temporal para el dibujo del personaje
        g.dispose();
        
        strategy.show();
        
        
        fps++;
    }

    
    // Este metodo sera llamado cada vez que un nuevo hilo sea creado
    @Override
    public void run() {
        while(running) {
            final int NS_POR_SEGUNDO = 1000000000;
            final byte APS_OBJETIVO= 60;
            final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / APS_OBJETIVO; // cada cuantos nanosegndos se hara una actualizacion
            
            long actualizacionReferencia = System.nanoTime();
            long contadorReferencia = System.nanoTime();
            
            double tiempoTranscurrido;
            double delta = 0;
            
            while(running) {
                final long inicioBucle = System.nanoTime();
                
                tiempoTranscurrido = inicioBucle - actualizacionReferencia;
                actualizacionReferencia = inicioBucle;
                
                delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;
                
                while (delta >= 1) {
                    actualizar();
                    delta--;
                }
                
                mostrar();
                
                if (System.nanoTime() - contadorReferencia > NS_POR_SEGUNDO) {
                    ventana.setTitle(NOMBRE + " || aps: " + aps + " || FPS: " + fps);
                    aps = 0;
                    fps = 0;
                    contadorReferencia = System.nanoTime();
                }
            }
        }
        
        
    }

    public static int getANCHO() {
        return ANCHO;
    }

    public static int getALTO() {
        return ALTO;
    }
    
}
