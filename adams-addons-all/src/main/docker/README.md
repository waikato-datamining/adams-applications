# Docker

## Build

**NB:** Downloads the .deb snapshot rather than building it. 

```
docker build -t theadamsflow/adams-addons-all:latest .
```

## Tag

```
docker tag \
  theadamsflow/adams-addons-all:latest \
  public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-addons-all:latest
```

## Push

Inhouse registry:

```
docker push public-push.aml-repo.cms.waikato.ac.nz:443/theadamsflow/adams-addons-all:latest
```

Docker hub:

```
docker push theadamsflow/adams-addons-all:latest
```


## Run image

Start the image as follows:

```
docker run -it theadamsflow/adams-addons-all:latest 
```
