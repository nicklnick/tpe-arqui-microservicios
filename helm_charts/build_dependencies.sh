#!/bin/bash

# Function to rebuild dependencies in a given directory
rebuild_dependencies() {
    local chart_dir=$1

    echo "Processing $chart_dir..."

    # Delete existing dependencies
    rm -rf "$chart_dir/charts/*"

    # Rebuild dependencies
    helm dependency build "$chart_dir"

    echo "Dependencies rebuilt for $chart_dir."
    echo "------------------------------------"
}

# Find all directories containing Chart.yaml
chart_dirs=$(find . -name "Chart.yaml" -exec dirname {} \;)

# Iterate over each chart directory and rebuild dependencies
for chart_dir in $chart_dirs; do
    rebuild_dependencies "$chart_dir"
done

echo "All dependencies rebuilt successfully."

