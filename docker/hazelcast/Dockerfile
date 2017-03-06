FROM hazelcast/hazelcast:latest
# Add your custom hazelcast.xml
ADD hazelcast.xml $HZ_HOME
ADD cache-api-1.0.0.jar $HZ_HOME

ENV CLASSPATH $HZ_HOME/cache-api-1.0.0.jar:$CLASSPATH/*

# Run hazelcast
CMD ./server.sh