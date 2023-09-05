
TCNAME=QCA9531_1
LIBSUFF=qca9531_musl

TCDIR=/home/lcz/toolchain/qca9531_1
TCBINDIR=$(TCDIR)/bin
TCINCDIR=$(TCDIR)/include
BINPREFFIX=mips-openwrt-linux-musl-

CC = $(TCBINDIR)/$(BINPREFFIX)gcc
CXX = $(TCBINDIR)/$(BINPREFFIX)g++
AR = $(TCBINDIR)/$(BINPREFFIX)ar
STRIP = $(TCBINDIR)/$(BINPREFFIX)strip
LIBES= -LS

LIBSTA = 
LIBDYN = -lpthread -ldl

CFLAGS = -Wall -O -fPIC -D_PG_OS_TYPE=_PG_OS_LINUX -I$(TCINCDIR)

LDFLAGS = -fPIC --sysroot=$(TCDIR)
