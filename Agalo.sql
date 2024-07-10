//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento


CREATE TABLE EMPLEADOR (
    IdEmpleador varchar2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50) UNIQUE,
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    Direccion VARCHAR2(100) NOT NULL,
    SitioWeb VARCHAR2(500),
    Departamento VARCHAR2(50) NOT NULL,
    Contrasena VARCHAR2(250) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Pendiente')),
    Foto varchar2(300));


Create table AreaDeTrabajo(
IdAreaDeTrabajo int PRIMARY KEY,
NombreAreaDetrabajo varchar2(100));


select * from empleador 

CREATE TABLE TRABAJO (
    IdTrabajo NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdEstadoTrabajo INT NOT NULL,
    IdAreaDeTrabajo INT NOT NULL,
    Descripcion VARCHAR2(150),
    Ubicacion VARCHAR2(100),
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    Salario NUMBER(3,3),
    Beneficios VARCHAR2(100),
    FechaDePublicacion DATE,
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador),
    CONSTRAINT FkAreadeTrabajotrabajo FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo)
);




CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion VARCHAR2(100) NOT NULL,
    Departamento VARCHAR2(50) NOT NULL,
    FechaDeNacimiento DATE,
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo INT,
    Habilidades varchar2(250),
    Curriculum BLOB,
    Foto varchar2(300),
    Contrasena VARCHAR2(250) NOT NULL,
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo)

);

CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo NUMBER NOT NULL,
    FechaSolicitud DATE NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente')),

    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante),
    CONSTRAINT FKEmpleadorSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo));


//Secuencias y triggers para auto incremento 
CREATE SEQUENCE Trabajo
START WITH 1
INCREMENT BY 1;

CREATE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
SELECT Trabajo.NEXTVAL INTO:NEW.IdTrabajo
FROM DUAL;
END;


--insert para area de trabajo
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (1,'Trabajo doméstico');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (2,'Freelancers');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (3,'Trabajos remotos');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (4,'Servicios de entrega');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (5,'Sector de la construcción');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (6,'�?rea de la salud');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (7,'Sector de la hostelería');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (8,'Servicios profesionales');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (9,'�?rea de ventas y atención al cliente');
insert into AreaDeTrabajo (IdAreaDeTrabajo, NombreAreaDetrabajo) values (10,'Educación y enseñanza');


select * from empleador;
SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseña1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseña1';
SELECT * FROM ESTADOSOLICITANTE ;

-- Eliminar secuencias
DROP SEQUENCE EstadoTrabajoSequence;
DROP SEQUENCE EstadoSolicitudSequence;
DROP SEQUENCE EstadoSolicitanteSequence;
DROP SEQUENCE TrabajoSequence;
DROP SEQUENCE SolicitudSequence;

-- Eliminar triggers
DROP TRIGGER TrigEstadoTrabajo;
DROP TRIGGER TrigEstadoSolicitud;
DROP TRIGGER TrigEstadoSolicitante;
DROP TRIGGER TrigTrabajo;
DROP TRIGGER TrigSolicitud;

-- Eliminar tablas
DROP TABLE SOLICITUD;
DROP TABLE SOLICITANTE;
DROP TABLE TRABAJO;
DROP TABLE ESTADOSOLICITANTE;
DROP TABLE ESTADOSOLICITUD;
DROP TABLE ESTADOTRABAJO;
DROP TABLE EMPLEADOR;
