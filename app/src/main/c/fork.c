#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

// https://www.cnblogs.com/winnxm/archive/2010/01/22/1654502.html
/*
 参数：
 pid：可能选择有以下四种

 1. pid大于零时，pid是信号欲送往的进程的标识。
 2. pid等于零时，信号将送往所有与调用kill()的那个进程属同一个使用组的进程。
 3. pid等于-1时，信号将送往所有调用进程有权给其发送信号的进程，除了进程1(init)。
 4. pid小于-1时，信号将送往以-pid为组标识的进程。

 sig：准备发送的信号代码，假如其值为零则没有任何信号送出，但是系统会执行错误检查，通常会利用sig值为零来检验某个进程是否仍在执行。

 返回值说明： 成功执行时，返回0。失败返回-1，errno被设为以下的某个值 EINVAL：指定的信号码无效（参数 sig 不合法） EPERM；权限不够无法传送信号给指定进程 ESRCH：参数 pid 所指定的进程或进程组不存在
 */

// https://blog.csdn.net/weixin_45003868/article/details/119517712

void sysErr(const char* str) {
	perror(str);
	exit(1);
}

int testForkKill() {
	pid_t pid, q;
	int i;
	int n = 5;

	for (i = 0; i < n; i++) {
		pid = fork();
		if (pid == -1) {
			sysErr("fork error");
		} else if (pid == 0) {
			break;
		}
		if (i == 2) {
			q = pid;
		}
	}

	if (i < 5) {
		while(1) {
			printf("I'm child %d, getpid = %u\n", i, getpid());
			sleep(1);
		}
	} else {
		sleep(1);
		kill(q, SIGKILL); //在父进程中杀死第三个创建的子进程
		while(1);
	}

	return EXIT_SUCCESS;
}
