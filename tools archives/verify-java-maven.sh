#!/bin/bash
echo "Checking Java installation..."
java -version
echo ""
echo "Checking Maven installation..."
mvn -version
echo ""
echo "If you see version numbers above, the installation was successful!"
echo "If you see 'command not found', the installation needs to be retried."