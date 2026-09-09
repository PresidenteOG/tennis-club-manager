package Tennis_ERP.TennisErp.service;

import Tennis_ERP.TennisErp.dao.EquipoDAO;
import Tennis_ERP.TennisErp.domain.Equipo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipoServiceImpl implements EquipoService {

    private EquipoDAO equipoDAO;

    @Autowired
    public void setEquipoDAO(EquipoDAO equipoDAO) {
        this.equipoDAO = equipoDAO;
    }

    @Override
    public Equipo crearEquipo(Equipo equipo) {
        return equipoDAO.save(equipo);
    }

    @Override
    public Optional<Equipo> obtenerEquipoPorId(Long id) {
        return equipoDAO.findById(id);
    }

    @Override
    public List<Equipo> listarEquipos() {
        return equipoDAO.findAll();
    }

    @Override
    public Equipo actualizarEquipo(Equipo equipo) {
        return equipoDAO.save(equipo);
    }

    @Override
    public void eliminarEquipo(Long id) {
        equipoDAO.deleteById(id);
    }

    @Override
    public List<Equipo> findEquiposByNombreCategoria(String nombreCategoria) {
        return equipoDAO.findEquiposByNombreCategoria(nombreCategoria);
    }

    @Override
    public List<Equipo> findAll() {
        return equipoDAO.findAll(); // Llama al método del repositorio
    }
    
}
