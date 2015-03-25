#include <stdio.h>
#include "target/shellcode.h"

int main() {
	int (* shell)();
	shell=SC;
	int c = shell(stdout);
	printf("hello");
}
