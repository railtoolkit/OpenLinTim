PROGRAMPATH=`dirname $0`
PYTHON_CORE_PATH=${PROGRAMPATH}/../../../core/python
PYTHONPATH="${PROGRAMPATH}/src:${PYTHON_CORE_PATH}"
env PYTHONPATH=${PYTHON_CORE_PATH}:${PYTHONPATH} python3 ${PROGRAMPATH}/execute.py
