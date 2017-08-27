#!/bin/sh

cd ./UI
echo $PWD
npm run build -- --prod --bh /ui/
cd ../
echo $PWD
cp -R ./UI/dist/* ./Api/src/main/resources/assets/ui/
cd ./Api
mvn clean package
