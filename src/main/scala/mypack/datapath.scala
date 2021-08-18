package mypack

import chisel3._
import chisel3.util._

class dectoexeIO extends Bundle{

    val exe_op1_data = Output(UInt(64.W))
    val exe_op2_data = Output(UInt(64.W))

        //which need transmit to wb stage 
    val exe_wbaddr = Output(UInt(5.W))

}

class wbtodecIO extends Bundle{

    val wb_rd_data  = Output(UInt(64.W))
    val wb_rd_addr  = Output(UInt(5.W))
}

class regfiledebugIO extends Bundle{

    val debug_addr = Input(UInt(5.W))
    val debug_rdata = Output(UInt(64.W))
    val debug_wdata =Input(UInt(64.W))
    val debug_en =Input(Bool())
} 


class FetchDataPath extends Module
{
    val io = IO(new Bundle{
        val ice = Output(UInt(1.W))
        val iaddr = Output(UInt(32.W))
        val pc = Output(UInt(32.W))
    })
    //Instruction Fetch State
    //reg 
    val if_reg_pc = RegInit(0.U(32.W))//RegInit(0x00000000.U)
    val ice_reg = RegInit(0.U(1.W))
    //temp wire 
    val if_pc_next = Wire(UInt(32.W))

    //function
    val if_pc_plus4 = (if_reg_pc + 4.asUInt(32.W))//one condition
    
    //according to the control signal
    if_pc_next := Mux(ice_reg === 1.U,if_pc_plus4,0.U)//maybe have a problem when control add
    //according to the stop signal
    

    //assign reg 
    ice_reg := 1.U
    if_reg_pc := if_pc_next
    // assign Output
    io.ice := ice_reg
    io.pc := if_reg_pc
    io.iaddr := if_reg_pc //now there is no tranformation for IRam 
}

class DecodeDataPath extends Module
{
    val io = IO(new Bundle{
        //data from IRAM
        val dec_inst = Input(UInt(32.W))
        //data from regfiles//regilrs be instantiated in module
        // val rd1 = Input(UInt(64.W))
        // val rd2 = Input(UInt(64.W))
        //data from Fetch and it will be transmit
        //val pc = Input(UInt(32.W))
        //choose source data to execute
        val exe_op1_data = Output(UInt(64.W))
        val exe_op2_data = Output(UInt(64.W))

        // //which need transmit to wb stage 
        val exe_wbaddr = Output(UInt(5.W))
        //val dectoexeIO = new dectoexeIO()
        //val exe_wen = Output(Uint(1.W)
        //which need be wirtten in contal path
        
        //data from write back stage to write regfile
        
        val wb_rd_en = Input(UInt(1.W))
        //val wbtodecIO = Flipped(new wbtodecIO())
        val wb_rd_addr =  Input(UInt(5.W))
        val wb_rd_data = Input(UInt(64.W))

        val debug_addr = Input(UInt(5.W))
        val debug_rdata = Output(UInt(64.W))
        val debug_wdata =Input(UInt(64.W))
        val debug_en =Input(Bool())
        //val debugIO = new regfiledebugIO()

        //debug contal but it is also a true contal signal
        val dec_wen = Input(Bool())
    
    })
    //because of Instruction Ram is SRAM which reads memory need one cycle so decode instruction reg is not need other stage may be need
    //init data reg which is used in execute stage
    val exe_reg_op1_data = RegInit(0.U(64.W))
    val exe_reg_op2_data = RegInit(0.U(64.W))
    val exe_reg_wbaddr   = RegInit(0.U(5.W))

    val dec_rs1_addr = io.dec_inst(19,15)
    val dec_rs2_addr = io.dec_inst(24,20)
    val dec_wbaddr = io.dec_inst(11,7)

    //RegFile connect
    val regfile = Module(new RegisterFile())

    regfile.io.rs1_addr := dec_rs1_addr
    regfile.io.rs2_addr := dec_rs2_addr
    
    regfile.io.wen :=io.dec_wen

    //debug io connect
    regfile.io.dm_addr  := io.debug_addr
    regfile.io.dm_wdata :=io.debug_wdata
   
    regfile.io.dm_en    :=io.debug_en
    io.debug_rdata:=regfile.io.dm_rdata 
    
    val rf_rs1_data = regfile.io.rs1_data
    val rf_rs2_data = regfile.io.rs2_data

    regfile.io.waddr := io.wb_rd_addr
    regfile.io.wdata := io.wb_rd_data
    regfile.io.wen   := io.wb_rd_en
    
    //transmit to execute stage
    //R type ALU instruction doesn't need MuxCase to choose op
    exe_reg_op1_data := rf_rs1_data
    exe_reg_op2_data := rf_rs2_data
    exe_reg_wbaddr   := dec_wbaddr

    //output 
    io.exe_op1_data := exe_reg_op1_data
    io.exe_op2_data := exe_reg_op2_data
    io.exe_wbaddr   := exe_reg_wbaddr


}

class ExecuteDataPath extends Module
{
    val io = IO(new Bundle{
        val exe_op1_data = Input(UInt(64.W))
        val exe_op2_data = Input(UInt(64.W))

        //which need transmit to wb stage 
        val exe_wbaddr = Input(UInt(5.W))


        //Output
        val mem_alu_out = Output(UInt(64.W))
        val mem_wbaddr  = Output(UInt(5.W))
        //contral signal

    })
    //reg init
    val mem_reg_alu_out = RegInit(0.U)
    val mem_reg_wbaddr  = RegInit(0.U(5.W))


    val exe_alu_op1 = io.exe_op1_data.asUInt()//asUInt maybe a let val have a type
    val exe_alu_op2 = io.exe_op2_data.asUInt()
    val exe_adder_out = (exe_alu_op1 + exe_alu_op2)(64-1,0)
    val exe_alu_out = Wire(UInt(64.W))
    //depend on contal signal 
    exe_alu_out := exe_adder_out//only add instruction

    //reg assign
    mem_reg_alu_out := exe_alu_out
    mem_reg_wbaddr  := io.exe_wbaddr
    //output

    io.mem_alu_out := mem_reg_alu_out
    io.mem_wbaddr  := mem_reg_wbaddr
}

class MemoryDataPath extends Module
{
    val io = IO(new Bundle{
        val mem_alu_out = Input(UInt(64.W))
        val mem_wbaddr  = Input(UInt(5.W))

        val wb_rd_data  = Output(UInt(64.W))
        val wb_rd_addr  = Output(UInt(5.W))
        
    })
    //reg init
    val wb_reg_rd_data = RegInit(0.U(64.W))
    val wb_reg_rd_addr  = RegInit(0.U(5.W))

    
    //reg assign
    wb_reg_rd_data := io.mem_alu_out
    wb_reg_rd_addr := io.mem_wbaddr

    //output
    io.wb_rd_data := wb_reg_rd_data
    io.wb_rd_addr := wb_reg_rd_addr

}

// class WriteDataPath extends Module
// {
//     val io = IO(nwe Bundle{
//         val wb_rd_data = Input(UInt(64.W))
//         val wb_rd_addr = Input(UInt(5.W))
//     })
// }


