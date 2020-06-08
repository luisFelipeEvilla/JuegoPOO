package principal.inventario.elementosMujeres;

import principal.Constantes;
import principal.GestorPrincipal;
import principal.inventario.Objeto;
import principal.maquinaestado.estado.logros.Mujer;
import principal.sprites.HojaSprites;
import principal.sprites.Sprite;

public class Elemento extends Objeto {

    public static HojaSprites hojaConsumibles = new HojaSprites(Constantes.RUTA_OBJETOS, 32, false);

    public Elemento(int id, String nombre, String descripcion) {
        super(id, nombre, descripcion);
    }

    public void asignarMujer(final Mujer mujer) {
        if (nombre.equals("Galleta")) {
            mujer.setImagenBiografia(Constantes.IMAGENFONDO);
        }
        if (nombre.equals("Zanahoria")) {
//            mujer.setImagenBiografia(Constantes.IMAGENFONDO);
        }
        if (nombre.equals("Bola dragon")) {
//            mujer.setImagenBiografia(Constantes.IMAGENFONDO);
        }
        if (nombre.equals("Bola verde")) {
//            mujer.setImagenBiografia(Constantes.IMAGENFONDO);
        }
    }

    public void cambiarEstado() {
        GestorPrincipal.ge.cambiarEstadoActual(7);
    }

    @Override
    public Sprite getSprite() {
        return hojaConsumibles.getSprite(id);
    }
}