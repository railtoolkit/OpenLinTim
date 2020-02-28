#!/usr/bin/env bash
PROGRAMPATH=`dirname $0`
PYTHON_CORE_PATH=${PROGRAMPATH}/../../core/python
COMMON_PATH=${PROGRAMPATH}/../commons/
export PYTHONPATH="${PYTHONPATH}:${PROGRAMPATH}:${COMMON_PATH}:${PYTHON_CORE_PATH}"
python3 ${PROGRAMPATH}/solve.py $1