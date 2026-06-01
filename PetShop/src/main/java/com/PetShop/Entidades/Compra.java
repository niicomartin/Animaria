package com.PetShop.Entidades;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Compra {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToMany
    private List<Alimento> alimentos;

    @ManyToMany
    private List<Accesorio> accesorios;

    private double valorCompra;
    private Boolean alta;
    private double total;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    @ManyToOne
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private EstadoCompra estado;

    private String metodoPago;
    private String direccionEnvio;
    private String localidadEnvio;
    private String provinciaEnvio;
    private String codigoPostalEnvio;
    private String telefonoContacto;
    private String observaciones;
    private String notaAdmin;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Alimento> getAlimentos() { return alimentos; }
    public void setAlimentos(List<Alimento> alimentos) { this.alimentos = alimentos; }

    public List<Accesorio> getAccesorios() { return accesorios; }
    public void setAccesorios(List<Accesorio> accesorios) { this.accesorios = accesorios; }

    public double getValorCompra() { return valorCompra; }
    public void setValorCompra(double valorCompra) { this.valorCompra = valorCompra; }

    public Boolean getAlta() { return alta; }
    public void setAlta(Boolean alta) { this.alta = alta; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Date getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(Date fechaAlta) { this.fechaAlta = fechaAlta; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public EstadoCompra getEstado() { return estado; }
    public void setEstado(EstadoCompra estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getDireccionEnvio() { return direccionEnvio; }
    public void setDireccionEnvio(String direccionEnvio) { this.direccionEnvio = direccionEnvio; }

    public String getLocalidadEnvio() { return localidadEnvio; }
    public void setLocalidadEnvio(String localidadEnvio) { this.localidadEnvio = localidadEnvio; }

    public String getProvinciaEnvio() { return provinciaEnvio; }
    public void setProvinciaEnvio(String provinciaEnvio) { this.provinciaEnvio = provinciaEnvio; }

    public String getCodigoPostalEnvio() { return codigoPostalEnvio; }
    public void setCodigoPostalEnvio(String codigoPostalEnvio) { this.codigoPostalEnvio = codigoPostalEnvio; }

    public String getTelefonoContacto() { return telefonoContacto; }
    public void setTelefonoContacto(String telefonoContacto) { this.telefonoContacto = telefonoContacto; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNotaAdmin() { return notaAdmin; }
    public void setNotaAdmin(String notaAdmin) { this.notaAdmin = notaAdmin; }
}
