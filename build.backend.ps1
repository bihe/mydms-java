rm ./Api/src/main/resources/static/ui/* -Force -Recurse
copy -R ./UI/dist/* ./Api/src/main/resources/static/ui/
cd ./Api
set MYDMS_TMP_PATH=./target
./mvnw.cmd clean package
cd ..