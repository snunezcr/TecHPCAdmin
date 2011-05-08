-- Function: getexperimentstatistics(integer)

-- DROP FUNCTION getnodestatistics(integer);

CREATE OR REPLACE FUNCTION getnodestatistics(executionId integer)
  RETURNS SETOF "NodeStatistics" AS
$BODY$
SELECT "NodeStatisticsId", "ExecutionId", "NodeNumber", "TotalTime", "UsedMemory", "CpuUsage"
	FROM  "NodeStatistics" WHERE "ExecutionId" = $1
$BODY$
  LANGUAGE sql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION getnodestatistics(integer) OWNER TO postgres;
