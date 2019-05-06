default rel

global print
global println
global getString
global getInt
global toString
global _string_length
global _string_substring
global _string_parseInt
global _string_ord
global __stradd
global __strcmp
global ___array_size

extern strcmp
extern memcpy
extern sprintf
extern strcpy
extern malloc
extern getchar
extern puts
extern printf
extern _GLOBAL_OFFSET_TABLE_


SECTION .text   

print:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     rax, qword [rbp-8H]
        add     rax, 8
        mov     rsi, rax
        lea     rdi, [rel L_016]
        mov     eax, 0
        call    printf
        nop
        leave
        ret


println:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     rax, qword [rbp-8H]
        add     rax, 8
        mov     rdi, rax
        call    puts
        nop
        leave
        ret


getString:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 32
        mov     dword [rbp-14H], 0
        lea     rax, [rel buffer.2951]
        mov     qword [rbp-10H], rax
        call    getchar
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        mov     byte [rax], dl
        jmp     L_002

L_001:  call    getchar
        add     qword [rbp-10H], 1
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        mov     byte [rax], dl
        add     dword [rbp-14H], 1
L_002:  mov     rax, qword [rbp-10H]
        movzx   eax, byte [rax]
        cmp     al, 10
        jz      L_003
        mov     rax, qword [rbp-10H]
        movzx   eax, byte [rax]
        cmp     al, 13
        jz      L_003
        mov     rax, qword [rbp-10H]
        movzx   eax, byte [rax]
        cmp     al, 32
        jz      L_003
        mov     rax, qword [rbp-10H]
        movzx   eax, byte [rax]
        cmp     al, -1
        jnz     L_001
L_003:  mov     rax, qword [rbp-10H]
        mov     byte [rax], 0
        mov     eax, dword [rbp-14H]
        add     eax, 9
        cdqe
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-8H], rax
        mov     eax, dword [rbp-14H]
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     rax, qword [rbp-8H]
        add     rax, 8
        lea     rsi, [rel buffer.2951]
        mov     rdi, rax
        call    strcpy
        mov     rax, qword [rbp-8H]
        leave
        ret


getInt:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], 0
        call    getchar
        mov     byte [rbp-0DH], al
        mov     dword [rbp-0CH], 0
        jmp     L_006

L_004:  cmp     byte [rbp-0DH], 45
        jnz     L_005
        mov     dword [rbp-0CH], 1
L_005:  call    getchar
        mov     byte [rbp-0DH], al
L_006:  cmp     byte [rbp-0DH], 47
        jle     L_004
        cmp     byte [rbp-0DH], 57
        jg      L_004
        jmp     L_008

L_007:  mov     rax, qword [rbp-8H]
        lea     rdx, [rax*8]
        mov     rax, qword [rbp-8H]
        add     rax, rax
        add     rdx, rax
        movsx   rax, byte [rbp-0DH]
        add     rax, rdx
        sub     rax, 48
        mov     qword [rbp-8H], rax
        call    getchar
        mov     byte [rbp-0DH], al
L_008:  cmp     byte [rbp-0DH], 47
        jle     L_009
        cmp     byte [rbp-0DH], 57
        jle     L_007
L_009:  cmp     dword [rbp-0CH], 0
        jz      L_010
        mov     rax, qword [rbp-8H]
        neg     rax
        jmp     L_011

L_010:  mov     rax, qword [rbp-8H]
L_011:  leave
        ret


toString:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 32
        mov     qword [rbp-18H], rdi
        mov     edi, 32
        call    malloc
        mov     qword [rbp-8H], rax
        mov     rax, qword [rbp-8H]
        lea     rcx, [rax+8H]
        mov     rax, qword [rbp-18H]
        mov     rdx, rax
        lea     rsi, [rel L_017]
        mov     rdi, rcx
        mov     eax, 0
        call    sprintf
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     rax, qword [rbp-8H]
        leave
        ret


_string_length:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-8H], rdi
        mov     rax, qword [rbp-8H]
        mov     rax, qword [rax]
        pop     rbp
        ret


