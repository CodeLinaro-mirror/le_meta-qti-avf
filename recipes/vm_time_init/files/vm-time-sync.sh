#!/bin/sh
# Copyright (c) Qualcomm Technologies, Inc. and/or its subsidiaries.
# SPDX-License-Identifier: BSD-3-Clause-Clear

# BUILD_DATE_EPOCH is the image build time, injected by the Yocto recipe.
# Hardware RTC values below this are stale/invalid (pre-build).
BUILD_DATE_EPOCH=@@BUILD_DATE_EPOCH@@

# MIN_WAIT: initial delay before the first RTC check, giving the host
# time_daemon enough time to run and correct the RTC after boot.
# MAX_WAIT: total budget; give up if RTC is never corrected within this window.
MIN_WAIT=10
MAX_WAIT=120
POLL_INTERVAL=2
elapsed=0
RTC_NODE=/sys/class/rtc/rtc0/since_epoch

if [ ! -r "$RTC_NODE" ]; then
    logger -t vm-time-sync "RTC node $RTC_NODE not available, skipping sync"
    exit 0
fi

logger -t vm-time-sync "Waiting ${MIN_WAIT}s before first RTC check"
sleep $MIN_WAIT
elapsed=$MIN_WAIT

logger -t vm-time-sync "Starting host RTC poll (elapsed ${elapsed}s, max ${MAX_WAIT}s)"

while [ $elapsed -lt $MAX_WAIT ]; do
    rtc_now=$(timeout 2 cat "$RTC_NODE" 2>/dev/null || echo 0)
    if [ "$rtc_now" -gt "$BUILD_DATE_EPOCH" ]; then
        date -s @${rtc_now} > /dev/null 2>&1
        logger -t vm-time-sync "Synced to host RTC: $(date -Iseconds) (after ${elapsed}s)"
        exit 0
    fi
    sleep $POLL_INTERVAL
    elapsed=$((elapsed + POLL_INTERVAL))
done

logger -t vm-time-sync "WARNING: host RTC not corrected after ${MAX_WAIT}s, using floor time"
exit 0


