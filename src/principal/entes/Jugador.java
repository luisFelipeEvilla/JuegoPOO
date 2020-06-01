package principal.entes;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import principal.Constantes;
import principal.ElementosPrincipales;
import principal.GestorPrincipal;
import principal.control.GestorControles;
import principal.herramientas.DibujoOpciones;
import principal.inventario.RegistroObjetos;
import principal.inventario.armas.Arma;
import principal.inventario.armas.DesArmado;
import principal.sprites.HojaSprites;

public class Jugador {

    private double posicionX;
    private double posicionY;

    private double velocidadMovimiento = 0.7;
    private boolean enMovimiento;
    private int animacion;
    private int a;
    private int d;

    private int direccion;

    private HojaSprites hs;
    private BufferedImage imagenActual;

    private final int anchoJugador = 16;
    private final int altoJugador = 16;

    private final Rectangle LIMITE_ARRIBA = new Rectangle(Constantes.CENTRO_VENTANA_X - 25, Constantes.CENTRO_VENTANA_Y - anchoJugador + 9, anchoJugador, 1);
    private final Rectangle LIMITE_ABAJO = new Rectangle(Constantes.CENTRO_VENTANA_X - 25, Constantes.CENTRO_VENTANA_Y - 2, anchoJugador, 1);
    private final Rectangle LIMITE_IZQUIERDA = new Rectangle(Constantes.CENTRO_VENTANA_X - 25, Constantes.CENTRO_VENTANA_Y - altoJugador - 2, 1, altoJugador - 7);
    private final Rectangle LIMITE_DERECHA = new Rectangle(Constantes.CENTRO_VENTANA_X - 9, Constantes.CENTRO_VENTANA_Y - altoJugador - 2, 1, altoJugador - 7);

    private int resistencia = 300;
    private int resistenciaTotal = 300;
    private int recuperacion = 100;
    private boolean recuperado = true;

    public int limitePeso = 100;
    public int pesoActual = 30;

    private AlmacenEquipo ae;
    private ArrayList<Rectangle> alcanceArma;

    private boolean spawning;
    private boolean visible;
    private static int spawnTime = 0;
    //Parpadear o titilar
    private static int flickerTime = 0;

    private int vida;
    private boolean muerto;
//
    //    public Jugador(String Ruta) {
    //
    //        this.posicionX = ElementosPrincipales.mapa.getCoordenadaInicial().getX();
    //        this.posicionY = ElementosPrincipales.mapa.getCoordenadaInicial().getY();
    //        this.enMovimiento = false;
    //        direccion = 2;
    //
    //        this.hs = new HojaSprites(Ruta, Constantes.LADO_SPRITE, false);
    //
    //        imagenActual = hs.getSprite(direccion, 0).getImagen();
    //        animacion = 0;
    //        a = 0;
    //        d = 3;
    //    }

    public Jugador() {

        this.posicionX = ElementosPrincipales.mapa.getCoordenadaInicial().getX();
        this.posicionY = ElementosPrincipales.mapa.getCoordenadaInicial().getY();
        this.enMovimiento = false;
        direccion = 0;

        this.hs = new HojaSprites(Constantes.RUTA_PERSONAJE, Constantes.LADO_SPRITE, false);

        imagenActual = hs.getSprite(direccion, 0).getImagen();
        animacion = 0;
        a = 0;
        d = 4;

        ae = new AlmacenEquipo((Arma) RegistroObjetos.getObjeto(599));
        alcanceArma = new ArrayList();

        vida = 1000;
        visible = true;
        muerto = false;
    }

    public void actualizar() {

        if (vida == 0) {
            muerto = true;
            GestorPrincipal.ge.cambiarEstadoActual(3);
        }
        if (spawning) {

            flickerTime++;
            spawnTime++;

            if (flickerTime > Constantes.FLICKER_TIME) {

                visible = !visible;
                flickerTime = 0;
            }

            if (spawnTime > Constantes.SPAWNING_TIME) {
                spawning = false;
                visible = true;
                muerto = false;
                spawnTime = 0;
                flickerTime = 0;
            }
        }

        if (animacion < 32767) {

            animacion++;
        } else {
            animacion = 0;
        }
        gestionarVelocidadResistencia();
        enMovimiento = false;
        determinarDireccion();
        animar();
        actualizarArma();
        cambiarHojaSprite();
    }

