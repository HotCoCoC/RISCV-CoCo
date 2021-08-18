import mypack._

import chisel3._

class AbcUser extends Module {
    val io = IO(new Bundle {})
    val abc = Module(new Abc())

}

object AbcUser extends App {
  (new chisel3.stage.ChiselStage).emitVerilog(new AbcUser())

  //println(getVerilog(new AbcUser()))
}