FROM openjdk:8-alpine

# Install tools
RUN apk --no-cache add bash iproute2

# Clear proxy.
ENV http_proxy=""
ENV https_proxy=""
ENV HTTP_PROXY=""
ENV HTTPS_PROXY=""

# API is listening here.
EXPOSE 5050

ARG PROJECT_NAME=app
ARG PROJECT_VERSION=1.4.0
ARG DIST_NAME=$PROJECT_NAME-$PROJECT_VERSION

COPY $PROJECT_NAME/build/distributions/$DIST_NAME.tar /
RUN tar -xvf $DIST_NAME.tar && \
    rm $DIST_NAME.tar && \
    mv /$DIST_NAME /$PROJECT_NAME

COPY $PROJECT_NAME/config.json /$PROJECT_NAME/

WORKDIR /$PROJECT_NAME
ENTRYPOINT ["bash", "bin/app"]
