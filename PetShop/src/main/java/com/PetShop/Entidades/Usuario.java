package com.PetShop.Entidades;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String nombre;
    private String apellido;
    private Long celular;
    private String email;
    private String password;

    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    @Column(name = "domicilio")
    private String direccion;
    private String localidad;
    private String provincia;
    private String codigoPostal;

    // Se conserva para compatibilidad con pantallas viejas, pero el registro nuevo usa fechaNacimiento.
    private Integer edad;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private Boolean activo;
    private Boolean cuentaVerificada;
    private String tokenVerificacion;
    private String tokenRecuperacion;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Long getCelular() { return celular; }
    public void setCelular(Long celular) { this.celular = celular; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDomicilio() { return direccion; }
    public void setDomicilio(String domicilio) { this.direccion = domicilio; }

    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getCuentaVerificada() { return cuentaVerificada; }
    public void setCuentaVerificada(Boolean cuentaVerificada) { this.cuentaVerificada = cuentaVerificada; }

    public String getTokenVerificacion() { return tokenVerificacion; }
    public void setTokenVerificacion(String tokenVerificacion) { this.tokenVerificacion = tokenVerificacion; }

    public String getTokenRecuperacion() { return tokenRecuperacion; }
    public void setTokenRecuperacion(String tokenRecuperacion) { this.tokenRecuperacion = tokenRecuperacion; }

    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }

    public String getNombreCompleto() {
        String n = nombre == null ? "" : nombre.trim();
        String a = apellido == null ? "" : apellido.trim();
        return (n + " " + a).trim();
    }
}
