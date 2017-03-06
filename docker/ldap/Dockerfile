FROM kwart/ldap-server:latest
VOLUME /tmp
ADD custom.ldif
ENTRYPOINT [ "sh", "-c", "java -jar ldap-server.jar /tmp/custom.ldif" ]