# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-spectral-app:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-spectral-app:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-spectral-app:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-spectral-app:latest
```

Docker hub:

```
docker push theadamsflow/adams-spectral-app:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-spectral-app:latest 
```
