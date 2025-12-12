package Modelo;

/**
 * Clase que representa un cliente en el sistema.
 * Hereda de Entidad, por lo que incluye datos comunes como nombre, NIF, email y teléfono.
 * En la base de datos corresponde a la tabla 'clientes', que se relaciona 1:1 con 'entidad'.
 * 
 * @author luisb
 */
public class Cliente extends Entidad {

    // Código único del cliente (distinto del ID de entidad)
    private int codigo;

    /**
     * Constructor vacío necesario para frameworks, serialización y formularios JavaFX.
     */
    public Cliente() {
        super(); // Inicializa también la parte de Entidad
    }

    /**
     * Constructor que inicializa solo el código del cliente.
     * Los datos de Entidad deben establecerse por separado para evitar conflictos con la BD.
     * 
     * @param codigo Código único del cliente
     */
    public Cliente(int codigo) {
        super(); // Inicializa también la parte de Entidad
        this.codigo = codigo;
    }

    // Getter y setter del código

    /**
     *
     * @return
     */

    public int getCodigo() {
        return codigo;
    }

    /**
     *
     * @param codigo
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}