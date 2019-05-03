#-------------------------------------------------------------------------------------------------------------
# Copyright (c) Microsoft Corporation. All rights reserved.
# Licensed under the MIT License. See https://go.microsoft.com/fwlink/?linkid=2090316 for license information.
#-------------------------------------------------------------------------------------------------------------

FROM openjdk:8-jdk

# Configure apt
ENV DEBIAN_FRONTEND=noninteractive

# Verify git, needed tools installed
RUN apt-get update \
    && apt-get -y install git procps curl

# Install sbt-extras
RUN curl -Ls https://git.io/sbt > /usr/bin/sbt && chmod 0755 /usr/bin/sbt

# Clean up
RUN apt-get autoremove -y \
    && apt-get clean -y \
    && rm -rf /var/lib/apt/lists/*
ENV DEBIAN_FRONTEND=dialog

# Copy endpoint specific user settings into container to specify Java path
COPY settings.vscode.json /root/.vscode-remote/data/Machine/settings.json
