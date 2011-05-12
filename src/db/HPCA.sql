--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.3
-- Dumped by pg_dump version 9.0.3
-- Started on 2011-05-11 21:48:14 CST

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1901 (class 1262 OID 41286)
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
-- TOC entry 361 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: postgres
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


ALTER PROCEDURAL LANGUAGE plpgsql OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- TOC entry 324 (class 1247 OID 41289)
-- Dependencies: 6 1553
-- Name: experimentparameter; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE experimentparameter AS (
	"ParameterName" text,
	"Type" text,
	"Value" text
);


ALTER TYPE public.experimentparameter OWNER TO postgres;

--
-- TOC entry 326 (class 1247 OID 41292)
-- Dependencies: 6 1554
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
-- TOC entry 18 (class 1255 OID 41293)
-- Dependencies: 361 6
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
-- TOC entry 19 (class 1255 OID 41294)
-- Dependencies: 6 361
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
-- TOC entry 35 (class 1255 OID 41502)
-- Dependencies: 6 361
-- Name: changepassword(text, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION changepassword(login text, "oldPassword" text, "newPassword" text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	userCount int;
BEGIN

	SELECT COUNT("UserName") INTO userCount FROM "User" 
	WHERE "UserName" = "login" AND "Password" = md5("oldPassword");
	IF userCount = 1 THEN
		UPDATE "User"
		SET
			"Password" = md5("newPassword")
		WHERE
			"UserName" = "login";
		return 0;
	ELSE
		return -1;
	END IF;
END;
$$;


ALTER FUNCTION public.changepassword(login text, "oldPassword" text, "newPassword" text) OWNER TO postgres;

--
-- TOC entry 20 (class 1255 OID 41295)
-- Dependencies: 361 6
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
-- TOC entry 21 (class 1255 OID 41296)
-- Dependencies: 6 361
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
-- TOC entry 22 (class 1255 OID 41297)
-- Dependencies: 6 361
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
		VALUES(username, md5(password), name, lastname1, lastname2, roleid);

		SELECT "UserId" INTO userId FROM "User" WHERE "UserName" = username;
		return userId;
	ELSE
		return -1;
	END IF;
END;
$$;


ALTER FUNCTION public.createuser(username text, password text, name text, lastname1 text, lastname2 text, role text) OWNER TO postgres;

--
-- TOC entry 32 (class 1255 OID 41298)
-- Dependencies: 326 6
-- Name: getallusers(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getallusers() RETURNS SETOF userdata
    LANGUAGE sql ROWS 10
    AS $$
	SELECT "UserId", "UserName", "Name", "LastName1", "LastName2", "Role", "CreationDate", "Enabled"
	FROM "User" INNER JOIN "Role" ON "User"."RoleId" = "Role"."RoleId"
	ORDER BY "UserName" ASC;
$$;


ALTER FUNCTION public.getallusers() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1555 (class 1259 OID 41299)
-- Dependencies: 1849 1850 6
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
-- TOC entry 23 (class 1255 OID 41307)
-- Dependencies: 328 6
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
-- TOC entry 1556 (class 1259 OID 41308)
-- Dependencies: 1852 6
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
-- TOC entry 1904 (class 0 OID 0)
-- Dependencies: 1556
-- Name: COLUMN "Experiment"."ExecutablePath"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "Experiment"."ExecutablePath" IS 'We''re adding this just in case the application path could change';


--
-- TOC entry 24 (class 1255 OID 41315)
-- Dependencies: 6 331
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
-- TOC entry 25 (class 1255 OID 41316)
-- Dependencies: 324 6
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
-- TOC entry 1557 (class 1259 OID 41317)
-- Dependencies: 6
-- Name: ExecutionStatistics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "ExecutionStatistics" (
    "ExecutionId" bigint NOT NULL,
    "ExperimentId" integer NOT NULL,
    "StartDateTime" timestamp without time zone NOT NULL,
    "FinishDateTime" timestamp without time zone NOT NULL,
    "UsedMemory" double precision NOT NULL,
    "WallClockTime" integer NOT NULL,
    "OutputPath" text NOT NULL,
    "CPUUsage" double precision NOT NULL
);


ALTER TABLE public."ExecutionStatistics" OWNER TO postgres;

--
-- TOC entry 1905 (class 0 OID 0)
-- Dependencies: 1557
-- Name: COLUMN "ExecutionStatistics"."UsedMemory"; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN "ExecutionStatistics"."UsedMemory" IS 'In MB';


--
-- TOC entry 26 (class 1255 OID 41323)
-- Dependencies: 6 334
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
  "ExecutionStatistics"."WallClockTime",
  "ExecutionStatistics"."OutputPath",
  "ExecutionStatistics"."CPUUsage"
FROM 
  public."ExecutionStatistics"
WHERE
  "ExperimentId" = $1
$_$;


ALTER FUNCTION public.getexperimentstatistics(expid integer) OWNER TO postgres;

--
-- TOC entry 1571 (class 1259 OID 41482)
-- Dependencies: 6
-- Name: NodeStatistics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "NodeStatistics" (
    "NodeStatisticsId" bigint NOT NULL,
    "ExecutionId" integer NOT NULL,
    "NodeNumber" integer NOT NULL,
    "TotalTime" integer NOT NULL,
    "UsedMemory" double precision NOT NULL,
    "CpuUsage" double precision NOT NULL
);


ALTER TABLE public."NodeStatistics" OWNER TO postgres;

--
-- TOC entry 33 (class 1255 OID 41493)
-- Dependencies: 359 6
-- Name: getnodestatistics(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getnodestatistics(executionid integer) RETURNS SETOF "NodeStatistics"
    LANGUAGE sql
    AS $_$
SELECT "NodeStatisticsId", "ExecutionId", "NodeNumber", "TotalTime", "UsedMemory", "CpuUsage"
	FROM  "NodeStatistics" WHERE "ExecutionId" = $1
$_$;


ALTER FUNCTION public.getnodestatistics(executionid integer) OWNER TO postgres;

--
-- TOC entry 1558 (class 1259 OID 41324)
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
-- TOC entry 27 (class 1255 OID 41330)
-- Dependencies: 6 337
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
-- TOC entry 1559 (class 1259 OID 41331)
-- Dependencies: 6
-- Name: ParameterType; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE "ParameterType" (
    "TypeId" integer NOT NULL,
    "Type" character varying(30) NOT NULL
);


ALTER TABLE public."ParameterType" OWNER TO postgres;

--
-- TOC entry 28 (class 1255 OID 41334)
-- Dependencies: 340 6
-- Name: getparametertypes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getparametertypes() RETURNS SETOF "ParameterType"
    LANGUAGE sql ROWS 10
    AS $$SELECT "TypeId", "Type"
	FROM "ParameterType"
	ORDER BY "Type"$$;


ALTER FUNCTION public.getparametertypes() OWNER TO postgres;

--
-- TOC entry 1560 (class 1259 OID 41335)
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
-- TOC entry 29 (class 1255 OID 41341)
-- Dependencies: 342 6
-- Name: getusertypes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getusertypes() RETURNS SETOF "Role"
    LANGUAGE sql ROWS 10
    AS $$SELECT "RoleId", "Role", "Description"
	FROM "Role"
	ORDER BY "Role"$$;


ALTER FUNCTION public.getusertypes() OWNER TO postgres;

--
-- TOC entry 30 (class 1255 OID 41342)
-- Dependencies: 326 6
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
-- TOC entry 31 (class 1255 OID 41343)
-- Dependencies: 6 361
-- Name: saveexecution(timestamp without time zone, timestamp without time zone, integer, double precision, integer, text, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" double precision, "wallClockTime" integer, "outputPath" text, "cpuUsage" double precision) RETURNS integer
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
		"UsedMemory",
		"OutputPath",
		"CPUUsage")
	VALUES(
		"expId", 
		"startDate", 
		"finishDate", 
		"wallClockTime", 
		"usedMemory",
		"outputPath",
		"cpuUsage");
	SELECT MAX("ExecutionId") INTO executionId FROM "ExecutionStatistics";
	RETURN executionId;
END;
$$;


ALTER FUNCTION public.saveexecution("startDate" timestamp without time zone, "finishDate" timestamp without time zone, "expId" integer, "usedMemory" double precision, "wallClockTime" integer, "outputPath" text, "cpuUsage" double precision) OWNER TO postgres;

--
-- TOC entry 34 (class 1255 OID 41494)
-- Dependencies: 361 6
-- Name: savenodestats(integer, integer, integer, double precision, double precision); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION savenodestats("execId" integer, "nodeNumber" integer, "cpuTime" integer, "usedMemory" double precision, "cpuUsage" double precision) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	nodeId int;
BEGIN
	INSERT INTO "NodeStatistics"(
		"ExecutionId",
		"NodeNumber",
		"TotalTime",
		"UsedMemory",
		"CpuUsage")
		
	VALUES(
		"execId", 
		"nodeNumber", 
		"cpuTime", 
		"usedMemory", 
		"cpuUsage");
	SELECT MAX("NodeStatisticsId") INTO nodeId FROM "NodeStatistics" ;
	RETURN nodeId;
END;
$$;


ALTER FUNCTION public.savenodestats("execId" integer, "nodeNumber" integer, "cpuTime" integer, "usedMemory" double precision, "cpuUsage" double precision) OWNER TO postgres;

--
-- TOC entry 36 (class 1255 OID 41499)
-- Dependencies: 361 6
-- Name: updateuserpersonalinfo(text, text, text, text, text, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION updateuserpersonalinfo(login text, "newName" text, "newLastName1" text, "newLastName2" text, "newRole" text, enabled boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN

	UPDATE "User"
	SET
		"Name" = "newName",
		"LastName1" = "newLastName1",
		"LastName2" = "newLastName2",
		"Enabled" = "enabled",
		"RoleId" = (SELECT "RoleId" FROM "Role" WHERE "Role" = "newRole")
	WHERE
		"UserName" = "login";
END;
$$;


ALTER FUNCTION public.updateuserpersonalinfo(login text, "newName" text, "newLastName1" text, "newLastName2" text, "newRole" text, enabled boolean) OWNER TO postgres;

--
-- TOC entry 1561 (class 1259 OID 41344)
-- Dependencies: 1555 6
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
-- TOC entry 1906 (class 0 OID 0)
-- Dependencies: 1561
-- Name: Application_ApplicationId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Application_ApplicationId_seq" OWNED BY "Application"."ApplicationId";


--
-- TOC entry 1907 (class 0 OID 0)
-- Dependencies: 1561
-- Name: Application_ApplicationId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Application_ApplicationId_seq"', 19, true);


--
-- TOC entry 1562 (class 1259 OID 41346)
-- Dependencies: 1557 6
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
-- TOC entry 1908 (class 0 OID 0)
-- Dependencies: 1562
-- Name: ExecutionStatistics_ExecutionId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "ExecutionStatistics_ExecutionId_seq" OWNED BY "ExecutionStatistics"."ExecutionId";


--
-- TOC entry 1909 (class 0 OID 0)
-- Dependencies: 1562
-- Name: ExecutionStatistics_ExecutionId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"ExecutionStatistics_ExecutionId_seq"', 49, true);


--
-- TOC entry 1563 (class 1259 OID 41348)
-- Dependencies: 6 1556
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
-- TOC entry 1910 (class 0 OID 0)
-- Dependencies: 1563
-- Name: Experiment_ExperimentId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Experiment_ExperimentId_seq" OWNED BY "Experiment"."ExperimentId";


--
-- TOC entry 1911 (class 0 OID 0)
-- Dependencies: 1563
-- Name: Experiment_ExperimentId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Experiment_ExperimentId_seq"', 44, true);


--
-- TOC entry 1564 (class 1259 OID 41350)
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
-- TOC entry 1565 (class 1259 OID 41356)
-- Dependencies: 6 1564
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
-- TOC entry 1912 (class 0 OID 0)
-- Dependencies: 1565
-- Name: InputParameters_ParameterId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "InputParameters_ParameterId_seq" OWNED BY "InputParameters"."ParameterId";


--
-- TOC entry 1913 (class 0 OID 0)
-- Dependencies: 1565
-- Name: InputParameters_ParameterId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"InputParameters_ParameterId_seq"', 7, true);


--
-- TOC entry 1570 (class 1259 OID 41480)
-- Dependencies: 1571 6
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
-- TOC entry 1914 (class 0 OID 0)
-- Dependencies: 1570
-- Name: NodeStatistics_NodeStatisticsId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "NodeStatistics_NodeStatisticsId_seq" OWNED BY "NodeStatistics"."NodeStatisticsId";


--
-- TOC entry 1915 (class 0 OID 0)
-- Dependencies: 1570
-- Name: NodeStatistics_NodeStatisticsId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"NodeStatistics_NodeStatisticsId_seq"', 2, true);


--
-- TOC entry 1566 (class 1259 OID 41363)
-- Dependencies: 1559 6
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
-- TOC entry 1916 (class 0 OID 0)
-- Dependencies: 1566
-- Name: ParameterType_TypeId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "ParameterType_TypeId_seq" OWNED BY "ParameterType"."TypeId";


--
-- TOC entry 1917 (class 0 OID 0)
-- Dependencies: 1566
-- Name: ParameterType_TypeId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"ParameterType_TypeId_seq"', 4, true);


--
-- TOC entry 1567 (class 1259 OID 41365)
-- Dependencies: 1560 6
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
-- TOC entry 1918 (class 0 OID 0)
-- Dependencies: 1567
-- Name: Role_RoleId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Role_RoleId_seq" OWNED BY "Role"."RoleId";


--
-- TOC entry 1919 (class 0 OID 0)
-- Dependencies: 1567
-- Name: Role_RoleId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Role_RoleId_seq"', 2, true);


--
-- TOC entry 1568 (class 1259 OID 41367)
-- Dependencies: 1858 1859 6
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
-- TOC entry 1569 (class 1259 OID 41375)
-- Dependencies: 6 1568
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
-- TOC entry 1920 (class 0 OID 0)
-- Dependencies: 1569
-- Name: User_UserId_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "User_UserId_seq" OWNED BY "User"."UserId";


--
-- TOC entry 1921 (class 0 OID 0)
-- Dependencies: 1569
-- Name: User_UserId_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"User_UserId_seq"', 16, true);


--
-- TOC entry 1851 (class 2604 OID 41377)
-- Dependencies: 1561 1555
-- Name: ApplicationId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Application" ALTER COLUMN "ApplicationId" SET DEFAULT nextval('"Application_ApplicationId_seq"'::regclass);


--
-- TOC entry 1854 (class 2604 OID 41378)
-- Dependencies: 1562 1557
-- Name: ExecutionId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "ExecutionStatistics" ALTER COLUMN "ExecutionId" SET DEFAULT nextval('"ExecutionStatistics_ExecutionId_seq"'::regclass);


--
-- TOC entry 1853 (class 2604 OID 41379)
-- Dependencies: 1563 1556
-- Name: ExperimentId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Experiment" ALTER COLUMN "ExperimentId" SET DEFAULT nextval('"Experiment_ExperimentId_seq"'::regclass);


--
-- TOC entry 1857 (class 2604 OID 41380)
-- Dependencies: 1565 1564
-- Name: ParameterId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "InputParameters" ALTER COLUMN "ParameterId" SET DEFAULT nextval('"InputParameters_ParameterId_seq"'::regclass);


--
-- TOC entry 1861 (class 2604 OID 41485)
-- Dependencies: 1570 1571 1571
-- Name: NodeStatisticsId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "NodeStatistics" ALTER COLUMN "NodeStatisticsId" SET DEFAULT nextval('"NodeStatistics_NodeStatisticsId_seq"'::regclass);


--
-- TOC entry 1855 (class 2604 OID 41382)
-- Dependencies: 1566 1559
-- Name: TypeId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "ParameterType" ALTER COLUMN "TypeId" SET DEFAULT nextval('"ParameterType_TypeId_seq"'::regclass);


--
-- TOC entry 1856 (class 2604 OID 41383)
-- Dependencies: 1567 1560
-- Name: RoleId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "Role" ALTER COLUMN "RoleId" SET DEFAULT nextval('"Role_RoleId_seq"'::regclass);


--
-- TOC entry 1860 (class 2604 OID 41384)
-- Dependencies: 1569 1568
-- Name: UserId; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE "User" ALTER COLUMN "UserId" SET DEFAULT nextval('"User_UserId_seq"'::regclass);


--
-- TOC entry 1890 (class 0 OID 41299)
-- Dependencies: 1555
-- Data for Name: Application; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Application" ("ApplicationId", "Description", "UpdateDate", "OwnerId", "Enabled", "RelativePath") FROM stdin;
\.


--
-- TOC entry 1892 (class 0 OID 41317)
-- Dependencies: 1557
-- Data for Name: ExecutionStatistics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ExecutionStatistics" ("ExecutionId", "ExperimentId", "StartDateTime", "FinishDateTime", "UsedMemory", "WallClockTime", "OutputPath", "CPUUsage") FROM stdin;
\.


--
-- TOC entry 1891 (class 0 OID 41308)
-- Dependencies: 1556
-- Data for Name: Experiment; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Experiment" ("ExperimentId", "Name", "Description", "ExecutablePath", "ApplicationId", "ParallelExecution", "InputParametersLine", "InputFilePath", "CreationDate", "OwnerId") FROM stdin;
\.


--
-- TOC entry 1896 (class 0 OID 41350)
-- Dependencies: 1564
-- Data for Name: InputParameters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "InputParameters" ("ParameterId", "ParameterName", "ExperimentId", "ParameterTypeId", "Value") FROM stdin;
\.


--
-- TOC entry 1898 (class 0 OID 41482)
-- Dependencies: 1571
-- Data for Name: NodeStatistics; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "NodeStatistics" ("NodeStatisticsId", "ExecutionId", "NodeNumber", "TotalTime", "UsedMemory", "CpuUsage") FROM stdin;
\.


--
-- TOC entry 1893 (class 0 OID 41324)
-- Dependencies: 1558
-- Data for Name: ParallelConfiguration; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ParallelConfiguration" ("ExperimentId", "NumberOfProcessors", "SaveNodeLog", "SharedWorkingDirectory", "Middleware") FROM stdin;
\.


--
-- TOC entry 1894 (class 0 OID 41331)
-- Dependencies: 1559
-- Data for Name: ParameterType; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "ParameterType" ("TypeId", "Type") FROM stdin;
1	float
2	int
3	char
4	string
\.


--
-- TOC entry 1895 (class 0 OID 41335)
-- Dependencies: 1560
-- Data for Name: Role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "Role" ("RoleId", "Role", "Description") FROM stdin;
2	Experiment Owner	This user can create new experiments, execute them, retrieve the statistics and results of a experiment and install and upload applications for execution.
1	Administrator	This user can create, edit and delete other non-administrator user, and it also has the experiment owner's rights.
\.


--
-- TOC entry 1897 (class 0 OID 41367)
-- Dependencies: 1568
-- Data for Name: User; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY "User" ("UserId", "UserName", "Password", "Name", "LastName1", "LastName2", "CreationDate", "RoleId", "Enabled") FROM stdin;
2	rdinarte	202cb962ac59075b964b07152d234b70	Rainiero	Dinarte	Chavarría	2011-03-13 18:32:00.761	1	t
3	cfernandez	202cb962ac59075b964b07152d234b70	Carlos Manuel	Fernandez	LorÂ­ia	2011-03-13 18:45:00.88	1	t
\.


--
-- TOC entry 1863 (class 2606 OID 41386)
-- Dependencies: 1555 1555
-- Name: ApplicationPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Application"
    ADD CONSTRAINT "ApplicationPK" PRIMARY KEY ("ApplicationId");


--
-- TOC entry 1869 (class 2606 OID 41388)
-- Dependencies: 1558 1558
-- Name: ConfigurationId; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ParallelConfiguration"
    ADD CONSTRAINT "ConfigurationId" PRIMARY KEY ("ExperimentId");


--
-- TOC entry 1865 (class 2606 OID 41390)
-- Dependencies: 1556 1556
-- Name: ExperimentPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "ExperimentPK" PRIMARY KEY ("ExperimentId");


--
-- TOC entry 1880 (class 2606 OID 41487)
-- Dependencies: 1571 1571
-- Name: NodePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "NodeStatistics"
    ADD CONSTRAINT "NodePK" PRIMARY KEY ("NodeStatisticsId");


--
-- TOC entry 1875 (class 2606 OID 41394)
-- Dependencies: 1564 1564
-- Name: ParameterPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "ParameterPK" PRIMARY KEY ("ParameterId");


--
-- TOC entry 1873 (class 2606 OID 41396)
-- Dependencies: 1560 1560
-- Name: RolePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "Role"
    ADD CONSTRAINT "RolePK" PRIMARY KEY ("RoleId");


--
-- TOC entry 1867 (class 2606 OID 41398)
-- Dependencies: 1557 1557
-- Name: StatisticsPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ExecutionStatistics"
    ADD CONSTRAINT "StatisticsPK" PRIMARY KEY ("ExecutionId");


--
-- TOC entry 1871 (class 2606 OID 41400)
-- Dependencies: 1559 1559
-- Name: TypePK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "ParameterType"
    ADD CONSTRAINT "TypePK" PRIMARY KEY ("TypeId");


--
-- TOC entry 1877 (class 2606 OID 41402)
-- Dependencies: 1568 1568
-- Name: UserPK; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY "User"
    ADD CONSTRAINT "UserPK" PRIMARY KEY ("UserId");


--
-- TOC entry 1878 (class 1259 OID 41403)
-- Dependencies: 1568
-- Name: fki_UserRole; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX "fki_UserRole" ON "User" USING btree ("RoleId");


--
-- TOC entry 1889 (class 2606 OID 41488)
-- Dependencies: 1557 1571 1866
-- Name: BelongsToExecution; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "NodeStatistics"
    ADD CONSTRAINT "BelongsToExecution" FOREIGN KEY ("ExecutionId") REFERENCES "ExecutionStatistics"("ExecutionId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1886 (class 2606 OID 41409)
-- Dependencies: 1564 1864 1556
-- Name: BelongsToExperiment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "BelongsToExperiment" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1884 (class 2606 OID 41414)
-- Dependencies: 1556 1557 1864
-- Name: ExecutedExperiment; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "ExecutionStatistics"
    ADD CONSTRAINT "ExecutedExperiment" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1885 (class 2606 OID 41419)
-- Dependencies: 1556 1558 1864
-- Name: ExperimentConfiguration; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "ParallelConfiguration"
    ADD CONSTRAINT "ExperimentConfiguration" FOREIGN KEY ("ExperimentId") REFERENCES "Experiment"("ExperimentId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1882 (class 2606 OID 41424)
-- Dependencies: 1568 1876 1556
-- Name: ExperimentOwner; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "ExperimentOwner" FOREIGN KEY ("OwnerId") REFERENCES "User"("UserId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1881 (class 2606 OID 41429)
-- Dependencies: 1555 1568 1876
-- Name: OwnerPK; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Application"
    ADD CONSTRAINT "OwnerPK" FOREIGN KEY ("OwnerId") REFERENCES "User"("UserId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1887 (class 2606 OID 41434)
-- Dependencies: 1564 1559 1870
-- Name: ParameterType; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "InputParameters"
    ADD CONSTRAINT "ParameterType" FOREIGN KEY ("ParameterTypeId") REFERENCES "ParameterType"("TypeId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1883 (class 2606 OID 41439)
-- Dependencies: 1556 1862 1555
-- Name: UsedApplication; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Experiment"
    ADD CONSTRAINT "UsedApplication" FOREIGN KEY ("ApplicationId") REFERENCES "Application"("ApplicationId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1888 (class 2606 OID 41444)
-- Dependencies: 1568 1560 1872
-- Name: UserRole; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "User"
    ADD CONSTRAINT "UserRole" FOREIGN KEY ("RoleId") REFERENCES "Role"("RoleId") ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- TOC entry 1903 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2011-05-11 21:48:16 CST

--
-- PostgreSQL database dump complete
--

