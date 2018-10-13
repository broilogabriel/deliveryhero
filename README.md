# Delivery Hero Restaurant REST Service
> Self contained API service for CRUD operations on Restaurants.

This service is built using AkkaHttp

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
docker run --rm -p 8080:8080 --name deliveryhero-rest com.deliveryhero/restservice:latest  
```

## Usage example

This API is documented using Swagger. Please visit the following URL for details

```sh
http://localhost:8080/swagger/index.html
```

### Examples
#### Healthcheck
GET /v1/healthcheck
```sh
curl -X GET "http://localhost:8080/v1/healthcheck" -H "accept: */*"
```
#### Restaurants
GET /v1/restaurants
> Gets the list of all stored available restaurants

```sh
curl -X GET "http://localhost:8080/v1/restaurants" -H "accept: */*"
```

GET /v1/restaurants/{id}
> Gets a given restaurant

```sh
curl -X GET "http://localhost:8080/v1/restaurants/0" -H "accept: */*"
```

POST /v1/restaurants
> Create a restaurant
```sh
curl -X POST "http://localhost:8080/v1/restaurants" -H "accept: */*" -H "Content-Type: */*" -d "{\"name\":\"string\",\"phoneNo\":\"string\",\"cuisines\":[\"string\"],\"address\":{\"line1\":\"string\",\"line2\":\"string\",\"city\":\"string\",\"country\":\"string\"},\"description\":\"string\"}"
```

PUT /v1/restaurants
> Updates a restaurant

```sh
curl -X PUT "http://localhost:8080/v1/restaurants/0" -H "accept: */*" -H "Content-Type: */*" -d "{\"name\":\"string\",\"phoneNo\":\"string\",\"cuisines\":[\"string\"],\"address\":{\"line1\":\"string\",\"line2\":\"string\",\"city\":\"string\",\"country\":\"string\"},\"description\":\"string\"}"
```

DELETE /v1/restaurants
> Delete a restaurant

```sh
curl -X DELETE "http://localhost:8080/v1/restaurants/0" -H "accept: */*"
```