#include <stdio.h>
#include<stdlib.h>
#include <string.h>

#define LINE_MAX_CAHRS  200
#define CMD_ERROR       256

int push(char strLine[]);

int main(int argc, char *argv[]) {
    FILE *fp;
    char strLine[LINE_MAX_CAHRS];
    int pushRet;
    char* fileName = "build.log";

    if (argc > 1) {
        fileName = argv[1];
    }
    if(NULL != (fp = fopen(fileName, "r"))) {
        while(NULL != fgets(strLine, LINE_MAX_CAHRS, fp)) {
            pushRet = push(strLine);
            if (pushRet == CMD_ERROR) {
                break;
            }
        }
        fclose(fp);
        if (pushRet == CMD_ERROR) {
            printf("error: to adb remount\n");
            return -2;
        }
    } else {
        printf("%s not found\n", fileName);
        return -1;
    }

    return 0;
}

int push(char strLine[]) {
    char *install = "Install:", *installHead = "Install: out/target/product/", *adbPush = "adb push";
    char *start, *end, cmd[LINE_MAX_CAHRS];

    strLine[strlen(strLine) - 1] = '\0';
    strcpy(cmd, adbPush); // cmd = "adb push"

    if (strstr(strLine, installHead) == NULL) {
        return -1;
    }

    start = strstr(strLine, install);
    strcat(cmd,  start + strlen(install)); // cmd = "adb push out/target/product/projectTarget/system/priv-app/Dialer/Dialer.apk"

    start = strstr(strLine, installHead);
    start = strchr(start + strlen(installHead), '/');
    end = strrchr(start, '/');
    if (NULL == start || NULL == end) {
        return -2;
    }
    strcat(cmd, " ");
    strncat(cmd, start, strlen(start) - strlen(end));

    printf("%s\n", cmd); // cmd = "adb push out/target/product/p6603/system/priv-app/Dialer/Dialer.apk /system/priv-app/Dialer"
    return system(cmd);
}
