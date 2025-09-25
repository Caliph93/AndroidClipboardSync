#!/usr/bin/env sh

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

set -e

# Attempt to set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME="/usr/lib/jvm/default-java"
fi

# Determine APP_HOME, the location of this script
APP_HOME="$(cd "`dirname "$0"`" && pwd -P)"

# Add default gradle options here
DEFAULT_JVM_OPTS=""

exec "$JAVA_HOME/bin/java" $DEFAULT_JVM_OPTS -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
