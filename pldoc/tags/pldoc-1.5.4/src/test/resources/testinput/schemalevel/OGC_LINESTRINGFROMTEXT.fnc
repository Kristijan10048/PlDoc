
  CREATE OR REPLACE FUNCTION "MDSYS"."OGC_LINESTRINGFROMTEXT" (
  wkt   IN VARCHAR2,
  srid  IN INTEGER DEFAULT NULL)
    RETURN ST_LineString IS
BEGIN
  RETURN TREAT(ST_GEOMETRY.FROM_WKT(wkt, srid) AS ST_LineString);
END OGC_LineStringFromText;

