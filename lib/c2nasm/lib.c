#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef long int64_t;
typedef char* pointer_t;
#define REG_SIZE 8


void print(pointer_t str) {
	str += REG_SIZE;
	while(*str != '\0')
		putchar(*(str++));
}
void println(pointer_t str) {
	str += REG_SIZE;
	while(*str != '\0')
		putchar(*(str++));
	putchar('\n');
}
pointer_t getString() {
	static char buffer[1024 * 1024];	//	1MB buffer
	int length = 0;
	char * str = buffer;
	*str = getchar();
	while(*str == '\n' || *str == '\r')
		*str = getchar();
	while(*str != '\n' && *str != '\r' && *str != -1){
		*(++str) = getchar();
		++length;
	}
	*str = '\0';
	pointer_t ret = malloc(length + REG_SIZE + 1);
	*((int64_t*)ret) = length;
	strcpy(ret + REG_SIZE, buffer);
	return ret;
}
int64_t getInt() {
	int64_t ret = 0;
	char ch = getchar();
	int neg = 0;
	while(ch < '0' || ch > '9'){
		if (ch == '-')
			neg = 1;
		ch = getchar();
	}
	while(ch >='0' && ch <= '9')
		ret = (ret << 3) + (ret << 1) + ch - '0', ch = getchar(); 
	return (neg) ? -ret : ret;
}
pointer_t toString(int64_t a) {
	pointer_t ret = malloc(REG_SIZE * 4);
	*((int64_t*)ret) = sprintf(ret + REG_SIZE, "%ld", a);
	return ret;
}
int64_t _string_length(pointer_t ptr) {
	return *((int64_t*)ptr);
}
pointer_t _string_substring(pointer_t ptr, int64_t left, int64_t right) {
	int64_t length = right - left + 1;
	pointer_t ret = malloc(REG_SIZE + length + 1);
	*((int64_t*)ret) = length;
	memcpy(ret + REG_SIZE, ptr + REG_SIZE + left, length);
	ret[REG_SIZE + length] = '\0';
	return ret;
}

int64_t _string_parseInt(pointer_t ptr) {
	int64_t value = 0;
	int neg = 0;
	ptr += REG_SIZE;
	if(*ptr == '-') {
		neg = 1;
		ptr++;
	}
	while(*ptr)
		value = value * 10 + (*(ptr++) - '0');
	return neg ? -value : value;
}

int64_t _string_ord(pointer_t ptr, int64_t pos) {
	return ptr[REG_SIZE + pos];
}

pointer_t __stradd(pointer_t sa, pointer_t sb) {
	int64_t la = *((int64_t*)sa);
	int64_t lb = *((int64_t*)sb);
	pointer_t ret = malloc(REG_SIZE + la + lb + 1);
	*((int64_t*)ret) = la + lb;
	memcpy(ret + REG_SIZE, sa + REG_SIZE, la);
	memcpy(ret + REG_SIZE + la, sb + REG_SIZE, lb);
	ret[REG_SIZE + la + lb] = '\0';
	return ret;
}

int64_t __strcmp(pointer_t sa, pointer_t sb) {
	return strcmp(sa + REG_SIZE, sb + REG_SIZE);
}

int64_t ___array_size(int64_t arr[]) {
		return (int64_t)(arr[-1]);
}
