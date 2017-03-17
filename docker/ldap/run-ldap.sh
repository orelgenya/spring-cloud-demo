#!/usr/bin/env bash
docker run -it --rm \
    -p 10389:10389 \
    -v /Users/orelgenya/workspace/spring-cloud-demo/docker/ldap:/mnt \
    kwart/ldap-server \
    java -jar ldap-server.jar /mnt/custom.ldif