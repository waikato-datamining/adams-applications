# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

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

Or, if you want to change the memory:

```
docker run -i -t  \
  -e RABBITMQ_HOST='HOST' \
  -e RABBITMQ_USER='USER' \
  -e RABBITMQ_PW='PASSWORD' \ 
  -e MEMORY='4g' \
  <imageid>
```
