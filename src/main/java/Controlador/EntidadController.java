package Controlador;

import DAO.EntidadDAO;
import Modelo.Entidad;
import java.util.List;

/**
 * Controlador para la entidad base.
 * Permite consultar todas las entidades de una empresa.
 * 
 * @author luisb
 */
public class EntidadController {
    private final EntidadDAO entidadDAO = new EntidadDAO();

    /**
     *
     * @param empresaId
     * @return
     */
    public List<Entidad> consultarTodos(long empresaId) {
        return entidadDAO.consultarTodos(empresaId);
    }
}