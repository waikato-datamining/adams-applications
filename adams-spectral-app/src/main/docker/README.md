# Docker

## Build

From within the `docker` directory:

```
docker build .
```

## Run image

Start the image as follows:

```
docker run -i -t  \
  -e RABBITMQ_HOST='HOST' \
  -e RABBITMQ_USER='USER' \
  -e RABBITMQ_PW='PASSWORD' \ 
  <imageid>
```
