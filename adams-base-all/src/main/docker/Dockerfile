# Creates a docker image: debian package of adams-base-all

# base image
ARG DOCKER_REGISTRY=public.aml-repo.cms.waikato.ac.nz:443/
FROM ${DOCKER_REGISTRY}ubuntu:22.04

# install java, wget and bash
RUN    apt-get -y update \
    && apt-get -y upgrade \
    && apt-get -y install openjdk-17-jdk \
    && apt-get -y install wget bash \
    && rm -rf /var/lib/apt/lists/*

# install ADAMS snapshot
RUN wget -O /tmp/adams-base-all-snapshot.deb https://adams.cms.waikato.ac.nz/snapshots/adams/adams-base-all-snapshot.deb \
    && dpkg -i /tmp/adams-base-all-snapshot.deb \
    && rm /tmp/adams-base-all-snapshot.deb

RUN mkdir /.cache && chmod 777 /.cache
COPY bash.bashrc /etc/bash.bashrc
