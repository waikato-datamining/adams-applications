# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-base-all:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-base-all:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-base-all:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-base-all:latest
```

Docker hub:

```
docker push theadamsflow/adams-base-all:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-base-all:latest 
```
