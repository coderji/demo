main: main.o epoll.o file.o fork.o socket.o
	gcc main.o epoll.o file.o fork.o socket.o -o demo
main.o: main.c
epoll.o: epoll.c
file.o: file.c
fork.o: fork.c
socket.o: socket.c

clean:
	rm *.o
	rm demo
