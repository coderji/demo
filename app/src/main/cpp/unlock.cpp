#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "md5.h"
#include "unlock.h"

#define TAG "Demo"
#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

void getUnlockCode(const char *sn, int length, char *code) {
    unsigned char sn_buff[SN_LENGTH + 1];
    unsigned char code_buff[SN_LENGTH];
    char *ptr = code;
    int i;

    for (i = 0; i < length && i < SN_LENGTH; i++) {
        sn_buff[i] = *(sn + i * 2);
    }
    sn_buff[i] = '\0';
    swapSN((char *) sn_buff);
    getMD5(sn_buff, code_buff);
    for (i = 0; i < SN_LENGTH; i++) {
        ptr += sprintf(ptr, "%02X", code_buff[i]);
    }
    *ptr = '\0';
    LOG("getUnlockCode swapSN:%s code:%s", sn_buff, code);
}

void swapSN(char *sn) {
    unsigned int length = strlen(sn);
    unsigned int i;
    char c;
    if (length > 4) {
        for (i = 0; i < length - 4; i++) {
            if (i % 2 == 0) {
                c = *(sn + i);
                *(sn + i) = *(sn + i + 1);
                *(sn + i + 1) = c;
            }
        }
    }
}

void getMD5(unsigned char *encrypt, unsigned char *decrypt) {
    MD5_CTX md5;
    MD5Init(&md5);
    MD5Update(&md5, encrypt, strlen((char *) encrypt));
    MD5Final(&md5, decrypt);
}
