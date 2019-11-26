#!/bin/bash -x
#
# Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0,
# or the Eclipse Distribution License v. 1.0 which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
#

mvn clean install -DskipTests

GF_BUNDLE_URL="central.maven.org/maven2/org/glassfish/main/distributions/glassfish/5.1.0/glassfish-5.1.0.zip"
TCK_NAME=jsonb-tck
TCK_VERSION=1.0.1

export YASSON_HOME=`pwd`
export TCK_HOME=${YASSON_HOME}"/target-tck"
rm -r ${TCK_HOME}
mkdir ${TCK_HOME}
cd ${TCK_HOME}
TS_HOME=${TCK_HOME}/${TCK_NAME}

echo "Downloading JSON-B TCK tests"
wget -q http://download.eclipse.org/ee4j/jakartaee-tck/jakartaee8-eftl/promoted/eclipse-${TCK_NAME}-$TCK_VERSION.zip
echo "Exporting downloaded TCK tests"
unzip -qq eclipse-${TCK_NAME}-*.zip -d ${TCK_HOME}

echo "Downloading GlassFish"
wget -q --no-cache ${GF_BUNDLE_URL} -O latest-glassfish.zip
echo "Exporting downloaded GlassFish"
unzip -qq ${TCK_HOME}/latest-glassfish.zip -d ${TCK_HOME}

cp -a ${YASSON_HOME}/target/yasson.jar ${TCK_HOME}/glassfish5/glassfish/modules/yasson.jar

cd ${TS_HOME}/bin

# Intentionally no space between "-i" and ".bak" for interop with both MacOS and Linux
sed -i.bak "s#^report.dir=.*#report.dir=${TS_HOME}report/${TCK_NAME}#g" ts.jte
sed -i.bak "s#^work.dir=.*#work.dir=${TS_HOME}work/${TCK_NAME}#g" ts.jte
sed -i.bak "s#jsonb\.classes=.*#jsonb.classes=${TCK_HOME}/glassfish5/glassfish/modules/jakarta.json.jar:${TCK_HOME}/glassfish5/glassfish/modules/jakarta.json.bind-api.jar:${TCK_HOME}/glassfish5/glassfish/modules/jakarta.json.jar:${TCK_HOME}/glassfish5/glassfish/modules/jakarta.inject.jar:${TCK_HOME}/glassfish5/glassfish/modules/jakarta.servlet-api.jar:${TCK_HOME}/glassfish5/glassfish/modules/yasson.jar#" ts.jte

# TCK test excludes
# JDK 11 have date formating changed
echo "com/sun/ts/tests/jsonb/api/annotation/AnnotationTest.java#testJsonbDateFormat_from_standalone" >> ts.jtx
# Signature test needs to run on JDK 1.8 . This allowes us to run on any JDK.
echo "com/sun/ts/tests/signaturetest/jsonb/JSONBSigTest.java#signatureTest_from_standalone" >> ts.jtx

mkdir -p ${TS_HOME}report/${TCK_NAME}
mkdir -p ${TS_HOME}work/${TCK_NAME}

# ant config.vi
cd ${TS_HOME}/src/com/sun/ts/tests/
#ant deploy.all
ant run.all | tee ${TCK_HOME}/result.log
export FAILED_COUNT=`grep -c "Finished Test:  FAILED" ${TCK_HOME}/result.log`

if [ "${FAILED_COUNT}" -gt "0" ]
then
        echo "FAILED TCK TESTS FOUND"
        exit 1
else
        echo "TCK OK"
        exit 0
fi
