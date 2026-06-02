package com.PetShop.Servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class SchemaFixServicio {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${app.admin.email:forrajelahuella@hotmail.com}")
    private String adminEmail;

    @Value("${app.admin.limpiar-usuarios-al-iniciar:false}")
    private Boolean limpiarUsuariosAlIniciar;

    @PostConstruct
    public void corregirTablaUsuario() {
        ejecutar("ALTER TABLE usuario MODIFY celular BIGINT");
        ejecutar("ALTER TABLE usuario ADD COLUMN apellido VARCHAR(255)");
        ejecutar("ALTER TABLE usuario ADD COLUMN fecha_nacimiento DATE");
        ejecutar("ALTER TABLE usuario ADD COLUMN domicilio VARCHAR(255)");
        ejecutar("ALTER TABLE usuario ADD COLUMN localidad VARCHAR(255)");
        ejecutar("ALTER TABLE usuario ADD COLUMN provincia VARCHAR(255)");
        ejecutar("ALTER TABLE usuario ADD COLUMN codigo_postal VARCHAR(20)");
        ejecutar("ALTER TABLE usuario ADD COLUMN activo BIT");
        ejecutar("ALTER TABLE usuario ADD COLUMN cuenta_verificada BIT");
        ejecutar("ALTER TABLE usuario ADD COLUMN token_verificacion VARCHAR(255)");
        ejecutar("ALTER TABLE usuario ADD COLUMN token_recuperacion VARCHAR(255)");

        corregirTablaCompra();

        repararRoles();
        limpiarUsuariosSiFuePedido();
    }

    private void corregirTablaCompra() {
        ejecutar("ALTER TABLE compra ADD COLUMN estado VARCHAR(40)");
        ejecutar("ALTER TABLE compra ADD COLUMN metodo_pago VARCHAR(80)");
        ejecutar("ALTER TABLE compra ADD COLUMN direccion_envio VARCHAR(255)");
        ejecutar("ALTER TABLE compra ADD COLUMN localidad_envio VARCHAR(255)");
        ejecutar("ALTER TABLE compra ADD COLUMN provincia_envio VARCHAR(255)");
        ejecutar("ALTER TABLE compra ADD COLUMN codigo_postal_envio VARCHAR(40)");
        ejecutar("ALTER TABLE compra ADD COLUMN telefono_contacto VARCHAR(80)");
        ejecutar("ALTER TABLE compra ADD COLUMN observaciones VARCHAR(255)");
        ejecutar("ALTER TABLE compra ADD COLUMN nota_admin VARCHAR(255)");
        ejecutar("UPDATE compra SET estado = 'PENDIENTE' WHERE estado IS NULL");
        ejecutar("UPDATE compra SET metodo_pago = 'PAGO_EN_DOMICILIO' WHERE metodo_pago IS NULL");
    }

    private void repararRoles() {
        try {
            // Todos los clientes quedan habilitados para comprar sin validación por correo.
            jdbcTemplate.update("UPDATE usuario SET rol = 'GENERAL', activo = 1, cuenta_verificada = 1, token_verificacion = NULL WHERE LOWER(email) <> LOWER(?)", adminEmail);
        } catch (Exception e) {
            // No detenemos el arranque si la tabla todavía no existe.
        }

        try {
            jdbcTemplate.update("UPDATE usuario SET rol = 'ADMIN', activo = 1, cuenta_verificada = 1, token_verificacion = NULL WHERE LOWER(email) = LOWER(?)", adminEmail);
        } catch (Exception e) {
            // No detenemos el arranque si la tabla todavía no existe.
        }
    }

    private void limpiarUsuariosSiFuePedido() {
        if (limpiarUsuariosAlIniciar == null || !limpiarUsuariosAlIniciar) {
            return;
        }
        try {
            jdbcTemplate.update("DELETE FROM usuario WHERE LOWER(email) <> LOWER(?)", adminEmail);
            System.out.println("Animaria: usuarios no admin eliminados por configuración app.admin.limpiar-usuarios-al-iniciar=true");
        } catch (Exception e) {
            System.out.println("Animaria: no se pudieron eliminar usuarios no admin: " + e.getMessage());
        }
    }

    private void ejecutar(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            // Si la columna ya existe o la tabla todavía no existe, no detenemos el arranque.
        }
    }
}
