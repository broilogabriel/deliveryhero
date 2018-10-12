# Delivery Hero Restaurant REST Service
> Self contained API service for CRUD operations on Restaurants.

This service is built using the following technologies:

![](https://avatars2.githubusercontent.com/u/7225556?s=280&v=4)

## Installation

Clone this repo

```sh
git clone https://github.com/smur89/deliveryhero.git
```

Create the docker image

```sh
gradle buildDocker
```

Run the docker image

```sh
docker run -p 8080:8080 com.deliveryhero/restservice:latest  
```

## Usage example

This API is documented using Swagger. Please visit the following URL for details

```sh
http://localhost:8080/swagger/index.html
```