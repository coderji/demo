#include <stdio.h>
#include <string.h>

#define FLAG_FILE   "flag.txt"
#define FLAG_OFFSET 0
#define FLAG_P      'P'
#define FLAG_F      'F'

void test_file_write() {
    FILE *fp = NULL;
    char flag = FLAG_P;
    fp = fopen(FLAG_FILE, "r+");

    if (fp != NULL) {
        printf("file open success\n");
    } else {
        printf("file open fail, create file\n");
        fp = fopen(FLAG_FILE, "w");
    }

    fseek(fp, FLAG_OFFSET, 0);
    fwrite(&flag, sizeof (char), 1, fp);

    if (fp != NULL) {
        fclose(fp);
    }
}

void test_file_read() {
    FILE *fp = NULL;
    char flag = FLAG_F;
    fp = fopen(FLAG_FILE, "r");

    if (fp != NULL) {
        printf("file open success\n");
    } else {
        printf("file open fail\n");
        return;
    }

    fseek(fp, FLAG_OFFSET, 0);
    fread(&flag, sizeof (char), 1, fp);
    printf("file read flag 0x%x %c\n", flag, flag);

    if (fp != NULL) {
        fclose(fp);
    }
}
