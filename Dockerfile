FROM openjdk:8-alpine

# Install tools
RUN apk --no-cache add bash iproute2

# API is listening here.
EXPOSE 5050

ARG PROJECT_NAME=app
ARG PROJECT_VERSION=1.4.0
ARG DIST_NAME=$PROJECT_NAME-$PROJECT_VERSION

COPY build/distributions/$DIST_NAME.tar /
RUN tar -xvf $DIST_NAME.tar
RUN rm $DIST_NAME.tar
RUN mv /$DIST_NAME /$PROJECT_NAME

COPY $PROJECT_NAME/config.json /$PROJECT_NAME/

WORKDIR /$PROJECT_NAME
ENTRYPOINT ["bash", "bin/app"]
