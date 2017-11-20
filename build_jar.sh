#!/bin/sh

# Make a clean build.
rm -rf lib/*
javac -d lib src/*.java

# Package the application.
jar -cmf Manifest.txt httpserver.jar -C lib httpserver