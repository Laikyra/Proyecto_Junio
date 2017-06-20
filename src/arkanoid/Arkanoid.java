/*************************************************************/
/* Proyecto Arkanoid realizado por Carolina Casillas Torres  */
/*                                                           */ 
/* versión: 1.0 19/06/2017                                   */
/*                                                           */
/*                                                           */
/*************************************************************/


package arkanoid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.awt.Font;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class Arkanoid extends JPanel {

    // Activar/Desactivar modo DEBUG por consola
    public static final boolean MODODEBUG = false;
    
    // Parámetros de la ventana
    public static final int ANCHOVENTANA = 500;
    public static final int ALTOVENTANA = 500;
    
    // Velocidad del juego: menos es más. Minimo: 12
    public int velocidadInicial = 12;
    public int velocidad;
    
    // Puntos actuales
    public static int puntosactuales;
    
    // Record
    public static int record;
    
    // Nivel
    public int nivel = 1;
    
    // Inicio del juego
    public static boolean iniciarJuego = false;
    
    // Pausar el juego
    public static boolean pausarJuego = false;
    
    // Control del rótulo de siguiente nivel
    public static boolean rotuloNivel = false;
    
    // Control del rótulo de vida perdida
    public static boolean rotuloVida = false;

    // Instanciamos la barra, la bola, los bloques y el texto
    Barra barra = new Barra();
    Bola bola = new Bola(this);
    ConjuntoBloques cbloques = new ConjuntoBloques(this);
    Texto texto = new Texto(this);
    
    // Añadimos el detector de teclas para mover la barra
    DetectorTeclas detector = new DetectorTeclas(this);
    
    // Vidas
    public static int vidas = 3;
    
    
    public Arkanoid() 
    {
        // Constructor de la clase: hacemos visible el JFRAME
        setLayout(null);
        setVisible(true);
        setBackground(Color.BLACK);
        this.velocidad = velocidadInicial;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Pintamos la barra pasando el contexto gráfico
        barra.paint(g2d);
        
        // Pintamos la bola
        bola.paint(g2d);
        
        // Pintamos los bloques
        cbloques.paint(g2d);
        
        // Pintamos el texto
        texto.paint(g2d);
        
    }

    // MAIN
    public static void main(String[] args) throws InterruptedException 
    {
        
        JFrame frame = new JFrame("Arkanoid");
        
        // Instanciamos un objeto ARKANOID
        Arkanoid juego = new Arkanoid();
        
        // Añadimos el objeto al panel y ponemos los parámetros de la ventana
        frame.getContentPane().add(juego);
        frame.setSize(ANCHOVENTANA, ALTOVENTANA);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Cambiamos la propiedad setResizable para que no podamos cambiar de tamaño la ventana
        frame.setResizable(false);
        
         // Abrimos el fichero en el que tenemos guardado el record y guardamos su contenido en una variable
        leerRecord("Record.txt", juego);
        
        // Inicio del bucle principal
        while (true) 
        {
            if (iniciarJuego && !pausarJuego) juego.bola.moverBola();
            if (iniciarJuego && !pausarJuego) juego.repaint();
            // Desde aquí se controla la velocidad del juego
            Thread.sleep(juego.velocidad);
        }
    }
    // FIN Main
    
    // leerRecord
    // Esta clase lee el contenido del fichero Record.txt
    public static void leerRecord(String rec, Arkanoid juego)
    {
        // Declaramos las variables para leer ficheros segun el parámetro REC
        try
        {
            File fichero = new File(rec);
            FileReader entrada = new FileReader(fichero);
            BufferedReader br = new BufferedReader(entrada);
            String reco = br.readLine();
            if (reco == null || reco.isEmpty())
            {
                if (juego.MODODEBUG) System.out.println("El fichero existe pero no tiene línea o bien ésta está en blanco. Ponemos el record a 0 y guardamos.");
                record = 0;
                // Como no existe línea en el fichero, escribimos el nuevo record (0)
                guardarRecord(rec, juego);
            }
            else 
            {
                record = Integer.parseInt(reco);
                if (juego.MODODEBUG) System.out.println("El fichero existe. El record cargado es " + record);
            }
            entrada.close();
        }
        catch (IOException e)
        {
            if (juego.MODODEBUG) System.out.println("No existe el fichero: creamos uno nuevo con el record 0."); 
            record = 0;
            // Como no existe el fichero, lo creamos
            guardarRecord(rec, juego);
        }
    }
    //Fin leerRecord
    
    // guardarRecord
    // Esta clase escribe el record en el fichero Record
    public static void guardarRecord(String rec, Arkanoid juego)
    {
        // Al finalizar la partida comprobamos que el record guardado sea menor que los puntos actuales. Si es así, guardamos los puntos actuales en el fichero
        try
        {
            // Leemos el fichero para comprobar cual es el record guardado
            File fichero = new File(rec);
            FileWriter salida = new FileWriter(fichero);
            if (record <= puntosactuales) salida.write(String.valueOf(puntosactuales));
            salida.close();
        }
        catch (IOException e)
        {
            if (juego.MODODEBUG) System.out.println("Error al escribir en el fichero.");
        }
    }
    // Fin del guardarRecord
 }
