#!/bin/bash
if git config --get remote.origin.url 2>&1 | grep -F github.com/zhanhb/ckfinder-spring-boot -q && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  ./mvnw -B -Duser.name=zhanhb clean deploy -s .travis/settings.xml $*
else
  ./mvnw -B -Duser.name=zhanhb install $*
fi

