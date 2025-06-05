LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qti-bsp/files/common-licenses/\
BSD-3-Clause-Clear;md5=3771d4920bd6cdb8cbdf1e8344489ee0"

DEPENDS += "nlohmann-json"

FILESEXTRAPATHS:prepend := "${WORKSPACE}:"
do_rm_work[noexec] = "1"

do_compile() {
    ${CXX}  ${CXXFLAGS}  ${LDFLAGS} -I${STAGING_DIR_TARGET}/usr/include -o vsock_guest_agent main.cpp
}

VSOCKGUESTCONFIGDIR = "${sysconfdir}/vsock_guest"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 vsock_guest_agent ${D}${bindir}

    install -d ${D}${systemd_unitdir}/system/multi-user.target.wants
    ln -sf ${systemd_unitdir}/system/vsock_guest_agent.service ${D}${systemd_unitdir}/system/multi-user.target.wants/vsock_guest_agent.service
    install -m 0644 ${WORKDIR}/vsock_guest_agent.service -D ${D}${systemd_unitdir}/system/vsock_guest_agent.service

    install -d ${D}${VSOCKGUESTCONFIGDIR}
    install -m 0644 ${WORKDIR}/vsock_guest_config.json -D ${D}${VSOCKGUESTCONFIGDIR}
}

FILES:${PN} = "\
              ${systemd_unitdir}/* \
              ${bindir}/vsock_guest_agent \
              ${VSOCKGUESTCONFIGDIR}/vsock_guest_config.json \
              "