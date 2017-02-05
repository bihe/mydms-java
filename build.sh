#!/bin/sh

cd ./Frontend
echo $PWD
npm run build.prod -- --base /ui/
cd ../
echo $PWD
cp -R ./Frontend/dist/prod/* ./Api/src/main/resources/assets/ui/
cd ./Api
mvn clean package
