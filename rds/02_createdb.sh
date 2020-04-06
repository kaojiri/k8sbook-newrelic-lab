#!/usr/bin/env bash
if [ $# -lt 1 ]
then
  usage: $0 RDS-endpoint
  exit 1
fi

RDS_ENDPOINT=$1
createdb -U mywork -h $RDS_ENDPOINT -E UTF8 myworkdb
