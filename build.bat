@echo off

cd ./Frontend
call npm install 
call npm run build.prod -- --base /ui/
cd ../
cp -R ./Frontend/dist/prod/* ./Api/src/main/resources/assets/ui/
cd ./Api
call mvn clean package
