#!/usr/bin/env bash
if [ $# -lt 1 ]
then
  echo usage: $0 RDS-endpoint
  exit 1
fi

RDS_ENDPOINT=$1
createuser -d -U eksdbadmin -P -h $RDS_ENDPOINT mywork
