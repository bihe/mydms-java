@echo off

cd ./UI
call yarn install 
call npm run build -- --prod --bh /ui/
cd ../
rm ./Api/src/main/resources/static/ui/* -Force -Recurse
cp -R ./UI/dist/* ./Api/src/main/resources/static/ui/
cd ./Api
set MYDMS_TMP_PATH=./target
call ./mvnw.cmd clean package