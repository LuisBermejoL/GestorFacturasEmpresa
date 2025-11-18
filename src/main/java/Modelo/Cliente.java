package Modelo;

/**
 *
 * @author luisb
 */
public class Cliente extends Entidad {
    private int codigo;

    public Cliente() {
    }

    public Cliente(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}