# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-ml-app:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-ml-app:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-ml-app:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-ml-app:latest
```

Docker hub:

```
docker push theadamsflow/adams-ml-app:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-ml-app:latest 
```
