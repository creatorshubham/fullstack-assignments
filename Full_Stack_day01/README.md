# Shubham's Order Processing Platform

Small Python-based order processing platform with three services:

- **Order API**: public API for creating orders.
- **Order Processor**: internal service that processes orders.
- **Notification Service**: internal service that queues order notifications.

The request flow is:

```text
Client -> Order API -> Order Processor -> Notification Service
```

Each service is a small Python HTTP application with its own Dockerfile. Basic unit tests cover order creation, processing, and notification payloads.

## Project ownership

This version is maintained by **Shubham Chaubey**. The public Order API identifies
the application as **Shubham's Order Platform** (version `1.0.0`) from its
`GET /` endpoint.

## Project structure

```text
order-api/             Order API and Dockerfile
order-processor/       Order Processor and Dockerfile
notification-service/  Notification Service and Dockerfile
tests/                 Unit tests
k8s.yaml               Kubernetes Deployments and Services
```

## Run tests

```bash
python3 -m unittest discover -s tests -v
```

On Windows, the equivalent command is:

```powershell
py -m unittest discover -s tests -v
```

## Build and deploy with Minikube

```bash
minikube start --driver=docker
eval "$(minikube docker-env)"

docker build -t order-api:latest ./order-api
docker build -t order-processor:latest ./order-processor
docker build -t notification-service:latest ./notification-service

python3 -m unittest discover -s tests -v

kubectl apply -f k8s.yaml
kubectl get pods,services
```

The `minikube docker-env` command makes the images available directly to the Minikube cluster, so Kubernetes does not need to pull them from a registry.

Check that all three pods are ready:

```bash
kubectl get pods
```

## Use the Order API

Opening the service URL in a browser sends `GET /`, which returns available endpoints:

```bash
minikube service order-api --url
```

Create an order with `POST /orders`:

```bash
ORDER_API_URL=$(minikube service order-api --url)
curl -X POST "$(minikube service order-api --url)/orders" \
  -H 'Content-Type: application/json' \
  -d '{"item":"keyboard","quantity":2}'
```

Health check:

```bash
curl "$ORDER_API_URL/health"
```

Order Processor and Notification Service are internal `ClusterIP` services and are called automatically by the Order API.

## Troubleshooting

If a browser shows `{"error":"not found"}`, it is likely using an older pod image or an unsupported path. Restart the API deployment after rebuilding:

```bash
docker build -t order-api:latest ./order-api
minikube image load order-api:latest
kubectl rollout restart deployment/order-api
kubectl rollout status deployment/order-api
```

## Clean up

```bash
kubectl delete -f k8s.yaml
```