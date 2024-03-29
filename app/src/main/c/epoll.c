#include <stdio.h>
#include <stdlib.h>
#include <sys/epoll.h>
#include <unistd.h>

// https://blog.csdn.net/tjcwt2011/article/details/125169005

int testEpoll() {
	int epfd, nfds;
	struct epoll_event ev, events[5]; //ev用于注册事件，数组用于返回要处理的事件
	epfd = epoll_create(1); //只需要监听一个描述符——标准输入
	ev.data.fd = STDIN_FILENO;
	ev.events = EPOLLIN | EPOLLET; //监听读状态同时设置ET模式
	epoll_ctl(epfd, EPOLL_CTL_ADD, STDIN_FILENO, &ev); //注册epoll事件
	for (;;) {
		nfds = epoll_wait(epfd, events, 5, -1);
		for (int i = 0; i < nfds; i++) {
			if (events[i].data.fd == STDIN_FILENO) {
				printf("welcome to epoll's world!\n");
			}
		}
	}
	return EXIT_SUCCESS;
}
