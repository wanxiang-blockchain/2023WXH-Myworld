#include "../include/TunnelDeamon.h"

int main(int argc, char * argv[])
{
    TRACE();

	/* 初始化守护程序 */
    if (InitDeamon() == -1)
    {
        PRINT(2, "InitDeamon = -1");
        exit(-1);
    }
	/* 启动隧道模块 */
    if (InitTunnel() == -1)
    {
        PRINT(2, "TunnelStartt = -1");
        exit(-1);
    }
	/* 安装信号处理函数 */
    if (SetSignal() == -1)
    {
        PRINT(2, "SetSignal = -1");
       	TunnelStop();
        exit(-1);
    }
    
	PRINT(0, "pause");

    while (1)
    {
        pause();	/* 挂起该线程 */
    }

   	TunnelStop();
    return 0;
}
