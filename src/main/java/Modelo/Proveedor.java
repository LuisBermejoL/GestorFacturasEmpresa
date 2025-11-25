package Modelo;

/**
 * Clase que representa a un proveedor en el sistema.
 * Hereda de Entidad, por lo que incluye nombre, NIF, email y teléfono.
 * En la base de datos, cada proveedor está vinculado a una entidad mediante id_entidad.
 * 
 * @author luisb
 */
public class Proveedor extends Entidad {

    // Código único del proveedor (distinto del ID de entidad)
    private int codigo;

    /**
     * Constructor vacío necesario para frameworks, serialización y uso en formularios.
     */
    public Proveedor() {
        super(); // Llama al constructor vacío de Entidad
    }

    /**
     * Constructor que inicializa solo el código del proveedor.
     * Los datos de Entidad deben establecerse por separado para evitar conflictos con la base de datos.
     * 
     * @param codigo Código único del proveedor
     */
    public Proveedor(int codigo) {
        super(); // Asegura que Entidad se inicializa correctamente
        this.codigo = codigo;
    }

    // Getter y setter del código

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}