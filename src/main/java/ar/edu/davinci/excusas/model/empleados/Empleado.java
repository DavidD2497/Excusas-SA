package com.excusas.model.empleados;

import com.excusas.model.empleados.interfaces.IEmpleado;
import com.excusas.model.excusas.Excusa;
import com.excusas.model.excusas.motivos.MotivoExcusa;

public class Empleado implements IEmpleado {

    private final String nombre;
    private final String email;
    private final int legajo;

    public Empleado(String nombre, String email, int legajo) {
        this.nombre = nombre;
        this.email = email;
        this.legajo = legajo;
    }

    @Override
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public int getLegajo() {
        return this.legajo;
    }

    @Override
    public Excusa crearExcusa(MotivoExcusa motivo, String descripcion) {
        return new Excusa(this, motivo, descripcion);
    }
}
