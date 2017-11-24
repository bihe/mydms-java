@echo off
java -jar target/mydms-1.0-SNAPSHOT.jar importData --tags ./import/tags.json --senders ./import/senders.json --documents ./import/documents.json --delete-database-contents true mydms.yml
