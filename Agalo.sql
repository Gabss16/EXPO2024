//Tablas 

//Varchar2(50) para poder usar el UUID
//Number para auto incremento
CREATE TABLE EMPRESA (
    IdEmpresa varchar2(50) PRIMARY KEY, 
    NombreEmpresa VARCHAR2(50) NOT NULL UNIQUE,
    CorreoElectronico VARCHAR2(50) NOT NULL UNIQUE,
    NumeroTelefono VARCHAR2(15) NOT NULL,
    DireccionEmpresa VARCHAR2(100) NOT NULL,
    SitioWebEmpresa VARCHAR2(500) NOT NULL,
    NombreRepresentante VARCHAR2(50) NOT NULL,
    Ciudad VARCHAR2(50) NOT NULL,
    Contrasena VARCHAR2(250) NOT NULL 
);

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
    Estado VARCHAR(10) CHECK (Estado IN ('Empleado', 'Desempleado'))
);

CREATE TABLE TRABAJO (
    IdTrabajoEmpleador NUMBER PRIMARY KEY, 
    Titulo VARCHAR2(50) NOT NULL,
    IdEmpresa VARCHAR2(50) NOT NULL,
    IdEstadoTrabajo INT NOT NULL,
    Descripcion VARCHAR2(150),
    Ubicacion VARCHAR2(100),
    Experiencia VARCHAR2(50),
    Requerimientos VARCHAR2(150),
    Salario NUMBER(3,3),
    Beneficios VARCHAR2(100),
    FechaDePublicacion DATE,
    CONSTRAINT FK_Empresa_Trabajo FOREIGN KEY (IdEmpresa) REFERENCES EMPRESA(IdEmpresa),
    CONSTRAINT FK_EstadoTrabajo_Trabajo FOREIGN KEY (IdEstadoTrabajo) REFERENCES ESTADOTRABAJO(IdEstadoTrabajo)
);

CREATE TABLE SOLICITANTE (
    IdSolicitante VARCHAR2(50) PRIMARY KEY, 
    Nombre VARCHAR2(50) NOT NULL,
    Telefono VARCHAR2(15) NOT NULL UNIQUE,
    Direccion VARCHAR2(100) NOT NULL,
    Ciudad VARCHAR2(50) NOT NULL,
    Pais VARCHAR2(50),
    FechaDeNacimiento DATE,
    Genero VARCHAR2(15),
    InteresEmpleo INT,
    Habilidades INT,
    Curriculum BLOB,
    Foto BLOB,
    Contrasena VARCHAR2(250) NOT NULL,
    IdEstadoSolicitante NUMBER NOT NULL,
    CONSTRAINT FK_EstadoSolicitante_Solicitante FOREIGN KEY (IdEstadoSolicitante) REFERENCES ESTADOSOLICITANTE(IdEstadoSolicitante)
);

CREATE TABLE SOLICITUD (
    IdSolicitud NUMBER PRIMARY KEY , 
    IdSolicitante VARCHAR2(50) NOT NULL,
    IdEmpresa VARCHAR2(50) NOT NULL,
    IdEstadoSolicitud NUMBER NOT NULL,
    FechaSolicitud DATE NOT NULL,
    CONSTRAINT FK_Solicitante_Solicitud FOREIGN KEY (IdSolicitante) REFERENCES SOLICITANTE(IdSolicitante),
    CONSTRAINT FK_Empleador_Solicitud FOREIGN KEY (IdEmpresa) REFERENCES EMPRESA(IdEmpresa),
    CONSTRAINT FK_EstadoSolicitud_Solicitud FOREIGN KEY (IdEstadoSolicitud) REFERENCES ESTADOSOLICITUD(IdEstadoSolicitud)
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



