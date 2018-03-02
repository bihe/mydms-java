#!/bin/sh

OUTPUT="target/outdated.txt"
mkdir -p target

./mvnw -q versions:display-dependency-updates -Dversions.outputFile="$OUTPUT" 

OUTDATED_LIBRARIES=`grep . "$OUTPUT" | tail -n +2 | wc -l`

echo "There is $OUTDATED_LIBRARIES outdated libraries!\n"
printf '%b\n\n' "$(cat target/outdated.txt)"
exit $OUTDATED_LIBRARIES
