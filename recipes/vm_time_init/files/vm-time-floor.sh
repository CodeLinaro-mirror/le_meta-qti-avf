#!/bin/sh
# Copyright (c) Qualcomm Technologies, Inc. and/or its subsidiaries.
# SPDX-License-Identifier: BSD-3-Clause-Clear

BUILD_DATE_EPOCH=@@BUILD_DATE_EPOCH@@

current=$(date +%s 2>/dev/null || echo 0)

if [ "$current" -lt "$BUILD_DATE_EPOCH" ]; then
    date -s @${BUILD_DATE_EPOCH} > /dev/null 2>&1
    logger -t vm-time-floor "Clock was at epoch (${current}s), applied floor: $(date -Iseconds)"
else
    logger -t vm-time-floor "Clock already sane (${current}s), floor not needed"
fi

