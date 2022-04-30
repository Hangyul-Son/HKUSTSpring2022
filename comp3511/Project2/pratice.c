#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {
    pid_t pid;
    pid = fork();
    int status = 0;
    if(pid>0) {
        printf("Parent process pid: %d\n", pid);
        pid = wait(&status);
        printf("pid: %d status: %d", pid, status);
    }
    else {
        printf("Child process pid: %d\n", pid);
        exit(4);
    }
    vmstat();
}