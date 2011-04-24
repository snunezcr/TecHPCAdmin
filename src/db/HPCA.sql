--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.3
-- Dumped by pg_dump version 9.0.3
-- Started on 2011-04-24 11:06:05 CST

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1897 (class 1262 OID 24686)
-- Name: HPCA; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "HPCA" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE "HPCA" OWNER TO postgres;

\connect "HPCA"

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 357 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 321 (class 1247 OID 24689)
-- Dependencies: 6 1549
-- Name: experimentparameter; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE experimentparameter AS (
	"ParameterName" text,
	"Type" text,
	"Value" text
);


ALTER TYPE public.experimentparameter OWNER TO postgres;

--
-- TOC entry 355 (class 1247 OID 24914)
-- Dependencies: 6 1567
-- Name: userdata; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE userdata AS (
	userid integer,
	username text,
	name text,
	lastname1 text,
	lastname2 text,
	role text,
	creationdate timestamp without time zone,
	enabled boolean
);


ALTER TYPE public.userdata OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 24849)
-- Dependencies: 357 6
-- Name: addexperimentparameter(integer, text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addexperimentparameter(experimentid integer, paramname text, paramtype text, paramvalue text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE 
	typeId integer;
BEGIN

	SELECT "TypeId" INTO typeId FROM "ParameterType" WHERE "Type" = paramType;
	INSERT INTO "InputParameters"("ExperimentId", "ParameterName", "ParameterTypeId", "Value")
	VALUES (experimentId, paramName, typeId, paramValue);

END;
$$;


ALTER FUNCTION public.addexperimentparameter(experimentid integer, paramname text, paramtype text, paramvalue text) OWNER TO postgres;

--
-- TOC entry 18 (class 1255 OID 24690)
-- Dependencies: 6 357
-- Name: addparallelconfiguration(integer, integer, boolean, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION addparallelconfiguration(experimentid integer, processors integer, saveeachnodelog boolean, sharedworkdir text, middleware text) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN
	INSERT INTO "ParallelConfiguration"("ExperimentId", "NumberOfProcessors",
	"SaveNodeLog", "SharedWorkingDirectory", "Middleware")
	VALUES (experimentId, processors, saveEachNodeLog, sharedWorkDir, middleware);
END;
$$;


ALTER FUNCTION public.addparallelconfiguration(experimentid integer, processors integer, saveeachnodelog boolean, sharedworkdir text, middleware text) OWNER TO postgres;

--
-- TOC entry 19 (class 1255 OID 24691)
-- Dependencies: 357 6
-- Name: createexperiment(text, text, text, boolean, text, text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION createexperiment(name text, description text, app text, parallel boolean, inputline text, inputpath text, owner integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	appId int;
	experimentId int;
BEGIN
	SELECT "ApplicationId" INTO appId FROM "Application" WHERE
	"OwnerId" = owner AND "RelativePath" = app;
	
	INSERT INTO "Experiment"("Name", "Description", "ExecutablePath", "ApplicationId",
	"ParallelExecution", "InputParametersLine", "InputFilePath", "OwnerId")
	VALUES(name, description, app, appId, parallel, inputline, inputpath, owner);

	SELECT MAX("ExperimentId") INTO experimentId FROM "Experiment";

	RETURN experimentId;
END;
$$;


ALTER FUNCTION public.createexperiment(name text, description text, app text, parallel boolean, inputline text, inputpath text, owner integer) OWNER TO postgres;

--
-- TOC entry 29 (class 1255 OID 24851)
-- Dependencies: 357 6
-- Name: createprogram(text, text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION createprogram(description text, relativepath text, owner integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	appId int;
BEGIN
	INSERT INTO "Application"("Description", "RelativePath", "OwnerId")
	VALUES(description, relativePath, owner);
	
	SELECT "ApplicationId" INTO appId FROM "Application" WHERE "OwnerId" = owner;

	RETURN appId;
END;
$$;


ALTER FUNCTION public.createprogram(description text, relativepath text, owner integer) OWNER TO postgres;

--
-- TOC entry 31 (class 1255 OID 24906)
-- Dependencies: 6 357
-- Name: createuser(text, text, text, text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION createuser(username text, password text, name text, lastname1 text, lastname2 text, role text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	roleId int;
	userCount int;
	userId int;
BEGIN

	SELECT COUNT("UserName") INTO userCount FROM "User" WHERE "UserName" = username;
	IF userCount = 0 THEN
		SELECT "RoleId" INTO roleId FROM "Role" WHERE
		"Role" = role;
		
		INSERT INTO "User"("UserName", "Password", "Name", "LastName1",
		"LastName2", "RoleId")
		VALUES(username, password, name, lastname1, lastname2, roleid);

		SELECT "UserId" INTO userId FROM "User" WHERE "UserName" = username;
		return userId;
	ELSE
		return -1;
	END IF;
END;
$$;


ALTER FUNCTION public.createuser(username text, password text, name text, lastname1 text, lastname2 text, role text) OWNER TO postgres;

--
-- TOC entry 33 (class 1255 OID 24917)
-- Dependencies: 355 6
-- Name: getallusers(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getallusers() RETURNS SETOF userdata
    LANGUAGE sql ROWS 10
    AS $$
	SELECT "UserId", "UserName", "Name", "LastName1", "LastName2", "Role", "CreationDate", "Enabled"
	FROM "User" INNER JOIN "Role" ON "User"."RoleId" = "Role"."RoleId"
	WHERE "Enabled" = true
	ORDER BY "UserName" ASC;
$$;


ALTER FUNCTION public.getallusers() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1550 (class 1259 OID 24692)
-- Dependencies: 1845 1846 6
-- Name: Application; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Application" (
    "ApplicationId" integer NOT NULL,
    "Description" text NOT NULL,
    "UpdateDate" timestamp without time zone DEFAULT now() NOT NULL,
    "OwnerId" integer NOT NULL,
    "Enabled" boolean DEFAULT true NOT NULL,
    "RelativePath" text NOT NULL
);


ALTER TABLE public."Application" OWNER TO postgres;

--
-- TOC entry 20 (class 1255 OID 24700)
-- Dependencies: 323 6
-- Name: getapplications(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getapplications(userid integer) RETURNS SETOF "Application"
    LANGUAGE sql
    AS $_$SELECT "ApplicationId", "Description", "UpdateDate", "OwnerId", true, "RelativePath"
	FROM "Application"
	WHERE "OwnerId" = $1 AND "Enabled" = true
	ORDER BY "RelativePath"$_$;


ALTER FUNCTION public.getapplications(userid integer) OWNER TO postgres;

--
-- TOC entry 1551 (class 1259 OID 24701)
-- Dependencies: 1848 6
-- Name: Experiment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Experiment" (
    "ExperimentId" integer NOT NULL,
    "Name" text NOT NULL,
    "Description" text NOT NULL,
    "ExecutablePath" text NOT NULL,
    "ApplicationId" integer NOT NULL,
    "ParallelExecution" boolean NOT NULL,
    "InputParametersLine" text,
    "InputFilePath" text,
    "CreationDate" timestamp without time zone DEFAULT now() NOT NULL,
    "OwnerId" integer NOT NULL
);


ALTER TABLE public."Experiment" OWNER TO postgres;

--
-- TOC entry 1900 (class 0 OID 0)
-- Dependencies: 1551
-- Name: COLUMN "Experiment"."ExecutablePath"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "Experiment"."ExecutablePath" IS 'We''re adding this just in case the application path could change';


--
-- TOC entry 24 (class 1255 OID 24708)
-- Dependencies: 326 6
-- Name: getexperimentgeneralinfo(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getexperimentgeneralinfo(userid integer) RETURNS SETOF "Experiment"
    LANGUAGE sql
    AS $_$
SELECT 
  "Experiment"."ExperimentId", 
  "Experiment"."Name", 
  "Experiment"."Description", 
  "Experiment"."ExecutablePath", 
  "Experiment"."ApplicationId", 
  "Experiment"."ParallelExecution", 
  "Experiment"."InputParametersLine", 
  "Experiment"."InputFilePath", 
  "Experiment"."CreationDate", 
  "Experiment"."OwnerId"
FROM 
  public."Experiment"
WHERE
  "OwnerId" = $1	
ORDER BY 
  "Name"
$_$;


ALTER FUNCTION public.getexperimentgeneralinfo(userid integer) OWNER TO postgres;

--
-- TOC entry 21 (class 1255 OID 24709)
-- Dependencies: 6 321
-- Name: getexperimentparameters(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getexperimentparameters(expid integer) RETURNS SETOF experimentparameter
    LANGUAGE sql
    AS $_$
SELECT 
  "InputParameters"."ParameterName", 
  "ParameterType"."Type", 
  "InputParameters"."Value"
FROM 
  public."InputParameters" INNER JOIN public."ParameterType" 
  ON ("InputParameters"."ParameterTypeId" = "ParameterType"."TypeId")
WHERE
  "ExperimentId" = $1
$_$;


ALTER FUNCTION public.getexperimentparameters(expid integer) OWNER TO postgres;

--
-- TOC entry 1552 (class 1259 OID 24710)
-- Dependencies: 6
-- Name: ExecutionStatistics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "ExecutionStatistics" (
    "ExecutionId" bigint NOT NULL,
    "ExperimentId" integer NOT NULL,
    "StartDateTime" timestamp without time zone NOT NULL,
    "FinishDateTime" timestamp without time zone NOT NULL,
    "UsedMemory" integer NOT NULL,
    "WallClockTime" integer
);


ALTER TABLE public."ExecutionStatistics" OWNER TO postgres;

--
-- TOC entry 1901 (class 0 OID 0)
-- Dependencies: 1552
-- Name: COLUMN "ExecutionStatistics"."UsedMemory"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "ExecutionStatistics"."UsedMemory" IS 'In MB';


--
-- TOC entry 25 (class 1255 OID 24713)
-- Dependencies: 329 6
-- Name: getexperimentstatistics(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getexperimentstatistics(expid integer) RETURNS SETOF "ExecutionStatistics"
    LANGUAGE sql
    AS $_$
SELECT 
  "ExecutionStatistics"."ExecutionId", 
  "ExecutionStatistics"."ExperimentId", 
  "ExecutionStatistics"."StartDateTime", 
  "ExecutionStatistics"."FinishDateTime", 
  "ExecutionStatistics"."UsedMemory",
  "ExecutionStatistics"."WallClockTime"
FROM 
  public."ExecutionStatistics"
WHERE
  "ExperimentId" = $1
$_$;


ALTER FUNCTION public.getexperimentstatistics(expid integer) OWNER TO postgres;

--
-- TOC entry 1553 (class 1259 OID 24714)
-- Dependencies: 6
-- Name: ParallelConfiguration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "ParallelConfiguration" (
    "ExperimentId" integer NOT NULL,
    "NumberOfProcessors" smallint NOT NULL,
    "SaveNodeLog" boolean NOT NULL,
    "SharedWorkingDirectory" text NOT NULL,
    "Middleware" text NOT NULL
);


ALTER TABLE public."ParallelConfiguration" OWNER TO postgres;

--
-- TOC entry 22 (class 1255 OID 24720)
-- Dependencies: 6 331
-- Name: getparallelconfiguration(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getparallelconfiguration(expid integer) RETURNS SETOF "ParallelConfiguration"
    LANGUAGE sql
    AS $_$
SELECT 
  "ParallelConfiguration"."ExperimentId", 
  "ParallelConfiguration"."NumberOfProcessors", 
  "ParallelConfiguration"."SaveNodeLog", 
  "ParallelConfiguration"."SharedWorkingDirectory", 
  "ParallelConfiguration"."Middleware"
FROM 
  public."ParallelConfiguration"
WHERE
  "ExperimentId" = $1
$_$;


ALTER FUNCTION public.getparallelconfiguration(expid integer) OWNER TO postgres;

--
-- TOC entry 1561 (class 1259 OID 24742)
-- Dependencies: 6
-- Name: ParameterType; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "ParameterType" (
    "TypeId" integer NOT NULL,
    "Type" character varying(30) NOT NULL
);


ALTER TABLE public."ParameterType" OWNER TO postgres;

--
-- TOC entry 26 (class 1255 OID 24845)
-- Dependencies: 6 344
-- Name: getparametertypes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getparametertypes() RETURNS SETOF "ParameterType"
    LANGUAGE sql ROWS 10
    AS $$SELECT "TypeId", "Type"
	FROM "ParameterType"
	ORDER BY "Type"$$;


ALTER FUNCTION public.getparametertypes() OWNER TO postgres;

--
-- TOC entry 1563 (class 1259 OID 24747)
-- Dependencies: 6
-- Name: Role; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "Role" (
    "RoleId" integer NOT NULL,
    "Role" character varying(40) NOT NULL,
    "Description" text
);


ALTER TABLE public."Role" OWNER TO postgres;

--
-- TOC entry 30 (class 1255 OID 24852)
-- Dependencies: 6 347
-- Name: getusertypes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getusertypes() RETURNS SETOF "Role"
    LANGUAGE sql ROWS 10
    AS $$SELECT "RoleId", "Role", "Description"
	FROM "Role"
	ORDER BY "Role"$$;


ALTER FUNCTION public.getusertypes() OWNER TO postgres;

--
-- TOC entry 32 (class 1255 OID 24915)
-- Dependencies: 6 355
-- Name: login(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION login(username text, password text) RETURNS SETOF userdata
    LANGUAGE sql ROWS 10
    AS $_$
	SELECT "UserId", "UserName", "Name", "LastName1", "LastName2", "Role", "CreationDate", "Enabled"
	FROM "User" INNER JOIN "Role" ON "User"."RoleId" = "Role"."RoleId"
	WHERE "UserName" = $1 AND "Password" = md5($2) AND "Enabled" = true;
$_$;


ALTER FUNCTION public.login(username text, password text) OWNER TO postgres;

--
-- TOC entry 23 (class 1255 OID 24722)
-- Dependencies: 357 6
-- Name: saveexecution(timestamp without time zone, timestamp without time zone, integer, real, interval); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" real, "wallClockTime" interval) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	executionId int;
BEGIN
	INSERT INTO "ExecutionStatistics"(
		"ExperimentId", 
		"StartDateTime", 
		"FinishDateTime", 
		"WallClockTime", 
		"UsedMemory")
	VALUES(
		"expId", 
		"startDate", 
		"finishDate", 
		"wallClockTime", 
		"usedMemory");
	SELECT MAX("ExecutionId") INTO executionId FROM "ExecutionStatistics";
	RETURN executionId;
END;
$$;


ALTER FUNCTION public.saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" real, "wallClockTime" interval) OWNER TO postgres;

--
-- TOC entry 27 (class 1255 OID 24846)
-- Dependencies: 6 357
-- Name: saveexecution(timestamp without time zone, timestamp without time zone, integer, real, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" real, "wallClockTime" integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	executionId int;
BEGIN
	INSERT INTO "ExecutionStatistics"(
		"ExperimentId", 
		"StartDateTime", 
		"FinishDateTime", 
		"WallClockTime", 
		"UsedMemory")
	VALUES(
		"expId", 
		"startDate", 
		"finishDate", 
		"wallClockTime", 
		"usedMemory");
	SELECT MAX("ExecutionId") INTO executionId FROM "ExecutionStatistics";
	RETURN executionId;
END;
$$;


ALTER FUNCTION public.saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" real, "wallClockTime" integer) OWNER TO postgres;

--
-- TOC entry 1554 (class 1259 OID 24723)
-- Dependencies: 1550 6
-- Name: Application_ApplicationId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Application_ApplicationId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Application_ApplicationId_seq" OWNER TO postgres;

--
-- TOC entry 1902 (class 0 OID 0)
-- Dependencies: 1554
-- Name: Application_ApplicationId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Application_ApplicationId_seq" OWNED BY "Application"."ApplicationId";


--
-- TOC entry 1903 (class 0 OID 0)
-- Dependencies: 1554
-- Name: Application_ApplicationId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Application_ApplicationId_seq"', 15, true);


--
-- TOC entry 1555 (class 1259 OID 24725)
-- Dependencies: 6 1552
-- Name: ExecutionStatistics_ExecutionId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "ExecutionStatistics_ExecutionId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."ExecutionStatistics_ExecutionId_seq" OWNER TO postgres;

--
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1555
-- Name: ExecutionStatistics_ExecutionId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "ExecutionStatistics_ExecutionId_seq" OWNED BY "ExecutionStatistics"."ExecutionId";


--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1555
-- Name: ExecutionStatistics_ExecutionId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"ExecutionStatistics_ExecutionId_seq"', 4, true);


--
-- TOC entry 1556 (class 1259 OID 24727)
-- Dependencies: 1551 6
-- Name: Experiment_ExperimentId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Experiment_ExperimentId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Experiment_ExperimentId_seq" OWNER TO postgres;

--
-- TOC entry 1906 (class 0 OID 0)
-- Dependencies: 1556
-- Name: Experiment_ExperimentId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Experiment_ExperimentId_seq" OWNED BY "Experiment"."ExperimentId";


--
-- TOC entry 1907 (class 0 OID 0)
-- Dependencies: 1556
-- Name: Experiment_ExperimentId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Experiment_ExperimentId_seq"', 40, true);


--
-- TOC entry 1557 (class 1259 OID 24729)
-- Dependencies: 6
-- Name: InputParameters; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "InputParameters" (
    "ParameterId" bigint NOT NULL,
    "ParameterName" text NOT NULL,
    "ExperimentId" integer NOT NULL,
    "ParameterTypeId" integer NOT NULL,
    "Value" text NOT NULL
);


ALTER TABLE public."InputParameters" OWNER TO postgres;

--
-- TOC entry 1558 (class 1259 OID 24735)
-- Dependencies: 6 1557
-- Name: InputParameters_ParameterId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "InputParameters_ParameterId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."InputParameters_ParameterId_seq" OWNER TO postgres;

--
-- TOC entry 1908 (class 0 OID 0)
-- Dependencies: 1558
-- Name: InputParameters_ParameterId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "InputParameters_ParameterId_seq" OWNED BY "InputParameters"."ParameterId";


--
-- TOC entry 1909 (class 0 OID 0)
-- Dependencies: 1558
-- Name: InputParameters_ParameterId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"InputParameters_ParameterId_seq"', 7, true);


--
-- TOC entry 1559 (class 1259 OID 24737)
-- Dependencies: 6
-- Name: NodeStatistics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "NodeStatistics" (
    "NodeStatisticsId" bigint NOT NULL,
    "ExecutionId" integer NOT NULL,
    "NodeNumber" integer NOT NULL,
    "TotalTime" interval NOT NULL,
    "UsedMemory" real NOT NULL
);


ALTER TABLE public."NodeStatistics" OWNER TO postgres;

--
-- TOC entry 1560 (class 1259 OID 24740)
-- Dependencies: 6 1559
-- Name: NodeStatistics_NodeStatisticsId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "NodeStatistics_NodeStatisticsId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."NodeStatistics_NodeStatisticsId_seq" OWNER TO postgres;

--
-- TOC entry 1910 (class 0 OID 0)
-- Dependencies: 1560
-- Name: NodeStatistics_NodeStatisticsId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "NodeStatistics_NodeStatisticsId_seq" OWNED BY "NodeStatistics"."NodeStatisticsId";


--
-- TOC entry 1911 (class 0 OID 0)
-- Dependencies: 1560
-- Name: NodeStatistics_NodeStatisticsId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"NodeStatistics_NodeStatisticsId_seq"', 1, false);


--
-- TOC entry 1562 (class 1259 OID 24745)
-- Dependencies: 1561 6
-- Name: ParameterType_TypeId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "ParameterType_TypeId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."ParameterType_TypeId_seq" OWNER TO postgres;

--
-- TOC entry 1912 (class 0 OID 0)
-- Dependencies: 1562
-- Name: ParameterType_TypeId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "ParameterType_TypeId_seq" OWNED BY "ParameterType"."TypeId";


--
-- TOC entry 1913 (class 0 OID 0)
-- Dependencies: 1562
-- Name: ParameterType_TypeId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"ParameterType_TypeId_seq"', 4, true);


--
-- TOC entry 1564 (class 1259 OID 24753)
-- Dependencies: 6 1563
-- Name: Role_RoleId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Role_RoleId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Role_RoleId_seq" OWNER TO postgres;

--
-- TOC entry 1914 (class 0 OID 0)
-- Dependencies: 1564
-- Name: Role_RoleId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Role_RoleId_seq" OWNED BY "Role"."RoleId";


--
-- TOC entry 1915 (class 0 OID 0)
-- Dependencies: 1564
-- Name: Role_RoleId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Role_RoleId_seq"', 2, true);


--
-- TOC entry 1565 (class 1259 OID 24755)
-- Dependencies: 1855 1856 6
-- Name: User; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "User" (
    "UserId" integer NOT NULL,
    "UserName" text NOT NULL,
    "Password" text NOT NULL,
    "Name" text NOT NULL,
    "LastName1" text NOT NULL,
    "LastName2" text NOT NULL,
    "CreationDate" timestamp without time zone DEFAULT now() NOT NULL,
    "RoleId" integer NOT NULL,
    "Enabled" boolean DEFAULT true NOT NULL
);


ALTER TABLE public."User" OWNER TO postgres;

--
-- TOC entry 1566 (class 1259 OID 24763)
-- Dependencies: 1565 6
-- Name: User_UserId_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "User_UserId_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."User_UserId_seq" OWNER TO postgres;

--
-- TOC entry 1916 (class 0 OID 0)
-- Dependencies: 1566
-- Name: User_UserId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "User_UserId_seq" OWNED BY "User"."UserId";


--
-- TOC entry 1917 (class 0 OID 0)
-- Dependencies: 1566
-- Name: User_UserId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"User_UserId_seq"', 14, true);


--
-- TOC entry 1847 (class 2604 OID 24765)
-- Dependencies: 1554 1550
-- Name: ApplicationId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Application" ALTER COLUMN "ApplicationId" SET DEFAULT nextval('"Application_ApplicationId_seq"'::regclass);


--
-- TOC entry 1850 (class 2604 OID 24766)
-- Dependencies: 1555 1552
-- Name: ExecutionId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "ExecutionStatistics" ALTER COLUMN "ExecutionId" SET DEFAULT nextval('"ExecutionStatistics_ExecutionId_seq"'::regclass);


--
-- TOC entry 1849 (class 2604 OID 24767)
-- Dependencies: 1556 1551
-- Name: ExperimentId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Experiment" ALTER COLUMN "ExperimentId" SET DEFAULT nextval('"Experiment_ExperimentId_seq"'::regclass);


--
-- TOC entry 1851 (class 2604 OID 24768)
-- Dependencies: 1558 1557
-- Name: ParameterId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "InputParameters" ALTER COLUMN "ParameterId" SET DEFAULT nextval('"InputParameters_ParameterId_seq"'::regclass);


--
-- TOC entry 1852 (class 2604 OID 24769)
-- Dependencies: 1560 1559
-- Name: NodeStatisticsId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "NodeStatistics" ALTER COLUMN "NodeStatisticsId" SET DEFAULT nextval('"NodeStatistics_NodeStatisticsId_seq"'::regclass);


--
-- TOC entry 1853 (class 2604 OID 24770)
-- Dependencies: 1562 1561
-- Name: TypeId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "ParameterType" ALTER COLUMN "TypeId" SET DEFAULT nextval('"ParameterType_TypeId_seq"'::regclass);


--
-- TOC entry 1854 (class 2604 OID 24771)
-- Dependencies: 1564 1563
-- Name: RoleId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Role" ALTER COLUMN "RoleId" SET DEFAULT nextval('"Role_RoleId_seq"'::regclass);


--
-- TOC entry 1857 (class 2604 OID 24772)
-- Dependencies: 1566 1565
-- Name: UserId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "User" ALTER COLUMN "UserId" SET DEFAULT nextval('"User_UserId_seq"'::regclass);


--
-- TOC entry 1886 (class 0 OID 24692)
-- Dependencies: 1550
-- Data for Name: Application; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Application" ("ApplicationId", "Description", "UpdateDate", "OwnerId", "Enabled", "RelativePath") FROM stdin;
\.


--
-- TOC entry 1888 (class 0 OID 24710)
-- Dependencies: 1552
-- Data for Name: ExecutionStatistics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ExecutionStatistics" ("ExecutionId", "ExperimentId", "StartDateTime", "FinishDateTime", "UsedMemory", "WallClockTime") FROM stdin;
\.


--
-- TOC entry 1887 (class 0 OID 24701)
-- Dependencies: 1551
-- Data for Name: Experiment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Experiment" ("ExperimentId", "Name", "Description", "ExecutablePath", "ApplicationId", "ParallelExecution", "InputParametersLine", "InputFilePath", "CreationDate", "OwnerId") FROM stdin;
\.


--
-- TOC entry 1890 (class 0 OID 24729)
-- Dependencies: 1557
-- Data for Name: InputParameters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "InputParameters" ("ParameterId", "ParameterName", "ExperimentId", "ParameterTypeId", "Value") FROM stdin;
\.


--
-- TOC entry 1891 (class 0 OID 24737)
-- Dependencies: 1559
-- Data for Name: NodeStatistics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "NodeStatistics" ("NodeStatisticsId", "ExecutionId", "NodeNumber", "TotalTime", "UsedMemory") FROM stdin;
\.


--
-- TOC entry 1889 (class 0 OID 24714)
-- Dependencies: 1553
-- Data for Name: ParallelConfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ParallelConfiguration" ("ExperimentId", "NumberOfProcessors", "SaveNodeLog", "SharedWorkingDirectory", "Middleware") FROM stdin;
\.


--
-- TOC entry 1892 (class 0 OID 24742)
-- Dependencies: 1561
-- Data for Name: ParameterType; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ParameterType" ("TypeId", "Type") FROM stdin;
1	float
2	int
3	char
4	string
\.


--
-- TOC entry 1893 (class 0 OID 24747)
-- Dependencies: 1563
-- Data for Name: Role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Role" ("RoleId", "Role", "Description") FROM stdin;
2	Experiment Owner	This user can create new experiments, execute them, retrieve the statistics and results of a experiment and install and upload applications for execution.
1	Administrator	This user can create, edit and delete other non-administrator user, and it also has the experiment owner's rights.
\.


--
-- TOC entry 1894 (class 0 OID 24755)
-- Dependencies: 1565
-- Data for Name: User; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "User" ("UserId", "UserName", "Password", "Name", "LastName1", "LastName2", "CreationDate", "RoleId", "Enabled") FROM stdin;
3	cfernandez	202cb962ac59075b964b07152d234b70	Carlos Manuel	Fernández	Loría	2011-03-13 18:45:00.88	1	t
2	rdinarte	202cb962ac59075b964b07152d234b70	Rainiero	Dinarte	Chavarría	2011-03-13 18:32:00.761	1	t
\.


--
-- TOC entry 1859 (class 2606 OID 24774)
-- Dependencies: 1550 1550
-- Name: ApplicationPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Application"
    ADD CONSTRAINT "ApplicationPK" PRIMARY KEY ("ApplicationId");


--
-- TOC entry 1865 (class 2606 OID 24776)
-- Dependencies: 1553 1553
-- Name: ConfigurationId; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ParallelConfiguration"
    ADD CONSTRAINT "ConfigurationId" PRIMARY KEY ("ExperimentId");


--
-- TOC entry 1861 (class 2606 OID 24778)
-- Dependencies: 1551 1551
-- Name: ExperimentPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "ExperimentPK" PRIMARY KEY ("ExperimentId");


--
-- TOC entry 1869 (class 2606 OID 24780)
-- Dependencies: 1559 1559
-- Name: NodePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "NodeStatistics"
    ADD CONSTRAINT "NodePK" PRIMARY KEY ("NodeStatisticsId");


--
-- TOC entry 1867 (class 2606 OID 24782)
-- Dependencies: 1557 1557
-- Name: ParameterPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "ParameterPK" PRIMARY KEY ("ParameterId");


--
-- TOC entry 1873 (class 2606 OID 24784)
-- Dependencies: 1563 1563
-- Name: RolePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Role"
    ADD CONSTRAINT "RolePK" PRIMARY KEY ("RoleId");


--
-- TOC entry 1863 (class 2606 OID 24786)
-- Dependencies: 1552 1552
-- Name: StatisticsPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ExecutionStatistics"
    ADD CONSTRAINT "StatisticsPK" PRIMARY KEY ("ExecutionId");


--
-- TOC entry 1871 (class 2606 OID 24788)
-- Dependencies: 1561 1561
-- Name: TypePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ParameterType"
    ADD CONSTRAINT "TypePK" PRIMARY KEY ("TypeId");


--
-- TOC entry 1875 (class 2606 OID 24790)
-- Dependencies: 1565 1565
-- Name: UserPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "User"
    ADD CONSTRAINT "UserPK" PRIMARY KEY ("UserId");


--
-- TOC entry 1876 (class 1259 OID 24791)
-- Dependencies: 1565
-- Name: fki_UserRole; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_UserRole" ON "User" USING btree ("RoleId");


--
-- TOC entry 1884 (class 2606 OID 24792)
-- Dependencies: 1862 1559 1552
-- Name: BelongsToExecution; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "NodeStatistics"
    ADD CONSTRAINT "BelongsToExecution" FOREIGN KEY ("ExecutionId") REFERENCES "ExecutionStatistics"("ExecutionId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1882 (class 2606 OID 24797)
-- Dependencies: 1860 1551 1557
-- Name: BelongsToExperiment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "BelongsToExperiment" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1880 (class 2606 OID 24802)
-- Dependencies: 1551 1552 1860
-- Name: ExecutedExperiment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "ExecutionStatistics"
    ADD CONSTRAINT "ExecutedExperiment" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1881 (class 2606 OID 24807)
-- Dependencies: 1860 1551 1553
-- Name: ExperimentConfiguration; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "ParallelConfiguration"
    ADD CONSTRAINT "ExperimentConfiguration" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1878 (class 2606 OID 24812)
-- Dependencies: 1551 1565 1874
-- Name: ExperimentOwner; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "ExperimentOwner" FOREIGN KEY ("OwnerId") REFERENCES "User"("UserId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1877 (class 2606 OID 24817)
-- Dependencies: 1874 1565 1550
-- Name: OwnerPK; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Application"
    ADD CONSTRAINT "OwnerPK" FOREIGN KEY ("OwnerId") REFERENCES "User"("UserId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1883 (class 2606 OID 24822)
-- Dependencies: 1870 1561 1557
-- Name: ParameterType; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "ParameterType" FOREIGN KEY ("ParameterTypeId") REFERENCES "ParameterType"("TypeId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1879 (class 2606 OID 24827)
-- Dependencies: 1858 1551 1550
-- Name: UsedApplication; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "UsedApplication" FOREIGN KEY ("ApplicationId") REFERENCES "Application"("ApplicationId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1885 (class 2606 OID 24832)
-- Dependencies: 1872 1565 1563
-- Name: UserRole; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "User"
    ADD CONSTRAINT "UserRole" FOREIGN KEY ("RoleId") REFERENCES "Role"("RoleId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1899 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-04-24 11:06:06 CST

--
-- PostgreSQL database dump complete
--

