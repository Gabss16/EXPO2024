//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento


CREATE TABLE DEPARTAMENTO(
IdDepartamento INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Nombre varchar2(50)
);

Create table AreaDeTrabajo(
IdAreaDeTrabajo INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
NombreAreaDetrabajo varchar2(100));
  
CREATE TABLE EMPLEADOR (
    IdEmpleador VARCHAR2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50),
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    Altitud VARCHAR2(200),
    Latitud VARCHAR2 (200),
    Direccion varchar2(250),
    IdDepartamento INT,
    SitioWeb VARCHAR2(500),
    Estado VARCHAR(20) CHECK (Estado IN ('Activo', 'Pendiente', 'Restringido')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkDepartamentoEmpleador FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

-- Limitar a 25,000 máximo 
CREATE TABLE TRABAJO (
    IdTrabajo INT PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdAreaDeTrabajo INT,
    Descripcion VARCHAR2(150),  
    Altitud varchar2(250),
    Latitud VARCHAR2 (200),
    Direccion varchar2(250),
    IdDepartamento INT,
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    SalarioMinimo NUMBER,
    SalarioMaximo NUMBER,
    Beneficios VARCHAR2(100),
    FechaDePublicacion  VARCHAR2(20),
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador) ON DELETE CASCADE,
    CONSTRAINT FkAreaDeTrabajoTrabajo FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDepartamentoTrabajo FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion varchar2(250),
    Altitud VARCHAR2(200),
    Latitud VARCHAR2 (200),
    IdDepartamento INT,  
    FechaDeNacimiento VARCHAR2(20),
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    EstadoCuenta VARCHAR(20) CHECK (EstadoCuenta IN ('Activo', 'Restringido')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo INT,
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDepartamentoSolicitante FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);
    
CREATE TABLE SOLICITUD (
    IdSolicitud INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo INT NOT NULL,
    FechaSolicitud VARCHAR2(20) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Aprobada', 'Rechazada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo) ON DELETE CASCADE);

CREATE TABLE ROLESCRITORIO(
IdRol INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Rol Varchar2(50));

CREATE TABLE UsuarioEscritorio(
IdAdmin INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Nombre Varchar2(50) NOT NULL,
Usuario Varchar2(50) NOT NULL,
Contrasena Varchar2(250) NOT NULL,
Foto VARCHAR2(300),
CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
IdRol INT,
CONSTRAINT FKRol FOREIGN KEY (IdRol) REFERENCES ROLESCRITORIO(IdRol) ON DELETE CASCADE);

CREATE TABLE AUDITORIA (
    IdAuditoria NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TablaAfectada VARCHAR2(50),
    Operacion VARCHAR2(10),
    Usuario VARCHAR2(50) not null,
    FechaAccion varchar2(50) not null,
    Detalles VARCHAR2(500) not null,
    IdTrabajo Number
);

CREATE OR REPLACE TRIGGER TrigAuditoriaInsertTrabajo
AFTER INSERT ON TRABAJO
FOR EACH ROW
BEGIN
    INSERT INTO AUDITORIA (TablaAfectada, Operacion, Usuario, FechaAccion, Detalles, IdTrabajo)
    VALUES (
        'Trabajo', 
        'INSERT', 
        :NEW.IdEmpleador, 
        TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'),
        'Se creó un trabajo con descripción: ' || :NEW.Descripcion,
        :NEW.IdTrabajo
    );
END;


CREATE OR REPLACE TRIGGER trg_audit_update_Borrar_trabajo
AFTER UPDATE ON TRABAJO
FOR EACH ROW
WHEN (OLD.Estado = 'Activo' AND NEW.Estado = 'Inactivo')
BEGIN
    INSERT INTO AUDITORIA (TablaAfectada, Operacion, Usuario, FechaAccion, Detalles, IdTrabajo)
    VALUES (
    'Trabajo',
    'UPDATE',                           -- Tipo de operación (actualización)
    :OLD.IdEmpleador,                   -- ID del empleador que realizó la operación
    SYSDATE,                            -- Fecha actual de la operación
   'Trabajo con Descripción ' || :OLD.Descripcion || ' fue marcado como inactivo.',-- Detalle
    :OLD.IdTrabajo
    );
END;

CREATE OR REPLACE TRIGGER trg_audit_update_Reactivar_trabajo
AFTER UPDATE ON TRABAJO
FOR EACH ROW
WHEN (OLD.Estado = 'Inactivo' AND NEW.Estado = 'Activo')
BEGIN
    INSERT INTO AUDITORIA (TablaAfectada, Operacion, Usuario, FechaAccion, Detalles, IdTrabajo)
    VALUES (
    'Trabajo',
    'UPDATE',                           -- Tipo de operación (actualización)
    :OLD.IdEmpleador,                   -- ID del empleador que realizó la operación
    SYSDATE,                            -- Fecha actual de la operación
   'Trabajo con Descripción ' || :OLD.Descripcion || ' fue reactivado.',-- Detalle
    :OLD.IdTrabajo
    );
END;

