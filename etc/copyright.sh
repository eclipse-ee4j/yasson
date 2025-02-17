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

die(){ echo "${1}" ; exit 1 ;}

readonly RESULT_FILE="target/copyright-check.txt"
mkdir target

mvn -q validate -Pcopyright \
        > ${RESULT_FILE} || (cat ${RESULT_FILE}; die "Error running the Maven command")

grep -i "copyright" ${RESULT_FILE} \
    && die "COPYRIGHT ERROR" || echo "COPYRIGHT OK"
