. .\_environment.ps1

docker run -d -p 8000:8080 `
-e "MYDMS_BASE_URL=http://localhost:8000/" `
-e MYDMS_JWT_TOKEN_SECRET `
-e MYDMS_DB_USER `
-e MYDMS_DB_PASS `
-e MYDMS_DB_HOST `
-e MYDMS_DB_PORT `
-e MYDMS_DB_NAME `
-e MYDMS_AWS_ACCESS_KEY `
-e MYDMS_AWS_SECRET_KEY `
-e MYDMS_AWS_BUCKET_NAME `
-e "MYDMS_STORE_PATH=/tmp" `
-e "MYDMS_TMP_PATH=/tmp" `
-e MYDMS_DETAILED_ERRORS `
-e SPRING_PROFILES_ACTIVE `
mydms