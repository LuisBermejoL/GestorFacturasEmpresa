package Controlador;

import DAO.EmpresaDAO;
import Modelo.Empresa;
import java.util.List;

/**
 *
 * @author luisb
 */
public class EmpresaController {
    private final EmpresaDAO empresaDAO;

    public EmpresaController() {
        this.empresaDAO = new EmpresaDAO();
    }

    public void añadirEmpresa(Empresa e) {
        empresaDAO.añadir(e);
    }

    public void modificarEmpresa(Empresa e) {
        empresaDAO.modificar(e);
    }

    public void borrarEmpresaPorNIF(String nif) {
        empresaDAO.borrarPorNIF(nif);
    }

    public Empresa consultarEmpresaPorNIF(String nif) {
        return empresaDAO.consultarPorNIF(nif);
    }

    public List<Empresa> consultarTodasEmpresas() {
        return empresaDAO.consultarTodas();
    }
}