package mypack

import chisel3._

class Abc extends Module {
    val io = IO(new Bundle {})
}

class AND extends Module {
    val io = IO(new Bundle{
        val a = Input(UInt(2.W))
        val b = Input(UInt(2.W))
        val out = Output(UInt(2.W))
    })
    io.out := io.a & io.b
}


class FullAdder extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(1.W))
    val b = Input(UInt(1.W))
    val cin = Input(UInt(1.W))
    val s = Output(UInt(1.W))
    val cout = Output(UInt(1.W))  
  })
 
  io.s := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | ((io.a | io.b) & io.cin)
}