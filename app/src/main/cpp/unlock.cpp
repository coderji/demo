#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "md5.h"
#include "unlock.h"

#define TAG "Demo-Unlock"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

void getUnlockCode(const char *sn, int length, char *code) {
    unsigned char sn_buff[32] = {0};
    unsigned char code_buff[32] = {0};
    char *ptr = code;
    int i;

    if (length == 8) {
        sn_buff[0] = *(sn + 1);
        sn_buff[1] = *sn;
        sn_buff[2] = *(sn + 3);
        sn_buff[3] = *(sn + 2);
        strcpy((char *) sn_buff + 4, sn + 4);
    } else if (length == 12) {
        sn_buff[0] = *(sn + 1);
        sn_buff[1] = *sn;
        sn_buff[2] = *(sn + 3);
        sn_buff[3] = *(sn + 2);
        sn_buff[4] = *(sn + 5);
        sn_buff[5] = *(sn + 4);
        sn_buff[6] = *(sn + 7);
        sn_buff[7] = *(sn + 6);
        sn_buff[8] = *(sn + 9);
        sn_buff[9] = *(sn + 8);
        sn_buff[10] = *(sn + 11);
        sn_buff[11] = *(sn + 10);
    }
    getMD5(sn_buff, code_buff);
    for (i = 0; i < 16; i++) {
        ptr += sprintf(ptr, "%02X", code_buff[i]);
    }
    *ptr = '\0';
    if (0 == sn_buff[0]) {
        LOG("getUnlockCode sn/sn_buff:%s code:%s", sn, code);
    } else {
        LOG("getUnlockCode sn:%s sn_buff:%s code:%s", sn, sn_buff, code);
    }
}

void getMD5(unsigned char *encrypt, unsigned char *decrypt) {
    MD5_CTX md5;
    MD5Init(&md5);
    MD5Update(&md5, encrypt, strlen((char *) encrypt));
    MD5Final(&md5, decrypt);
}