_string_substring:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 48
        mov     qword [rbp-18H], rdi
        mov     qword [rbp-20H], rsi
        mov     qword [rbp-28H], rdx
        mov     rax, qword [rbp-28H]
        sub     rax, qword [rbp-20H]
        add     rax, 1
        mov     qword [rbp-10H], rax
        mov     rax, qword [rbp-10H]
        add     rax, 9
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-8H], rax
        mov     rax, qword [rbp-8H]
        mov     rdx, qword [rbp-10H]
        mov     qword [rax], rdx
        mov     rax, qword [rbp-10H]
        mov     rdx, qword [rbp-20H]
        lea     rcx, [rdx+8H]
        mov     rdx, qword [rbp-18H]
        lea     rsi, [rcx+rdx]
        mov     rdx, qword [rbp-8H]
        lea     rcx, [rdx+8H]
        mov     rdx, rax
        mov     rdi, rcx
        call    memcpy
        mov     rax, qword [rbp-10H]
        add     rax, 8
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     byte [rax], 0
        mov     rax, qword [rbp-8H]
        leave
        ret


_string_parseInt:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-18H], rdi
        mov     qword [rbp-8H], 0
        mov     dword [rbp-0CH], 0
        add     qword [rbp-18H], 8
        mov     rax, qword [rbp-18H]
        movzx   eax, byte [rax]
        cmp     al, 45
        jnz     L_013
        mov     dword [rbp-0CH], 1
        add     qword [rbp-18H], 1
        jmp     L_013

L_012:  mov     rdx, qword [rbp-8H]
        mov     rax, rdx
        shl     rax, 2
        add     rax, rdx
        add     rax, rax
        mov     rcx, rax
        mov     rax, qword [rbp-18H]
        lea     rdx, [rax+1H]
        mov     qword [rbp-18H], rdx
        movzx   eax, byte [rax]
        movsx   eax, al
        sub     eax, 48
        cdqe
        add     rax, rcx
        mov     qword [rbp-8H], rax
L_013:  mov     rax, qword [rbp-18H]
        movzx   eax, byte [rax]
        test    al, al
        jnz     L_012
        cmp     dword [rbp-0CH], 0
        jz      L_014
        mov     rax, qword [rbp-8H]
        neg     rax
        jmp     L_015

L_014:  mov     rax, qword [rbp-8H]
L_015:  pop     rbp
        ret


_string_ord:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     rax, qword [rbp-10H]
        add     rax, 8
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        movzx   eax, byte [rax]
        movsx   rax, al
        pop     rbp
        ret


__stradd:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 48
        mov     qword [rbp-28H], rdi
        mov     qword [rbp-30H], rsi
        mov     rax, qword [rbp-28H]
        mov     rax, qword [rax]
        mov     qword [rbp-18H], rax
        mov     rax, qword [rbp-30H]
        mov     rax, qword [rax]
        mov     qword [rbp-10H], rax
        mov     rax, qword [rbp-18H]
        lea     rdx, [rax+8H]
        mov     rax, qword [rbp-10H]
        add     rax, rdx
        add     rax, 1
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-8H], rax
        mov     rdx, qword [rbp-18H]
        mov     rax, qword [rbp-10H]
        add     rdx, rax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     rax, qword [rbp-18H]
        mov     rdx, qword [rbp-28H]
        lea     rsi, [rdx+8H]
        mov     rdx, qword [rbp-8H]
        lea     rcx, [rdx+8H]
        mov     rdx, rax
        mov     rdi, rcx
        call    memcpy
        mov     rax, qword [rbp-10H]
        mov     rdx, qword [rbp-30H]
        lea     rsi, [rdx+8H]
        mov     rdx, qword [rbp-18H]
        lea     rcx, [rdx+8H]
        mov     rdx, qword [rbp-8H]
        add     rcx, rdx
        mov     rdx, rax
        mov     rdi, rcx
        call    memcpy
        mov     rax, qword [rbp-18H]
        lea     rdx, [rax+8H]
        mov     rax, qword [rbp-10H]
        add     rax, rdx
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     byte [rax], 0
        mov     rax, qword [rbp-8H]
        leave
        ret


__strcmp:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     rax, qword [rbp-10H]
        lea     rdx, [rax+8H]
        mov     rax, qword [rbp-8H]
        add     rax, 8
        mov     rsi, rdx
        mov     rdi, rax
        call    strcmp
        cdqe
        leave
        ret


___array_size:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-8H], rdi
        mov     rax, qword [rbp-8H]
        mov     rax, qword [rax-8H]
        pop     rbp
        ret



SECTION .data   


SECTION .bss    align=32

buffer.2951:
        resb    1048576


SECTION .rodata 

L_016:
        db 25H, 73H, 00H

L_017:
        db 25H, 6CH, 64H, 00H


