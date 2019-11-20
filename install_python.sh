#!/bin/bash -uxe

PACKAGE=ActivePython-3.6.6.0000-linux-x86_64-glibc-2.12-432e1938

# make directory
mkdir -p /opt/bin
cp ${PACKAGE}.tar.gz /opt
cd /opt

tar -xzvf ${PACKAGE}.tar.gz

mv ${PACKAGE} apy && cd apy && ./install.sh -I /opt/python/

ln -sf /opt/python/bin/easy_install /opt/bin/easy_install
ln -sf /opt/python/bin/pip /opt/bin/pip
ln -sf /opt/python/bin/python3 /opt/bin/python
ln -sf /opt/python/bin/python3 /opt/bin/python3
ln -sf /opt/python/bin/virtualenv /opt/bin/virtualenv
