echo "Removing Chart.lock..."
rm app/Chart.lock
echo "Removed Chart.lock"

echo "--------------------"

sh build_dependencies.sh

echo "--------------------"

echo "Uninstalling release..."
helm uninstall doc-manager
echo "Release uninstalled"

echo "--------------------"

echo "Installing new release..."
helm install doc-manager app --debug
echo "Done"