//Secuencias y triggers para auto incremento en trabajo
CREATE SEQUENCE Trabajoseq
START WITH 1
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
SELECT Trabajoseq.NEXTVAL INTO:NEW.IdTrabajo
FROM DUAL;
END;

//Procedimiento almacenado para verificar correos electrónicos
CREATE OR REPLACE PROCEDURE VerificarCorreoElectronico(
    p_Nombre IN VARCHAR2,
    p_Usuario IN VARCHAR2,
    p_Contrasena IN VARCHAR2,
    p_Foto IN VARCHAR2,
    p_CorreoElectronico IN VARCHAR2,
    p_IdRol IN INT
) AS
    v_patronRegex VARCHAR2(100) := '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';
BEGIN
    -- Verificar si el correo electrónico cumple con el patrón
    IF NOT REGEXP_LIKE(p_CorreoElectronico, v_patronRegex) THEN
        RAISE_APPLICATION_ERROR(-20001, 'Correo electrónico no válido.');
    END IF;

    -- Insertar el usuario en la tabla UsuarioEscritorio
    INSERT INTO UsuarioEscritorio (Nombre, Usuario, Contrasena, Foto, CorreoElectronico, IdRol)
    VALUES (p_Nombre, p_Usuario, p_Contrasena, p_Foto, p_CorreoElectronico, p_IdRol);
    
END VerificarCorreoElectronico;

// INSERTS a tablas normalizadas por datos repetidos
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Trabajo doméstico');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Freelancers');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Trabajos remotos');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Servicios de entrega');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Sector de la construcción');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Área de la salud');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Sector de la hostelería');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Servicios profesionales');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Área de ventas y atención al cliente');
INSERT INTO AreaDeTrabajo (nombreareadetrabajo) VALUES ('Educación y enseñanza');  



Insert into DEPARTAMENTO(Nombre) values ('Ahuachapán');
Insert into DEPARTAMENTO(Nombre) values ('Cabañas');
Insert into DEPARTAMENTO(Nombre) values ('Chalatenango');
Insert into DEPARTAMENTO(Nombre) values ('Cuscatlán');
Insert into DEPARTAMENTO(Nombre) values ('La Libertad');
Insert into DEPARTAMENTO(Nombre) values ('Morazán');
Insert into DEPARTAMENTO(Nombre) values ('La Paz');
Insert into DEPARTAMENTO(Nombre) values ('Santa Ana');
Insert into DEPARTAMENTO(Nombre) values ('San Miguel');
Insert into DEPARTAMENTO(Nombre) values ('San Vicente');
Insert into DEPARTAMENTO(Nombre) values ('San Salvador');
Insert into DEPARTAMENTO(Nombre) values ('Sonsonate');
Insert into DEPARTAMENTO(Nombre) values ('La Unión');
Insert into DEPARTAMENTO(Nombre) values ('Usulután');

//Insert roles de escritorio
INSERT INTO ROLESCRITORIO(Rol) Values('Admin');
INSERT INTO ROLESCRITORIO(Rol) Values('Super admin');


//Inner join para ver Trabajo
SELECT 
    T.IdTrabajo AS "Número de trabajo",
    T.Titulo AS "Título",
    A.NombreAreaDetrabajo AS "Área de trabajo",
    T.Descripcion AS "Descripción",
    T.IdEmpleador AS "Código de empleador",
    E.NombreRepresentante AS "Nombre del empleador",
    E.CorreoElectronico AS "Correo Electrónico de contacto",
    E.NumeroTelefono AS "Número de contacto",
    T.Direccion AS "Dirección del trabajo",
    T.Experiencia AS "Experiencia requerida",
    T.Requerimientos,
    T.Salario, 
    T.Beneficios
FROM 
    TRABAJO T
INNER JOIN 
    EMPLEADOR E ON T.IdEmpleador = E.IdEmpleador
INNER JOIN
AreaDeTrabajo A ON T.IdAreaDeTrabajo = A.IdAreaDeTrabajo;

//INNER JOIN para ver solicitudes
SELECT 
    S.IdSolicitante AS "Código del solicitante",
    S.Nombre AS "Nombre del solicitante",
    S.CorreoElectronico AS "Correo Electrónico del solicitante",
    S.Telefono AS "Teléfono del solicitante",
    T.Titulo AS "Título del trabajo",
    A.NombreAreaDetrabajo AS "Área de trabajo",
    T.Descripcion AS "Descripción del trabajo",
    T.Direccion AS "Dirección del trabajo",
    T.Salario AS "Salario",
    T.Beneficios AS "Beneficios",
    Sol.FechaSolicitud AS "Fecha de solicitud",
    Sol.Estado AS "Estado de solicitud"
FROM 
    SOLICITUD Sol
INNER JOIN 
    SOLICITANTE S ON Sol.IdSolicitante = S.IdSolicitante
INNER JOIN 
    TRABAJO T ON Sol.IdTrabajo = T.IdTrabajo
INNER JOIN
AreaDeTrabajo A ON T.IdAreaDeTrabajo = A.IdAreaDeTrabajo;

