# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-deeplearning-spectral-app:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-deeplearning-spectral-app:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-deeplearning-spectral-app:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-deeplearning-spectral-app:latest
```

Docker hub:

```
docker push theadamsflow/adams-deeplearning-spectral-app:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-deeplearning-spectral-app:latest 
```
