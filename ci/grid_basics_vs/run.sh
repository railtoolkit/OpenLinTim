#!/usr/bin/env bash
set -e

# Enter your data here
TARGET_DATASET=grid
COMMANDS_TO_RUN="make lc-line-concept && make ean && make tim-timetable && make ro-rollout && make ro-trips && make ro-trips-evaluate && make vs-vehicle-schedules && make vs-vehicle-schedules-evaluate"

# STATIC PART OF THE SCRIPT
SCRIPT_LOCATION=`dirname $(readlink -f ${BASH_SOURCE[0]})`
echo ------------------------Executing test `basename ${SCRIPT_LOCATION}`
TIMESTAMP=`date +"%Y-%m-%d_%H-%M-%S"`
TARGET_LOCATION=../../datasets/${TARGET_DATASET}_${TIMESTAMP}
BASE_TARGET_LOCATION=../../datasets/${TARGET_DATASET}
cp -r ${BASE_TARGET_LOCATION} ${TARGET_LOCATION}
cp -r * ${TARGET_LOCATION}
cd ${TARGET_LOCATION}
eval ${COMMANDS_TO_RUN}
python3 ${SCRIPT_LOCATION}/../util/evaluate_statistics.py ${SCRIPT_LOCATION}/expected-statistic.sta statistic/statistic.sta
cd ${SCRIPT_LOCATION}
rm -rf ${TARGET_LOCATION}