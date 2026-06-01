package com.PetShop.Servicios;

import com.PetShop.Entidades.Rol;
import com.PetShop.Entidades.Usuario;
import com.PetShop.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailServicio emailServicio;

    @Value("${app.admin.email:forrajelahuella@hotmail.com}")
    private String adminEmail;

    @Transactional(rollbackFor = {Exception.class})
    public Usuario crear(String nombre, String apellido, Long celular, String email, Date fechaNacimiento,
            String direccion, String localidad, String provincia, String codigoPostal,
            String password, String password2) throws Exception {

        validarRegistro(nombre, apellido, celular, email, fechaNacimiento, direccion, localidad, provincia, password, password2);

        String emailNormalizado = normalizarEmail(email);

        Usuario existente = usuarioRepositorio.buscarPorEmail(emailNormalizado);
        if (existente != null) {
            throw new Exception("Correo ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCelular(celular);
        usuario.setEmail(emailNormalizado);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setEdad(calcularEdad(fechaNacimiento));
        usuario.setDireccion(direccion.trim());
        usuario.setLocalidad(localidad.trim());
        usuario.setProvincia(provincia.trim());
        usuario.setCodigoPostal(codigoPostal == null ? null : codigoPostal.trim());
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setActivo(true);
        usuario.setFechaAlta(new Date());

        if (esEmailAdministrador(emailNormalizado)) {
            usuario.setRol(Rol.ADMIN);
            usuario.setCuentaVerificada(true);
            usuario.setTokenVerificacion(null);
            return usuarioRepositorio.save(usuario);
        }

        usuario.setRol(Rol.GENERAL);
        usuario.setCuentaVerificada(false);
        usuario.setTokenVerificacion(UUID.randomUUID().toString());

        usuarioRepositorio.save(usuario);

        try {
            emailServicio.enviarValidacionCuenta(usuario);
        } catch (Exception e) {
            // No dejamos que un problema SMTP rompa el registro ni haga rollback.
            // La cuenta queda creada como GENERAL y pendiente de validación.
            System.out.println("No se pudo enviar el correo de validación: " + e.getMessage());
            System.out.println("Link manual de validación: " + emailServicio.generarLinkValidacion(usuario));
        }

        return usuario;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void validarCuenta(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token inválido");
        }

        Usuario usuario = usuarioRepositorio.buscarPorTokenVerificacion(token);
        if (usuario == null) {
            throw new Exception("El enlace de validación no existe o ya fue usado");
        }

        usuario.setCuentaVerificada(true);
        usuario.setTokenVerificacion(null);
        usuarioRepositorio.save(usuario);
    }



    @Transactional(rollbackFor = {Exception.class})
    public Usuario solicitarRecuperacionPassword(String email) throws Exception {
        Usuario usuario = buscarPorEmail(email);
        usuario.setTokenRecuperacion(UUID.randomUUID().toString());
        usuarioRepositorio.save(usuario);

        try {
            emailServicio.enviarRecuperacionPassword(usuario);
        } catch (Exception e) {
            System.out.println("No se pudo enviar el correo de recuperación: " + e.getMessage());
            System.out.println("Link manual de recuperación: " + emailServicio.generarLinkRecuperacion(usuario));
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorTokenRecuperacion(String token) throws Exception {
        if (token == null || token.trim().isEmpty()) {
            throw new Exception("Token inválido");
        }
        Usuario usuario = usuarioRepositorio.buscarPorTokenRecuperacion(token);
        if (usuario == null) {
            throw new Exception("El enlace de recuperación no existe o ya fue usado");
        }
        return usuario;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void cambiarPasswordConToken(String token, String password, String password2) throws Exception {
        Usuario usuario = buscarPorTokenRecuperacion(token);

        if (password == null || password.trim().length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }
        if (password2 == null || !password.equals(password2)) {
            throw new Exception("Las contraseñas no coinciden");
        }

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTokenRecuperacion(null);
        usuario.setActivo(true);

        usuarioRepositorio.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) throws Exception {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        }
        throw new Exception("Usuario inexistente o incorrecto");
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) throws Exception {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(normalizarEmail(email));
        if (usuario == null) {
            throw new Exception("No existe un usuario con ese correo");
        }
        return usuario;
    }

    @Transactional(rollbackFor = {Exception.class})
    public Usuario editar(String id, String nombre, String apellido, Long celular, Date fechaNacimiento,
            String direccion, String localidad, String provincia, String codigoPostal) throws Exception {

        Usuario usuario = buscarPorId(id);

        validarPerfil(nombre, apellido, celular, fechaNacimiento, direccion, localidad, provincia);

        usuario.setNombre(nombre.trim());
        usuario.setApellido(apellido.trim());
        usuario.setCelular(celular);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setEdad(calcularEdad(fechaNacimiento));
        usuario.setDireccion(direccion.trim());
        usuario.setLocalidad(localidad.trim());
        usuario.setProvincia(provincia.trim());
        usuario.setCodigoPostal(codigoPostal == null ? null : codigoPostal.trim());

        return usuarioRepositorio.save(usuario);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Usuario editar(String id, String nombre, String domicilio, Integer edad, Long celular) throws Exception {
        Usuario usuario = buscarPorId(id);
        if (nombre != null && !nombre.trim().isEmpty()) usuario.setNombre(nombre.trim());
        if (domicilio != null && !domicilio.trim().isEmpty()) usuario.setDireccion(domicilio.trim());
        if (edad != null && edad > 0) usuario.setEdad(edad);
        if (celular != null && celular > 0) usuario.setCelular(celular);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Usuario modificarPassword(String email, String password, String nombre, Long celular) throws Exception {
        Usuario usuario = buscarPorEmail(email);

        if (usuario.getCelular() == null || !usuario.getCelular().equals(celular)
                || nombre == null || !usuario.getNombre().equalsIgnoreCase(nombre.trim())) {
            throw new Exception("No coincide el nombre, celular y email");
        }
        if (password == null || password.trim().length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }

        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTokenRecuperacion(null);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getAll() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(normalizarEmail(email));

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        boolean activo = usuario.getActivo() != null && usuario.getActivo();
        boolean verificado = usuario.getCuentaVerificada() != null && usuario.getCuentaVerificada();

        List<GrantedAuthority> permisos = new ArrayList<>();
        permisos.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString()));

        if (activo && verificado) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
        }

        return new User(usuario.getEmail(), usuario.getPassword(), activo, true, true, verificado, permisos);
    }

    private boolean esEmailAdministrador(String email) {
        return normalizarEmail(adminEmail).equals(normalizarEmail(email));
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private void validarRegistro(String nombre, String apellido, Long celular, String email, Date fechaNacimiento,
            String direccion, String localidad, String provincia, String password, String password2) throws Exception {

        validarPerfil(nombre, apellido, celular, fechaNacimiento, direccion, localidad, provincia);

        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new Exception("El email es obligatorio y debe ser válido");
        }

        if (password == null || password.trim().length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }

        if (password2 == null || !password.equals(password2)) {
            throw new Exception("Las contraseñas no coinciden");
        }
    }

    private void validarPerfil(String nombre, String apellido, Long celular, Date fechaNacimiento,
            String direccion, String localidad, String provincia) throws Exception {

        if (nombre == null || nombre.trim().isEmpty()) throw new Exception("El nombre es obligatorio");
        if (apellido == null || apellido.trim().isEmpty()) throw new Exception("El apellido es obligatorio");
        if (celular == null || celular <= 0) throw new Exception("El celular es obligatorio");
        if (fechaNacimiento == null) throw new Exception("La fecha de nacimiento es obligatoria");
        if (direccion == null || direccion.trim().isEmpty()) throw new Exception("La dirección es obligatoria");
        if (localidad == null || localidad.trim().isEmpty()) throw new Exception("La localidad es obligatoria");
        if (provincia == null || provincia.trim().isEmpty()) throw new Exception("La provincia es obligatoria");
    }

    private Integer calcularEdad(Date fechaNacimiento) {
        if (fechaNacimiento == null) {
            return null;
        }

        Calendar nacimiento = Calendar.getInstance();
        nacimiento.setTime(fechaNacimiento);

        Calendar hoy = Calendar.getInstance();
        int edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR);

        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }

        return edad;
    }
}
