package mypack

import chisel3._
import chisel3.util._

import Instructions._
import Constants._

// object tester extends CtrlPath{
//     def main(args:Array[String])
//     {
//         println(cs0)
//     }
    
// }

class CtlToDatIO extends Bundle()
{

   val op1_sel    = Output(UInt(2.W))//1
   val op2_sel    = Output(UInt(3.W))//2
   val alu_fun    = Output(UInt(4.W))//3
   val wb_sel     = Output(UInt(2.W))//4
   val rf_wen     = Output(Bool())//5 need to see
   val mem_val    = Output(Bool())
   val mem_fcn    = Output(Bool())
   val mem_typ    = Output(UInt(3.W))
}

class CtrlPath extends Module{
    val io = IO(new Bundle{
        val dec_inst = Input(UInt(32.W))
        val ctl      = new CtlToDatIO()
    })

    val csignals = 
        ListLookup(io.dec_inst,List(N,OP1_X,OP2_X,ALU_X,WB_X,REN_0),
        Array(      /* val  |  op1  |   op2   |  ALU  | wb    |  rf  |*/
                    /* inst |  sel  |   sel   |  fcn  | sel   |  wen |*/
              ADDI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_ADD,WB_ALU, REN_1), 
              ANDI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_AND,WB_ALU, REN_1), 
              ORI  -> List(Y, OP1_RS1, OP2_ITYPE,ALU_OR ,WB_ALU, REN_1), 
              XORI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_XOR,WB_ALU, REN_1), 
              SLTI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_SLT,WB_ALU, REN_1), 
              SLTIU-> List(Y, OP1_RS1, OP2_ITYPE,ALU_SLTU,WB_ALU, REN_1), 
              SRAI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_SRA,WB_ALU, REN_1), 
              SRLI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_SRL,WB_ALU, REN_1), 
              SLLI -> List(Y, OP1_RS1, OP2_ITYPE,ALU_SLL,WB_ALU, REN_1),

              ADD  -> List(Y, OP1_RS1, OP2_RS2 ,ALU_ADD, WB_ALU, REN_1),
              AND  -> List(Y, OP1_RS1, OP2_RS2,ALU_AND,WB_ALU, REN_1), 
              OR   -> List(Y, OP1_RS1, OP2_RS2,ALU_OR ,WB_ALU, REN_1), 
              XOR  -> List(Y, OP1_RS1, OP2_RS2,ALU_XOR,WB_ALU, REN_1), 
              SLT  -> List(Y, OP1_RS1, OP2_RS2,ALU_SLT,WB_ALU, REN_1), 
              SLTU -> List(Y, OP1_RS1, OP2_RS2,ALU_SLTU,WB_ALU, REN_1), 
              SRA  -> List(Y, OP1_RS1, OP2_RS2,ALU_SRA,WB_ALU, REN_1), 
              SRL  -> List(Y, OP1_RS1, OP2_RS2,ALU_SRL,WB_ALU, REN_1), 
              SLL  -> List(Y, OP1_RS1, OP2_RS2 ,ALU_SLL, WB_ALU, REN_1)

              ))

    val (cs_val_inst: Bool) :: cs_op1_sel :: cs_op2_sel :: cs0 = csignals
    val cs_alu_fun :: cs_wb_sel :: (cs_rf_wen: Bool) :: Nil = cs0

    io.ctl.op1_sel := cs_op1_sel
    io.ctl.op2_sel := cs_op2_sel
    io.ctl.alu_fun     := cs_alu_fun
    io.ctl.wb_sel      := cs_wb_sel
    io.ctl.rf_wen      := cs_rf_wen

    //val dec_wbaddr   = io.dec_inst(11, 7)

    // val exe_reg_wbaddr      = Reg(UInt())
    // val mem_reg_wbaddr      = Reg(UInt())
    // val wb_reg_wbaddr       = Reg(UInt())

    // exe_reg_wbaddr := dec_wbaddr
    
    // mem_reg_wbaddr := exe_reg_wbaddr
    // wb_reg_wbaddr  := mem_reg_wbaddr


}


