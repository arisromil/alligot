ALTER TABLE SEQUENCE ADD CONSTRAINT SEQUENCE_SEQNAME_PK PRIMARY KEY (SEQ_NAME);

--//@UNDO

ALTER TABLE SEQUENCE DROP CONSTRAINT SEQUENCE_SEQNAME_PK;