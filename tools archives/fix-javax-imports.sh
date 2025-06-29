#!/bin/bash
# Fix javax imports to jakarta in courier services

echo "Fixing javax imports to jakarta..."

# Find all Java files and replace javax.persistence with jakarta.persistence
find . -name "*.java" -type f -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} +

# Find all Java files and replace javax.validation with jakarta.validation
find . -name "*.java" -type f -exec sed -i 's/import javax\.validation\./import jakarta.validation./g' {} +

echo "Import fixes completed!"