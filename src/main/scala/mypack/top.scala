package mypack

import chisel3._
import chisel3.util._

class TopDataPath extends Module{

    val io = IO(new Bundle{
        val dec_inst = Input(UInt(32.W))

        val debug_addr = Input(UInt(5.W))
        val debug_rdata = Output(UInt(64.W))
        val debug_wdata =Input(UInt(64.W))
        val debug_en =Input(Bool())

        val dec_wen = Input(Bool())
    })

    val fetchdatapath = Module( new FetchDataPath())


    val decodedatapath = Module( new DecodeDataPath())
    val executedatapath = Module( new ExecuteDataPath())
    val memorydatapath = Module( new MemoryDataPath())

    



    io <> decodedatapath.io

    
    executedatapath.io <> memorydatapath.io
    memorydatapath.io <> decodedatapath.io
    decodedatapath.io <> executedatapath.io
    
}