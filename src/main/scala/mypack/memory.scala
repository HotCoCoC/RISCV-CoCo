package mypack

import chisel3._
import chisel3.util._

class MemPortIO extends Bundle
{
    val en = Output(Bool())
    val rIdx = Output(UInt(64.W))
    val rdata = Input(UInt(64.W))
    val wIdx = Output(UInt(64.W))
    val wdata = Output(UInt(64.W))
    val wmask = Output(UInt(64.W))
    val wen = Output(Bool())

    //val clk = Output(Clock())  RAMHelper时钟信号
}