//INNER JOIN PARA VER ROL EN TABLA USUARIOS ESCRITORIO
SELECT u.IdADmin as "Id", u.Nombre, u.Usuario, u.Contrasena, u.Foto, u.CorreoElectronico, R.Rol, u.IdRol
FROM UsuarioEscritorio u
INNER JOIN ROLESCRITORIO R ON u.IdRol = R.IdRol;

//INNER JOIN PARA PERFIL SOLICITANTE
SELECT 
    s.Nombre, 
    s.CorreoElectronico, 
    s.Telefono, 
    s.Direccion, 
    d.Nombre, 
    s.FechaDeNacimiento, 
    s.Genero, 
    a.NombreAreaDeTrabajo, 
    s.Habilidades, 
    s.Foto 
FROM 
    SOLICITANTE s 
INNER JOIN 
    DEPARTAMENTO d ON s.IdDepartamento = d.IdDepartamento 
INNER JOIN 
    AreaDeTrabajo a ON s.IdAreaDeTrabajo = a.IdAreaDeTrabajo 
WHERE 
    s.CorreoElectronico = 'prueba@gmail.com';
    
    
commit;

//Pruebas 
begin 
VerificarCorreoElectronico('Ricardo de paz', 'RicAdmin3', 'ContrasenaEncriptada', 'Foto1', 'prueba3@gmail.com', 1);
end;
SELECT 
    T.IdTrabajo, 
    T.Titulo, 
    T.IdEmpleador, 
    A.NombreAreaDetrabajo AS NombreAreaDeTrabajo, 
    T.Descripcion,   
    T.Direccion, 
    T.IdDepartamento, 
    T.Experiencia, 
    T.Requerimientos, 
    T.Estado, 
    T.Salario, 
    T.Beneficios, 
    T.FechaDePublicacion
FROM 
    TRABAJO T
INNER JOIN 
    AreaDeTrabajo A
ON 
    T.IdAreaDeTrabajo = A.IdAreaDeTrabajo
 WHERE IdEmpleador = 'b3e80aa6-a7be-4a18-b074-aa5cf01bc2b9'  AND Estado = 'Inactivo'

select * from empleador;
select * from solicitante;
select * from solicitud;
select * from trabajo;
            
select * from AreadeTrabajo
delete from Empleador where idEmpleador = 'fdc019cf-6449-4655-8913-685ffbb9bf1b';

SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM ESTADOSOLICITANTE ;


select * from auditoria;
select * from trabajo;

delete from auditoria; 

//Para eliminar
-- Eliminar secuencias
DROP SEQUENCE SolicitudSeq;
DROP SEQUENCE Trabajoseq;


-- Eliminar triggers
DROP TRIGGER TrigSolicitud;
DROP TRIGGER TrigTrabajo;

select * from solicitante


UPDATE solicitante SET EstadoCuenta = 'Restringido' WHERE IdSolicitante = '74a58d12-c9be-463a-806b-4c7ee1d6cce1'

select * from solicitante
commit;

SELECT
            s.IdSolicitud,
            s.IdSolicitante,
            ss.Nombre as NombreSolicitante,
            s.IdTrabajo,
            s.FechaSolicitud,
            s.Estado,
            t.Titulo AS TituloTrabajo,
            t.IdAreaDeTrabajo AS CategoriaTrabajo
        FROM SOLICITUD s
        INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
        INNER JOIN SOLICITANTE ss ON s.IdSolicitante = ss.IdSolicitante
        WHERE s.Estado = 'Pendiente' AND s.IdTrabajo = ?

SELECT
                s.IdSolicitud,
                s.IdSolicitante,
                ss.Nombre as NombreSolicitante,
                s.IdTrabajo,
                s.FechaSolicitud,
                s.Estado,
                t.Titulo AS TituloTrabajo,
                t.IdAreaDeTrabajo,
                A.NombreAreaDetrabajo AS CategoriaTrabajo

            FROM SOLICITUD s
            INNER JOIN TRABAJO t ON s.IdTrabajo = t.IdTrabajo
                INNER JOIN SOLICITANTE ss ON s.IdSolicitante = ss.IdSolicitante
                INNER JOIN AreaDeTrabajo A ON t.IdAreaDeTrabajo = A.IdAreaDeTrabajo

            WHERE s.Estado = 'Pendiente' AND s.idTrabajo = 4
            
            select * from solicitante

-- Eliminar tablas
DROP TABLE SOLICITUD;
DROP TABLE SOLICITANTE;
DROP TABLE TRABAJO;
DROP TABLE EMPLEADOR;
DROP TABLE AREADETRABAJO;
Drop table UsuarioEscritorio;
Drop table ROLESCRITORIO;
Drop table DEPARTAMENTO;
drop table AUDITORIA;

INSERT INTO SOLICITANTE (IdSolicitante, Nombre, CorreoElectronico, Telefono, Direccion,IdDepartamento, FechaDeNacimiento, Estado, Genero ,IdAreaDeTrabajo, Habilidades,Curriculum,Foto, Contrasena, EstadoCuenta) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)

