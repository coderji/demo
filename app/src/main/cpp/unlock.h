#ifndef DEMO_UNLOCK_H
#define DEMO_UNLOCK_H

#define SN_LENGTH   16
#define CODE_LENGTH 32

void getUnlockCode(const char *sn, int length, char *code);
void swapSN(char *sn);
void getMD5(unsigned char *encrypt, unsigned char *decrypt);

#endif //DEMO_UNLOCK_H
