#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>

void child_suicide(int signo) {
    wait(NULL);
    printf("Child suicide\n");
}

int fd_is_valid(int fd) {
    return fcntl(fd, F_GETFD) != -1 || errno != EBADF;
}

int main() {
    signal(SIGCHLD, child_suicide);
    int fd, cfd, on = 1;
    struct sockaddr_in saddr, caddr;
    socklen_t l;

    fd = socket(PF_INET, SOCK_STREAM, 0);
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (char *) &on, sizeof(on));
    saddr.sin_family = AF_INET;
    saddr.sin_port = htons(1234);
    saddr.sin_addr.s_addr = INADDR_ANY;

    bind(fd, (struct sockaddr *) &saddr, sizeof(saddr));

    listen(fd, 10);

    while (1) {
        l = sizeof(caddr);
        cfd = accept(fd, (struct sockaddr *) &caddr, &l);
        int pid = fork();
        if (pid) {
            close(cfd);
        } else {
            close(fd);
            dup2(cfd, 1);
            char buf[256] = {0};
            int e = 0;
            do {
                e = read(cfd, buf, 256);
//                write(cfd, "No kurwaaa\n", 11);
//                if(e>0) fprintf((FILE *) 2, "%i\n", e);
                if (strncmp(buf, "exit", 4) == 0) {
                    close(cfd);
                    exit(0);
                }
                for (int i = e; i < 256; i++) buf[i] = 0;
                system(buf);
                write(cfd, "\n", 1);
            } while (1);
            exit(0);
        }
    }
    close(fd);
    return 0;
}
