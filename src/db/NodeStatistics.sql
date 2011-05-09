-- Table: "NodeStatistics"

 DROP TABLE "NodeStatistics";

CREATE TABLE "NodeStatistics"
(
  "NodeStatisticsId" bigserial NOT NULL,
  "ExecutionId" integer NOT NULL,
  "NodeNumber" integer NOT NULL,
  "TotalTime" int NOT NULL,
  "UsedMemory" double precision NOT NULL,
  "CpuUsage" double precision NOT NULL,
  CONSTRAINT "NodePK" PRIMARY KEY ("NodeStatisticsId"),
  CONSTRAINT "BelongsToExecution" FOREIGN KEY ("ExecutionId")
      REFERENCES "ExecutionStatistics" ("ExecutionId") MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "NodeStatistics" OWNER TO postgres;
