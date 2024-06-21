# Deploying a Helm Chart

1. Get the IP or FQDN of the **master** node

2. Make a local SSH tunnel to the **master** node.

    ```bash
    ssh -i <ssh_key> -L localhost:8080:localhost:8080 user@master_node
    ```

3. Inside the **master** node run:

    ```bash
    kubectl proxy --port=8080
    ```

4. Inside the computer with the ssh tunnel run:

    ```bash
    helm install <release-name> helm-chart
    ```

5. That's it!

If you want to uninstall the realease run `helm uninstall <release-name>`.