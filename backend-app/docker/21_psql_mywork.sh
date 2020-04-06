#!/usr/bin/env bash
docker run \
 -it \
 --rm \
 --name my-work-psql \
 --network my-work-network \
 -v /tmp/work/scripts:/scripts \
 postgres:10.6 \
 psql -h my-work-pg -U mywork myworkdb