    private void actualizarArma() {
        if (!(ae.getArma() instanceof DesArmado)) {
            calcularAlcance();
            ae.getArma().actualizar();
        }
    }

    private void calcularAlcance() {
        if (!(ae.getArma() instanceof DesArmado)) {
            alcanceArma = ae.getArma().getAlcance(this);
        }
    }

    private void cambiarHojaSprite() {
        if (ae.getArma() instanceof Arma && !(ae.getArma() instanceof DesArmado)) {
            hs = new HojaSprites(Constantes.RUTA_PERSONAJEARMADO, Constantes.LADO_SPRITE, false);
        }
    }

    private void gestionarVelocidadResistencia() {

        if (GestorControles.teclado.run && resistencia > 0) {
            velocidadMovimiento = 1.2;
            recuperado = false;
            recuperacion = 0;
        } else {
            velocidadMovimiento = 0.7;
            if (!recuperado && recuperacion < 100) {
                recuperacion++;
            }
            if (recuperacion == 100 && resistencia < resistenciaTotal) {
                resistencia++;
            }
        }
    }

    private void determinarDireccion() {

        final int velocidadX = getVelocidadX();
        final int velocidadY = getVelocidadY();

        if (velocidadX == 0 && velocidadY == 0) {
            return;
        }
        if ((velocidadX != 0 && velocidadY == 0) || (velocidadX == 0 && velocidadY != 0)) {
            mover(velocidadX, velocidadY);
        } else {
            if (velocidadX == -1 && velocidadY == -1) {
                if (GestorControles.teclado.left.getUltimaPulsacion() > GestorControles.teclado.up.getUltimaPulsacion()) {
                    mover(velocidadX, 0);
                } else {
                    mover(0, velocidadY);
                }
            }
            if (velocidadX == -1 && velocidadY == 1) {
                if (GestorControles.teclado.left.getUltimaPulsacion() > GestorControles.teclado.down.getUltimaPulsacion()) {
                    mover(velocidadX, 0);
                } else {
                    mover(0, velocidadY);
                }
            }
            if (velocidadX == 1 && velocidadY == -1) {
                if (GestorControles.teclado.right.getUltimaPulsacion() > GestorControles.teclado.up.getUltimaPulsacion()) {
                    mover(velocidadX, 0);
                } else {
                    mover(0, velocidadY);
                }
            }
            if (velocidadX == 1 && velocidadY == 1) {
                if (GestorControles.teclado.right.getUltimaPulsacion() > GestorControles.teclado.down.getUltimaPulsacion()) {
                    mover(velocidadX, 0);
                } else {
                    mover(0, velocidadY);
                }
            }
        }
    }

    private int getVelocidadX() {

        int velocidadX = 0;

        if (GestorControles.teclado.left.isPulsada() && !GestorControles.teclado.right.isPulsada()) {
            velocidadX = -1;
        } else if (GestorControles.teclado.right.isPulsada() && !GestorControles.teclado.left.isPulsada()) {
            velocidadX = 1;
        }
        return velocidadX;
    }

    private int getVelocidadY() {

        int velocidadY = 0;

        if (GestorControles.teclado.up.isPulsada() && !GestorControles.teclado.down.isPulsada()) {
            velocidadY = -1;
        } else if (GestorControles.teclado.down.isPulsada() && !GestorControles.teclado.up.isPulsada()) {
            velocidadY = 1;
        }
        return velocidadY;
    }

