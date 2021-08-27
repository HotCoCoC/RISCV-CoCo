package mypack

import chisel3._
import chisel3.util._

// import difftest._

class RAMHelper extends BlackBox {
    val io = IO(new Bundle {
        val clk = Input(Clock())
        val en = Input(Bool())
        val rIdx = Input(UInt(64.W))
        val rdata = Output(UInt(64.W))
        val wIdx = Input(UInt(64.W))
        val wdata = Input(UInt(64.W))
        val wmask = Input(UInt(64.W))
        val wen = Input(Bool())
        })
}

class TopDataPath extends Module{

    val io = IO(new Bundle{
        // val dec_inst = Input(UInt(32.W))

        val debugIO = new regfiledebugIO()

        //val wb_rd_en = Input(Bool())

        //val dec_wen = Input(Bool())
    })
    val fetchdatapath = Module( new FetchDataPath())

    val ctrlpath = Module( new CtrlPath())
    val decodedatapath = Module( new DecodeDataPath())
    val executedatapath = Module( new ExecuteDataPath())
    val memorydatapath = Module( new MemoryDataPath())

    val cnt_reg = RegInit(0.U(32.W))

    cnt_reg := cnt_reg + 1.U 


    val iram = Module(new RAMHelper)
    // val dram = Module(new RAMHelper)

    val inst = RegInit(0.U(32.W))

    val idata = Wire(UInt(32.W))
    val iaddr = Wire(UInt(64.W))
    val lowdata = Wire(UInt(32.W))
    val highdata = Wire(UInt(32.W))

    val testdata = "h20009300100093".U(64.W)

    iaddr := (((fetchdatapath.io.iaddr)-(BigInt("80000000", 16)).U) >> 3)
    //iaddr := (fetchdatapath.io.iaddr)
    lowdata := iram.io.rdata(31,0)
    highdata := iram.io.rdata(63,32)
    idata := Mux(fetchdatapath.io.iaddr(2),highdata,lowdata)
    // idata := Mux(fetchdatapath.io.iaddr(2),iram.io.rdata(63,32),iram.io.rdata(31,0))
    //idata := Mux(fetchdatapath.io.iaddr(2),testdata(63,32),testdata(31,0))

    iram.io.clk :=clock
    iram.io.en := fetchdatapath.io.ice
    iram.io.rIdx := iaddr
    iram.io.wIdx := 0.U
    iram.io.wdata := 0.U
    iram.io.wmask := 0.U
    iram.io.wen := 0.U

    // dram.io.clk :=clock
    // dram.io.en := memorydatapath.io.dmem.en
    // dram.io.rIdx := memorydatapath.io.dmem.rIdx
    // memorydatapath.io.dmem.rdata := dram.io.rdata
    // dram.io.wIdx := memorydatapath.io.dmem.wIdx
    // dram.io.wdata := memorydatapath.io.dmem.wdata
    // dram.io.wmask := memorydatapath.io.dmem.wmask
    // dram.io.wen := memorydatapath.io.dmem.wen

    memorydatapath.io.dmem <> DontCare

    
    inst := idata
    //val inst = RegNext(idata)
    // ctrlpath.io.dec_inst := idata
    // decodedatapath.io.dec_inst := idata
    ctrlpath.io.dec_inst := inst
    decodedatapath.io.dec_inst := inst
    //difftest csr

    val csr = Module(new difftest.DifftestCSRState)
    csr.io.clock := clock
    csr.io.coreid := 0.U
    csr.io.mstatus := 0.U
    csr.io.mcause := 0.U
    csr.io.mepc := 0.U
    csr.io.sstatus := 0.U
    csr.io.scause := 0.U
    csr.io.sepc := 0.U
    csr.io.satp := 0.U
    csr.io.mip := 0.U
    csr.io.mie := 0.U
    csr.io.mscratch := 0.U
    csr.io.sscratch := 0.U
    csr.io.mideleg := 0.U
    csr.io.medeleg := 0.U
    csr.io.mtval:= 0.U
    csr.io.stval:= 0.U
    csr.io.mtvec := 0.U
    csr.io.stvec := 0.U
    csr.io.priviledgeMode := 0.U


    //指令提交
    val commit = Module(new difftest.DifftestInstrCommit)
    commit.io.clock := clock
    commit.io.coreid := 0.U
    commit.io.index := 0.U

    //commit.io.valid := RegNext(memorydatapath.io.wbtodecIO.wb_rd_en)
    commit.io.valid := RegNext(memorydatapath.io.wbtodecIO.wb_rd_en)
    commit.io.pc := RegNext(memorydatapath.io.wb_pc)
    commit.io.instr := RegNext(memorydatapath.io.wb_inst)
    commit.io.skip := false.B
    commit.io.isRVC := false.B
    commit.io.scFailed := false.B
    commit.io.wen := RegNext(memorydatapath.io.wbtodecIO.wb_rd_en)
    commit.io.wdata := RegNext(memorydatapath.io.wbtodecIO.wb_rd_data)
    commit.io.wdest := RegNext(memorydatapath.io.wbtodecIO.wb_rd_addr)

    //接入trap
    val trap = Module(new difftest.DifftestTrapEvent)
    trap.io.clock    := clock
    trap.io.coreid   := 0.U
    trap.io.valid    := RegNext(memorydatapath.io.wb_inst) === BigInt("0000006b", 16).U
    trap.io.code     := 0.U // GoodTrap
    trap.io.pc       := RegNext(memorydatapath.io.wb_pc)
    trap.io.cycleCnt := cnt_reg
    trap.io.instrCnt := cnt_reg

    //io.dec_inst<>ctrlpath.io.dec_inst
    //io.dec_inst<>decodedatapath.io.dec_inst

    io.debugIO<>decodedatapath.io.debugIO

    ctrlpath.io.ctl<>decodedatapath.io.ctl

    decodedatapath.io.dec_pc := fetchdatapath.io.pc

    decodedatapath.io.dectoexeIO<>executedatapath.io.dectoexeIO
    executedatapath.io.exetomemIO<> memorydatapath.io.exetomemIO
    memorydatapath.io.wbtodecIO<>decodedatapath.io.wbtodecIO

    //bypass
    decodedatapath.io.exetodecIO<>executedatapath.io.exetodecIO
    decodedatapath.io.memtodecIO<>memorydatapath.io.memtodecIO



    
}


class Top2DataPath extends Module{

    val io = IO(new Bundle{
        val dec_inst = Input(UInt(32.W))

        val debugIO = new regfiledebugIO()

        //val wb_rd_en = Input(Bool())

        //val dec_wen = Input(Bool())
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

    decodedatapath.io.dec_pc := fetchdatapath.io.pc

    decodedatapath.io.dectoexeIO<>executedatapath.io.dectoexeIO
    executedatapath.io.exetomemIO<> memorydatapath.io.exetomemIO
    memorydatapath.io.wbtodecIO<>decodedatapath.io.wbtodecIO

    memorydatapath.io.dmem := DontCare



    
}