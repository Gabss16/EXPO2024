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
    Foto BLOB);

CREATE TABLE ESTADOTRABAJO (
    IdEstadoTrabajo NUMBER PRIMARY KEY , 
    Estado VARCHAR(10) CHECK (Estado IN ('Activo', 'Inactivo', 'Pendiente'))
);

CREATE TABLE ESTADOSOLICITUD (
    IdEstadoSolicitud NUMBER PRIMARY KEY , 
    Estado VARCHAR(10) CHECK (Estado IN ('Activa', 'Finalizada', 'Pendiente'))
);

CREATE TABLE ESTADOSOLICITANTE (
    IdEstadoSolicitante NUMBER PRIMARY KEY , 
    Estado VARCHAR(11) CHECK (Estado IN ('Empleado', 'Desempleado'))
);

CREATE TABLE TRABAJO (
    IdTrabajoEmpleador NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdEstadoTrabajo INT NOT NULL,
    Descripcion VARCHAR2(150),
    Ubicacion VARCHAR2(100),
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Salario NUMBER(3,3),
    Beneficios VARCHAR2(100),
    FechaDePublicacion DATE,
    CONSTRAINT FKEmpleadorTrabajo FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador),
    CONSTRAINT FKEstadoTrabajoTrabajo FOREIGN KEY (IdEstadoTrabajo) REFERENCES ESTADOTRABAJO(IdEstadoTrabajo)
);

Create table AreaDeTrabajo(
IdAreaDeTrabajo int PRIMARY KEY,
NombreAreaDetrabajo varchar2(100));

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion VARCHAR2(100) NOT NULL,
    Departamento VARCHAR2(50) NOT NULL,
    FechaDeNacimiento DATE,
    Genero VARCHAR2(20) CHECK (Genero IN ('Masculino', 'Femenino', 'Prefiero no decirlo')),
    IdAreaDeTrabajo INT,
    Habilidades varchar2(250),
    Curriculum BLOB,
    Foto BLOB,
    Contrasena VARCHAR2(250) NOT NULL,
    IdEstadoSolicitante NUMBER NOT NULL,
    CONSTRAINT FKEstadoSolicitanteSolicitante FOREIGN KEY (IdEstadoSolicitante) REFERENCES ESTADOSOLICITANTE(IdEstadoSolicitante),
    CONSTRAINT FkAreaDeTrabajoSolicitante FOREIGN KEY (IdAreaDeTrabajo) REFERENCES AreaDeTrabajo(IdAreaDeTrabajo)

);


CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdEmpleador VARCHAR2(50) NOT NULL,
    IdEstadoSolicitud NUMBER NOT NULL,
    FechaSolicitud DATE NOT NULL,
    CONSTRAINT FKSolicitanteSolicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante),
    CONSTRAINT FKEmpleadorSolicitud FOREIGN KEY (IdEmpleador) REFERENCES EMPLEADOR(IdEmpleador),
    CONSTRAINT FKEstadoSolicitudSolicitud FOREIGN KEY (IdEstadoSolicitud) REFERENCES ESTADOSOLICITUD(IdEstadoSolicitud)
);


//Secuencias y triggers para auto incremento 
CREATE SEQUENCE EstadoTrabajoSequence
  START WITH 1
  INCREMENT BY 1;

CREATE TRIGGER TrigEstadoTrabajo
BEFORE INSERT ON ESTADOTRABAJO
FOR EACH ROW 
BEGIN 
  SELECT EstadoTrabajoSequence.NEXTVAL INTO:NEW.IdEstadoTrabajo
  FROM Dual;
END;

CREATE SEQUENCE EstadoSolicitudSequence
  START WITH 1
  INCREMENT BY 1;
  
  CREATE TRIGGER TrigEstadoSolicitud
BEFORE INSERT ON ESTADOSOLICITUD
FOR EACH ROW 
BEGIN 
  SELECT EstadoSolicitudSequence.NEXTVAL INTO:NEW.IdEstadoSolicitud
  FROM Dual;
END;

CREATE SEQUENCE EstadoSolicitanteSequence
  START WITH 1
  INCREMENT BY 1;

CREATE TRIGGER TrigEstadoSolicitante
BEFORE INSERT ON ESTADOSOLICITANTE
FOR EACH ROW 
BEGIN 
  SELECT EstadoSolicitanteSequence.NEXTVAL INTO:NEW.IdEstadoSolicitante
  FROM Dual;
END;

CREATE SEQUENCE TrabajoSequence
  START WITH 1
  INCREMENT BY 1;

CREATE TRIGGER TrigTrabajo
BEFORE INSERT ON TRABAJO
FOR EACH ROW 
BEGIN 
  SELECT TrabajoSequence.NEXTVAL INTO:NEW.IdTrabajoEmpleador
  FROM Dual;
END;


CREATE SEQUENCE SolicitudSequence
  START WITH 1
  INCREMENT BY 1;

CREATE TRIGGER TrigSolicitud
BEFORE INSERT ON SOLICITUD
FOR EACH ROW 
BEGIN 
  SELECT SolicitudSequence.NEXTVAL INTO:NEW.IdSolicitud
  FROM Dual;
END;

//Inserts de prueba Login
INSERT INTO EMPLEADOR (
    IdEmpleador, 
    NombreEmpresa, 
    CorreoElectronico, 
    NumeroTelefono, 
    Direccion, 
    SitioWeb, 
    NombreRepresentante, 
    Ciudad, 
    Contrasena
) VALUES (
    'EMP001', 
    'Innovaciones Tecnológicas SA de CV', 
    'contacto@innovaciones.com.sv', 
    '+503 9876-5432', 
    'Boulevard de Los Héroes, San Salvador', 
    'https://www.innovaciones.com.sv', 
    'Carlos Hernández', 
    'San Salvador', 
    'contraseña1'
);

insert into ESTADOSOLICITANTE (Estado) values ('Desempleado');


INSERT INTO SOLICITANTE (
    IdSolicitante, 
    Nombre, 
    Telefono, 
    Direccion, 
    Ciudad, 
    Pais, 
    FechaDeNacimiento, 
    Genero, 
    InteresEmpleo, 
    Habilidades, 
    Curriculum, 
    Foto, 
    Contrasena, 
    IdEstadoSolicitante, 
    CorreoElectronico
) VALUES (
    'SOL001', 
    'Ana Martinez', 
    '+503 1234-5678', 
    'Calle El Mirador #123, San Salvador', 
    'San Salvador', 
    'El Salvador', 
    TO_DATE('1990-05-14', 'YYYY-MM-DD'), 
    'Femenino', 
    1, 
    1, 
    EMPTY_BLOB(), 
    EMPTY_BLOB(), 
    'contraseña1', 
    1, 
    'ana.martinez@example.com'
);


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