    private void mover(final int velocidadX, final int velocidadY) {

        enMovimiento = true;

        cambiarDireccion(velocidadX, velocidadY);

        if (!fueraMapa(velocidadX, velocidadY)) {

            if (velocidadX == -1 && !enColisionIzquierda(velocidadX)) {
                posicionX += velocidadX * velocidadMovimiento;
                if (GestorControles.teclado.run && resistencia > 0) {
                    resistencia--;
                }
            }
            if (velocidadX == 1 && !enColisionDerecha(velocidadX)) {
                posicionX += velocidadX * velocidadMovimiento;
                if (GestorControles.teclado.run && resistencia > 0) {
                    resistencia--;
                }
            }
            if (velocidadY == -1 && !enColisionArriba(velocidadY)) {
                posicionY += velocidadY * velocidadMovimiento;
                if (GestorControles.teclado.run && resistencia > 0) {
                    resistencia--;
                }
            }
            if (velocidadY == 1 && !enColisionAbajo(velocidadY)) {
                posicionY += velocidadY * velocidadMovimiento;
                if (GestorControles.teclado.run && resistencia > 0) {
                    resistencia--;
                }
            }
        }
    }

    private boolean enColisionArriba(int velocidadY) {

        for (int r = 0; r < ElementosPrincipales.mapa.getAreasColisionJugador().size(); r++) {
            final Rectangle area = ElementosPrincipales.mapa.getAreasColisionJugador().get(r);

            int origenX = area.x;
            int origenY = area.y + velocidadY * (int) (velocidadMovimiento + 0.6) + 3 * (int) (velocidadMovimiento + 0.6);

            final Rectangle areaFutura = new Rectangle(origenX, origenY, area.width, area.height);

            if (LIMITE_ARRIBA.intersects(areaFutura)) {
                return true;
            }
        }
        return false;
    }

    private boolean enColisionAbajo(int velocidadY) {

        for (int r = 0; r < ElementosPrincipales.mapa.getAreasColisionJugador().size(); r++) {
            final Rectangle area = ElementosPrincipales.mapa.getAreasColisionJugador().get(r);

            int origenX = area.x;
            int origenY = area.y + velocidadY * (int) (velocidadMovimiento + 0.6) - 3 * (int) (velocidadMovimiento + 0.6);

            final Rectangle areaFutura = new Rectangle(origenX, origenY, area.width, area.height);

            if (LIMITE_ABAJO.intersects(areaFutura)) {
                return true;
            }
        }
        return false;
    }

    private boolean enColisionIzquierda(int velocidadX) {

        for (int r = 0; r < ElementosPrincipales.mapa.getAreasColisionJugador().size(); r++) {
            final Rectangle area = ElementosPrincipales.mapa.getAreasColisionJugador().get(r);

            int origenX = area.x + velocidadX * (int) (velocidadMovimiento + 0.6) + 3 * (int) (velocidadMovimiento + 0.6);
            int origenY = area.y;

            final Rectangle areaFutura = new Rectangle(origenX, origenY, area.width, area.height);

            if (LIMITE_IZQUIERDA.intersects(areaFutura)) {
                return true;
            }
        }
        return false;
    }

    private boolean enColisionDerecha(int velocidadX) {

        for (int r = 0; r < ElementosPrincipales.mapa.getAreasColisionJugador().size(); r++) {
            final Rectangle area = ElementosPrincipales.mapa.getAreasColisionJugador().get(r);

            int origenX = area.x + velocidadX * (int) (velocidadMovimiento + 0.6) - 3 * (int) (velocidadMovimiento + 0.6);
            int origenY = area.y;

            final Rectangle areaFutura = new Rectangle(origenX, origenY, area.width, area.height);

            if (LIMITE_DERECHA.intersects(areaFutura)) {
                return true;
            }
        }
        return false;
    }

