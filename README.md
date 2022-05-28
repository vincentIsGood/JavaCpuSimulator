# Java Cpu Simulator
I tried to create a Simplified CPU simulator myself after having a computer organization course on assembly language with a simple architecture of CPU.

Hopefully I explained each instruction well.

## Instruction Set
The instruction set which this CPU can read is small, but the basics are shown as follows:

Instruction | Opcode | Instruction | Opcode | Instruction | Opcode
---|---|---|---|---|---
ADD | 00000000 | MOV | 00000101 | PUSH | 00001010
SUB | 00000001 | LD  | 00000110 | POP  | 00001011
NOT | 00000010 | ST  | 00000111 | CALL | 00001100
AND | 00000011 | Jcc | 00001000 | RET  | 00001101
OR  | 00000100 | HLT | 00001001 | 

Since I adopted a RISC (Reduced Instruction Set Computer)-ish philosophy, all instructions involving data transfer, except for `ST` and `LD`, are register-register instructions.

## Byte code
Our instructions uses `word` as a unit. For example, `ADD` instruction is a 1-word instruction. `1 word` is defined as `4 bytes` or `32 bit`.

For `src1`, `src2`, and `dst` bytes, they are the number of the register which you choose to use. For example, if you want to use `r1` then you need `01`.

## ADD, SUB, AND, OR, NOT, MOV
Basic instruction format for many instructions:
Opcode|src1|src2|dst
---|---|---|---

For 2-operand instructions (`NOT`, `MOV`), `src2` becomes a `00` byte

Example:
```
ADD r1, r2, r3; r1 + r2 -> r3

byte code: 00010203
```

## LD, ST
These are 2-word instructions. Their format is defined as:

Load (`LD`):
```
         +------+----+---------+---+
1st WORD:|Opcode| 00 |addr mode|dst|
         +------+----+---------+---+
2nd WORD:|      data address       |
         +-------------------------+
```

Store (`ST`):
```
         +------+----+---------+---+
1st WORD:|Opcode| src|addr mode|00 |
         +------+----+---------+---+
2nd WORD:|      data address       |
         +-------------------------+
```

**Note**: Currently, we have absolute (direct) addressing mode only (`0xff`)

Example:
```
LD addr, r1 ; addr -> r3

byte code: 0600ff01 00000008 
(0x8 is the address which contains the data LD wants)
```

## Jcc
This is also a 2-word instruction. Its condition code is shown as follows:

Condition Code (cc)|Instruction|Description
---|---|---
00000001| JR  | Always jump
00000001| JZ  | Jump if zero (zero flag is 1)
00000010| JNZ | Jump if not zero (zero flag is 0)

Jump (`Jcc`):
```
         +------+----+---------+---+
1st WORD:|Opcode| cc |addr mode| 00|
         +------+----+---------+---+
2nd WORD:|      dest address       |
         +-------------------------+
```

Example:
```
JNZ label

byte code: 0802ff01 00000008
(0x8 is the address which it jumps to)
```

## HLT
A simple 1-word instruction used to stop the program. It is defined as: `09 00 00 00`

## PUSH
This 1-word instruction. It pushes a value from a register to the stack.

Format:
```
+------+----+----+----+
|Opcode| Rn | 00 | 00 |
+------+----+----+----+

Where Rn is register n
```

Example:
```
push r1

0a010000
```

## POP
This 1-word instruction as well. It pops a value from stack to a register.

Format:
```
+------+----+----+----+
|Opcode| 00 | 00 | Rn |
+------+----+----+----+

Where Rn is register n
```

Example:
```
pop r1

0b000001
```

## CALL
This 2-word instruction saves the next instruction address on the main routine to the stack and jumps to a location.

Format:
```
         +------+----+---------+----+
1st WORD:|Opcode| 00 |addr mode| 00 |
         +------+----+---------+----+
2nd WORD:|      dest address        |
         +--------------------------+
```

Example:
```
call label

0c00ff00
00000008
(0x8 is the dest address)
```

## RET
This 1-word instruction returns the flow control from sub-routine back to main-routine by popping the stored address from the stack. It's byte code is `0d 00 00 00`.