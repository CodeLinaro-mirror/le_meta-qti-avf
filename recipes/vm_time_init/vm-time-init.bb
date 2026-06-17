DESCRIPTION = "VM boot time floor and host-RTC sync services"

LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-bsp/files/common-licenses/\
BSD-3-Clause-Clear;md5=3771d4920bd6cdb8cbdf1e8344489ee0"

SRC_URI = " \
    file://vm-time-floor.sh \
    file://vm-time-floor.service \
    file://vm-time-sync.sh \
    file://vm-time-sync.service \
    "

inherit systemd

SYSTEMD_SERVICE:${PN} = "vm-time-floor.service vm-time-sync.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    # Use wall-clock build time, not SOURCE_DATE_EPOCH which reflects the
    # layer's git commit date (can be years old on stable/release branches).
    BUILD_EPOCH=$(date -u +%s)

    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/vm-time-floor.sh ${D}${bindir}/vm-time-floor.sh
    install -m 0755 ${WORKDIR}/vm-time-sync.sh   ${D}${bindir}/vm-time-sync.sh

    # Stamp the actual image build time into both scripts so the floor and
    # sync threshold automatically track the build date without manual updates.
    sed -i "s/@@BUILD_DATE_EPOCH@@/${BUILD_EPOCH}/g" ${D}${bindir}/vm-time-floor.sh
    sed -i "s/@@BUILD_DATE_EPOCH@@/${BUILD_EPOCH}/g" ${D}${bindir}/vm-time-sync.sh

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/vm-time-floor.service ${D}${systemd_unitdir}/system/vm-time-floor.service
    install -m 0644 ${WORKDIR}/vm-time-sync.service  ${D}${systemd_unitdir}/system/vm-time-sync.service

    install -d ${D}${systemd_unitdir}/system/sysinit.target.wants
    ln -sf ${systemd_unitdir}/system/vm-time-floor.service \
        ${D}${systemd_unitdir}/system/sysinit.target.wants/vm-time-floor.service

    install -d ${D}${systemd_unitdir}/system/multi-user.target.wants
    ln -sf ${systemd_unitdir}/system/vm-time-sync.service \
        ${D}${systemd_unitdir}/system/multi-user.target.wants/vm-time-sync.service
}

FILES:${PN} = " \
    ${bindir}/vm-time-floor.sh \
    ${bindir}/vm-time-sync.sh \
    ${systemd_unitdir}/* \
    "

