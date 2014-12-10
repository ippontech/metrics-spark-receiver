# DOCKER-VERSION 1.3.2
FROM ubuntu:14.04
MAINTAINER Antoine Hars <ahars@ippon.fr>

# make sure the package repository is up to date
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list
RUN apt-get -y update

# install python-software-properties (so you can do add-apt-repository)
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-software-properties software-properties-common

# install oracle java from PPA & other tools
RUN add-apt-repository ppa:webupd8team/java -y
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get update && apt-get install -y \
    sudo \
    git \
    maven \
    curl \
    oracle-java8-installer && \
    apt-get clean

# Set oracle java as the default java
RUN update-java-alternatives -s java-8-oracle
RUN echo "export JAVA_HOME=/usr/lib/jvm/java-8-oracle" >> ~/.bashrc

# Install Spark 1.1.1
RUN curl http://d3kbcqa49mib13.cloudfront.net/spark-1.1.1-bin-hadoop2.4.tgz | tar xvz

# install the metrics-spark-receiver
RUN git clone https://github.com/ahars/metrics-spark.git
RUN cd metrics-spark/ && \
    mvn package

EXPOSE 8080 7077 9999

CMD spark-1.1.1-bin-hadoop2.4/bin/spark-submit \
    --class "fr.ippontech.metrics.spark.streaming.MetricsToConsole" \
    metrics-spark/target/metrics-spark-1.0-SNAPSHOT.jar
