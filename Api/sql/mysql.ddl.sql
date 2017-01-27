--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: migrations.xml
--  Ran at: 1/27/17 4:27 PM
--  *********************************************************************

CREATE TABLE TAGS (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_TAGS PRIMARY KEY (id));

ALTER TABLE TAGS ADD CONSTRAINT tag_name UNIQUE (name);

CREATE TABLE SENDERS (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_SENDERS PRIMARY KEY (id));

ALTER TABLE SENDERS ADD CONSTRAINT sender_name UNIQUE (name);

CREATE TABLE DOCUMENTS (id CHAR(36) NOT NULL, title NVARCHAR(255) NOT NULL, filename NVARCHAR(255) NOT NULL, alternativeid VARCHAR(128) NULL, previewlink VARCHAR(128) NULL, amount numeric NULL, created datetime DEFAULT NOW() NOT NULL, modified datetime NULL, taglist TEXT NULL, senderlist TEXT NULL, CONSTRAINT PK_DOCUMENTS PRIMARY KEY (id));

ALTER TABLE DOCUMENTS ADD CONSTRAINT alternativeid_unique UNIQUE (alternativeid);

CREATE TABLE UPLOADS (id CHAR(36) NOT NULL, filename NVARCHAR(255) NOT NULL, mimetype NVARCHAR(255) NOT NULL, created datetime DEFAULT NOW() NOT NULL, CONSTRAINT PK_UPLOADS PRIMARY KEY (id));

CREATE TABLE DOCUMENTS_TO_TAGS (document_id CHAR(36) NOT NULL, tag_id BIGINT NOT NULL, 
	CONSTRAINT `fk_tag_document_id` FOREIGN KEY (tag_id) REFERENCES TAGS(id) ON DELETE CASCADE, 
	CONSTRAINT `fk_document_tag_id` FOREIGN KEY (document_id) REFERENCES DOCUMENTS(id) ON DELETE CASCADE);

ALTER TABLE DOCUMENTS_TO_TAGS ADD PRIMARY KEY (document_id, tag_id);

CREATE TABLE DOCUMENTS_TO_SENDERS (document_id CHAR(36) NOT NULL, sender_id BIGINT NOT NULL, 
	CONSTRAINT `fk_document_sender_id` FOREIGN KEY (document_id) REFERENCES DOCUMENTS(id) ON DELETE CASCADE, 
    CONSTRAINT `fk_sender_document_id` FOREIGN KEY (sender_id) REFERENCES SENDERS(id) ON DELETE CASCADE);

ALTER TABLE DOCUMENTS_TO_SENDERS ADD PRIMARY KEY (document_id, sender_id);

