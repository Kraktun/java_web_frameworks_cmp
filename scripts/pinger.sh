#!/bin/bash

API_ENDPOINT="/api/v2/rsa/ping"
OUT_FILE=$1

while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' localhost:8080${API_ENDPOINT})" != "200" ]]; do sleep .0001; done

echo "End:" `(date +"%T.%3N")` >> $OUT_FILE