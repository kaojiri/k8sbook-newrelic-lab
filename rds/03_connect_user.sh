#!/usr/bin/env bash
if [ $# -lt 1 ]
then
  echo usage $0 RDS-endpoint
  exit 1
fi

RDS_ENDPOINT=$1
psql -U mywork -h $RDS_ENDPOINT myworkdb
