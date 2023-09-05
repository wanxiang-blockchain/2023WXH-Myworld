#TCNAME=QCA9531
#LIBSUFF=qca9531
#
#TCDIR=/home/lcz/toolchain/qca9531
#TCBINDIR=$(TCDIR)/bin
#TCINCDIR=$(TCDIR)/include
#BINPREFFIX=mips-openwrt-linux-uclibc-
#
#CC = $(TCBINDIR)/$(BINPREFFIX)gcc
#CXX = $(TCBINDIR)/$(BINPREFFIX)g++
#AR = $(TCBINDIR)/$(BINPREFFIX)ar
#STRIP = $(TCBINDIR)/$(BINPREFFIX)strip
#LIBES= -LS
#
#LIBSTA = 
#LIBDYN = -lpthread -ldl
#
#CFLAGS = -Wall -O -fPIC -D_PG_OS_TYPE=_PG_OS_LINUX -I$(TCINCDIR)
#
#LDFLAGS = -fPIC --sysroot=$(TCDIR)
#version 1.1
#Include the toolchain configure.

#GXX = /home/ctkj/ctkj/toolchain/bin/mipsel-openwrt-linux-g++
#STRIP = /home/ctkj/ctkj/toolchain/bin/mipsel-openwrt-linux-strip
#LIBS += -Wl,-Bstatic -ltunnel_$(LIBSUFF) $(LIBSTA) -Wl,-Bdynamic $(LIBDYN)
#LIB = -std=c++11 -Bstatic -L $(LIBDIR) -l tunnel_mt7628_openwrt -L $(LIBDIR) -l crypto -Bdynamic -l pthread -l rt -l dl

include config.mk

ROOTDIR = ..
SOURCEDIR = $(ROOTDIR)/source
LIBDIR = $(ROOTDIR)/lib
INCDIR = $(ROOTDIR)/Include
SOURCE = $(SOURCEDIR)/main.cpp $(SOURCEDIR)/Trace-Change.cpp $(SOURCEDIR)/pgTunnelDeamon.cpp

LIB = -Wl,-Bstatic -ltunnel_$(LIBSUFF) $(LIBSTA) -lstdc++ -Wl,-Bdynamic $(LIBDYN)

LDFLAGS += -L./ -L$(LIBDIR)
 

NAME = TunnelDeamon

$(NAME): $(SOURCE)
	$(CXX) $(LDFLAGS) $(SOURCE) $(LIB) -o $(NAME)
	$(STRIP) $(NAME)

clean: 
	-rm $(NAME)