    private boolean fueraMapa(final int velocidadX, final int velocidadY) {

        int posicionFuturaX = (int) posicionX + velocidadX * (int) (velocidadMovimiento + 0.6);
        int posicionFuturaY = (int) posicionY + velocidadY * (int) (velocidadMovimiento + 0.6);

        final Rectangle bordesMapa = ElementosPrincipales.mapa.getBordes(posicionFuturaX, posicionFuturaY);

        final boolean FUERA;

        if (LIMITE_ARRIBA.intersects(bordesMapa) || LIMITE_ABAJO.intersects(bordesMapa) || LIMITE_IZQUIERDA.intersects(bordesMapa)
                || LIMITE_DERECHA.intersects(bordesMapa)) {

            FUERA = false;
        } else {
            FUERA = true;
        }

        return FUERA;
    }

    private void cambiarDireccion(final int velocidadX, final int velocidadY) {

        if (velocidadX == -1) {
            direccion = 3;
        } else if (velocidadX == 1) {
            direccion = 2;
        }
        if (velocidadY == -1) {
            direccion = 1;
        } else if (velocidadY == 1) {
            direccion = 0;
        }
    }

    private void animar() {

        if (enMovimiento) {
            GestorControles.teclado.dance = false;
            if (animacion % 10 == 0) {
                a++;
                if (a >= 4) {
                    a = 0;
                }
            }
            switch (a) {
                case 0:
                    imagenActual = hs.getSprite(direccion, 1).getImagen();
                    break;
                case 1:
                    imagenActual = hs.getSprite(direccion, 0).getImagen();
                    break;
                case 2:
                    imagenActual = hs.getSprite(direccion, 2).getImagen();
                    break;
                case 3:
                    imagenActual = hs.getSprite(direccion, 0).getImagen();
                    break;
            }
        } else {
            if (!GestorControles.teclado.dance) {
                a = 0;
                d = 4;
            }
            if (GestorControles.teclado.dance) {
                if (animacion % 13 == 0) {
                    a++;
                    if (a >= 3) {
                        a = 0;
                        d++;
                        if (d >= 7) {
                            d = 4;
                        }
                    }
                }
                imagenActual = hs.getSprite(d, a).getImagen();
                direccion = 0;
            } else {
                imagenActual = hs.getSprite(direccion, 0).getImagen();
            }
        }

//        if (direccion == 2 || direccion == 0) {
//            if (enMovimiento) {
//                if (animacion * velocidadMovimiento % 46 > 23) {
//                    imagenActual = hs.getSprite(direccion + 1, 0).getImagen();
//                } else {
//                    imagenActual = hs.getSprite(direccion + 1, 1).getImagen();
//                }
//            } else {
//                if (animacion % 180 >= 0 && animacion % 180 <= 20) {
//                    imagenActual = hs.getSprite(direccion, 0).getImagen();
//                } else if (animacion % 180 > 20 && animacion % 180 <= 40) {
//                    imagenActual = hs.getSprite(direccion, 1).getImagen();
//                } else if (animacion % 180 > 40 && animacion % 180 <= 60) {
//                    imagenActual = hs.getSprite(direccion, 2).getImagen();
//                } else if (animacion % 180 > 60 && animacion % 180 <= 80) {
//                    imagenActual = hs.getSprite(direccion, 3).getImagen();
//                } else if (animacion % 180 > 80 && animacion % 180 <= 100) {
//                    imagenActual = hs.getSprite(direccion, 4).getImagen();
//                } else if (animacion % 180 > 100 && animacion % 180 <= 120) {
//                    imagenActual = hs.getSprite(direccion, 3).getImagen();
//                } else if (animacion % 180 > 120 && animacion % 180 <= 140) {
//                    imagenActual = hs.getSprite(direccion, 2).getImagen();
//                } else if (animacion % 180 > 140 && animacion % 180 <= 160) {
//                    imagenActual = hs.getSprite(direccion, 1).getImagen();
//                } else {
//                    imagenActual = hs.getSprite(direccion, 0).getImagen();
//                }
//            }
//        }
//        if (direccion == 4 || direccion == 5) {
//            if (enMovimiento) {
//                if (animacion * velocidadMovimiento % 80 >= 0 && animacion * velocidadMovimiento % 80 <= 20) {
//                    imagenActual = hs.getSprite(direccion, 1).getImagen();
//                } else if (animacion * velocidadMovimiento % 80 > 20 && animacion * velocidadMovimiento % 80 <= 40) {
//                    imagenActual = hs.getSprite(direccion, 0).getImagen();
//                } else if (animacion * velocidadMovimiento % 80 > 40 && animacion * velocidadMovimiento % 80 <= 60) {
//                    imagenActual = hs.getSprite(direccion, 2).getImagen();
//                } else {
//                    imagenActual = hs.getSprite(direccion, 0).getImagen();
//                }
//            } else {
//                imagenActual = hs.getSprite(direccion, 0).getImagen();
//            }
//        }
    }

