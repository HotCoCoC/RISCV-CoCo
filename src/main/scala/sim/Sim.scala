package sim

import chisel3._
import mypack._
import difftest._

class SimTop extends Module {
  // 规定的端口格式，不多不少，但可以暂时不使用。
  val io = IO(new Bundle {
    val logCtrl = new LogCtrlIO
    val perfInfo = new PerfInfoIO
    val uart = new UARTIO
  })

  val rvcore = Module(new TopDataPath)

  // rvcore 访问内存的端口，下个步骤会说明，此处暂不连接
  //TopDataPath.io.ram.rdata := 0.U
//   rvcore.io.dec_inst := 0.U
  rvcore.io.debugIO := DontCare
  // 暂不使用 difftest 给出的端口，但是还是需要初始化合法值
  io.uart.in.valid := false.B
  io.uart.out.valid := false.B
  io.uart.out.ch := 0.U
}