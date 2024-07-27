# RabbitMQ Helm Chart

This chart was based on the one in [this link](https://github.com/marcel-dempers/docker-development-youtube-series/tree/master/messaging/rabbitmq/kubernetes).

After setting up the 3 replicas, you need to go inside one of the replicas using this command:

```bash
kubectl exec -it rabbitmq-0 bash
```

and execute this command to set up automatic synchronization between the nodes:

```bash
rabbitmqctl set_policy ha-fed \
    ".*" '{"federation-upstream-set":"all", "ha-sync-mode":"automatic", "ha-mode":"nodes", "ha-params":["rabbit@rabbitmq-0.rabbitmq.rabbits.svc.cluster.local","rabbit@rabbitmq-1.rabbitmq.rabbits.svc.cluster.local","rabbit@rabbitmq-2.rabbitmq.rabbits.svc.cluster.local"]}' \
    --priority 1 \
    --apply-to queues
```
