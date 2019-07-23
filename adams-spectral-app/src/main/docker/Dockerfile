# Creates a docker image that runs the following ADAMS flow:
#   adams-rabbitmq-rats-execute_flow_and_jobs-envvars.flow
# These flows connect to a RabbitMQ server using PLAIN protocol.
# The flows require the following environment variables as minimum:
# - RABBITMQ_HOST
# - RABBITMQ_USER
# - RABBITMQ_PW
# Optional environment variables:
# - RABBITMQ_PORT
# - RABBITMQ_VHOST
# - RABBITMQ_QUEUE_FLOW
# - RABBITMQ_QUEUE_JOBS

# base image
FROM ubuntu:18.04

# install java and wget
RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get -y install openjdk-11-jre-headless \
    && apt-get -y install wget bash \
    && rm -rf /var/lib/apt/lists/*

# install ADAMS snapshot
RUN wget -O /tmp/adams-spectral-app-snapshot.deb https://adams.cms.waikato.ac.nz/snapshots/adams/adams-spectral-app-snapshot.deb \
    && dpkg -i /tmp/adams-spectral-app-snapshot.deb \
    && rm /tmp/adams-spectral-app-snapshot.deb

# install flows
WORKDIR /opt/adams-spectral-app
RUN wget -O execute_flow_and_jobs.flow https://raw.githubusercontent.com/waikato-datamining/adams-addons/master/adams-rabbitmq-rats/src/main/flows/adams-rabbitmq-rats-execute_flow_and_jobs-envvars.flow

# start flows
CMD ["bash", "-c", "adams-spectral-app-exec -headless -memory 4g -input /opt/adams-spectral-app/execute_flow_and_jobs.flow" ]
