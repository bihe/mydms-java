@echo off

cd ./UI
call npm install 
call npm run build -- --prod --bh /ui/
cd ../
rm ./Api/src/main/resources/assets/ui/* -Force -Recurse
cp -R ./UI/dist/* ./Api/src/main/resources/assets/ui/
cd ./Api
call mvn clean package
