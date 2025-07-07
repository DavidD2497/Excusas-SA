package ar.edu.davinci.excusas.model.empleados.encargados;

import ar.edu.davinci.excusas.model.excusas.Excusa;
import ar.edu.davinci.excusas.model.empleados.Encargado;


public class GerenteRecursosHumanos extends Encargado {

    public GerenteRecursosHumanos(String nombre, String email, int legajo) {
        super(nombre, email, legajo);
    }

    @Override
    public boolean puedeManejarComplejo() {
        return true;
    }

    @Override
    public void procesarExcusa(Excusa excusa) {
        System.out.println("Gerente de RRHH procesando excusa compleja para: " + excusa.getNombreEmpleado());
        System.out.println("Excusa: " + excusa.getDescripcion());
    }
}
