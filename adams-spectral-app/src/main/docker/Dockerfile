# Creates a docker image that runs two ADAMS background processes for the
# the following two flows:
# - adams-rabbitmq-execute_jobs-envvars.flow (port 12345)
# - adams-rabbitmq-execute_flow-envvars.flow (port 12346)
# These flows connect to a RabbitMQ server using PLAIN protocol.
# The flows require the following environment variables as minimum:
# - RABBITMQ_HOST
# - RABBITMQ_USER
# - RABBITMQ_PW
# Optional environment variables:
# - RABBITMQ_PORT
# - RABBITMQ_VHOST
# - RABBITMQ_QUEUE

# base image
FROM ubuntu:18.04

# install java and wget
RUN apt-get -y update \
    && apt-get -y upgrade \
    && apt-get -y install openjdk-11-jre-headless \
    && apt-get -y install wget bash \
    && rm -rf /var/lib/apt/lists/*

# install ADAMS snapshot
# using "--force-all" since we are installing only "openjdk-11-jre-headless" and not "openjdk-11-jre"
RUN wget -O /tmp/adams-spectral-app-snapshot.deb https://adams.cms.waikato.ac.nz/snapshots/adams/adams-spectral-app-snapshot.deb \
    && dpkg -i --force-all /tmp/adams-spectral-app-snapshot.deb \
    && rm /tmp/adams-spectral-app-snapshot.deb

# install flows
WORKDIR /opt/adams-spectral-app
RUN wget -O execute_jobs.flow https://raw.githubusercontent.com/waikato-datamining/adams-addons/master/adams-rabbitmq/src/main/flows/adams-rabbitmq-execute_jobs-envvars.flow \
    && wget -O execute_flow.flow https://raw.githubusercontent.com/waikato-datamining/adams-addons/master/adams-rabbitmq/src/main/flows/adams-rabbitmq-execute_flow-envvars.flow

# start flows
# use "tail -f /dev/null" to stop from exiting and see output
CMD ["bash", "-c", "adams-spectral-app-daemon start 4g /opt/adams-spectral-app/execute_jobs.flow 12345 ; adams-spectral-app-daemon start 4g /opt/adams-spectral-app/execute_flow.flow 12346 ; tail -f /dev/null" ]
