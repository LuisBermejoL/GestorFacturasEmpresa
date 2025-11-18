package Modelo;

/**
 *
 * @author luisb
 */
public class Proveedor extends Entidad {
    private int codigo;

    public Proveedor() {
    }

    public Proveedor(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}