CREATE OR REPLACE PACKAGE pldoc_bug.TESTCONSTANTS_3512150 IS

map CONSTANT VARCHAR2(4) := 'map';

END;
/

CREATE OR REPLACE PACKAGE BODY pldoc_bug.TEST_API_3512150 IS

PROCEDURE Test 
IS

BEGIN

dbms_output.put_line (TESTCONSTANTS_3512150.map);

END Test;

END;
/

