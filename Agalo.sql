//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento


CREATE TABLE DEPARTAMENTO(
IdDepartamento number GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Nombre varchar2(50)
);

CREATE TABLE DIRECCIONES(
IdDirecciones number GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Direccion varchar2(50),
IdDepartamento number,
CONSTRAINT FkDireccionDepartamento FOREIGN KEY (IdDepartamento) REFERENCES DEPARTAMENTO(IdDepartamento) ON DELETE CASCADE);

Create table AreaDeTrabajo(
IdAreaDeTrabajo number GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
NombreAreaDetrabajo varchar2(100));
  

CREATE TABLE EMPLEADOR (
    IdEmpleador VARCHAR2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50),
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    IdDirecciones number,
    SitioWeb VARCHAR2(500),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Pendiente')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkDireccionEmpleador FOREIGN KEY (IdDirecciones) REFERENCES DIRECCIONES(IdDirecciones) ON DELETE CASCADE);

CREATE TABLE TRABAJO (
    IdTrabajo NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdAreaDeTrabajo number,
    Descripcion VARCHAR2(150),  
    IdDirecciones number,
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    Salario NUMBER,
    Beneficios VARCHAR2(100),
    FechaDePublicacion  VARCHAR2(20),
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador) ON DELETE CASCADE,
    CONSTRAINT FkAreaDeTrabajoTrabajo FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDireccionTrabajo FOREIGN KEY (IdDirecciones) REFERENCES DIRECCIONES(IdDirecciones) ON DELETE CASCADE);


CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    IdDirecciones number,
    FechaDeNacimiento VARCHAR2(20),
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo number,
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo) ON DELETE CASCADE,
    CONSTRAINT FkDIreccionSolicitante FOREIGN KEY (IdDirecciones) REFERENCES DIRECCIONES(IdDirecciones) ON DELETE CASCADE);


CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo NUMBER NOT NULL,
    FechaSolicitud VARCHAR2(20) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo) ON DELETE CASCADE
);
CREATE TABLE ROLESCRITORIO(
IdRol INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
Rol Varchar2(50))

CREATE TABLE UsuarioEscritorio(
IdAdmin VARCHAR PRIMARY KEY,
Nombre Varchar2(50) NOT NULL,
Usuario Varchar2(50) NOT NULL,
Contrasena Varchar2(250) NOT NULL,
Foto VARCHAR2(300),
CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
IdRol INT,
CONSTRAINT FKRol FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante) ON DELETE CASCADE,

);

//Secuencias y triggers para auto incremento 
CREATE SEQUENCE Trabajoseq
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
SELECT Trabajoseq.NEXTVAL INTO:NEW.IdTrabajo
FROM DUAL;
END;


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

//Secuencia y trigger para las solicitudes

CREATE SEQUENCE SolicitudSeq 
START WITH 1 
INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TrigSolicitud
BEFORE INSERT ON SOLICITUD
FOR EACH ROW 
BEGIN 
    SELECT SolicitudSeq.NEXTVAL
    INTO :NEW.IdSolicitud
    FROM DUAL;
END;

select * from empleador;
select * from solicitante;
select * from solicitud;
select * from trabajo;

delete from Empleador where idEmpleador = 'fdc019cf-6449-4655-8913-685ffbb9bf1b';

SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM ESTADOSOLICITANTE ;

-- Eliminar secuencias
DROP SEQUENCE SolicitudSeq;


-- Eliminar triggers
DROP TRIGGER TrigSolicitud;

-- Eliminar tablas
DROP TABLE SOLICITUD;
DROP TABLE SOLICITANTE;
DROP TABLE TRABAJO;
DROP TABLE EMPLEADOR;
DROP TABLE AREADETRABAJO;