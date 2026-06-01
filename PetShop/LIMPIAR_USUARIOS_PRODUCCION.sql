USE Petshop;

UPDATE usuario
SET rol = 'GENERAL'
WHERE LOWER(email) <> LOWER('forrajelahuella@hotmail.com');

UPDATE usuario
SET rol = 'ADMIN', activo = 1, cuenta_verificada = 1, token_verificacion = NULL
WHERE LOWER(email) = LOWER('forrajelahuella@hotmail.com');

-- Ejecutar esta línea SOLO si querés borrar todos los usuarios de prueba y dejar únicamente el admin:
-- DELETE FROM usuario WHERE LOWER(email) <> LOWER('forrajelahuella@hotmail.com');
