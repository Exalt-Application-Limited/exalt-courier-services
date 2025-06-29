#!/bin/bash
# Fix all javax imports to jakarta in courier services

echo "Fixing all javax imports to jakarta..."

# Fix javax.persistence to jakarta.persistence
find . -name "*.java" -type f -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} +

# Fix javax.validation to jakarta.validation
find . -name "*.java" -type f -exec sed -i 's/import javax\.validation\./import jakarta.validation./g' {} +

# Fix javax.servlet to jakarta.servlet
find . -name "*.java" -type f -exec sed -i 's/import javax\.servlet\./import jakarta.servlet./g' {} +

# Fix javax.annotation to jakarta.annotation
find . -name "*.java" -type f -exec sed -i 's/import javax\.annotation\./import jakarta.annotation./g' {} +

echo "Import fixes completed!"