package mypack

import chisel3._
import chisel3.util._

class TopDataPath extends Module{

    val io = IO(new Bundle{
        val dec_inst = Input(UInt(32.W))

        val debugIO = new regfiledebugIO()

        //val wb_rd_en = Input(Bool())

        val dec_wen = Input(Bool())
    })

    val fetchdatapath = Module( new FetchDataPath())

    val ctrlpath = Module( new CtrlPath())
    val decodedatapath = Module( new DecodeDataPath())
    val executedatapath = Module( new ExecuteDataPath())
    val memorydatapath = Module( new MemoryDataPath())

    io.dec_inst<>ctrlpath.io.dec_inst
    io.dec_inst<>decodedatapath.io.dec_inst
    io.debugIO<>decodedatapath.io.debugIO

    ctrlpath.io.ctl<>decodedatapath.io.ctl

    decodedatapath.io.dectoexeIO<>executedatapath.io.dectoexeIO
    executedatapath.io.exetomemIO<> memorydatapath.io.exetomemIO
    memorydatapath.io.wbtodecIO<>decodedatapath.io.wbtodecIO



    // io.dec_inst <> decodedatapath.io.dec_inst
    // io.debug_addr<>decodedatapath.io.debug_addr
    // io.debug_rdata<>decodedatapath.io.debug_rdata
    // io.debug_wdata<>decodedatapath.io.debug_wdata
    // io.debug_en<>decodedatapath.io.debug_en
    // io.dec_wen<>decodedatapath.io.dec_wen
    // io.wb_rd_en<>decodedatapath.io.wb_rd_en

    // decodedatapath.io.exe_op1_data<>executedatapath.io.exe_op1_data
    // decodedatapath.io.exe_op2_data<>executedatapath.io.exe_op2_data
    // decodedatapath.io.exe_wbaddr<>executedatapath.io.exe_wbaddr
    
    // executedatapath.io.mem_alu_out <> memorydatapath.io.mem_alu_out
    // executedatapath.io.mem_wbaddr <> memorydatapath.io.mem_wbaddr

    // memorydatapath.io.wb_rd_addr<>decodedatapath.io.wb_rd_addr
    // memorydatapath.io.wb_rd_data<>decodedatapath.io.wb_rd_data

    //behind the times
    // executedatapath.io <> memorydatapath.io
    // memorydatapath.io <> decodedatapath.io
    // decodedatapath.io <> executedatapath.io
    
}