    public void renacer() {
        this.posicionX = ElementosPrincipales.mapa.getCoordenadaInicial().x;
        this.posicionY = ElementosPrincipales.mapa.getCoordenadaInicial().y;
        this.direccion = 0;
        vida = 1000;
        spawning = true;
    }

    public void dibujar(Graphics g) {
        if (!visible) {
            return;
        }

        final int centroX = Constantes.ANCHO_JUEGO / 2 - Constantes.LADO_SPRITE;
        final int centroY = Constantes.ALTO_JUEGO / 2 - Constantes.LADO_SPRITE;

        //Frente y espalda
//        g.drawRect(centroX + 7, centroY, 17, 32);
        // Derecha e izquierda
//        g.drawRect(centroX + 13, centroY, 6, 32);
        DibujoOpciones.dibujarImagen(g, imagenActual, centroX, centroY);
//        DibujoOpciones.dibujarRectBorde(g, getArea(), Color.red);
//        g.drawRect(LIMITE_ARRIBA.x, LIMITE_ARRIBA.y, LIMITE_ARRIBA.width, LIMITE_ARRIBA.height);
//        g.drawRect(LIMITE_ABAJO.x, LIMITE_ABAJO.y, LIMITE_ABAJO.width, LIMITE_ABAJO.height);
//        g.drawRect(LIMITE_IZQUIERDA.x, LIMITE_IZQUIERDA.y, LIMITE_IZQUIERDA.width, LIMITE_IZQUIERDA.height);
//        g.drawRect(LIMITE_DERECHA.x, LIMITE_DERECHA.y, LIMITE_DERECHA.width, LIMITE_DERECHA.height);
    }

    public void setPosicionX(double posicionX) {
        this.posicionX = posicionX;
    }

    public void setPosicionY(double posicionY) {
        this.posicionY = posicionY;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public double getPosicionY() {
        return posicionY;
    }

    public int getPosicionXINT() {
        return (int) posicionX;
    }

    public int getPosicionYINT() {
        return (int) posicionY;
    }

    public Rectangle getLIMITE_ARRIBA() {
        return LIMITE_ARRIBA;
    }

    public int getAnchoJugador() {
        return anchoJugador;
    }

    public int getAltoJugador() {
        return altoJugador;
    }

    public int getResistencia() {
        return resistencia;
    }

    public int getResistenciaTotal() {
        return resistenciaTotal;
    }

    public AlmacenEquipo getAlmacenEquipo() {
        return ae;
    }

    public int getDireccion() {
        return direccion;
    }

    public ArrayList<Rectangle> getAlcanceArma() {
        return alcanceArma;
    }

    public Rectangle getArea() {
        return new Rectangle(Constantes.CENTRO_VENTANA_X - Constantes.LADO_SPRITE + 8, Constantes.CENTRO_VENTANA_Y - Constantes.LADO_SPRITE + 10, Constantes.LADO_SPRITE / 2, Constantes.LADO_SPRITE - 10);
    }

    public void setVida(final int bajoVida) {
        if (vida - bajoVida < 0) {
            vida = 0;
        } else {
            vida -= bajoVida;
        }
    }

    public int getVida() {
        return vida;
    }

    public boolean isMuerto() {
        return muerto;
    }

}