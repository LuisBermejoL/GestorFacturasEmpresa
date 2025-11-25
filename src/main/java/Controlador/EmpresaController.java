package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import java.util.List;

/**
 * Controlador para la entidad Empresa.
 * Encapsula la lógica de negocio y delega en EmpresaDAO
 * las operaciones CRUD sobre la tabla 'empresa'.
 * 
 * @author luisb
 */
public class EmpresaController {
    private final EmpresaDAO empresaDAO = new EmpresaDAO();

    /**
     * Añadir una nueva empresa.
     * 
     * @param e Objeto Empresa con los datos a insertar
     */
    public void añadir(Empresa e) {
        empresaDAO.añadir(e);
    }

    /**
     * Modificar los datos de una empresa existente.
     * 
     * @param e Objeto Empresa con los datos actualizados
     */
    public void modificar(Empresa e) {
        empresaDAO.modificar(e);
    }

    /**
     * Borrar una empresa por su NIF.
     * 
     * @param nif Identificador fiscal único de la empresa
     */
    public void borrarPorNIF(String nif) {
        empresaDAO.borrarPorNIF(nif);
    }

    /**
     * Consultar una empresa por su NIF.
     * 
     * @param nif Identificador fiscal único de la empresa
     * @return Empresa encontrada o null si no existe
     */
    public Empresa consultarPorNIF(String nif) {
        return empresaDAO.consultarPorNIF(nif);
    }

    /**
     * Consultar todas las empresas registradas.
     * 
     * @return Lista de objetos Empresa
     */
    public List<Empresa> consultarTodas() {
        return empresaDAO.consultarTodas();
    }
}