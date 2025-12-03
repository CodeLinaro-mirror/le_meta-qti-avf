DESCRIPTION = "Vsock Guest Agent for oemvm"

require vsock-guest.inc

SRC_URI = " \
            file://vsock_guest_agent.service \
            file://vsock_guest_config.json \
            file://vendor/qcom/opensource/vm-interlink/main.cpp \
            "

S = "${WORKDIR}/vendor/qcom/opensource/vm-interlink"

CXXFLAGS += "-DOEMVM"
