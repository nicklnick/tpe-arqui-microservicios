# Uninstall Helm Releases
helm uninstall arqui-app -n default
helm uninstall istiod -n istio-system
helm uninstall istio-base -n istio-system

# Remove Kubernetes Resources Applied via Hooks
kubectl delete -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/kiali.yaml
kubectl delete -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/prometheus.yaml
kubectl delete -f https://raw.githubusercontent.com/istio/istio/release-1.22/samples/addons/jaeger.yaml

# Remove Namespace Label
kubectl label namespace default istio-injection

# Remove Istio System Namespace (Optional)
kubectl delete namespace istio-system
kubectl delete namespace istio-ingress
