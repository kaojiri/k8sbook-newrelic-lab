#!/usr/bin/env bash
docker run \
 -d \
 --name my-work-pg \
 -e POSTGRES_PASSWORD=myworkpg \
 -e PGDATA=/data \
 -v /tmp/work/data:/data \
 -p 5432:5432 \
 --network my-work-network \
 postgres:10.6

