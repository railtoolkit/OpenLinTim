#!/bin/bash

PROGRAMPATH=`dirname $0`

source ${PROGRAMPATH}/../base.sh

SL_CONCEPT=`${CONFIGCMD} -s sl_model -u`
JGRAPHT_JAR_FILE=${PROGRAMPATH}/../../libs/jgrapht/jgrapht-core-1.1.0.jar

if [[ ${SL_CONCEPT} == "dsl" ]]; then
	ant -q -f ${PROGRAMPATH}/src/build.xml build-stop-location
	java -cp ${CLASSPATH}${PATHSEP}${PROGRAMPATH}/src${PATHSEP}${PROGRAMPATH}/../essentials/config${PATHSEP}${JGRAPHT_JAR_FILE}${PATHSEP}${PROGRAMPATH}/../essentials/sl-helper/PTNTools SolveDSL "basis/Config.cnf"

elif [[ ${SL_CONCEPT} == "greedy" ]]; then
	ant -q -f ${PROGRAMPATH}/src/build.xml build-stop-location
	java -cp ${CLASSPATH}${PATHSEP}${PROGRAMPATH}/src${PATHSEP}${PROGRAMPATH}/../essentials/config${PATHSEP}${JGRAPHT_JAR_FILE}${PATHSEP}${PROGRAMPATH}/../essentials/sl-helper/PTNTools SolveGreedy "basis/Config.cnf"

elif [[ ${SL_CONCEPT} == "dsl-tt" ]]; then
	ant -q -f ${PROGRAMPATH}/src/build.xml build-stop-location
	java ${JFLAGS} -cp ${CLASSPATH}${PATHSEP}${PROGRAMPATH}/src${PATHSEP}${PROGRAMPATH}/../essentials/config${PATHSEP}${JGRAPHT_JAR_FILE}${PATHSEP}${PROGRAMPATH}/../essentials/sl-helper/PTNTools SolveDSLTT "basis/Config.cnf"

elif [[ ${SL_CONCEPT} == "dsl-tt-2" ]]; then
	ant -q -f ${PROGRAMPATH}/src/build.xml build-stop-location
	java ${JFLAGS} -cp ${CLASSPATH}${PATHSEP}${PROGRAMPATH}/src${PATHSEP}${PROGRAMPATH}/../essentials/config${PATHSEP}${JGRAPHT_JAR_FILE}${PATHSEP}${PROGRAMPATH}/../essentials/sl-helper/PTNTools SolveDSLTT2 "basis/Config.cnf"

else
	echo "Error: Requested SL_CONCEPT \"${SL_CONCEPT}\" not available!"
	exit 1
fi

EXITSTATUS=$?

exit ${EXITSTATUS}