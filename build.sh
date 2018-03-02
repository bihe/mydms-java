#!/bin/sh

RED='\033[0;31m'
NC='\033[0m' # No Color

cd ./UI
echo $PWD
yarn install
if [ $? -eq 0 ]; then
	npm run build -- --prod --bh /ui/
	cd ../
	if [ $? -eq 0 ]; then
		echo $PWD
		cp -R ./UI/dist/* ./Api/src/main/resources/static/ui/
		if [ $? -eq 0 ]; then
			cd ./Api
			./mvnw clean package
		else
			echo -e "${RED}Could not copy frontend files!${NC}"
		fi
	else
		echo "${RED}Could not build frontend!${NC}"
	fi
else
	echo "${RED}Error during UI package installation!${NC}"
fi

