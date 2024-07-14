//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento

CREATE TABLE EMPLEADOR (
    IdEmpleador VARCHAR2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50),
    NombreRepresentante VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    Direccion VARCHAR2(100) NOT NULL,
    SitioWeb VARCHAR2(500),
    Departamento VARCHAR2(50) NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Pendiente')),
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL
);

CREATE TABLE TRABAJO (
    IdTrabajo NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    AreaDeTrabajo VARCHAR2(100) NOT NULL CHECK (AreaDeTrabajo IN (
        'Trabajo doméstico', 
        'Freelancers', 
        'Trabajos remotos', 
        'Servicios de entrega', 
        'Sector de la construcción', 
        'Área de la salud', 
        'Sector de la hostelería', 
        'Servicios profesionales', 
        'Área de ventas y atención al cliente', 
        'Educación y enseñanza'
    )),
    Descripcion VARCHAR2(150),
    Ubicacion VARCHAR2(100),
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo')),
    Salario NUMBER(3,3),
    Beneficios VARCHAR2(100),
    FechaDePublicacion DATE,
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador)
);

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion VARCHAR2(100) NOT NULL,
    Departamento VARCHAR2(50) NOT NULL,
    FechaDeNacimiento VARCHAR2(20),
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado')),
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    AreaDeTrabajo VARCHAR2(100) NOT NULL CHECK (AreaDeTrabajo IN (
        'Trabajo doméstico', 
        'Freelancers', 
        'Trabajos remotos', 
        'Servicios de entrega', 
        'Sector de la construcción', 
        'Área de la salud', 
        'Sector de la hostelería', 
        'Servicios profesionales', 
        'Área de ventas y atención al cliente', 
        'Educación y enseñanza'
    )),
    Habilidades VARCHAR2(250),
    Curriculum BLOB,
    Foto VARCHAR2(300),
    Contrasena VARCHAR2(250) NOT NULL
);

CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdTrabajo NUMBER NOT NULL,
    FechaSolicitud DATE NOT NULL,
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente')),
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante),
    CONSTRAINT FKTrabajoSolicitud FOREIGN KEY (IdTrabajo) REFERENCES TRABAJO(IdTrabajo)
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


select * from empleador;
SELECT * FROM EMPLEADOR WHERE CorreoElectronico = 'contacto@innovaciones.com.sv' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM SOLICITANTE WHERE CorreoElectronico =  'ana.martinez@example.com' AND Contrasena = 'contraseÃ±a1';
SELECT * FROM ESTADOSOLICITANTE ;

-- Eliminar secuencias
DROP SEQUENCE EstadoTrabajoSequence;
DROP SEQUENCE EstadoSolicitudSequence;
DROP SEQUENCE EstadoSolicitanteSequence;
DROP SEQUENCE TrabajoSeq;
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
DROP TABLE EMPLEADOR;
DROP TABLE AREADETRABAJO;

DROP INDEX UX_NombreEmpresa_Unique;

CREATE UNIQUE INDEX UX_NombreEmpresa_Unique ON EMPLEADOR (NombreEmpresa);

