package com.PetShop.Servicios;

import com.PetShop.Entidades.Usuario;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServicio {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Value("${spring.mail.username:}")
    private String remitente;

    @Value("${app.mail.modo-desarrollo:true}")
    private Boolean modoDesarrollo;

    @Value("${app.mail.enviar-real:true}")
    private Boolean enviarReal;

    private String ultimoError;
    private String ultimoLinkGenerado;

    public void enviarValidacionCuenta(Usuario usuario) throws Exception {
        String enlace = generarLinkValidacion(usuario);
        String asunto = "Confirmá tu cuenta en Animaria";
        String texto = "Hola " + usuario.getNombreCompleto() + ",\n\n"
                + "Gracias por registrarte en Animaria.\n\n"
                + "Para activar tu cuenta y poder iniciar sesión, abrí este enlace:\n\n"
                + enlace + "\n\n"
                + "Si no creaste esta cuenta, ignorá este correo.\n\n"
                + "Equipo de Animaria";

        ultimoLinkGenerado = enlace;
        guardarCopiaDebug(usuario.getEmail(), asunto, texto, enlace);
        enviarCorreo(usuario.getEmail(), asunto, texto);

        System.out.println("Correo de validación enviado a: " + usuario.getEmail());
        System.out.println("Link de validación: " + enlace);
    }

    public void enviarRecuperacionPassword(Usuario usuario) throws Exception {
        String enlace = generarLinkRecuperacion(usuario);
        String asunto = "Restablecé tu contraseña en Animaria";
        String texto = "Hola " + usuario.getNombreCompleto() + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Para crear una nueva contraseña, abrí este enlace:\n\n"
                + enlace + "\n\n"
                + "Si no pediste este cambio, ignorá este correo.\n\n"
                + "Equipo de Animaria";

        ultimoLinkGenerado = enlace;
        guardarCopiaDebug(usuario.getEmail(), asunto, texto, enlace);
        enviarCorreo(usuario.getEmail(), asunto, texto);

        System.out.println("Correo de recuperación enviado a: " + usuario.getEmail());
        System.out.println("Link de recuperación: " + enlace);
    }

    public void enviarCorreoPrueba(String destino) throws Exception {
        String asunto = "Prueba de correo Animaria";
        String texto = "Si recibiste este mensaje, el correo de Animaria está funcionando correctamente.\n\n"
                + "Fecha: " + new Date() + "\n"
                + "URL app: " + appUrl;
        guardarCopiaDebug(destino, asunto, texto, appUrl);
        enviarCorreo(destino, asunto, texto);
    }

    private void enviarCorreo(String destino, String asunto, String texto) throws Exception {
        if (destino == null || destino.trim().isEmpty()) {
            ultimoError = "No hay correo de destino.";
            System.out.println(ultimoError);
            return;
        }

        if (remitente == null || remitente.trim().isEmpty()) {
            ultimoError = "Correo real sin configurar. No se envió SMTP, pero se generó link manual y copia debug.";
            System.out.println(ultimoError);
            return;
        }

        if (enviarReal == null || !enviarReal) {
            ultimoError = "Envío real desactivado. Revisá el link/copia debug generado.";
            System.out.println(ultimoError);
            return;
        }

        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setReplyTo(remitente);
            mensaje.setTo(destino);
            mensaje.setSubject(asunto);
            mensaje.setText(texto);

            mailSender.send(mensaje);
            ultimoError = null;
        } catch (Exception e) {
            ultimoError = e.getMessage();
            System.out.println("ERROR SMTP Animaria: " + e.getMessage());
            throw e;
        }
    }

    private void guardarCopiaDebug(String destino, String asunto, String texto, String enlace) {
        try {
            File carpeta = new File("target/email-debug");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }
            String nombreArchivo = "email-" + System.currentTimeMillis() + ".txt";
            File archivo = new File(carpeta, nombreArchivo);
            FileWriter writer = new FileWriter(archivo);
            writer.write("DESTINO: " + destino + "\n");
            writer.write("ASUNTO: " + asunto + "\n");
            writer.write("ENLACE: " + enlace + "\n");
            writer.write("----------------------------------------\n");
            writer.write(texto);
            writer.close();
            System.out.println("Copia de correo guardada en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("No se pudo guardar copia debug del correo: " + e.getMessage());
        }
    }

    public String generarLinkValidacion(Usuario usuario) {
        if (usuario == null || usuario.getTokenVerificacion() == null || usuario.getTokenVerificacion().trim().isEmpty()) {
            return "";
        }
        return appUrl + "/usuario/validar/" + usuario.getTokenVerificacion();
    }

    public String generarLinkRecuperacion(Usuario usuario) {
        if (usuario == null || usuario.getTokenRecuperacion() == null || usuario.getTokenRecuperacion().trim().isEmpty()) {
            return "";
        }
        return appUrl + "/login/restablecer/" + usuario.getTokenRecuperacion();
    }

    public Boolean getModoDesarrollo() {
        return modoDesarrollo != null && modoDesarrollo;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    public String getUltimoLinkGenerado() {
        return ultimoLinkGenerado;
    }
}
