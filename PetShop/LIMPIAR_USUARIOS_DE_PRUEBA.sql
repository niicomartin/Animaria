USE Petshop;

-- Ejecutar solo si querés dejar únicamente el usuario administrador.
DELETE FROM usuario
WHERE LOWER(email) <> LOWER('forrajelahuella@hotmail.com');

UPDATE usuario
SET rol = 'ADMIN', activo = 1, cuenta_verificada = 1, token_verificacion = NULL
WHERE LOWER(email) = LOWER('forrajelahuella@hotmail.com');
