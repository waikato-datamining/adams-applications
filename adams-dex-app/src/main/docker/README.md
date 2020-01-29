# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-dex-app:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-dex-app:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-dex-app:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-dex-app:latest
```

Docker hub:

```
docker push theadamsflow/adams-dex-app:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-dex-app:latest 
```