// FIN Clase ARKANOID

// DetectorTeclas
// Esta clase detecta la tecla pulsada y mueve la barra
class DetectorTeclas implements KeyListener 
{
    private Arkanoid juego;
    private static final int MOVIMIENTOBARRA = 15;
    
    DetectorTeclas(Arkanoid juego)
    {
        juego.addKeyListener(this);
        juego.setFocusable(true);
        this.juego = juego;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 39)
        {
            // Si la barra ya está en el límite horizontal derecho o lo supera, la dejamos ahí
            if (juego.barra.posBarraX >= (juego.ANCHOVENTANA - juego.barra.ANCHOBARRA))
            {
                juego.barra.posBarraX = juego.ANCHOVENTANA - juego.barra.ANCHOBARRA;
            }
            else
            {
                // Mover barra a la derecha
                juego.barra.posBarraX += MOVIMIENTOBARRA;
            }
        } 
        else if (e.getKeyCode() == 37) 
        {
            // Si la barra ya está en el límite horizontal derecho o lo supera, la dejamos ahí
            if (juego.barra.posBarraX <= 0)
            {
                juego.barra.posBarraX = 0;
            }
            else
            {
                // Mover barra a la izquierda
                juego.barra.posBarraX -= MOVIMIENTOBARRA;
            }
        }
        else if (e.getKeyCode() == 32) 
        {
            // Iniciamos el juego o lo pausamos
            if (!juego.iniciarJuego)
            {
                juego.iniciarJuego = true;
            }
            else
            {
                if (juego.pausarJuego)
                {
                    juego.pausarJuego = false;
                    if (juego.MODODEBUG) System.out.println("Pausa ACTIVADA");
                }
                else
                {
                    juego.pausarJuego = true;
                    if (juego.MODODEBUG) System.out.println("Pausa DESACTIVADA");
                }
            }
            
            juego.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
// FIN DetectorTeclas

// Barra
// Esta clase se encarga de dibujar la barra
class Barra
{
    // Parámetros de la barra. Públicas para ser accesibles desde el Listener
    public final int  ANCHOBARRA = 60;
    public final int  ALTOBARRA = 12;
    
     // Posición de la barra inicial. Son variables públicas para que sean accesibles desde el Listener
    public final int POSBARRAINICIALX = 220;
    public final int POSBARRAINICIALY = 420;
    
    // Posición de la barra. Son variables públicas para que sean accesibles desde el Listener
    public int posBarraX;
    public int posBarraY;
    
    // Color de la barra
    private final Color COLORBARRA = Color.WHITE;
    
    Barra()
    {
        reiniciarBarra();
    }
    
    public void paint(Graphics2D g) 
    {
        g.setColor(COLORBARRA);
        g.fillRect(posBarraX, posBarraY, ANCHOBARRA, ALTOBARRA);
    }
    
        //Reiniciamos la posicion de la bola al suir de nivel o al perder una vida
    public void reiniciarBarra()
    {
        posBarraX = POSBARRAINICIALX;
        posBarraY = POSBARRAINICIALY;
    }
    
}
// FIN Barra

// Bola
// Esta clase controla el movimiento de la bola y la dibuja en pantalla. Controla también las colisiones de la bola con los demás elementos de la pantalla
class Bola
{
    private Arkanoid juego;
    
    // Parámetros de la bola
    public final int DIAMETROBOLA = 12;
    public final int VELOCIDAD = 2;
    public final int FACTORVELOCIDAD = 2;
    
    // Ajuste de colisiones
    public final int AJUSTECOLISIONES = 5;
    public boolean COLISIONBARRA;
    public final int OFFSETSUPERIOR = 25;
    
    // Posición inicial horizontal de la bola
    public final int POSBOLAINICIALX = 244;
    
    // Posición de la bola
    public int posBolaX;
    public int posBolaY;
    
    // Velocidad horizontal y vertical de la bola
    public int velX;
    public int velY;
    
    // Color de la bola
    private final Color COLORBOLA = Color.RED;
    
    Bola (Arkanoid juego)
    {
        this.juego = juego;
        reiniciarBola();
        aleatorizarAngulos();
    }
    
    public void paint(Graphics2D g) 
    {
        g.setColor(COLORBOLA);
	g.fillOval(posBolaX, posBolaY, DIAMETROBOLA, DIAMETROBOLA);
    }
    
    // reiniciarBola
    //Reiniciamos la posicion de la bola al suir de nivel o al perder una vida
    public void reiniciarBola()
    {
        aleatorizarAngulos();
        
        this.posBolaX = POSBOLAINICIALX;
        this.posBolaY = juego.barra.POSBARRAINICIALY - DIAMETROBOLA - 1;
        
        // Ajustamos la velocidad en función del nivel del juego
        juego.velocidad = juego.velocidadInicial - 2 * (juego.nivel -1);
    }
    // FIN reiniciarBola
    
    // aleatorizarAngulos
    // Aletorizamos los ángulos de comienzo de la bola para que no empiece siempre en la misma dirección
    public void aleatorizarAngulos()
    {
        // En el eje Y, la bola siempre sale hacia arriba
        velY = (-1);
        
        // Generamos el número aleatorio para X
        // La coordenada X tiene 2 valores posibles sin necesidad de modificar la velocidad del juego
        int numeroRandom = (int) (Math.random()*2+1);

        // Según el número, devolvemos un color distinto
        switch (numeroRandom)
        {
            case 1:
                velX = 1;
                break;
            default:
                velX = -1;
                break;
        }
    }
    // FIN aleatorizarAngulos
    
    // moverBola
    // Esta función mueve la bola y controla las colisiones
    public void moverBola()
    {
        // Comprobamos colisión con los bordes de la ventana
        if (posBolaX <= 0) 
        {
            velX = velX * (-1);
            posBolaX = 0;
        }
        if (posBolaX >= (juego.ANCHOVENTANA - DIAMETROBOLA)) 
        {
            velX = velX * (-1);
            posBolaX = juego.ANCHOVENTANA - DIAMETROBOLA;
        }
        if (posBolaY <= 0 + OFFSETSUPERIOR) velY = velY * (-1);
        
        // Comprobamos si la bola sale por el límite inferior, en cuyo caso restamos 1 al número de vidas
        if (posBolaY >= 500)
        {
            juego.vidas--;
            
            // Mostramos el rótulo de VIDA PERDIDA
            juego.rotuloVida = true;
            
            if (juego.vidas >= 1)
            {
                // Aún quedan vidas: reiniciamos posición de la bola, de la barra y pausamos el juego
                juego.pausarJuego = true;
                juego.bola.reiniciarBola();
                juego.barra.reiniciarBarra();
                aleatorizarAngulos();
                juego.repaint();
            }
            else
            {
                // Se han agotado las vidas.
                // Mostramos un mensaje al usuario
                if (juego.record > juego.puntosactuales)
                {
                    int seleccion = JOptionPane.showOptionDialog( null,"No te quedan vidas. La puntuación obtenida ha sido de " + juego.puntosactuales + " puntos y el record es " + juego.record + ". ¡Inténtalo de nuevo!", "Arkanoid",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,new Object[] { "Jugar de nuevo", "Salir"},"opcion 1");
                    switch (seleccion)
                    {
                        case 0:
                            // Reiniciamos el juego
                            juego.cbloques.reiniciarJuego(juego);
                            break;
                        case 1:
                            // Salimos del juego
                            System.exit(0);
                            break;
                    }
                }
                if (juego.record < juego.puntosactuales)
                {
                    // Si el nuevo record es superior al anterior, lo guardamos en el fichero.
                    juego.guardarRecord("Record.txt" , juego);
                    
                    int seleccion = JOptionPane.showOptionDialog( null,"No te quedan vidas. La puntuación obtenida ha sido de " + juego.puntosactuales + " puntos y el record es " + juego.record + ". ¡Felicidades! Has logrado un nuevo récord.", "Arkanoid",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,new Object[] { "Jugar de nuevo", "Salir"},"opcion 1");
                    switch (seleccion)
                    {
                        case 0:
                            // Reiniciamos el juego
                            // En este caso, antes de reiniciarlo, el record maximo se actualiza
                            juego.record = juego.puntosactuales;
                            juego.cbloques.reiniciarJuego(juego);
                            break;
                        case 1:
                            // Salimos del juego
                            System.exit(0);
                            break;
                    }
                }
                if (juego.record == juego.puntosactuales)
                {
                    int seleccion = JOptionPane.showOptionDialog( null,"No te quedan vidas. La puntuación obtenida ha sido de " + juego.puntosactuales + " puntos y el record es " + juego.record + ". ¡Casi consigues superarlo!", "Arkanoid",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,new Object[] { "Jugar de nuevo", "Salir"},"opcion 1");
                    switch (seleccion)
                    {
                        case 0:
                            // Reiniciamos el juego
                            juego.cbloques.reiniciarJuego(juego);
                            break;
                        case 1:
                            // Salimos del juego
                            System.exit(0);
                            break;
                    }
                }
            }
        }
        
        // Comprobamos colisión con la barra
        COLISIONBARRA = false;
        if (velY >= 1 && ((posBolaY + DIAMETROBOLA) >= (juego.barra.posBarraY) && (posBolaY + DIAMETROBOLA) <= (juego.barra.posBarraY + juego.barra.ALTOBARRA)))
        {
            // Hay colisión: comprobamos la parte y el ángulo, y actuamos sobre los ejes
            int anchoColision = juego.barra.ANCHOBARRA / 3;
            
            // Colisión con la parte CENTRAL de la barra - mantenemos el ángulo de salida de la bola
            if (posBolaX > (juego.barra.posBarraX + anchoColision) && posBolaX + DIAMETROBOLA < (juego.barra.posBarraX + juego.barra.ANCHOBARRA - anchoColision))
            {
                if (juego.MODODEBUG) System.out.println("Colisión con barra parte CENTRAL en " + posBolaX + ". Valores anteriores a la colisión: velX=" + velX + " velY=" + velY);
                velY = velY * (-1);
                COLISIONBARRA = true;
                // Reducimos la velocidad en el eje X si esta es 3, 2, -2 o -3
                if (velX >= 2)
                {
                    velX--;
                }
                if (velX <= (-2))
                {
                    velX++;
                }
                if (juego.MODODEBUG) System.out.println("Colisión con barra parte CENTRAL en " + posBolaX + ".Valores NUEVOS tras colision: velX=" + velX + " velY=" + velY);
            }
            
            // Colisión con la parte IZQUIERDA de la barra - si velX es positivo, aumentamos el ángulo, si es negativo lo reducimos
            if (posBolaX + DIAMETROBOLA >= (juego.barra.posBarraX) && posBolaX <= (juego.barra.posBarraX + anchoColision))
            {
                if (velX > 0)
                {
                    // La bola viene de izquierda a derecha: reducimos el ángulo si la velocidad en X es 1 o 2
                    switch (velX)
                    {
                        case 1: case 2:
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte IZQUIERDA en " + posBolaX + ". Valores anteriores a la colisión: velX=" + velX + " velY=" + velY);
                            velX = velX + 1;
                            juego.velocidad = juego.velocidad + FACTORVELOCIDAD;
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte IZQUIERDA en " + posBolaX + ". Valores NUEVOS tras colision: velX=" + velX + " velY=" + velY);
                            break;
                    }
                    
                    // El eje X se invierte
                    velX = velX * (-1);
                }
                else
                {
                    // La bola viene de derecha a izquierda: aumentamos el ángulo si la velocidad en X es -2 o -3
                    switch (velX)
                    {
                        case -2: case -3:
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte IZQUIERDA en " + posBolaX + ". Valores anteriores a la colisión: velX=" + velX + " velY=" + velY);
                            velX = velX + 1;
                            juego.velocidad = juego.velocidad - FACTORVELOCIDAD;
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte IZQUIERDA en " + posBolaX + ". Valores NUEVOS tras colision: velX=" + velX + " velY=" + velY);
                            break;
                    }
                }
                
                // El eje Y se invierte
                velY = velY * (-1);
                COLISIONBARRA = true;
            }
            
            // Colisión con la parte DERECHA de la barra - si velX es positivo, reducimos el ángulo, si es negativo lo aumentamos
            if (posBolaX + DIAMETROBOLA >= (juego.barra.posBarraX + 2 * anchoColision) && posBolaX <= (juego.barra.posBarraX + juego.barra.ANCHOBARRA))
            {
                if (velX > 0)
                {
                    // La bola viene de izquierda a derecha: aumentamos el ángulo si la velocidad en X es 1 o 2
                    switch (velX)
                    {
                        case 2: case 3:
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte DERECHA en " + posBolaX + ". Valores anteriores a la colisión: velX=" + velX + " velY=" + velY);
                            velX = velX - 1;
                            juego.velocidad = juego.velocidad - FACTORVELOCIDAD;
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte DERECHA en " + posBolaX + ". Valores NUEVOS tras colision: velX=" + velX + " velY=" + velY);
                            break;
                    }
                }
                else
                {
                    // La bola viene de derecha a izquierda: reducimos el ángulo si la velocidad en X es -2 o -3
                    switch (velX)
                    {
                        case -1: case -2:
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte DERECHA en " + posBolaX + ". Valores anteriores a la colisión: velX=" + velX + " velY=" + velY);
                            velX = velX - 1;
                            juego.velocidad = juego.velocidad + FACTORVELOCIDAD;
                            if (juego.MODODEBUG) System.out.println("Colisión con barra parte DERECHA en " + posBolaX + ". Valores NUEVOS tras colision: velX=" + velX + " velY=" + velY);
                            break;
                    }
                    
                    // El eje X se invierte
                    velX = velX * (-1);
                }

                // El eje Y se invierte
                velY = velY * (-1);
                COLISIONBARRA = true;
            }
        }
        
        // Comprobamos colisión con los bloques
        int xbloque;
        int ybloque;
        boolean hayColision = false;
        
        for (int j = 0; j <= 2; j++)
        {
            for (int i=0; i <= 9; i++)
            {
                if(juego.cbloques.bloques[i][j].visible)
                {
                    // Obtenemos los parámetros del bloque
                    xbloque = juego.cbloques.bloques[i][j].posX;
                    ybloque = juego.cbloques.bloques[i][j].posY;
                    
                    // Colisión superior
                    if (hayColision == false && ((posBolaX + DIAMETROBOLA >= xbloque) && (posBolaX <= xbloque + juego.cbloques.ANCHOBLOQUE)))
                    {
                        if ((posBolaY + DIAMETROBOLA >= ybloque) && (posBolaY + DIAMETROBOLA <= ybloque + AJUSTECOLISIONES))
                        {
                            if (juego.MODODEBUG) System.out.println("Colisión SUPERIOR");
                            juego.cbloques.bloques[i][j].visible = false;
                            velY = velY * (-1);
                            hayColision = true;
                        }
                    }
                  
                    // Colisión inferior
                    if (hayColision == false && ((posBolaX + DIAMETROBOLA >= xbloque) && (posBolaX <= xbloque + juego.cbloques.ANCHOBLOQUE)))
                    {
                        if ((posBolaY >= ybloque + juego.cbloques.ALTOBLOQUE - AJUSTECOLISIONES) && (posBolaY <= ybloque + juego.cbloques.ALTOBLOQUE))
                        {
                            if (juego.MODODEBUG) System.out.println("Colisión INFERIOR");
                            juego.cbloques.bloques[i][j].visible = false;
                            velY = velY * (-1);
                            hayColision = true;
                        }
                    }
                    
                    // Colisión izquierda
                    if (hayColision == false && ((posBolaX + DIAMETROBOLA) >= xbloque && (posBolaX + DIAMETROBOLA) <= xbloque + AJUSTECOLISIONES))
                    {
                        if (posBolaY + DIAMETROBOLA >= ybloque && posBolaY <= ybloque + juego.cbloques.ALTOBLOQUE)
                        {
                            if (juego.MODODEBUG) System.out.println("Colisión IZQUIERDA");
                            juego.cbloques.bloques[i][j].visible = false;
                            velX = velX * (-1);
                            hayColision = true;
                        }
                    }
                    
                    // Colisión derecha
                    if (hayColision == false && ((posBolaX) >= xbloque + juego.cbloques.ANCHOBLOQUE - AJUSTECOLISIONES && (posBolaX) <= xbloque + juego.cbloques.ANCHOBLOQUE))
                    {
                        if (posBolaY + DIAMETROBOLA >= ybloque && posBolaY <= ybloque + juego.cbloques.ALTOBLOQUE)
                        {
                            if (juego.MODODEBUG) System.out.println("Colisión DERECHA");
                            juego.cbloques.bloques[i][j].visible = false;
                            velX = velX * (-1);
                            hayColision = true;
                        }
                    }
                }
            }
        }
        // Si se ha producido una colisión con un bloque...
        if (hayColision) 
        {
            // ... sumamos la cantidad de puntos obtenidos al total
            juego.puntosactuales = juego.puntosactuales + (juego.nivel * 10);
            // Sumamos 1 a la cantidad de bloques destruidos
            juego.cbloques.destruidos++;
            
            // Comprobamos si quedan bloques para romper
            if (juego.cbloques.destruidos >= 30)
            {
                juego.cbloques.destruidos = 0;
                juego.cbloques.subirNivel(juego);
            }
        }   
        
        hayColision = false;

        // Movimiento de la bola
        posBolaX = posBolaX + VELOCIDAD * (velX);
        posBolaY = posBolaY + VELOCIDAD * (velY);
        
    }
    // FIN moverBola
    

}
// FIN Bola

// ConjuntoBloques
// Esta clase se encarga de pintar los bloques en la pantalla
class ConjuntoBloques
{
    private Arkanoid juego;
    
    // Tamaño del bloque
    public static final int ALTOBLOQUE = 10;
    public static final int ANCHOBLOQUE = 20;
    
    // Parámetros de dibujado del bloque
    private static final int OFFSETX = 100;
    private static final int OFFSETY = 100;
    private static final int SEPARACIONX = 11;
    private static final int SEPARACIONY = 15;
    
    // Instanciamos el array de bloques
    public Bloque[][] bloques = new Bloque[10][3];
    
    // Cantidad bloques destruidos, la inicializamos a 0
    public static int destruidos = 0;
    
    // Constructor base para la clase Bloque
    ConjuntoBloques()
    {}
         
    ConjuntoBloques(Arkanoid juego)
    {
        int calculoposicionx;
        int calculoposiciony;
        
        this.juego = juego;
        
        for (int j = 0; j <= 2; j++)
        {
            for (int i=0; i <= 9; i++)
            {
                calculoposicionx = OFFSETX + i * (ANCHOBLOQUE + SEPARACIONX);
                calculoposiciony = OFFSETY + j * (ALTOBLOQUE + SEPARACIONY);
                bloques[i][j] = new Bloque(calculoposicionx , calculoposiciony, "Bloque " + j + i);
                if (juego.MODODEBUG) System.out.println("Instanciando el bloque i=" + i + ", j=" + j + " con los valores posX=" + bloques[i][j].posX + ", posY=" + bloques[i][j].posY);
            }
        }
    }
    
    public void paint(Graphics2D g) 
    {
        for (int j = 0; j <= 2; j++)
        {
            for (int i=0; i <= 9; i++)
            {
                if (bloques[i][j].visible)
                {
                    g.setColor(bloques[i][j].colorBloque);
                    g.fillRect(bloques[i][j].posX, bloques[i][j].posY, ANCHOBLOQUE, ALTOBLOQUE);
                }
            }
        }
    }

    // subirNivel
    // Este método lo que hace es subir el nivel del juego, con lo que subimos la velocidad del movimiento de la bola y los puntos ganados por cada bloque destruido
    public void subirNivel (Arkanoid juego)
    {
        if(juego.nivel <= 4 )
        {    
            // Si el nivel es 4 o menor, pasamos al siguiente nivel
            juego.pausarJuego = true;
            juego.nivel++;
            juego.velocidad -= 2;
            juego.bola.reiniciarBola();
            juego.barra.reiniciarBarra();
            juego.cbloques.reiniciarBloques();
            juego.rotuloNivel = true;
            juego.repaint();
        }
        else
        {
            // En caso de que el nivel sea superior a 5, el juego acaba y felicitamos al usuario
            JOptionPane.showMessageDialog(null, "¡Enhorabuena! Has superado el nivel máximo del juego, has obtenido: " + juego.puntosactuales + " puntos y el record es " + juego.record + ". ¡Nuevo record registrado!","¡Has ganado!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
    // FIN subirNivel
    
    // reiniciarJuego
    // Este método reinicia todas las variables para recargar el juego desde el nivel 1
    public void reiniciarJuego (Arkanoid juego)
    {
        // Si el nivel es 4 o menor, pasamos al siguiente nivel
        juego.pausarJuego = false;
        juego.iniciarJuego = false;
        juego.puntosactuales = 0;
        juego.nivel = 1;
        juego.vidas = 3;
        juego.velocidad = juego.velocidadInicial;
        juego.bola.reiniciarBola();
        juego.barra.reiniciarBarra();
        juego.cbloques.reiniciarBloques();
        juego.cbloques.destruidos = 0;
        juego.rotuloNivel = false;
        juego.rotuloVida = false;
        juego.repaint();
    }
    // FIN subirnivel
    
    // reiniciarBloques
    // Esta función reinicializa los bloques al subir de nivel
    public void reiniciarBloques()
    {
        for (int j = 0; j <= 2; j++)
        {
            for (int i=0; i <= 9; i++)
            {
                bloques[i][j].visible = true;
                bloques[i][j].colorBloque = this.bloques[i][j].colorRandomBloque();
            }
        }
    }
    // FIN reiniciarBloques
}
// FIN ConjuntoBloques

// Bloque
// Esta clase controla las propiedades individuales de cada bloque
class Bloque extends ConjuntoBloques
{   
    public String nombre;
    
    // Posición del bloque
    public int posX;
    public int posY;
    
    // Color del bloque
    public Color colorBloque;
    
    // Mostrar bloque: visible = true por defecto
    public Boolean visible = true;
   
    Bloque (int X, int Y, String n)
    {
        this.nombre = n;
        this.posX = X;
        this.posY = Y;
        this.colorBloque = colorRandomBloque();
    }
    
    // colorRandomBloque
    // Aleatorizamos el color de los ladrillos cada vez que se generan
    public Color colorRandomBloque()
    {
        // Generamos el número aleatorio entre 1 y 6
        int numeroColor = (int) (Math.random()*6+1);

        // Según el número, devolvemos un color distinto
        switch (numeroColor)
        {
            case 1:
                return (Color.ORANGE);
            case 2:
                return (Color.PINK);
            case 3:
                return (Color.CYAN);
            case 4:
                return (Color.BLUE);
            case 5:
                return (Color.GREEN);
            default:
                return (Color.MAGENTA);
        }
    }
    // FIN colorRandomBloque
    
}
// FIN Bloque

// Texto
// Esta clase controla y dibuja el texto en pantalla
class Texto
{
    private Arkanoid juego;
    
    // Color del texto
    public final Color COLORTEXTO = Color.WHITE;

    // Texto para el nivel
    private final int nivelX = 75;
    private final int nivelY = 15;
    
    // Texto para las vidas
    private final int vidasX = 175;
    private final int vidasY = 15;
    
    // Texto para los puntos
    private final Font fuente = new Font("Arial",Font.BOLD,10);
    private final int ptosactualesX = 275;
    private final int ptosactualesY = 15;
    
    // Texto para el record
    private final int recordX = 375;
    private final int recordY = 15;
    
    // Texto para la pausa
    public final Color COLORPAUSA = Color.PINK;
    private final Font fuentep = new Font("Arial",Font.BOLD,16);
    private final int pausaX = 70;
    private final int pausaY = 250;
    
    // Texto pulsar para comenzar inicial
    private final Font fuentei = new Font("Arial",Font.BOLD,16);
    private final int pinicialX = 130;
    private final int pinicialY = 300;
    
    // Texto nombre
    private final Font fuenten = new Font("Palatino Linotype",Font.ITALIC,16);
    private final int nombreX = 130;
    private final int nombreY = 380;
     
    // Texto para el título
    private final Font fuenteinicio = new Font("Impact",Font.BOLD,100);
    public final Color COLORTITULO = new Color(51,51,255);
    private final int inicioX = 25;
    private final int inicioY = 250;
    
    // Texto siguiente nivel
    private final Font fuentenivel = new Font("Impact",Font.BOLD,100);
    public final Color COLORNIVEL = Color.YELLOW;
    private final int rotnivelX = 90;
    private final int rotnivelY = 300;
    
    // Texto vida perdida
    private final Font fuentevida = new Font("Impact",Font.BOLD,70);
    public final Color COLORVIDA = Color.RED;
    private final int rotvidaX = 36;
    private final int rotvidaY = 300;
    
    Texto(Arkanoid juego)
    {
        this.juego = juego;
    }
    
    public void paint(Graphics2D g) 
    {
        g.setFont(fuente);
        g.setColor(COLORTEXTO);
        
	g.drawString("Puntos: " + juego.puntosactuales, ptosactualesX, ptosactualesY);
        g.drawString("Nivel: " + juego.nivel, nivelX, nivelY);
        g.drawString("Vidas: " + juego.vidas, vidasX, vidasY);
        g.drawString("Record: " + juego.record, recordX, recordY);
        
        if (!juego.iniciarJuego)
        {
            // Pintamos el título
            g.setFont(fuenteinicio);
            g.setColor(COLORTITULO);
            g.drawString("ARKANOID", inicioX, inicioY );
            
            // Pintamos el rótulo PULSA PARA COMENZAR
            g.setFont(fuentei);
            g.setColor(COLORTEXTO);
            g.drawString("Pulsa ESPACIO para comenzar", pinicialX, pinicialY);
            
            // Pintamos el nombre
            g.setFont(fuenten);
            g.drawString("Proyecto de Carolina Casillas Torres", nombreX, nombreY);
        }
        else if (juego.pausarJuego)
        {
            if (juego.rotuloNivel)
            {
                // En caso de que la variable rotuloNivel sea true, mostramos el rótulo del siguiente nivel
                g.setFont(fuentenivel);
                g.setColor(COLORNIVEL);
                g.drawString("NIVEL " + juego.nivel, rotnivelX, rotnivelY);
                juego.rotuloNivel = false;
            }
            else
            {
                if (juego.rotuloVida)
                {
                    // En caso de que la variable rotuloVida sea true, mostramos el rótulo de vida perdida
                    g.setFont(fuentevida);
                    g.setColor(COLORVIDA);
                    g.drawString("VIDA PERDIDA", rotvidaX, rotvidaY);
                    juego.rotuloVida = false;
                }
                else
                {
                    // En caso contrario, mostramos el texto normal de juego en pausa
                    g.setFont(fuentep);
                    g.setColor(COLORPAUSA);
                    g.drawString("Juego pausado: pulsa ESPACIO para continuar", pausaX, pausaY);
                }
            }

        }
    }
}
// FIN Texto
