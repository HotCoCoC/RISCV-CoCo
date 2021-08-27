package mypack


import chisel3._
import chisel3.util._

trait OpConstants{
    //control signal

    val Y = true.B
    val N = false.B

    //PC select signal
    val PC_4 = 0.asUInt(2.W)
    //Branch type
    val BR_N = 0.asUInt(4.W)
    //RS1 operand select signal
    val OP1_RS1 = 0.asUInt(2.W)
    val OP1_X   = 0.asUInt(2.W)
    //RS2 operand select signal
    val OP2_RS2 = 0.asUInt(3.W)
    val OP2_ITYPE  = 1.asUInt(3.W) // immediate, I-type
    val OP2_X   = 0.asUInt(3.W)
    //Register operand output enable signal
    val OEN_0   = false.B
    val OEN_1   = true.B

    //register file write enable signal
    val REN_0   = false.B
    val REN_1   = true.B
    //alu operation signal
    val ALU_ADD    = 0.asUInt(4.W)
    val ALU_SUB    = 1.asUInt(4.W)
    val ALU_SLL    = 2.asUInt(4.W)
    val ALU_SRL    = 3.asUInt(4.W)
    val ALU_SRA    = 4.asUInt(4.W)
    val ALU_AND    = 5.asUInt(4.W)
    val ALU_OR     = 6.asUInt(4.W)
    val ALU_XOR    = 7.asUInt(4.W)
    val ALU_SLT    = 8.asUInt(4.W)
    val ALU_SLTU   = 9.asUInt(4.W)
    val ALU_COPY_1 = 10.asUInt(4.W)
    val ALU_COPY_2 = 11.asUInt(4.W)
    val ALU_X      = 0.asUInt(4.W)

    //writeback select signal
    val WB_ALU  = 0.asUInt(2.W)
    val WB_MEM  = 1.asUInt(2.W)
    val WB_PC4  = 2.asUInt(2.W)
    val WB_CSR  = 3.asUInt(2.W)
    val WB_X    = 0.asUInt(2.W)
    //memory write signal
    val MWR_0   = false.B
    val MWR_1   = true.B
    val MWR_X   = false.B
    //memory enable signal
    val MEN_0   = false.B
    val MEN_1   = true.B
    val MEN_X   = false.B
    //memory mask type signal
    val MSK_B   = 0.asUInt(3.W)
    val MSK_BU  = 1.asUInt(3.W)
    val MSK_H   = 2.asUInt(3.W)
    val MSK_HU  = 3.asUInt(3.W)
    val MSK_W   = 4.asUInt(3.W)
    val MSK_X   = 4.asUInt(3.W)



}

trait MemoryOpConstants
{
   val MT_X  = 0.asUInt(3.W)
   val MT_B  = 1.asUInt(3.W)
   val MT_H  = 2.asUInt(3.W)
   val MT_W  = 3.asUInt(3.W)
   val MT_D  = 4.asUInt(3.W)
   val MT_BU = 5.asUInt(3.W)
   val MT_HU = 6.asUInt(3.W)
   val MT_WU = 7.asUInt(3.W)

   val M_X   = false.B
   val M_XRD = false.B // int load
   val M_XWR = true.B // int store

}