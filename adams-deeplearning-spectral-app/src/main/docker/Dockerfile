# Creates a docker image: debian package of adams-deeplearning-spectral-app

# base image
ARG DOCKER_REGISTRY=public.aml-repo.cms.waikato.ac.nz:443/
FROM ${DOCKER_REGISTRY}ubuntu:18.04

# install java, wget and bash
ARG UBUNTU_MIRROR=mirror.fsmg.org.nz
RUN mv /etc/apt/sources.list /etc/apt/sources.list.original \
    && cat /etc/apt/sources.list.original | sed s/"archive.ubuntu.com"/"$UBUNTU_MIRROR"/g > /etc/apt/sources.list \
    && apt-get -y update \
    && apt-get -y upgrade \
    && apt-get -y install openjdk-11-jre-headless \
    && apt-get -y install wget bash \
    && rm -rf /var/lib/apt/lists/*

# install ADAMS snapshot
RUN wget -O /tmp/adams-deeplearning-spectral-app-snapshot.deb https://adams.cms.waikato.ac.nz/snapshots/adams/adams-deeplearning-spectral-app-snapshot.deb \
    && dpkg -i /tmp/adams-deeplearning-spectral-app-snapshot.deb \
    && rm /tmp/adams-deeplearning-spectral-app-snapshot.deb

COPY bash.bashrc /etc/bash.bashrc
COPY Placeholders.props /root/.adams/Placeholders.props
